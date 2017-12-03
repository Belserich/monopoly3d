package de.btu.monopoly.core;

import de.btu.monopoly.data.Card;
import de.btu.monopoly.data.CardStack;
import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.data.parser.*;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * @author Christian Prinz
 */
public class Game {

    public static final Logger LOGGER = Logger.getLogger(Game.class.getCanonicalName());
    private FieldManager fieldManager;
    public GameBoard board;
    public final Player[] players;
    public boolean gameOver;

    public FieldManager getFieldManager() {
        return this.fieldManager;
    }

    /**
     * Die zentrale Manager-Klasse für alles was das Spiel betrifft.
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

        assert board != null;

        fieldManager = new FieldManager(board.getFields());

        for (int i = 0; i < players.length; i++) {
            players[i] = new Player("Mathias " + (i + 1), i, 1500);
        }
    }

    public void start() {
        LOGGER.setLevel(Level.ALL);
        LOGGER.log(Level.INFO, "Spiel beginnt.");

        do {
            for (int i = 0; i < players.length; i++) {
                Player activePlayer = players[i];
                if (!(activePlayer.isBankrupt())) {
                    turn(activePlayer);
                }
            }
        } while (!gameOver);

        for (Player player : players) {
            if (!player.isBankrupt()) {
                LOGGER.log(Level.INFO, player.getName() + " hat das Spiel gewonnen!");
            }
        }
    }

    public void turn(Player player) {
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

                if (doubletCounter >= 3) {
                    fieldPhase(player, rollResult);
                }
                actionPhase(player);
            } while (rollResult[0] == rollResult[1] && doubletCounter < 3);
        }
    }

    public void jailPhase(Player player) {
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
        if (player.getJailCardAmount() > 0) {
            LOGGER.info(String.format("%s hat eine Gefängnis-Frei-Karte benutzt.", player.getName()));
            player.removeJailCard();
            PlayerService.freeFromJail(player);
        } else {
            LOGGER.info(String.format("%s hat keine Gefängnis-Frei-Karten mehr.", player.getName()));
        }
    }

    public int[] rollPhase(Player player, int doubletCounter) {
        int[] rollResult;

        LOGGER.info(String.format("%s ist dran mit würfeln.", player.getName()));
        rollResult = PlayerService.roll(player);
        doubletCounter += (rollResult[0] == rollResult[1]) ? 1 : 0;

        if (doubletCounter >= 3) {
            LOGGER.info(String.format("%s hat seinen 3. Pasch und geht nicht über LOS, direkt ins Gefängnis!", player.getName()));
            fieldManager.toJail(player);
        } else {
            fieldManager.movePlayer(player, rollResult[0] + rollResult[1], ((GoField) board.getFields()[0]).getAmount());
        }
        return rollResult;
    }

    public void fieldPhase(Player player, int[] rollResult) {
        GameBoard.FieldType type = GameBoard.FIELD_STRUCTURE[player.getPosition()];
        switch (type) {
            case GO:
                break;

            case TAX: // Steuerfeld
                processPlayerOnTaxField(player, (TaxField) board.getFields()[player.getPosition()]);
                break;

            case CARD: // Kartenfeld
                processPlayerOnCardField(player, (CardField) board.getFields()[player.getPosition()]);
                break;

            case GO_JAIL: // "Gehen Sie Ins Gefaengnis"-Feld
                LOGGER.log(Level.INFO, player.getName() + " muss ins Gefaengnis!");
                fieldManager.toJail(player);
                break;

            case CORNER:
                break;

            default:
                processPlayerOnPropertyField(player, ((Property) board.getFields()[player.getPosition()]), rollResult);
                break;
        }
    }

    private void processPlayerOnCardField(Player player, CardField field) {
        LOGGER.fine(String.format("%s steht auf einem Kartenfeld (%s).", player.getName(),
                board.getFields()[player.getPosition()].getName()));
        Card nextCard = field.getCardStack().nextCard();
        Card.Action[] actions = nextCard.getActions();

        assert actions.length > 0;

        switch (actions[0]) {
            case JAIL:
                player.addJailCard();
                break;
            case GIVE_MONEY:
                PlayerService.giveMoney(player, nextCard.getArgs()[0]); // TODO check args
                break;
            case GO_JAIL:
                fieldManager.toJail(player);
                break;
            case PAY_MONEY:
                PlayerService.takeMoney(player, nextCard.getArgs()[0]);
                break;
            case MOVE_PLAYER:
                fieldManager.movePlayer(player, nextCard.getArgs()[0], ((GoField) board.getFields()[0]).getAmount());
                break;
            case SET_POSITION:
                fieldManager.movePlayer(player, nextCard.getArgs()[0] - player.getPosition(),
                        ((GoField) board.getFields()[0]).getAmount());
                break;
            case PAY_MONEY_ALL:
                int amount = nextCard.getArgs()[0];
                PlayerService.takeMoney(player, amount * players.length);
                for (Player other : players) {
                    PlayerService.giveMoney(other, amount);
                }
                break;
            case NEXT_SUPPLY:
                int fields = 0;
                while (GameBoard.FIELD_STRUCTURE[player.getPosition() + (++fields)] != GameBoard.FieldType.SUPPLY);
                fieldManager.movePlayer(player, fields, ((GoField) board.getFields()[0]).getAmount());
            case NEXT_STATION_RENT_AMP:
                fields = 0;
                while (GameBoard.FIELD_STRUCTURE[player.getPosition() + fields] != GameBoard.FieldType.STATION) {
                    fields++;
                }
                fieldManager.movePlayer(player, fields, ((GoField) board.getFields()[0]).getAmount()); // TODO Amplifier
            case BIRTHDAY: // TODO
            case RENOVATE: // TODO
        }
    }

    private void processPlayerOnTaxField(Player player, TaxField field) {
        LOGGER.fine(String.format("%s steht auf einem Steuerfeld.", player.getName()));
        if (PlayerService.checkLiquidity(player, field.getTax())) {
            PlayerService.takeMoney(player, field.getTax());
        } else {
            LOGGER.info(String.format("%s kann seine Steuern nicht abzahlen!", player.getName()));
            this.gameOver = PlayerService.bankrupt(player, board, players);
        }
    }

    private void processPlayerOnPropertyField(Player player, Property field, int[] rollResult) {
        Player other = field.getOwner();
        if (other == null) { // Feld frei
            LOGGER.info(String.format("%s steht auf einem freien Grundstück und kann es: %n[1] Kaufen %n[2] Nicht Kaufen",
                    player.getName()));
            switch (getUserInput(2)) {
                case 1: //Kaufen
                    LOGGER.info(player.getName() + " >> " + field.getName());
                    if (!fieldManager.buyProperty(player, field, field.getPrice())) {
                        LOGGER.info(player.getName() + "hat nicht genug Geld! " + field.getName() + " wird nun zwangsversteigert.");
                        betPhase(field);
                    }
                    break;
                case 2: //Auktion @multiplayer
                    LOGGER.info(player.getName() + "hat sich gegen den Kauf entschieden, die Straße wird nun versteigert.");
                    betPhase(field);
                    break;
                default:
                    LOGGER.log(Level.WARNING, "getUserInput() hat index außerhalb des zurückgegeben.");
                    break;
            }
        } else if (other == player) { // Property im eigenen Besitz
            LOGGER.log(Level.FINE, player.getName() + " steht auf seinem eigenen Grundstück.");
        } else {                      // Property nicht in eigenem Besitz
            LOGGER.log(Level.INFO, player.getName() + " steht auf dem Grundstück von " + other.getName() + ".");

            int rent = field.getRent();
            if (field instanceof SupplyField) {
                rent = rent * (rollResult[0] + rollResult[1]);
            }

            if (PlayerService.checkLiquidity(player, rent)) {
                LOGGER.info(String.format("%s zahlt %d Miete.", player.getName(), rent));
                PlayerService.takeMoney(player, rent);
                PlayerService.giveMoney(field.getOwner(), rent);
            } else {
                LOGGER.info(String.format("%s kann die geforderte Miete nicht zahlen!", player.getName()));
                this.gameOver = PlayerService.bankrupt(player, board, players); // TODO
            }
        }
    }

    public void actionPhase(Player player) {
        int choice;

        do {
            LOGGER.log(Level.INFO, player.getName() + "! Waehle eine Aktion:\n[1] - Nichts\n[2] - Haus kaufen\n[3] - Haus verkaufen\n[4] - "
                    + "Hypothek aufnehmen\n[5] - Hypothek abbezahlen");

            choice = getUserInput(5);
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
                            fieldManager.buyHouse(streetField);
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
                            fieldManager.sellHouse(streetField);
                            break;
                        case 4: // Hypothek aufnehmen
                            if (property.getOwner() == player && (!(property.isMortgageTaken()))) {
                                fieldManager.takeMortgage(property);
                            } else {
                                LOGGER.info("Diese Straße gehört dir nicht, oder hat schon eine Hypothek.");
                            }
                            break;
                        case 5: // Hypothek zurückzahlen {
                            if (property.getOwner() != player) {
                                LOGGER.log(Level.INFO, "Diese Straße gehört dir nicht, oder hat keine Hypothek zum abzahlen.");
                            }
                            fieldManager.payMortgage(property);
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

    public void betPhase(Property property) { // TODO
        Auction auc = new Auction(property, players);
        auc.startAuction();
    }

    //-------------------------------------------------------------------------
    //------------ Console-Input Methoden -------------------------------------
    //-------------------------------------------------------------------------
    /**
     *
     * @param max maximale Anzahl an Auswahlmoeglichkeiten
     * @return int der Durch den Anwender gewaehlt wurde
     */
    public int getUserInput(int max) {

        Scanner scanner = new Scanner(System.in);
        int output = -1;

        // Solange nicht der richtige Wertebereich eingegeben wird, wird die Eingabe wiederholt.
        do {
            LOGGER.log(Level.INFO, "Eingabe: ");
            try {
                output = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.WARNING, "FEHLER: falsche Eingabe!");
            }

            if (output < 1 || output > max) {
                LOGGER.log(Level.INFO, "Deine Eingabe liegt nicht im Wertebereich! Bitte erneut versuchen:");
            }
        } while (output < 1 || output > max);

        return output;
    }
}
