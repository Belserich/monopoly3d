package de.btu.monopoly.core;

import de.btu.monopoly.core.service.FieldService;
import de.btu.monopoly.core.service.PlayerService;
import de.btu.monopoly.data.Auction;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.data.parser.CardStackParser;
import de.btu.monopoly.data.parser.GameBoardParser;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.input.InputHandler;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.networkClasses.BroadcastPlayerChoiceRequest;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * @author Christian Prinz
 */
public class Game {

    public static long SEED;

    private static final Logger LOGGER = Logger.getLogger(Game.class.getCanonicalName());

    /**
     * Spielbrett
     */
    private GameBoard board;

    /**
     * Spieler (Zuschauer und aktive Spieler)
     */
    private final Player[] players;

    private final GameClient client;

    /**
     * Die fachliche Komponente des Spiels als Einheit, bestehend aus einem Spielbrett, den Spielern sowie Zuschauern.
     *
     * @param players Spieler
     *
     */
    public Game(Player[] players, GameClient client, long randomSeed) {
        this.players = players;
        this.client = client;
        this.SEED = randomSeed;
    }

    public void init() {
        LOGGER.log(Level.INFO, "Spiel wird initialisiert.");

        try {
            CardStack stack = CardStackParser.parse("/data/card_data.xml");
            LOGGER.log(Level.FINEST, stack.toString());
            GameBoardParser.setCardLoadout0(stack);
            GameBoardParser.setCardLoadout1(stack);
            board = GameBoardParser.parse("/data/field_data.xml");
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            LOGGER.log(Level.WARNING, "Fehler beim initialisieren des Boards / der Karten.", ex);
        }

        for (Player player : players) {
            board.addPlayer(player);
        }

        System.err.println("-------------------------");
    }

    public void start() {
        LOGGER.setLevel(Level.ALL);
        LOGGER.info("Spiel beginnt.");

        while (board.getActivePlayers().size() > 1) {
            for (Player activePlayer : board.getActivePlayers()) {
                turn(activePlayer);
                if (!activePlayer.getBank().isLiquid()) {
                    PlayerService.bankrupt(activePlayer, board);
                }
            }
        }

        LOGGER.info(String.format("%s hat das Spiel gewonnen!", board.getActivePlayers().get(0).getName()));
    }

    private void turn(Player player) {
        int[] rollResult;
        int doubletCounter = 0;

        LOGGER.info(String.format("%s ist an der Reihe.", player.getName()));
        if (player.isInJail()) {
            jailPhase(player);
        }

        if (!player.isInJail()) {
            do {
                rollResult = rollPhase(player, doubletCounter);
                doubletCounter += (rollResult[0] == rollResult[1]) ? 1 : 0;

                if (doubletCounter < 3) {
                    fieldPhase(player, rollResult);
                    if (player.isInJail()) {
                        // Spieler gerät durch die fieldPhase, also durch eine Karte oder durch das Feld ins Gefängnis
                        break; // keine Aktionsphase unter dieser Voraussetzung
                    }
                }
                actionPhase(player);
            } while (rollResult[0] == rollResult[1]);
        }
    }

    private void jailPhase(Player player) {
        int choice;
        do {
            LOGGER.info(String.format(" %s ist im Gefängnis und kann: %n[1] - 3-mal Würfeln, um mit einem Pasch freizukommen "
                    + "%n[2] - Bezahlen (50€) %n[3] - Gefängnis-Frei-Karte benutzen", player.getName()));

            choice = getClientChoice(player, 3);
            switch (choice) {
                case 1:
                    processJailRollOption(player);
                    break;

                case 2: // Freikaufen
                    processJailPayOption(player);
                    break;

                case 3: // Freikarte ausspielen
                    processJailCardOption(player);
                    break;

                default:
                    LOGGER.log(Level.WARNING, "Fehler: Die Wahl des Spielers ist außerhalb des erlaubten Bereichs!");
                    break;
            }
        } while (player.isInJail() && choice != 1);
    }

    private int getClientChoice(Player player, int max) {
        Player thisPlayer = client.getPlayerOnClient();

        if (thisPlayer == player) {
            int choice = InputHandler.getUserInput(max);
            BroadcastPlayerChoiceRequest packet = new BroadcastPlayerChoiceRequest();
            packet.setChoice(choice);
            client.sendTCP(packet);
            return choice;
        } else {
            do {
                BroadcastPlayerChoiceRequest[] packets = client.getPlayerChoiceObjects();
                if (packets.length > 1) {
                    LOGGER.warning("Fehler: Mehr als ein choice-Packet registriert!");
                    return -1;
                } else if (packets.length == 1) {
                    return packets[0].getChoice();
                }
            } while (true);
        }
    }

    public void processJailRollOption(Player player) {
        int[] result = PlayerService.roll(player);
        if (result[0] == result[1]) {
            PlayerService.freeFromJail(player);
        } else {
            player.addDayInJail();
            if (player.getDaysInJail() >= 3) {
                LOGGER.info("Drei Runden ohne Pasch, Spieler muss zahlen.");
                PlayerService.takeMoneyUnchecked(player, 50);
                PlayerService.freeFromJail(player);
            }
        }
    }

    public void processJailPayOption(Player player) {
        if (PlayerService.takeMoney(player, 50)) {
            LOGGER.info(String.format("%s hat 50 gezahlt und ist frei!", player.getName()));
            PlayerService.freeFromJail(player);
        } else {
            LOGGER.info(String.format("%s hat kein Geld um sich freizukaufen.", player.getName()));
        }
    }

    public void processJailCardOption(Player player) {
        if (board.getCardManager().useJailCard(player)) {
            LOGGER.info(String.format("%s hat eine Gefängnis-Frei-Karte benutzt.", player.getName()));
        } else {
            LOGGER.info(String.format("%s hat keine Gefängnis-Frei-Karten mehr.", player.getName()));
        }
    }

    private int[] rollPhase(Player player, int doubletCounter) {
        int[] rollResult;
        int doubletCount = doubletCounter;

        LOGGER.info(String.format("%s ist dran mit würfeln.", player.getName()));
        rollResult = PlayerService.roll(player);
        doubletCount += (rollResult[0] == rollResult[1]) ? 1 : 0;

        if (doubletCount >= 3) {
            LOGGER.info(String.format("%s hat seinen 3. Pasch und geht nicht über LOS, direkt ins Gefängnis!", player.getName()));
            FieldService.toJail(player);
        } else {
            board.getFieldManager().movePlayer(player, rollResult[0] + rollResult[1]);
        }
        return rollResult;
    }

    public void fieldPhase(Player player, int[] rollResult) {
        GameBoard.FieldType type = GameBoard.FIELD_STRUCTURE[player.getPosition()];
        switch (type) {
            case TAX: // Steuerfeld
                TaxField taxField = (TaxField) board.getFields()[player.getPosition()];
                FieldService.payTax(player, taxField);
                break;

            case CARD: // Kartenfeld
                CardField cardField = (CardField) board.getFields()[player.getPosition()];
                LOGGER.fine(String.format("%s steht auf einem Kartenfeld (%s).", player.getName(), cardField.getName()));
                board.getCardManager().manageCardAction(player, cardField.nextCard());
                break;

            case GO_JAIL: // "Gehen Sie Ins Gefaengnis"-Feld
                LOGGER.info(String.format("%s muss ins Gefaengnis!", player.getName()));
                FieldService.toJail(player);
                break;

            case CORNER: // Eckfeld
                break;

            case GO: // "LOS"-Feld
                break;

            default:
                PropertyField prop = (PropertyField) board.getFields()[player.getPosition()];
                processPlayerOnPropertyField(player, prop, rollResult);
                break;
        }
    }

    private void processPlayerOnPropertyField(Player player, PropertyField prop, int[] rollResult) {
        Player other = prop.getOwner();
        if (other == null) { // Feld frei
            LOGGER.info(String.format("%s steht auf %s. Wähle eine Aktion!%n[1] Kaufen %n[2] Nicht kaufen",
                    player.getName(), prop.getName()));
            processBuyPropertyFieldOption(player, prop);
        } else if (other == player) { // PropertyField im eigenen Besitz
            LOGGER.fine(String.format("%s steht auf seinem eigenen Grundstück.", player.getName()));
        } else { // PropertyField nicht in eigenem Besitz
            LOGGER.info(String.format("%s steht auf %s. Dieses Grundstück gehört von %s.",
                    player.getName(), prop.getName(), other.getName()));
            PlayerService.takeAndGiveMoneyUnchecked(player, other, FieldService.getRent(prop, rollResult));
        }
    }

    private void processBuyPropertyFieldOption(Player player, PropertyField prop) {
        switch (getClientChoice(player, 2)) {
            case 1: // Kaufen
                LOGGER.info(String.format("%s >> %s", player.getName(), prop.getName()));
                if (!FieldService.buyPropertyField(player, prop, prop.getPrice())) {
                    LOGGER.warning(String.format("%s hat nicht genug Geld! %s wird zwangsversteigert.",
                            player.getName(), prop.getName()));
                    betPhase(prop);
                }
                break;

            case 2: // Auktion
                LOGGER.info(player.getName() + " hat sich gegen den Kauf entschieden, die Straße wird nun versteigert.");
                betPhase(prop);
                break;

            default:
                LOGGER.warning("Fehler: Wahl außerhalb des gültigen Bereichs!");
                break;
        }
    }

    private void actionPhase(Player player) {
        int choice;

        do {
            LOGGER.info(String.format("%s ist an der Reihe! Waehle eine Aktion:%n[1] - Nichts%n[2] - Haus kaufen%n[3] - Haus verkaufen%n[4] - "
                    + "Hypothek aufnehmen%n[5] - Hypothek abbezahlen", player.getName()));

            choice = getClientChoice(player, 5);
            if (choice != 1) {
                Field currField = board.getFields()[InputHandler.askForField(player, board) - 1]; // Wahl der Strasse
                if (currField instanceof PropertyField) {
                    PropertyField property = (PropertyField) currField;
                    switch (choice) {
                        case 2: // Haus kaufen
                            if (!(currField instanceof StreetField)) {
                                LOGGER.info("Gewähltes Feld ist keine Straße!");
                                break;
                            }
                            StreetField streetField = (StreetField) property;
                            if (streetField.getOwner() != player) {
                                LOGGER.info("Diese Straße gehört dir nicht.");
                                break;
                            }
                            board.getFieldManager().buyHouse(streetField);
                            break;

                        case 3: //Haus verkaufen
                            if (!(currField instanceof StreetField)) {
                                LOGGER.info("Gewähltes Feld ist keine Straße!");
                                break;
                            }
                            streetField = (StreetField) property;
                            if (streetField.getOwner() != player) {
                                LOGGER.info("Diese Straße gehört dir nicht, oder hat keine Häuser zum verkaufen.");
                                break;
                            }
                            board.getFieldManager().sellHouse(streetField);
                            break;

                        case 4: // Hypothek aufnehmen
                            if (property.getOwner() == player && (!(property.isMortgageTaken()))) {
                                board.getFieldManager().takeMortgage(property);
                            } else {
                                LOGGER.info("Diese Straße gehört dir nicht, oder hat schon eine Hypothek.");
                            }
                            break;

                        case 5: // Hypothek zurückzahlen {
                            if (property.getOwner() != player) {
                                LOGGER.log(Level.INFO, "Diese Straße gehört dir nicht, oder hat keine Hypothek zum abzahlen.");
                            }
                            board.getFieldManager().payMortgage(property);
                            LOGGER.log(Level.INFO, "Hypothek abgezahlt.");
                            break;

                        default:
                            LOGGER.log(Level.WARNING, "Fehler: StreetFieldSwitch überlaufen.");
                            break;
                    }
                }
            }
        } while (choice != 1);
    }

    private void betPhase(PropertyField property) {
        Auction auc = new Auction(property, players);
        auc.startAuction();
    }

    public GameBoard getBoard() {
        return board;
    }

    public Player[] getPlayers() {
        return players;
    }
}
