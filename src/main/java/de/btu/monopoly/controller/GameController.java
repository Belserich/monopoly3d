package de.btu.monopoly.controller;

import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.data.parser.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Christian Prinz
 */
public class GameController {

    /**
     * Waehrungstyp
     */
    private static final String CURRENCY_TYPE = "€";

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(GameController.class.getPackage().getName());

    /**
     * das Spielbrett
     */
    private GameBoard board;

    /**
     * die Mitspieler
     */
    private final Player[] players;

    /**
     * momentaner Spieler
     */
    private Player currPlayer;

    /**
     * Gibt an, ob das Spiel beendet ist.
     */
    private boolean gameOver;

    /**
     * Anzahl Pasches
     */
    private int doubletCounter;

    /**
     * Gibt an, ob ein Pasch gewürfelt wurde
     */
    private boolean isDoublet;

    /**
     * letztes Wurfergebnis
     */
    private int diceResult;

    /**
     * Feld, auf dem sich der Spieler befindet
     */
    private Field currField;

    /**
     * Feldtyp
     */
    private int fieldSwitch;

    /**
     * Die zentrale Manager-Klasse für alles was das Spiel betrifft.
     *
     * @param playerCount Anzahl Spieler
     *
     */
    public GameController(int playerCount) {
        this.players = new Player[playerCount];
    }

    /**
     * Spielinitialisierung
     */
    public void init() {
        logger.setLevel(Level.ALL);
        logger.log(Level.INFO, "Spiel wird initialisiert");

        // TODO Maxi (Sprint 3) GameBoardParser überarbeiten
        // Spielbrett
        GameBoardParser parser = new GameBoardParser();
        try {
            this.board = parser.parseGameBoard("data/field_data.config");
        } catch (IOException ex) {
            logger.warning("field_data.config konnte nicht gelesen werden");
            ex.printStackTrace();
        }

        assert board != null;

        // Spieler
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player("Mathias " + i, i, 1500); //@parser @rules
        }

        startGame();
    }

    /**
     * Spielstart
     */
    public void startGame() {
        logger.log(Level.INFO, "Spiel beginnt.");

        do {
            for (int i = 0; i < players.length; i++) {
                currPlayer = players[i];		            // aktiven Spieler setzen
                if (!(currPlayer.isSpectator())) {	        // Spieler ist kein Beobachter
                    turnPhase();
                }
            }
        } while (!gameOver);

        // suche letzten aktiven Spieler
        for (Player player : players) {
            if (!player.isSpectator()) {
                logger.log(Level.WARNING, "Spieler " + player.getName() + " hat das Spiel gewonnen!");
            }
        }
    }

    /**
     * Rundenphase
     */
    private void turnPhase() {
        logger.log(Level.INFO, currPlayer.getName() + " ist dran!");

        doubletCounter = 0; 	        // Paschzaehler zuruecksetzen
        if (currPlayer.isInJail()) {
            jailPhase();
        }
        do {		                    // bei Pasch wiederholen
            rollPhase();
            fieldPhase();
            if (currPlayer.isInJail() && (currPlayer.getDaysInJail() == 0)) {
                break;                  // actionPhase() entfaellt, wenn der Spieler grade ins Gefaengnis kam
            }
            actionPhase();
        } while (isDoublet);
    }

//-----------------------------------------------------------------------------
//----------- EINZELNE PHASEN -------------------------------------------------
//-----------------------------------------------------------------------------
    /**
     * Gefängnisphase
     */
    private void jailPhase() {
        boolean repeat;
        do {
            repeat = false;
            logger.log(Level.INFO, "Du bist im Gefängnis! Du kannst: \n1. 3 mal Würfeln, um mit einem Pasch freizukommen "
                    + "\n2. Bezahlen (50€) \n3. Gefängnis-Frei-Karte benutzen");

            switch (getUserInput(3)) { // @GUI
                // OPTION 1: wuerfeln (bis zu drei mal)
                case 1:
                    roll();                 // wuerfeln
                    if (!isDoublet) {       // kein Pasch -> im Gefaengnis bleiben
                        logger.log(Level.INFO, "Du hast keinen Pasch und bleibst im Gefängnis.");
                        currPlayer.addDayInJail();
                    } else {                        // sonst frei
                        logger.log(Level.INFO, "Du hast einen Pasch und bist frei.");
                        currPlayer.setInJail(false);
                        currPlayer.setDaysInJail(0);
                    }
                    // Wenn drei mal kein Pasch dann bezahlen
                    if (currPlayer.getDaysInJail() == 3) {
                        if (checkLiquidity(currPlayer, 50)) {
                            logger.log(Level.INFO, "Du hast schon 3 mal gewürfelt. nun musst du 50€ zahlen!");
                            takeMoney(currPlayer, 50);
                            currPlayer.setInJail(false);
                            currPlayer.setDaysInJail(0);
                        } else {        //wenn pleite game over
                            logger.log(Level.INFO, "Du hast schon 3 mal gewürfelt und kannst nicht zahlen.");
                            bankrupt(currPlayer);
                        }
                    }
                    break;

                //OPTION 2: Bezahlen
                case 2:
                    if (checkLiquidity(currPlayer, 50)) {
                        logger.log(Level.INFO, "Du hast 50€ gezahlt und bist frei!");
                        takeMoney(currPlayer, 50);
                        currPlayer.setInJail(false);
                        currPlayer.setDaysInJail(0);
                    } else { // muss in der GUI deaktiviert sein!!!
                        logger.log(Level.INFO, "Du hast kein Geld um dich freizukaufen.");
                        repeat = true;
                    }
                    break;

                //OPTION 3: Freikarte ausspielen
                case 3:
                    if (currPlayer.getJailCardAmount() > 0) {
                        logger.log(Level.INFO, "Du hast eine Gefängnis-Frei-Karte benutzt.");
                        currPlayer.removeJailCard(); // TODO jail-card
                        currPlayer.setInJail(false);
                        currPlayer.setDaysInJail(0);
                    } else { // muss in der GUI deaktiviert sein!!!
                        logger.log(Level.INFO, "Du hast keine Gefängnis-Frei-Karten mehr.");
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
     */
    private void rollPhase() {
        logger.log(Level.INFO, "Du bist dran mit würfeln.");
        if (!(currPlayer.isInJail())) { //Gefaengnis hat eigenes Wuerfeln
            roll();
            if (doubletCounter == 3) {
                logger.log(Level.INFO, "Du hast deinen 3. Pasch und gehst nicht über LOS, direkt ins Gefängnis!");
                moveToJail();
            }
        }
        if (!(currPlayer.isInJail())) { //kann sich nach wuerfeln aendern
            movePlayer();
        }
    }

    /**
     * die Feldphase (Feldaktionen)
     */
    private void fieldPhase() {
        locate(currPlayer);

        switch (fieldSwitch) { //@optimize
            case 1: // Strasse / Bahnhof / Werk
                logger.log(Level.INFO, "Du befindest dich auf " + currField.getName());
                if (currField instanceof Property) {
                    Property actualProperty = (Property) currField;
                    // wenn das Feld in eigenem Besitz ist
                    if (actualProperty.getOwner() == currPlayer) {
                        logger.log(Level.INFO, "Du stehst auf deinem eigenen Grundstück.");
                        break;
                    } else if (actualProperty.getOwner() == null) { //wenn frei
                        logger.log(Level.INFO, "Du stehst auf einem freien Grundstück. Du kannst es: \n1. Kaufen \n2. nicht Kaufen");
                        switch (getUserInput(2)) { //@GUI
                            case 1: //Kaufen
                                if (checkLiquidity(currPlayer, actualProperty.getPrice())) {
                                    logger.log(Level.INFO, "Du kaufst das Grundstück für " + actualProperty.getPrice() + "€!");
                                    actualProperty.setOwner(currPlayer);
                                    takeMoney(currPlayer, actualProperty.getPrice());
                                    break;
                                } else {
                                    logger.log(Level.INFO, "Du hast nicht genug Geld.");
                                }
                            case 2: //Auktion - NOCH DEAKTIVIERT @multiplayer
                                logger.log(Level.INFO, "Auktionsphase noch nicht implementert.");
                                betPhase();
                                break;
                            default:
                                logger.log(Level.WARNING, "FEHLER: SteetBuySwitch überlaufen.");
                                break;
                        }
                    } else { // wenn nicht in eigenem Besitz
                        logger.log(Level.INFO, "Dieses Grundstück gehört jemand anderes.");
                        int rent = actualProperty.getRent();
                        // wenn es sich um ein Werk handelt:
                        if (actualProperty instanceof SupplyField) {
                            rent = rent * diceResult;
                        }

                        if (checkLiquidity(currPlayer, rent)) {
                            logger.log(Level.INFO, "Du zahlst Miete:");
                            takeMoney(currPlayer, rent);
                            giveMoney(actualProperty.getOwner(), rent);
                        } else {
                            logger.log(Level.INFO, "Du kannst die Miete nicht zahlen!");
                            bankrupt(currPlayer);
                        }
                    }
                } else { // kann nicht auftreten
                    logger.log(Level.WARNING, "FEHLER: Field falsch ermittelt! (Property)");
                }
                break;

            case 2: // LOS
                // LOS abfrage erfolgt in der Methode movePlayer()
                // ansonsten passiert hier nicht @rules
                break;

            case 3: // Frei-Parken
                // hier passier normalerweise nichts @rules
                break;

            case 4: // Gefaengnis
                // hier passier in jedem Fall nichts
                break;

            case 5: // Steuerfeld
                logger.log(Level.INFO, "Du musst Steuern zahlen.");
                if (currField instanceof TaxField) {
                    TaxField taxField = (TaxField) currField;
                    if (checkLiquidity(currPlayer, taxField.getTax())) {
                        logger.log(Level.INFO, "Dir werden " + taxField.getTax() + "€ abgezogen!");
                        takeMoney(currPlayer, taxField.getTax());
                    } else {
                        logger.log(Level.INFO, "Die kannst du aber nicht zahlen!");
                        bankrupt(currPlayer);
                    }
                    // spaeter kommt hier evtl. der Steuertopf zum Zuge @rules
                } else { // kann nicht auftreten
                    logger.log(Level.WARNING, "FEHLER: FieldSwitch überlaufen! (Steuerfeld)");
                }
                break;

            case 6: // Kartenfeld
                if (currField instanceof CardField) {
                    logger.log(Level.INFO, "Du bist auf einem Kartenfeld gelandet. Die Karten liegen aber noch in der Druckerei. "
                            + "Nichts passiert.");
                    // CardField cardField = (CardField) currField;
                    /*
                     * TODO @cards - Karten auslesen und co, wenn man hier auf ein anderes Feld gesetzt wird, muss in der
                     * Kartenmethode die Feldphase nochmal aufgerufen werden, oder ein in der Karte vorgegebener alternativer
                     * Feldphasenabruf stattfinden. FeldPhase muss so nicht "geschleift" werden.
                     */
                } else { // kann nicht auftreten
                    logger.log(Level.WARNING, "FEHLER: FieldSwitch überlaufen (Cardfield)");
                }

                break;

            case 7: // GehInsGefaengnis-Feld
                logger.log(Level.INFO, "Du wurdest bei einer Straftat erwischt.");
                moveToJail();
                break;

            default: // kann nicht auftreten!
                logger.log(Level.WARNING, "FEHLER: FieldSwitch überlaufen.");
                break;
        }

    }

    /**
     * @author Eli
     *
     * Die Aktionsphase (Bebauung, Hypothek, Handeln)
     */
    private void actionPhase() { //@optimize switches vereinfachen
        int choice;
        do {
            logger.log(Level.INFO, "Waehle eine Aktion:\n[1] - Nichts\n[2] - Haus kaufen\n[3] - Haus verkaufen\n[4] - "
                    + "Hypothek aufnehmen\n[5] - Hypothek abbezahlen");

            choice = getUserInput(5);
            if (choice != 1) {
                currField = board.getFields()[askForField() - 1]; // Wahl der Strasse

                if (currField instanceof Property) {
                    Property property = (Property) currField;
                    switch (choice) {
                        case 2: /* Haus kaufen */ {
                            if (!(currField instanceof StreetField)) {
                                logger.log(Level.INFO, "Gewähltes Feld ist keine Straße!");
                                break;
                            }
                            StreetField streetField = (StreetField) property;
                            if ((streetField.getOwner() == currPlayer) && (streetField.getHouseCount() < 5)) {
                                // wenn im Besitz und nicht vollgebaut
                                buyBuilding(streetField);
                            } else {
                                logger.log(Level.INFO, "Diese Straße gehört dir nicht, oder ist voll bebaut.");
                            }
                            break;
                        }
                        case 3: /* Haus verkaufen */ {
                            if (!(currField instanceof StreetField)) {
                                logger.log(Level.INFO, "Gewähltes Feld ist keine Straße!");
                                break;
                            }
                            StreetField streetField = (StreetField) property;
                            if ((streetField.getOwner() == currPlayer) && (streetField.getHouseCount() > 0)) {
                                // wenn im Besitz und nicht 'hauslos'
                                sellBuilding(streetField);
                            } else {
                                logger.log(Level.INFO, "Diese Straße gehört dir nicht, oder hat keine Häuser zum verkaufen.");
                            }
                            break;
                        }
                        case 4: /* Hypothek aufnehmen */ {
                            // wenn im Besitz und noch keine Hypothek aufgenommen
                            if (property.getOwner() == currPlayer && (!(property.isMortgageTaken()))) {
                                takeMortgage(property);
                                logger.log(Level.INFO, "Hypothek aufgenommen.");
                            } else {
                                logger.log(Level.INFO, "Diese Straße gehört dir nicht, oder hat schon eine Hypothek.");
                            }
                            break;
                        }
                        case 5: /* Hypothek abbezahlen */ {
                            // wenn im Besitz und Hypothek aufgenommen
                            if (property.getOwner() == currPlayer && (property.isMortgageTaken())) {
                                payMortgage(property);
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

    private int askForField() {
        String mesg = "Wähle ein Feld:\n";
        Field[] fields = board.getFields();
        for (int i = 0; i < fields.length; i++) {
            mesg += String.format("[%d] - %s\n", i + 1, fields[i].getName());
        }
        logger.log(Level.INFO, mesg);
        return getUserInput(39);
    }

    /**
     * die Versteigerungsphase
     */
    private void betPhase() {
        // @multiplayer
    }

//-----------------------------------------------------------------------------
//----------- EINZELNE METHODEN------------------------------------------------
//-----------------------------------------------------------------------------
    /**
     * das Wuerfeln. Summe in diceResult speichern. Pasch nicht vergessen
     */
    private void roll() {

        // 2 interne Wuerfel
        int dice1;
        int dice2;

        // Erzeugen der Zufallszahl
        dice1 = ((int) (Math.random() * 6)) + 1;
        dice2 = ((int) (Math.random() * 6)) + 1;

        // Bei Pasch, erhöhe doubletCounter und isDoublet
        if (dice1 == dice2) {
            doubletCounter++;
            isDoublet = true;
        } else {
            isDoublet = false;
        }

        diceResult = dice1 + dice2;
        logger.log(Level.INFO, "Dein Wuerfelergebnis: " + dice1 + " + " + dice2 + " = " + diceResult);
    }

    /**
     * bewegt den Spieler (currPlayer) zu einer neuen Position.
     *
     */
    private void movePlayer() {
        /*
         * falls die Zahl 39 ueberschritten wird und damit das Spielfeld dann geht der Spieler ueber Los (bekommt 200) und wird
         * auf die Position aktuelles Feld + Wuerfelanzahl minus der Gesamtfeldanzahl falls nicht dann geht der Spieler nicht
         * ueber Los und wird einfach auf das aktuelle Feld plus der Anzahl der Wuerfelanzahl gesetzt
         */
        if ((currPlayer.getPosition() + diceResult) > 39) {

            logger.log(Level.INFO, currPlayer.getName() + " ist ueber Los gegangen und erhaelt " + ((GoField) board.getFields()[0]).getAmount()
                    + " " + CURRENCY_TYPE + ".");

            giveMoney(currPlayer, ((GoField) board.getFields()[0]).getAmount());

            currPlayer.setPosition(((currPlayer.getPosition() + diceResult) - 39));
        } else {
            currPlayer.setPosition(currPlayer.getPosition() + diceResult);
        }
    }

    /**
     * analog zu movePlayer(), nur dass kein Geld beim ueber-LOS-gehen ueberwiesen wird!
     */
    private void moveToJail() {
        /*
         * Spieler wird auf Position 10 gesetzt setInJail wird true, damit der Spieler nicht "nur zu Besuch" ist Die Tage im
         * Gefängnis werden auf 0 gesetzt
         */
        currPlayer.setPosition(10);
        currPlayer.setInJail(true);
        currPlayer.setDaysInJail(0);
        logger.log(Level.INFO, currPlayer.getName() + " wurde in das Gefaengnis gesetzt.");
    }

    /**
     * ueberpruft ob der uebergebene Spieler mindestens soviel Geld besitzt, wie die Methode uebergeben bekommt.
     *
     * @param player Spieler der auf Liquiditaet geprueft wird
     * @param amount Geld was der Spieler besitzen muss
     */
    private boolean checkLiquidity(Player player, int amount) {

        // Die Methode benötigt die uebergabe des Spielers, da bei einem Ereignisfeld, eine Karte vorkommt, bei der der
        // actualPlayer Geld von den Mitspielern einsammeln kann
        logger.log(Level.INFO, "Es wird geprüft, ob du genug Geld hast für die folgende Transaktion.");
        return ((player.getMoney() - amount) > 0);

    }

    /**
     * Einem Spieler wird der uebergebene Betrag von seinem Konto abgezogen.
     *
     * @param player Spieler dem der Betrag abgezogen wird
     * @param amount Betrag der dem Spieler abgezogen wird
     */
    private void takeMoney(Player player, int amount) {

        player.setMoney(player.getMoney() - amount);
        logger.log(Level.INFO, "Dir werden " + amount + "€ abgezogen.");

        if (amount < 0) {
            bankrupt(player);
        }

        logger.log(Level.INFO, "Dein Kontostand beträgt nun: " + currPlayer.getMoney() + "€.");

    }

    /**
     * Einem Spieler wird der uebergebene Betrag auf dem Konto gutgeschrieben.
     *
     * @param player Spieler dem der Betrag gutgeschrieben wird
     * @param amount Betrag der dem Spieler gutgeschrieben wird
     */
    private void giveMoney(Player player, int amount) {

        player.setMoney(player.getMoney() + amount);
        logger.log(Level.INFO, "Du erhälst " + amount + "€.");
        logger.log(Level.INFO, "Dein Kontostand beträgt nun: " + currPlayer.getMoney() + "€.");
    }

    /**
     * Macht einen Spieler zum Beobachter und entfernt all seinen Besitz!
     *
     *
     * @param player Spieler der bankrott gegangen ist
     */
    private void bankrupt(Player player) { //TODO !hier sollt ihr nicht den currPlayer sondern den parameter nehmen!!!
        logger.log(Level.INFO, "Du bist Bankrott und ab jetzt nur noch Zuschauer.");

        // Spieler als Zuschauer festlegen
        currPlayer.setSpectator(true);

        // Temporär das Feldarray zum Durchgehen zwischenspeichern
        Field[] fields = board.getFields();

        if (countActivePlayers() <= 1) {
            gameOver = true;
        }

        // Durchgehen des Array fields, ggf. Eigentum löschen
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            if (field instanceof Property) {

                // Löschen der Hypothek und des Eigentums
                if (((Property) field).getOwner() == currPlayer) {

                    ((Property) field).setOwner(null);
                    ((Property) field).setMortgageTaken(false);

                    // Löschen der Anzahl an Häusern
                    if (field instanceof StreetField) {

                        ((StreetField) fields[i]).setHouseCount(0);

                    }
                }
            }
        }
        // TODO @cards - Gefängnisfreikarten müssen zurück in den Stapel
    }

    private int countActivePlayers() {
        return (int) Arrays.stream(players)
                .filter(p -> !(p.isSpectator()))
                .count();
    }

    /**
     * ermittelt anhand der Position des Spielers das Feld mit der ID auf dem GameBoard, welches der Variablen currField
     * uebergeben wird, zudem wird fieldSwitch festgelegt, zur GameController Steuerung
     *
     * @param player Spieler dessen Position ermittelt werden soll
     */
    private void locate(Player player) {

        // locate: TODO fieldSwitch als return!
        // Da es keine Implementierung des Gefängnisfeldes
        // und des Freiparkenfeldes gibt. Wurden die zwei
        // if Abfragen zunächst über die Spielerposition realisiert
        // Lösung ist korrekt so. GoToJail wurde hinzugefügt
        currField = board.getFields()[player.getPosition()];

        if (currField instanceof Property) {
            fieldSwitch = 1;
        } else if (currField instanceof GoField) {
            fieldSwitch = 2;
        } else if (player.getPosition() == 20) {
            fieldSwitch = 3;
        } else if (player.getPosition() == 10) {
            fieldSwitch = 4;
        } else if (currField instanceof TaxField) {
            fieldSwitch = 5;
        } else if (currField instanceof CardField) {
            fieldSwitch = 6;
        } else if (player.getPosition() == 30) {
            fieldSwitch = 7;
        }

        // fieldSwitch Belegung
        // 1 - Straße / Bahnhof / Werk (Property)
        // 2 - LOS
        // 3 - Frei-Parken
        // 4 - Gefaengnis
        // 5 - Steuer
        // 6 - Ereignis- / Gemeinschaftskartenfeld
        // 7 - Geh ins Gefaengnis
    }

    /**
     * Kauf von Haus/Hotel - wenn der aktive Spieler genügend Geld
     *
     * @param field - das Feld worauf ein Haus/Hotel gekauft/gebaut wird
     */
    private void buyBuilding(StreetField field) {
        //@Eli, added Hausbau hinzugefuegt. TODO Spectator unmöglich

        if (!(currPlayer.isSpectator()) && checkLiquidity(currPlayer, field.getHousePrice())) {
            if (field.complete() && checkBalance(field, true)) {
                takeMoney(currPlayer, field.getHousePrice()); // enfernt: * field.getHouseCount());
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
     * @param field - das Feld wovon ein haus/Hotel verkauft/abbebaut wird
     */
    private void sellBuilding(StreetField field) {
        if (!(currPlayer.isSpectator()) && checkBalance(field, false)) {
            giveMoney(currPlayer, field.getHousePrice()); //@rules MAXI du moegest bitte pruefen!
            field.setHouseCount(field.getHouseCount() - 1); // Haus abbauen
            logger.log(Level.INFO, "Haus wurde verkauft!");
        } else {
            logger.log(Level.INFO, "Straßenzug würde unausgeglichen sein");
        }
    }

    /**
     * @return true wenn eine Strasse gleiches Gewicht von Haeuser hat und false wenn nicht
     * @param field die auf Ausgeglichenheit im Strassenzug zu pruefende Strasse
     * @param buyIntend gibt an, ob der Spieler ein Haus kaufen möchte
     */
    private boolean checkBalance(StreetField field, boolean buyIntend) {
        //@Eli, replaced. neighbours ist eine Liste

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
     * @param field - das Feld, dessen Hypothek aufgenommen wurde
     */
    private void takeMortgage(Property field) {
        giveMoney(currPlayer, field.getMortgageValue());
        field.setMortgageTaken(true);
        logger.log(Level.INFO, "Hypothek wurde aufgenommen!");
    }

    /**
     * Hypothek zurueck zahlen
     *
     * @param field - das Feld ist wieder aktiv und hat Rent
     */
    private void payMortgage(Property field) {
        int mortgageBack = field.getMortgageBack();
        if (checkLiquidity(currPlayer, mortgageBack)) {
            takeMoney(currPlayer, field.getMortgageBack());
            field.setMortgageTaken(false);
            logger.log(Level.INFO, "Hypothek wurde zurueckgezahlt!");
        } else {
            logger.log(Level.INFO, "Hypothek kann nicht zurückgezahlt werden! (Nicht genug Geld)");
        }
    }

    //-------------------------------------------------------------------------
    //------------ Karten Methoden --------------------------------------------
    //-------------------------------------------------------------------------
    /**
     * Doppelte Miete -- Diese Methode wird verwendet, wenn in einer Karte gefordert ist die doppelte Miete zu zahlen.
     *
     * @param giver Spieler der zahlen muss
     * @param taker Spieler der die Miete bekommt
     */
    private void doubleRent(Player giver, Player taker) {
        //TODO Maxi
    }

    /**
     * Alle Gebaeuden eines Spielers werden gezaehlt
     *
     * Die Preise fuer Renovierung werden von dem entsprechenden Karte bekannt und dies wird mit der Anzahl von Haeuser/Hotels
     * multipliziert und am Ende addiert = Summe
     *
     * @param house_price
     * @param hotel_price
     */
    private void sumRenovation(int house_price, int hotel_price) {
        //TODO spaeter, wenn Kartenstapel gedruckt wurde
        StreetField field = (StreetField) currField;
        int renovation_hotel = 0;
        int renovation_house = 0;
        for (Property nei : field.getNeighbours()) {
            int houses = ((StreetField) nei).getHouseCount();
            if (houses < 5) {
                renovation_house += (house_price * houses);

            } else {
                renovation_hotel += hotel_price;

            }
        }
        int sum = renovation_house + renovation_hotel;
        if (checkLiquidity(currPlayer, sum)) {
            takeMoney(currPlayer, sum);
        } else {
            bankrupt(currPlayer);
        }
    }

    //-------------------------------------------------------------------------
    //------------ Console-Input Methoden -------------------------------------
    //-------------------------------------------------------------------------
    public int getUserInput(int max) {

        Scanner scanner = new Scanner(System.in);
        int output = -1;

        // Solange nicht der richtige Wertebereich eingegeben wird, wird die Eingabe wiederholt.
        do {

            System.err.print("Eingabe: ");
            try {
                output = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException ex) {
            }

            if (output < 1 || output > max) {
                System.err.println("Deine Eingabe liegt nicht im Wertebereich! Bitte erneut versuchen:");
            }
        } while (output < 1 || output > max);

        return output;
    }
}
