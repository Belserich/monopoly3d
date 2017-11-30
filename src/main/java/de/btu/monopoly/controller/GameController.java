package de.btu.monopoly.controller;

import de.btu.monopoly.data.Card;
import de.btu.monopoly.data.CardStack;
import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.data.parser.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * @author Christian Prinz
 */
public class GameController {

    /**
     * Waehrungstyp
     */
    public static final String CURRENCY_TYPE = "€";

    /**
     * Logger
     */
    public static final Logger logger = Logger.getLogger(GameController.class.getCanonicalName());

    /**
     * das Spielbrett
     */
    public GameBoard board;

    /**
     * die Mitspieler
     */
    public final Player[] players;

    /**
     * Gibt an, ob das Spiel beendet ist.
     */
    public boolean gameOver;

    /**
     * Anzahl Pasches
     */
    //public int doubletCounter;
    /**
     * Array mit beiden Wuerfelergebnissen
     */
    //public int[] rollResult = new int[2];
    /**
     * Feld, auf dem sich der Spieler befindet
     */
    //public Field currField;
    /**
     * Die zentrale Manager-Klasse für alles was das Spiel betrifft.
     *
     * @param playerCount Anzahl Spieler
     *
     */
    public GameController(int playerCount) {
        this.players = new Player[playerCount];
    }

    public void init() {
        logger.log(Level.INFO, "Spiel wird initialisiert");

        try {
            CardStack stack = CardStackParser.parse("/data/card_data.xml");
            logger.log(Level.FINEST, stack.toString());
            GameBoardParser.setCardLoadout0(stack);
            GameBoardParser.setCardLoadout1(stack);
            board = GameBoardParser.parse("/data/field_data.xml");
        } catch (IOException | SAXException | ParserConfigurationException ex) {
            logger.log(Level.WARNING, "Fehler beim initialisieren des Boards / der Karten.", ex);
        }

        assert board != null;

        for (int i = 0; i < players.length; i++) {
            players[i] = new Player("Mathias " + (i + 1), i, 1500);
        }
    }

    public void start() {
        logger.log(Level.INFO, "Spiel beginnt.");

        do {
            for (int i = 0; i < players.length; i++) {
                Player activePlayer = players[i];		        // momentanen Spieler setzen
                if (!(activePlayer.isSpectator())) {	        // Spieler ist kein Beobachter
                    turnPhase(activePlayer);
                }
            }
        } while (!gameOver);

        for (Player player : players) {
            if (!player.isSpectator()) {
                logger.log(Level.INFO, player.getName() + " hat das Spiel gewonnen!");
            }
        }
    }

    /**
     * Rundenphase eines Spielers
     */
    public void turnPhase(Player player) {
        logger.log(Level.INFO, player.getName() + " ist dran!");
        int[] result;
        int doubletCounter = 0; 	        // Paschzaehler zuruecksetzen

        if (player.isInJail()) {
            jailPhase(player);
        }
        do {		                    // bei Pasch wiederholen
            result = rollPhase(player, doubletCounter);
            fieldPhase(player, result);

            // actionPhase() entfaellt, wenn der Spieler grade erst ins Gefaengnis kam
            if (!player.isInJail() || (player.getDaysInJail() > 0)) {
                actionPhase(player);
            }
        } while (result[0] == result[1]);

        //Wuerfelergebnis zuruecksetzen
        result[0] = 0;
        result[1] = 0;
    }

//-----------------------------------------------------------------------------
//----------- EINZELNE PHASEN -------------------------------------------------
//-----------------------------------------------------------------------------
    /**
     * Gefängnisphase
     *
     * @param player Spieler in der Gefaengnisphase
     */
    public void jailPhase(Player player) {
        boolean repeat;
        do {
            repeat = false;
            logger.log(Level.INFO, player.getName() + "ist im Gefängnis und kann: \n1. 3 mal Würfeln, um mit einem Pasch freizukommen "
                    + "\n2. Bezahlen (50€) \n3. Gefängnis-Frei-Karte benutzen");

            switch (getUserInput(3)) { // @GUI
                // OPTION 1: wuerfeln (bis zu drei mal)
                case 1:
                    int[] result = roll(player);                 // wuerfeln
                    if (!(result[0] == result[1])) {       // kein Pasch -> im Gefaengnis bleiben
                        logger.log(Level.INFO, player.getName() + " hat keinen Pasch und bleibt im Gefängnis.");
                        player.addDayInJail();
                    } else {                        // sonst frei
                        logger.log(Level.INFO, player.getName() + " hat einen Pasch und ist frei.");
                        player.setInJail(false);
                        player.setDaysInJail(0);
                    }
                    // Wenn drei mal kein Pasch dann bezahlen
                    if (player.getDaysInJail() == 3) {
                        if (checkLiquidity(player, 50)) {
                            logger.log(Level.INFO, player.getName() + "hat schon 3 mal keinen Pasch und muss nun 50"
                                    + CURRENCY_TYPE + " zahlen!");
                            takeMoney(player, 50);
                            player.setInJail(false);
                            player.setDaysInJail(0);
                        } else {        //wenn pleite game over
                            logger.log(Level.INFO, player.getName() + " hat schon 3 mal gewürfelt und kann nicht zahlen.");
                            bankrupt(player);
                        }
                    }
                    break;

                //OPTION 2: Bezahlen
                case 2:
                    if (checkLiquidity(player, 50)) {
                        logger.log(Level.INFO, player.getName() + " hat 50" + CURRENCY_TYPE + " gezahlt und ist frei!");
                        takeMoney(player, 50);
                        player.setInJail(false);
                        player.setDaysInJail(0);
                    } else { // muss in der GUI deaktiviert sein!!!
                        logger.log(Level.INFO, player.getName() + " hat kein Geld um sich freizukaufen.");
                        repeat = true;
                    }
                    break;

                //OPTION 3: Freikarte ausspielen
                case 3:
                    if (player.getJailCardAmount() > 0) {
                        logger.log(Level.INFO, player.getName() + " hat eine Gefängnis-Frei-Karte benutzt.");
                        player.removeJailCard(); // TODO jail-card
                        player.setInJail(false);
                        player.setDaysInJail(0);
                    } else { // muss in der GUI deaktiviert sein!!!
                        logger.log(Level.INFO, player.getName() + " hat keine Gefängnis-Frei-Karten mehr.");
                        repeat = true;
                    }
                    break;

                default:
                    logger.log(Level.WARNING, "FEHLER: Gefängnis-Switch überschritten");
                    repeat = true;
                    break;

            }
        } while (repeat);

    }

    /**
     * die Wurfphase (wuerfeln und ziehen)
     *
     * @param player Spieler in der Wurfphase
     */
    public int[] rollPhase(Player player, int doubletCounter) {
        int[] rollResult = null;
        logger.log(Level.INFO, player.getName() + " ist dran mit würfeln.");
        if (!(player.isInJail())) { //Gefaengnis hat eigenes Wuerfeln
            rollResult = roll(player);
            doubletCounter += (rollResult[0] == rollResult[1]) ? 1 : 0;
            if (doubletCounter >= 3) {
                logger.log(Level.INFO, player.getName() + " hat seinen 3. Pasch und geht nicht über LOS, direkt ins Gefängnis!");
                moveToJail(player);
            }
        }
        if (!(player.isInJail())) { //kann sich nach wuerfeln aendern
            movePlayer(player, rollResult[0] + rollResult[1]);
        }
        return rollResult;
    }

    /**
     * die Feldphase (Feldaktionen)
     *
     * @param player Spieler in der Feldphase
     */
    public void fieldPhase(Player player, int[] rollResult) {
        GameBoard.FieldType type = GameBoard.FIELD_STRUCTURE[player.getPosition()];
        switch (type) {
            case STREET:
            // siehe case SUPPLY

            case STATION:
            // siehe case SUPPLY

            case SUPPLY: // Strasse / Bahnhof / Werk
                processPlayerOnPropertyField(player, (Property) board.getFields()[player.getPosition()], rollResult);
                break;

            case TAX: // Steuerfeld
                processPlayerOnTaxField(player, (TaxField) board.getFields()[player.getPosition()]);
                break;

            case CARD: // Kartenfeld
                processPlayerOnCardField(player, (CardField) board.getFields()[player.getPosition()]);
                break;

            case GO_JAIL: // "Gehen Sie Ins Gefaengnis"-Feld
                logger.log(Level.INFO, player.getName() + " muss ins Gefaengnis!");
                moveToJail(player);
                break;

            default:
                break; // "Los"-Feld
        }
    }

    private void processPlayerOnCardField(Player player, CardField field) {
        logger.fine(String.format("%s steht auf einem Kartenfeld (%s).", player.getName(),
                board.getFields()[player.getPosition()].getName()));
        Card nextCard = field.getCardStack().nextCard();
        Card.Action[] actions = nextCard.getActions();

        assert actions.length > 0;

        switch (actions[0]) {
            case JAIL:
                player.addJailCard();
                break;
            case GIVE_MONEY:
                giveMoney(player, nextCard.getArgs()[0]); // TODO check args
                break;
            case GO_JAIL:
                moveToJail(player);
                break;
            case PAY_MONEY:
                takeMoney(player, nextCard.getArgs()[0]);
                break;
            case MOVE_PLAYER:
                movePlayer(player, nextCard.getArgs()[0]);
                break;
            case SET_POSITION:
                movePlayer(player, nextCard.getArgs()[0] - player.getPosition());
                break;
            case PAY_MONEY_ALL:
                int amount = nextCard.getArgs()[0];
                takeMoney(player, amount * players.length);
                for (Player other : players) {
                    giveMoney(other, amount);
                }
                break;
            case NEXT_SUPPLY:
                int fields = 0;
                while (GameBoard.FIELD_STRUCTURE[player.getPosition() + (++fields)] != GameBoard.FieldType.SUPPLY);
                movePlayer(player, fields);
            case NEXT_STATION_RENT_AMP:
                fields = 0;
                while (GameBoard.FIELD_STRUCTURE[player.getPosition() + (++fields)] != GameBoard.FieldType.STATION);
                movePlayer(player, fields); // TODO Amplifier
            case BIRTHDAY: // TODO
            case RENOVATE: // TODO
        }
    }

    private void processPlayerOnTaxField(Player player, TaxField field) {
        logger.log(Level.FINE, player.getName() + " steht auf einem Steuerfeld.");
        if (checkLiquidity(player, field.getTax())) {
            takeMoney(player, field.getTax());
        } else {
            logger.log(Level.INFO, player.getName() + " kann seine Steuern nicht abzahlen!");
            bankrupt(player);
        }
    }

    private void processPlayerOnPropertyField(Player player, Property field, int[] rollResult) { //TODO was wenn im eigenen Besitz
        // prüft den Besitzer
        Player other = field.getOwner();
        if (other == null) { // Feld frei
            logger.log(Level.INFO, player.getName() + " steht auf einem freien Grundstück und kann es: \n[1] Kaufen \n[2] Nicht Kaufen");
            switch (getUserInput(2)) { //@GUI
                case 1: //Kaufen
                    logger.info(player.getName() + " >> " + field.getName());
                    if (!buyStreet(player, field, field.getPrice())) {
                        logger.info(player.getName() + "hat nicht genug Geld! " + field.getName() + " wird nun zwangsversteigert.");
                        betPhase(field);
                    }
                    break;
                case 2: //Auktion @multiplayer
                    logger.info(player.getName() + "hat sich gegen den Kauf entschieden, die Straße wird nun versteigert.");
                    betPhase(field);
                    break;
                default:
                    logger.log(Level.WARNING, "getUserInput() hat index außerhalb des zurückgegeben.");
                    break;
            }
        } else if (other != null) { // Property nicht im eigenen Besitz
            logger.log(Level.INFO, player.getName() + " steht auf dem Grundstück von " + other.getName() + ".");

            int rent = field.getRent();
            if (field instanceof SupplyField) {
                rent = rent * (rollResult[0] + rollResult[1]);
            }

            if (checkLiquidity(player, rent)) {
                logger.log(Level.INFO, player.getName() + " zahlt " + rent + CURRENCY_TYPE + " Miete.");
                takeMoney(player, rent);
                giveMoney(field.getOwner(), rent);
            } else {
                logger.log(Level.INFO, player.getName() + " kann die geforderte Miete nicht zahlen!");
                bankrupt(player);
            }
        } else {
            logger.log(Level.FINE, player.getName() + " steht auf seinem eigenen Grundstück.");
        }
    }

    /**
     *
     * Die Aktionsphase (Bebauung, Hypothek, aktivieren der Handelsphase)
     *
     * @param player Spieler in der Aktionsphase
     */
    public void actionPhase(Player player) { //@optimize switches vereinfachen
        // TODO hier muss später noch der Handel implementiert werden
        int choice;
        do {
            logger.log(Level.INFO, player.getName() + "! Waehle eine Aktion:\n[1] - Nichts\n[2] - Haus kaufen\n[3] - Haus verkaufen\n[4] - "
                    + "Hypothek aufnehmen\n[5] - Hypothek abbezahlen");

            choice = getUserInput(5);
            if (choice != 1) {
                Field currField = board.getFields()[askForField(player) - 1]; // Wahl der Strasse

                if (currField instanceof Property) {
                    Property property = (Property) currField;
                    switch (choice) {
                        case 2: /*
                         * Haus kaufen
                         */ {
                            if (!(currField instanceof StreetField)) {
                                logger.log(Level.INFO, "Gewähltes Feld ist keine Straße!");
                                break;
                            }
                            StreetField streetField = (StreetField) property;
                            if ((streetField.getOwner() == player) && (streetField.getHouseCount() < 5)) {
                                // wenn im Besitz und nicht vollgebaut
                                buyBuilding(player, streetField);
                            } else {
                                logger.log(Level.INFO, "Diese Straße gehört dir nicht, oder ist voll bebaut.");
                            }
                            break;
                        }
                        case 3: /*
                         * Haus verkaufen
                         */ {
                            if (!(currField instanceof StreetField)) {
                                logger.log(Level.INFO, "Gewähltes Feld ist keine Straße!");
                                break;
                            }
                            StreetField streetField = (StreetField) property;
                            if ((streetField.getOwner() == player) && (streetField.getHouseCount() > 0)) {
                                // wenn im Besitz und nicht 'hauslos'
                                sellBuilding(player, streetField);
                            } else {
                                logger.log(Level.INFO, "Diese Straße gehört dir nicht, oder hat keine Häuser zum verkaufen.");
                            }
                            break;
                        }
                        case 4: /*
                         * Hypothek aufnehmen
                         */ {
                            // wenn im Besitz und noch keine Hypothek aufgenommen
                            if (property.getOwner() == player && (!(property.isMortgageTaken()))) {
                                takeMortgage(player, property);
                                logger.log(Level.INFO, "Hypothek aufgenommen.");
                            } else {
                                logger.log(Level.INFO, "Diese Straße gehört dir nicht, oder hat schon eine Hypothek.");
                            }
                            break;
                        }
                        case 5: /*
                         * Hypothek abbezahlen
                         */ {
                            // wenn im Besitz und Hypothek aufgenommen
                            if (property.getOwner() == player && (property.isMortgageTaken())) {
                                payMortgage(player, property);
                                logger.log(Level.INFO, "Hypothek abgezahlt.");
                            } else {
                                logger.log(Level.INFO, "Diese Straße gehört dir nicht, oder hat keine Hypothek zum abzahlen.");
                            }
                            break;
                        }
                        default:
                            logger.log(Level.WARNING, "FEHLER: StreetFieldSwitch überlaufen.");
                            break;
                    }
                }
            }
        } while (choice != 1);
    }

    /**
     * die Versteigerungsphase
     */
    public void betPhase(Property property) {
        // Starten einer neuen Autkion
        Auction auc = new Auction(property, players);
        auc.startAuction();

        // Kauf der auktionierten Strasse
        buyStreet(auc.getWinner(), property, auc.getPrice());

        // @multiplayer
    }

//-----------------------------------------------------------------------------
//----------- EINZELNE METHODEN------------------------------------------------
//-----------------------------------------------------------------------------
//----------- SPIELERMETHODEN -------------------------------------------------
    /**
     * das Wuerfeln. Ergebnisse werden lokal in rollResult und doubletCounter gespeichert
     */
    public int[] roll(Player player) {
        int[] rollResult = new int[2];
        // Erzeugen der Zufallszahl
        rollResult[0] = ((int) (Math.random() * 6)) + 1;
        rollResult[1] = ((int) (Math.random() * 6)) + 1;

        logger.log(Level.INFO, player.getName() + " würfelt " + rollResult[0] + " + " + rollResult[1]
                + " = " + (rollResult[0] + rollResult[1]));

        return rollResult;
    }

    /**
     * bewegt den Spieler zu einer neuen Position.
     *
     * @param player Spieler der bewegt wird
     */
    public void movePlayer(Player player, int fields) {
        if (player.getPosition() + fields > 39) {
            logger.log(Level.FINE, player.getName() + " hat das \"LOS\"-Feld passiert und erhaelt "
                    + ((GoField) board.getFields()[0]).getAmount() + " " + CURRENCY_TYPE + ".");
            giveMoney(player, ((GoField) board.getFields()[0]).getAmount());

            player.setPosition(player.getPosition() + fields - 39);
        } else {
            player.setPosition(player.getPosition() + fields);
        }
    }

    /**
     * bewegt den Spieler ins Gefängnis und setzt seine Attribute entsprechend
     *
     * @param player Spieler der ins Gefaengnis kommt
     */
    public void moveToJail(Player player) {
        /*
         * Spieler wird auf Position 10 gesetzt setInJail wird true, damit der Spieler nicht "nur zu Besuch" ist Die Tage im
         * Gefängnis werden auf 0 gesetzt
         */
        player.setPosition(10);
        player.setInJail(true);
        player.setDaysInJail(0);
        logger.log(Level.INFO, player.getName() + " ist jetzt im Gefaengnis.");
    }

    /**
     * ueberpruft ob der uebergebene Spieler mindestens soviel Geld besitzt, wie die Methode uebergeben bekommt.
     *
     * @param player Spieler der auf Liquiditaet geprueft wird
     * @param amount Geld was der Spieler besitzen muss
     */
    public boolean checkLiquidity(Player player, int amount) {

        logger.log(Level.INFO, "Es wird geprüft, ob " + player.getName() + " genug Geld hat für die Transaktion.");
        return (player.getMoney() - amount) > 0;

    }

    /**
     * Einem Spieler wird der uebergebene Betrag von seinem Konto abgezogen.
     *
     * @param player Spieler dem der Betrag abgezogen wird
     * @param amount Betrag der dem Spieler abgezogen wird
     */
    public void takeMoney(Player player, int amount) {

        player.setMoney(player.getMoney() - amount);
        logger.log(Level.INFO, player.getName() + " werden " + amount + CURRENCY_TYPE + " abgezogen.");

        if (amount < 0) {
            bankrupt(player);
        }

        logger.log(Level.INFO, "Der Kontostand von " + player.getName() + " beträgt nun: " + player.getMoney() + CURRENCY_TYPE + ".");

    }

    /**
     * Einem Spieler wird der uebergebene Betrag auf dem Konto gutgeschrieben.
     *
     * @param player Spieler dem der Betrag gutgeschrieben wird
     * @param amount Betrag der dem Spieler gutgeschrieben wird
     */
    public void giveMoney(Player player, int amount) {

        player.setMoney(player.getMoney() + amount);
        logger.log(Level.INFO, player.getName() + " erhält " + amount + CURRENCY_TYPE + ".");
        logger.log(Level.INFO, "Der Kontostand von " + player.getName() + " beträgt nun: " + player.getMoney() + CURRENCY_TYPE + ".");
    }

    /**
     * Macht einen Spieler zum Beobachter und entfernt all seinen Besitz!
     *
     * @param player Spieler der bankrott gegangen ist
     */
    public void bankrupt(Player player) {
        logger.log(Level.INFO, player.getName() + " ist Bankrott und ab jetzt nur noch Zuschauer. All sein Besitz geht zurück an die Bank.");

        // Spieler als Zuschauer festlegen
        player.setSpectator(true);

        // Temporär das Feldarray zum Durchgehen zwischenspeichern
        Field[] fields = board.getFields();

        // geht ein Spieler bankrott wird geprueft, ob er der vorletzte war
        if (countActivePlayers() <= 1) {
            gameOver = true;
        }

        // Durchgehen des Array fields, ggf. Eigentum löschen
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            // Löschen der Hypothek und des Eigentums
            if (field instanceof Property) {
                if (((Property) field).getOwner() == player) {
                    ((Property) field).setOwner(null);
                    ((Property) field).setMortgageTaken(false);
                }

                // Löschen der Anzahl an Häusern
                if (field instanceof StreetField) {
                    ((StreetField) fields[i]).setHouseCount(0);

                }
            }
        }
        // TODO @cards - Gefängnisfreikarten müssen zurück in den Stapel
    }

    /**
     *
     * @return Anzahl der Spieler die nicht pleite sind
     */
    public int countActivePlayers() {
        return (int) Arrays.stream(players)
                .filter(p -> !(p.isSpectator()))
                .count();
    }

//-----------------------------------------------------------------------------
//------------ FELDMETHODEN ---------------------------------------------------
//-----------------------------------------------------------------------------
    /**
     *
     * @param player Spieler der die Strasse kauft
     * @param property Strasse die gekauft werden soll
     * @param price Preis der Strasse
     * @return ob die Strasse gekauft wurde
     */
    public boolean buyStreet(Player player, Property property, int price) {
        if (checkLiquidity(player, price)) {
            logger.log(Level.INFO, player.getName() + " kauft das Grundstück für " + price + CURRENCY_TYPE);
            property.setOwner(player);
            takeMoney(player, price);
            return true;
        } else {
            logger.log(Level.INFO, player.getName() + " hat nicht genug Geld.");
            return false;
        }
    }

    /**
     * Kauf von Haus/Hotel - wenn der aktive Spieler genügend Geld
     *
     * @param field Feld worauf ein Haus/Hotel gekauft/gebaut wird
     * @param player Spieler dem die Strasse gehoert
     */
    public void buyBuilding(Player player, StreetField field) {
        //@Eli, added Hausbau hinzugefuegt. TODO Spectator unmöglich

        if (!(player.isSpectator()) && checkLiquidity(player, field.getHousePrice())) {
            if (field.complete() && checkBalance(field, true)) {
                takeMoney(player, field.getHousePrice());
                field.setHouseCount(field.getHouseCount() + 1); //Haus bauen
                logger.log(Level.INFO, "Haus wurde gekauft!");
            } else {
                logger.log(Level.INFO, "Straßenzug nicht komplett, oder unausgeglichen!");
            }
        }
    }

    /**
     * Verkauf von Haus/Hotel
     *
     * @param field Feld wovon ein haus/Hotel verkauft/abbebaut wird
     * @param player Spieler dem die Strasse gehoert
     */
    public void sellBuilding(Player player, StreetField field) {
        if (!(player.isSpectator()) && checkBalance(field, false)) {
            giveMoney(player, field.getHousePrice()); //@rules MAXI du moegest bitte pruefen!
            field.setHouseCount(field.getHouseCount() - 1); // Haus abbauen
            logger.log(Level.INFO, "Haus wurde verkauft!");
        } else {
            logger.log(Level.INFO, "Straßenzug würde unausgeglichen sein");
        }
    }

    /**
     * @return true wenn eine Strasse gleiches Gewicht von Haeuser hat und false wenn nicht
     * @param field die auf Ausgeglichenheit im Strassenzug zu pruefende Strasse
     * @param buyIntend gibt an, ob der Spieler ein Haus <b>kaufen</b> möchte
     */
    public boolean checkBalance(StreetField field, boolean buyIntend) {
        for (Property nei : field.getNeighbours()) {  // Liste der Nachbarn durchgehen

            int housesHere = field.getHouseCount();      // Haueser auf der aktuellen Strasse
            int housesThere = ((StreetField) nei).getHouseCount();       // Haeuser auf der Nachbarstrasse
            if (((housesHere - housesThere) > 0) && buyIntend) {        // Wenn die Nachbarn weniger Haueser haben
                return false;
            } else if (((housesHere - housesThere) < 0) && !buyIntend) {// Wenn die Nachbarn mehr Haeuser haben
                return false;
            }
        }
        return true;
    }

    /**
     * Hypothek aufnehmen
     *
     * @param field Grundstueck, dessen Hypothek aufgenommen wird
     * @param player Spieler, dem das Grundstueck gehoert
     */
    public void takeMortgage(Player player, Property field) { //TODO Abfrage ob noch Haeuser drauf sind
        giveMoney(player, field.getMortgageValue());
        field.setMortgageTaken(true);
        logger.log(Level.INFO, "Hypothek wurde aufgenommen!");
    }

    /**
     * Hypothek zurueck zahlen
     *
     * @param field Grundstueck, desseh Hypothek abgezahlt wird
     * @param player Spieler, dem das Grundstueck gehoert
     */
    public void payMortgage(Player player, Property field) {
        int mortgageBack = field.getMortgageBack();
        if (checkLiquidity(player, mortgageBack)) {
            takeMoney(player, field.getMortgageBack());
            field.setMortgageTaken(false);
            logger.log(Level.INFO, "Hypothek wurde zurueckgezahlt!");
        } else {
            logger.log(Level.INFO, "Hypothek kann nicht zurückgezahlt werden! (Nicht genug Geld)");
        }
    }

//-----------------------------------------------------------------------------
//------------ Karten Methoden ------------------------------------------------
//-----------------------------------------------------------------------------
    /**
     * Alle Gebaeude eines Spielers werden gezaehlt
     *
     * Die Preise fuer Renovierung werden von dem entsprechenden Karte bekannt und dies wird mit der Anzahl von Haeuser/Hotels
     * multipliziert und am Ende addiert = Summe
     *
     * @param housePrice Hauspreis
     * @param hotelPrice Hotelpreis
     */
    public void sumRenovation(Player player, int housePrice, int hotelPrice) {
        //TODO spaeter, wenn Kartenstapel gedruckt wurde

        int renovationHotel = 0;
        int renovationHouse = 0;
        for (Field field : board.getFields()) {
            if (field instanceof StreetField) {
                int houses = ((StreetField) field).getHouseCount();
                if (houses < 5) {
                    renovationHouse += (housePrice * houses);

                } else {
                    renovationHotel += hotelPrice;

                }
            }
        }
        int sum = renovationHouse + renovationHotel;
        if (checkLiquidity(player, sum)) {
            takeMoney(player, sum);
        } else {
            bankrupt(player);
        }
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
            logger.log(Level.INFO, "Eingabe: ");
            try {
                output = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException ex) {
                logger.log(Level.WARNING, "FEHLER: falsche Eingabe!");
            }

            if (output < 1 || output > max) {
                logger.log(Level.INFO, "Deine Eingabe liegt nicht im Wertebereich! Bitte erneut versuchen:");
            }
        } while (output < 1 || output > max);

        return output;
    }

    /**
     * Methode zum Auswaehen einer Strasse die Bearbeitet werden soll in der actionPhase()
     *
     * @param player Spieler der eine Eingabe machen soll
     * @return ein int Wert zu auswaehen einer Strasse
     */
    public int askForField(Player player) {
        String mesg = player.getName() + "! Wähle ein Feld:\n";
        Field[] fields = board.getFields();
        for (int i = 0; i < fields.length; i++) {
            mesg += String.format("[%d] - %s%n", i + 1, fields[i].getName());
        }
        logger.log(Level.INFO, mesg);
        return getUserInput(39);
    }

}
