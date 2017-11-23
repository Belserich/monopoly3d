package de.btu.monopoly.controller;

import com.sun.istack.internal.logging.Logger;
import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.data.parser.*;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * @author Christian Prinz
 */
public class GameController {

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
     * Waehrungstyp
     */
    public static final String CURRENCY_TYPE = " Euro"; //TODO € durch CURRENCY_TYPE ersetzen

    /**
     * Logger für den GameController
     */
    private static final Logger logger = Logger.getLogger("de.btu.monopoly.controller", GameController.class);

    /**
     * Die zentrale Manager-Klasse für alles was ein Spiel betrifft.
     *
     * @param playerCount Anzahl Spieler
     *
     */
    public GameController(int playerCount) {
        this.players = new Player[playerCount];
        //TODO @cards Kartenstapel initialisieren
        init();
    }

    /**
     * Spielinitialisierung
     */
    public void init() {
        logger.log(Level.FINE, "Spiel wird initialisiert");

        // Board init
        GameBoardParser parser = new GameBoardParser();
        try {
            this.board = parser.readBoard("data/field_data.config");
        } catch (IOException ex) {
            System.err.println("field_data.config konnte nicht gelesen werden"); //@output
        }

        // Player init
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player("Mathias " + i, i, 1500); //@parser @rules
        }

        startGame();
    }

    /**
     * Spielstart
     */
    public void startGame() {
        logger.log(Level.FINER, "Spiel beginnt.");
        do {
            for (int i = 0; i < players.length; i++) {      // für alle Spieler
                currPlayer = players[i];		    // aktiven Spieler setzen
                if (!(currPlayer.isSpectator())) {	    // Beobachter ist nicht am Zug
                    turnPhase();
                }
            }
        } while (!gameOver);
    }

    /**
     * Rundenphase
     */
    private void turnPhase() {
        logger.log(Level.FINER, currPlayer.getName() + " ist dran!");
        doubletCounter = 0; 	        // PaschZaehler zuruecksetzen
        if (currPlayer.isInJail()) {	// Spieler im Gefaengnis
            jailPhase();
        }
        do {		// bei Pasch wiederholen
            /*
             * @output: if(isDoublet){ "Du bist nochmal dran."}
             */
            rollPhase();
            fieldPhase();
            if (currPlayer.isInJail() && (currPlayer.getDaysInJail() < 1)) {
                // actionPhase() entfaellt, wenn der Spieler grade ins Gefaengnis kam
            } else {
                actionPhase();
            }
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
            logger.log(Level.FINER, "Du bist im Gefängnis! Du kannst: \n1. 3 mal Würfeln, um mit einem Pasch freizukommen "
                    + "\n2. Bezahlen (50€) \n3. Gefängnis-Frei-Karte benutzen");
            switch (getUserInput(3)) { // @GUI
                //OPTION 1: 3mal wuerfeln
                case 1:
                    roll();                 // wuerfeln
                    if (!isDoublet) {       // kein Pasch > im Gefaengnis bleiben
                        logger.log(Level.FINER, "Du hast keinen Pasch und bleibst im Gefängnis.");
                        currPlayer.addDayInJail();
                    } else {                        // sonst frei
                        logger.log(Level.FINER, "Du hast einen Pasch und bist frei.");
                        currPlayer.setInJail(false);
                        currPlayer.setDaysInJail(0);
                    }
                    // Wenn 3 mal kein Pasch dann bezahlen
                    if (currPlayer.getDaysInJail() == 3) {
                        if (checkLiquidity(currPlayer, 50)) {
                            logger.log(Level.FINER, "Du hast schon 3 mal gewürfelt. nun musst du 50€ zahlen!");
                            takeMoney(currPlayer, 50);
                            currPlayer.setInJail(false);
                            currPlayer.setDaysInJail(0);
                        } else {        //wenn pleite game over
                            logger.log(Level.FINER, "Du hast schon 3 mal gewürfelt und kannst nicht zahlen.");
                            bankrupt(currPlayer);
                        }
                    }
                    break;

                //OPTION 2: Bezahlen
                case 2:
                    if (checkLiquidity(currPlayer, 50)) {
                        logger.log(Level.FINER, "Du hast 50€ gezahlt und bist frei!");
                        takeMoney(currPlayer, 50);
                        currPlayer.setInJail(false);
                        currPlayer.setDaysInJail(0);
                    } else { // muss in der GUI deaktiviert sein!!!
                        logger.log(Level.FINER, "Du hast kein Geld um dich freizukaufen.");
                        repeat = true;
                    }
                    break;

                //OPTION 3: Freikarte ausspielen
                case 3:
                    if (currPlayer.getJailCardAmount() > 0) {
                        logger.log(Level.FINER, "Du hast eine Gefängnis-Frei-Karte benutzt.");
                        currPlayer.removeJailCard();
                        enqueueJailCard();
                        currPlayer.setInJail(false);
                        currPlayer.setDaysInJail(0);
                    } else { // muss in der GUI deaktiviert sein!!!
                        logger.log(Level.FINER, "Du hast keine Gefängnis-Frei-Karten mehr.");
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
        logger.log(Level.FINER, "Du bist dran mit würfeln.");
        if (!(currPlayer.isInJail())) { //Gefaengnis hat eigenes Wuerfeln
            roll();
            if (doubletCounter == 3) {
                logger.log(Level.FINER, "Du hast deinen 3. Pasch und gehst nicht über LOS, direkt ins Gefängnis!");
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
                logger.log(Level.FINER, "Du befindest dich auf " + currField.getName());
                if (currField instanceof Property) {
                    Property actualProperty = (Property) currField;
                    // wenn das Feld in eigenem Besitz ist
                    if (actualProperty.getOwner() == currPlayer) {
                        logger.log(Level.FINER, "Du stehst auf deinem eigenen Grundstück.");
                        break;
                    } else if (actualProperty.getOwner() == null) { //wenn frei
                        logger.log(Level.FINER, "Du stehst auf einem freien Grundstück. Du kannst es: \n1. Kaufen \n2. nicht Kaufen");
                        switch (getUserInput(2)) { //@GUI
                            case 1: //Kaufen
                                if (checkLiquidity(currPlayer, actualProperty.getPrice())) {
                                    logger.log(Level.FINER, "Du kaufst das Grundstück für " + actualProperty.getPrice() + "€!");
                                    actualProperty.setOwner(currPlayer);
                                    takeMoney(currPlayer, actualProperty.getPrice());
                                    break;
                                } else {
                                    logger.log(Level.FINER, "Du hast nicht genug Geld.");
                                }
                            case 2: //Auktion - NOCH DEAKTIVIERT @multiplayer
                                logger.log(Level.FINER, "Auktionsphase noch nicht implementert.");
                                betPhase();
                                break;
                            default:
                                logger.log(Level.WARNING, "FEHLER: SteetBuySwitch überlaufen.");
                                break;
                        }
                    } else { // wenn nicht in eigenem Besitz
                        logger.log(Level.FINER, "Dieses Grundstück gehört jemand anderes.");
                        int rent = actualProperty.getRent();
                        // wenn es sich um ein Werk handelt:
                        if (actualProperty instanceof SupplyField) {
                            rent = rent * diceResult;
                        }

                        if (checkLiquidity(currPlayer, rent)) {
                            logger.log(Level.FINER, "Du zahlst Miete:");
                            takeMoney(currPlayer, rent);
                            giveMoney(actualProperty.getOwner(), rent);
                        } else {
                            logger.log(Level.FINER, "Du kannst die Miete nicht zahlen!");
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
                logger.log(Level.FINER, "Du musst Steuern zahlen.");
                if (currField instanceof TaxField) {
                    TaxField taxField = (TaxField) currField;
                    if (checkLiquidity(currPlayer, taxField.getTax())) {
                        logger.log(Level.FINER, "Dir werden " + taxField.getTax() + "€ abgezogen!");
                        takeMoney(currPlayer, taxField.getTax());
                    } else {
                        logger.log(Level.FINER, "Die kannst du aber nicht zahlen!");
                        bankrupt(currPlayer);
                    }
                    // spaeter kommt hier evtl. der Steuertopf zum Zuge @rules
                } else { // kann nicht auftreten
                    logger.log(Level.WARNING, "FEHLER: FieldSwitch überlaufen! (Steuerfeld)");
                }
                break;

            case 6: // Kartenfeld
                if (currField instanceof CardField) {
                    logger.log(Level.FINER, "Du bist auf einem Kartenfeld gelandet. Die Karten liegen aber noch in der Druckerei. "
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
                logger.log(Level.FINER, "Du wurdest bei einer Straftat erwischt.");
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
        // TODO Eli, hier noch Überlegungen zum input (2 Stellen), (Auflistung aller Straßen im Besitz?).
        // alles noch iwie schleifen!

        logger.log(Level.FINER, "Wähle ein Feld. LOS ist Feld 1"); //TODO MAXI Felder zeigen
        this.currField = board.getFields()[getUserInput(40) - 1]; //Wahl der Strasse

        if (currField instanceof StreetField) { //wenn Feld eine Straße ist
            /*
             * @output: "Du hast eine Straße gewählt. Du kannst hier: \n1. ein Haus bauen. \n2. ein Haus abreißen. \n3. eine
             * Hypothek aufnehmen. \n4. die Hypothek abzahlen"
             */
            StreetField field = (StreetField) currField;
            switch (getUserInput(4)) {
                case 1: // Haus bauen
                    // wenn im Besitz und nicht vollgebaut
                    if ((field.getOwner().equals(currPlayer)) && (field.getHouseCount() < 5)) {
                        buyBuilding(field);
                        /*
                         * @output: "Haus gebaut.
                         */
                    } else {
                        /*
                         * @output: "Diese Straße gehört dir nicht, oder ist vollgebaut."
                         */
                    }
                    break;

                case 2: // Haus verkaufen
                    // wenn im Besitz und nicht Hauslos
                    if ((field.getOwner().equals(currPlayer)) && (field.getHouseCount() > 0)) {
                        sellBuilding(field);
                        /*
                         * @output: "Haus abgerissen"
                         */
                    } else {
                        /*
                         * @output: "Diese Straße gehört dir nicht, oder hat keine Häuser zum verkaufen."
                         */
                    }
                    break;

                case 3: // Hypothek aufnehmen TODO Maxi guck dir das mal an ob die cases nicht weg können
                    // wenn im Besitz und noch keine Hypothek aufgenommen
                    if (field.getOwner().equals(currPlayer) && (!(field.isMortgageTaken()))) {
                        takeMortgage(field);
                        /*
                         * @output: "Hypothek aufgenommen."
                         */
                    } else {
                        /*
                         * @output: "Diese Straße gehört dir nicht, oder hat schon eine Hypothek."
                         */
                    }
                    break;

                case 4: // Hypothek abbezahlen
                    // wenn im Besitz und Hypothek aufgenommen
                    if (field.getOwner().equals(currPlayer) && (field.isMortgageTaken())) {
                        payMortgage(field);
                        /*
                         * @output: "Hypothek abgezahlt."
                         */
                    } else {
                        /*
                         * @output: "Diese Straße gehört dir nicht, oder hat keine Hypothek zum abzahlen."
                         */
                    }
                    break;

                default:
                    // @output Warning StreetFieldSwitch überlaufen
                    break;
            }
        }

        if (currField instanceof Property) { //wenn Feld ein Bahnhof oder Werk ist
            /*
             * @output: "Du hast einen Bahhof, oder ein Werk gewählt. Du kannst hier: \n1.Hypothek aufnehmen \n2. Hypothek
             * abzahlen."
             */
            Property field = (Property) currField;
            switch (getUserInput(2)) {
                case 1: // Hypothek aufnehmen
                    // wenn im Besitz und noch keine Hypothek aufgenommen
                    if (field.getOwner().equals(currPlayer) && (!(field.isMortgageTaken()))) {
                        takeMortgage(field);
                        /*
                         * @output: "Hypothek aufgenommen."
                         */
                    } else {
                        /*
                         * @output: "Dieses Grundstück gehört dir nicht, oder hat schon eine Hypothek."
                         */
                    }
                    break;

                case 2: // Hypothek abbezahlen
                    // wenn im Besitz und Hypothek aufgenommen
                    if (field.getOwner().equals(currPlayer) && (field.isMortgageTaken())) {
                        payMortgage(field);
                        /*
                         * @output: "Hypothek abgezahlt."
                         */
                    } else {
                        /*
                         * @output: "Dieses Grundstück gehört dir nicht, oder hat keine Hypothek zum abzahlen."
                         */
                    }
                    break;

                default:

                    break;
            }
        }

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
            currPlayer.setMoney(currPlayer.getMoney() + ((GoField) board.getFields()[0]).getAmount());
            //@output fuer Zahlung von GoField
            System.out.println(currPlayer.getName() + " ist ueber Los gegangen und erhaelt " + ((GoField) board.getFields()[0]).getAmount()
                    + " " + CURRENCY_TYPE + ".");
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
        //@output Spieler in das Gefaengnis setzten
        System.out.println(currPlayer.getName() + " wurde in das Gefaengnis gesetzt.");
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
        System.out.println("Es wird geprüft, ob du genug Geld hast für die folgende Transaktion.");

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
        //@output
        System.out.println("Dir werden " + amount + "Monopoly Dollar abgezogen.");

    }

    /**
     * Einem Spieler wird der uebergebene Betrag auf dem Konto gutgeschrieben.
     *
     * @param player Spieler dem der Betrag gutgeschrieben wird
     * @param amount Betrag der dem Spieler gutgeschrieben wird
     */
    private void giveMoney(Player player, int amount) {

        player.setMoney(player.getMoney() + amount);
        //output
        System.out.println("Du erhälst " + amount + " Monopoly Dollar.");

    }

    /**
     * Macht einen Spieler zum Beobachter und entfernt all seinen Besitz!
     *
     *
     * @param player Spieler der bankrott gegangen ist
     */
    private void bankrupt(Player player) { //TODO !hier sollt ihr nicht den currPlayer sondern den parameter nehmen!!!
        //@output
        System.out.println("Du bist Bankrott und ab jetzt nur noch Zuschauer.");

        // Spieler auf Spectator setzen
        currPlayer.setSpectator(true);

        // Temporär das Feldarray zum Durchgehen zwischenspeichern
        Field[] fields = board.getFields();

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

    /**
     * fuegt dem Kartenstapel wieder eine Gefaengnis-frei-Karte hinzu.
     */
    private void enqueueJailCard() {
        /*
         * TODO @cards - Methode ist fertig mit: "CardQueue".addCard(JailCard); Nur wissen wir noch nicht, wie der Kartenstapel
         * initialisiert wird..
         */

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
            } else {
                //@output Straßenzug nicht komplett, oder unausgeglichen!
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
        } else {
            //@output Straßenzug würde unausgeglichen sein
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
     * Hypotheke aufnehmen
     *
     * @param filed - das Feld, dessen Hypotheke aufgenommen wurde
     */
    private void takeMortgage(Property field) {
        giveMoney(currPlayer, field.getMortgageValue());
        field.setMortgageTaken(true);
    }

    /**
     * Hypotheke zurueck zahlen
     *
     * @param field - das Feld ist wieder aktiv und hat Rent
     */
    private void payMortgage(Property field) {
        //TODO Eli, hier ist noch Offen, dass der Betrag beim Zurueckzahlen hoeher sein muss. Streeftfield zu Property geändert
        int mortageBack;
        mortageBack = (field.getMortgageValue() / 100) * 110;
        takeMoney(currPlayer, mortageBack);
        field.setMortgageTaken(false);
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

        int renovation_house = house_price * field.getHouseCount();
        int renovation_hotel = hotel_price; //????? getHotelCount??
        int sum = renovation_house + renovation_hotel;
        if (checkLiquidity(currPlayer, sum)) {
            takeMoney(currPlayer, sum);
        } else {
            gameOver = true;
        }
    }

    //-------------------------------------------------------------------------
    //------------ Console-Input Methoden -------------------------------------
    //-------------------------------------------------------------------------
    public int getUserInput(int max) {

        Scanner scanner = new Scanner(System.in);
        int output;

        System.out.println("Eingabe:");
        // Solange nicht der richtige Wertebereich eingegeben wird, wird die Eingabe wiederholt
        do {

            output = scanner.nextInt();

            if (output >= 1 && output <= max) {
                return output;
            }

            System.out.println("Deine Eingabe liegt nicht im Wertebereich! Bitte erneut versuchen:");

        } while (true);

    }

}
