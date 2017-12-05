package de.btu.monopoly.core;

import de.btu.monopoly.core.service.FieldService;
import de.btu.monopoly.core.service.PlayerService;
import de.btu.monopoly.data.*;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.data.parser.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;

import de.btu.monopoly.input.InputHandler;
import org.xml.sax.SAXException;

/**
 * @author Christian Prinz
 */
public class Game {

    public static final Logger LOGGER = Logger.getLogger(Game.class.getCanonicalName());
    
    /**
     * Spielbrett
     */
    public GameBoard board;
    
    /**
     * Spieler (Zuschauer und aktive Spieler)
     */
    public final Player[] players;

    /**
     * Die fachliche Komponente des Spiels als Einheit, bestehend aus einem Spielbrett, den Spielern sowie Zuschauern.
     *
     * @param playerCount Anzahl Spieler
     *
     */
    public Game(int playerCount) {
        this.players = new Player[playerCount];
    }

    public void init() {
        LOGGER.log(Level.INFO, "Spiel wird initialisiert");

        try {
            CardStack stack = CardStackParser.parse("/data/card_data.xml");
            LOGGER.log(Level.FINEST, stack.toString());
            GameBoardParser.setCardLoadout0(stack);
            GameBoardParser.setCardLoadout1(stack);
            board = GameBoardParser.parse("/data/field_data.xml");
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            LOGGER.log(Level.WARNING, "Fehler beim initialisieren des Boards / der Karten.", ex);
        }

        for (int i = 0; i < players.length; i++) {
            Player player = new Player("Mathias " + (i + 1), i, 1500);
            players[i] = player;
            board.addPlayer(player);
        }
    }

    public void start() {
        LOGGER.setLevel(Level.ALL);
        LOGGER.log(Level.INFO, "Spiel beginnt.");

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

        LOGGER.info(String.format("%s ist an der Reihe", player.getName()));
        if (player.isInJail()) {
            jailPhase(player);
        }

        if (!player.isInJail()) {
            do {
                rollResult = rollPhase(player, doubletCounter);
                doubletCounter = rollResult[0] + rollResult[1];

                if (doubletCounter < 3) {
                    fieldPhase(player, rollResult);
                    if (player.isInJail()) {
                        // Spieler gerät durch die fieldPhase, also durch eine Karte oder durch das Feld ins Gefängnis
                        break; // keine Aktionsphase unter dieser Voraussetzung
                    }
                }
                actionPhase(player);
            }
            while (rollResult[0] == rollResult[1] && doubletCounter < 3);
        }
    }

    private void jailPhase(Player player) {
        int choice;
        do {
            LOGGER.info(String.format(" %s ist im Gefängnis und kann: %n1. 3 mal Würfeln, um mit einem Pasch freizukommen "
                    + "%n2. Bezahlen (50€) %n3. Gefängnis-Frei-Karte benutzen", player.getName()));

            choice = InputHandler.getUserInput(3);
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
                    LOGGER.log(Level.WARNING, "Fehler: Gefängnis-Switch überschritten!");
                    break;
            }
        } while (player.isInJail() && choice != 1);
    }

    private void processJailRollOption(Player player) {
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

    private void processJailPayOption(Player player) {
        if (PlayerService.takeMoney(player, 50)) {
            LOGGER.info(String.format("%s hat 50 gezahlt und ist frei!", player.getName()));
            PlayerService.freeFromJail(player);
        } else {
            LOGGER.info(String.format("%s hat kein Geld um sich freizukaufen.", player.getName()));
        }
    }

    private void processJailCardOption(Player player) {
        if (board.getCardManager().useJailCard(player)) {
            LOGGER.info(String.format("%s hat eine Gefängnis-Frei-Karte benutzt.", player.getName()));
        }
        else LOGGER.info(String.format("%s hat keine Gefängnis-Frei-Karten mehr.", player.getName()));
    }

    private int[] rollPhase(Player player, int doubletCounter) {
        int[] rollResult;

        LOGGER.info(String.format("%s ist dran mit würfeln.", player.getName()));
        rollResult = PlayerService.roll(player);
        doubletCounter += (rollResult[0] == rollResult[1]) ? 1 : 0;

        if (doubletCounter >= 3) {
            LOGGER.info(String.format("%s hat seinen 3. Pasch und geht nicht über LOS, direkt ins Gefängnis!", player.getName()));
            FieldService.toJail(player);
        } else {
            board.getFieldManager().movePlayer(player, rollResult[0] + rollResult[1]);
        }
        return rollResult;
    }

    private void fieldPhase(Player player, int[] rollResult) {
        GameBoard.FieldType type = GameBoard.FIELD_STRUCTURE[player.getPosition()];
        switch (type) {
            case TAX: // Steuerfeld
                TaxField taxField = (TaxField) board.getFields()[player.getPosition()];
                FieldService.payTax(player, taxField);
                break;

            case CARD: // Kartenfeld
                CardField cardField = (CardField) board.getFields()[player.getPosition()];
                LOGGER.fine(String.format("%s steht auf einem Kartenfeld (%s).", player.getName(), cardField));
                board.getCardManager().processPlayerOnCardField(player);
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
                Property prop = (Property) board.getFields()[player.getPosition()];
                processPlayerOnPropertyField(player, prop, rollResult);
                break;
        }
    }

    private void processPlayerOnPropertyField(Player player, Property prop, int[] rollResult) {
        Player other = prop.getOwner();
        if (other == null) { // Feld frei
            LOGGER.info(String.format("%s steht auf einem freien Grundstück und kann es: %n[1] Kaufen %n[2] Nicht Kaufen",
                    player.getName()));
            processBuyPropertyFieldOption(player, prop);
        }
        else if (other == player) { // Property im eigenen Besitz
            LOGGER.fine(String.format("%s steht auf seinem eigenen Grundstück.", player.getName()));
        }
        else { // Property nicht in eigenem Besitz
            LOGGER.info(String.format("%s steht auf dem Grundstück von %s.", player.getName(), other.getName()));
            PlayerService.takeAndGiveMoneyUnchecked(player, other, FieldService.getRent(prop, rollResult));
        }
    }
    
    private void processBuyPropertyFieldOption(Player player, Property prop) {
        switch (InputHandler.getUserInput(2)) {
            case 1: // Kaufen
                LOGGER.info(String.format("%s >> %s", player.getName(), prop.getName()));
                if (!FieldService.buyProperty(player, prop, prop.getPrice())) {
                    LOGGER.warning(String.format("%s hat nicht genug Geld! %s wird zwangsversteigert.",
                            player.getName(), prop.getName()));
                    betPhase(prop);
                }
                break;
                
            case 2: // Auktion
                LOGGER.info(player.getName() + "hat sich gegen den Kauf entschieden, die Straße wird nun versteigert.");
                betPhase(prop);
                break;
                
            default:
                LOGGER.warning("getUserInput() hat index außerhalb des zurückgegeben.");
                break;
        }
    }

    private void actionPhase(Player player) {
        int choice;

        do {
            LOGGER.log(Level.INFO, player.getName() + "! Waehle eine Aktion:\n[1] - Nichts\n[2] - Haus kaufen\n[3] - Haus verkaufen\n[4] - "
                    + "Hypothek aufnehmen\n[5] - Hypothek abbezahlen");

            choice = InputHandler.getUserInput(5);
            if (choice != 1) {
                Field currField = board.getFields()[InputHandler.askForField(player, board) - 1]; // Wahl der Strasse
                if (currField instanceof Property) {
                    Property property = (Property) currField;
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

    private void betPhase(Property property) {
        Auction auc = new Auction(property, players);
        auc.startAuction();
    }
}
