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
     * Gibt an, ob das Spiel beendet ist.
     */
    private boolean gameOver;

    /**
     * Anzahl Pasches
     */
    private int doubletCounter;

    /**
     * Array mit beiden Wuerfelergebnissen
     */
    private int[] rollResult = new int[2];

    /**
     * Feld, auf dem sich der Spieler befindet
     */
    private Field currField;

    /**
     * Preis fuer eine zur Auktion freigegebenen Strasse
     */
    private int betPrice;

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
                Player currPlayer = players[i];		            // aktiven Spieler setzen
                if (!(currPlayer.isSpectator())) {	        // Spieler ist kein Beobachter
                    turnPhase(currPlayer);
                }
            }
        } while (!gameOver);

        // suche letzten aktiven Spieler
        for (Player player : players) {
            if (!player.isSpectator()) {
                logger.log(Level.INFO, player.getName() + " hat das Spiel gewonnen!");
            }
        }
    }

    /**
     * Rundenphase eines Spielers
     */
    private void turnPhase(Player currPlayer) {
        logger.log(Level.INFO, currPlayer.getName() + " ist dran!");

        doubletCounter = 0; 	        // Paschzaehler zuruecksetzen
        if (currPlayer.isInJail()) {
            jailPhase(currPlayer);
        }
        do {		                    // bei Pasch wiederholen
            rollPhase(currPlayer);
            fieldPhase(currPlayer);
            if (currPlayer.isInJail() && (currPlayer.getDaysInJail() == 0)) {
                break;                  // actionPhase() entfaellt, wenn der Spieler grade ins Gefaengnis kam
            }
            actionPhase(currPlayer);
        } while (rollResult[0] == rollResult[1]);

        //Wuerfelergebnis zuruecksetzen
        rollResult[0] = 0;
        rollResult[1] = 0;
    }

//-----------------------------------------------------------------------------
//----------- EINZELNE PHASEN -------------------------------------------------
//-----------------------------------------------------------------------------
    /**
     * Gefängnisphase
     *
     * @param player Spieler in der Gefaengnisphase
     */
    private void jailPhase(Player player) {
        boolean repeat;
        do {
            repeat = false;
            logger.log(Level.INFO, player.getName() + "ist im Gefängnis und kann: \n1. 3 mal Würfeln, um mit einem Pasch freizukommen "
                    + "\n2. Bezahlen (50€) \n3. Gefängnis-Frei-Karte benutzen");

            switch (getUserInput(3)) { // @GUI
                // OPTION 1: wuerfeln (bis zu drei mal)
                case 1:
                    roll(player);                 // wuerfeln
                    if (!(rollResult[0] == rollResult[1])) {       // kein Pasch -> im Gefaengnis bleiben
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
    private void rollPhase(Player player) {
        logger.log(Level.INFO, player.getName() + " ist dran mit würfeln.");
        if (!(player.isInJail())) { //Gefaengnis hat eigenes Wuerfeln
            roll(player);
            if (doubletCounter == 3) {
                logger.log(Level.INFO, player.getName() + " hat seinen 3. Pasch und geht nicht über LOS, direkt ins Gefängnis!");
                moveToJail(player);
            }
        }
        if (!(player.isInJail())) { //kann sich nach wuerfeln aendern
            movePlayer(player);
        }
    }

    /**
     * die Feldphase (Feldaktionen)
     *
     * @param player Spieler in der Feldphase
     */
    private void fieldPhase(Player player) {

        switch (locate(player)) { //@optimize
            case 1: // Strasse / Bahnhof / Werk
                if (currField instanceof Property) {
                    Property actualProperty = (Property) currField;
                    // wenn das Feld in eigenem Besitz ist
                    if (actualProperty.getOwner() == player) {
                        logger.log(Level.INFO, player.getName() + " steht auf seinem eigenen Grundstück.");
                        break;
                    } else if (actualProperty.getOwner() == null) { //wenn frei
                        logger.log(Level.INFO, player.getName() + " steht auf einem freien Grundstück und kann es: \n1. Kaufen \n2. nicht Kaufen");
                        switch (getUserInput(2)) { //@GUI
                            case 1: //Kaufen
                                /*
                                 * kauft die Strasse, wenn nicht moeglich, findet Aution statt
                                 */
                                if (!buyStreet(player, actualProperty, actualProperty.getPrice())) {
                                    betPhase(actualProperty);
                                }
                                break;
                            case 2: //Auktion - NOCH DEAKTIVIERT @multiplayer
                                logger.log(Level.INFO, "Auktionsphase noch nicht abschließend implementert. "
                                        + "Straße wird dem ersten Spieler für 0" + CURRENCY_TYPE + " verkauft!!!");
                                betPhase(actualProperty);
                                break;
                            default:
                                logger.log(Level.WARNING, "FEHLER: SteetBuySwitch überlaufen.");
                                break;
                        }
                    } else { // wenn nicht in eigenem Besitz
                        logger.log(Level.INFO, player.getName() + " steht auf dem Grundstück von " + actualProperty.getOwner().getName());
                        int rent = actualProperty.getRent();
                        // wenn es sich um ein Werk handelt:
                        if (actualProperty instanceof SupplyField) {
                            rent = rent * (rollResult[0] + rollResult[1]);
                        }

                        if (checkLiquidity(player, rent)) {
                            logger.log(Level.INFO, player.getName() + " zahlt Miete:");
                            takeMoney(player, rent);
                            giveMoney(actualProperty.getOwner(), rent);
                        } else {
                            logger.log(Level.INFO, player.getName() + " kann die Miete nicht zahlen!");
                            bankrupt(player);
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
                logger.log(Level.INFO, player.getName() + " muss Steuern zahlen.");
                if (currField instanceof TaxField) {
                    TaxField taxField = (TaxField) currField;
                    if (checkLiquidity(player, taxField.getTax())) {
                        takeMoney(player, taxField.getTax());
                    } else {
                        logger.log(Level.INFO, player.getName() + " hat nicht genug Geld für die Steuern");
                        bankrupt(player);
                    }
                    // spaeter kommt hier evtl. der Steuertopf zum Zuge @rules
                } else { // kann nicht auftreten
                    logger.log(Level.WARNING, "FEHLER: FieldSwitch überlaufen! (Steuerfeld)");
                }
                break;

            case 6: // Kartenfeld
                if (currField instanceof CardField) {
                    logger.log(Level.INFO, player.getName() + " ist auf einem Kartenfeld gelandet. "
                            + "Die Karten liegen aber noch in der Druckerei. Nichts passiert.");
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
                logger.log(Level.INFO, player.getName() + " muss ins Gefaengnis!");
                moveToJail(player);
                break;

            default: // kann nicht auftreten!
                logger.log(Level.WARNING, "FEHLER: FieldSwitch überlaufen.");
                break;
        }

    }

    /**
     *
     * Die Aktionsphase (Bebauung, Hypothek, aktivieren der Handelsphase)
     *
     * @param player Spieler in der Aktionsphase
     */
    private void actionPhase(Player player) { //@optimize switches vereinfachen
        // TODO hier muss später noch der Handel implementiert werden
        int choice;
        do {
            logger.log(Level.INFO, player.getName() + "! Waehle eine Aktion:\n[1] - Nichts\n[2] - Haus kaufen\n[3] - Haus verkaufen\n[4] - "
                    + "Hypothek aufnehmen\n[5] - Hypothek abbezahlen");

            choice = getUserInput(5);
            if (choice != 1) {
                currField = board.getFields()[askForField(player) - 1]; // Wahl der Strasse

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
     * Methode zum Auswaehen einer Strasse die Bearbeitet werden soll in der actionPhase()
     *
     * @param player Spieler der eine Eingabe machen soll
     * @return ein int Wert zu auswaehen einer Strasse
     */
    private int askForField(Player player) {
        String mesg = player.getName() + "! Wähle ein Feld:\n";
        Field[] fields = board.getFields();
        for (int i = 0; i < fields.length; i++) {
            mesg += String.format("[%d] - %s%n", i + 1, fields[i].getName());
        }
        logger.log(Level.INFO, mesg);
        return getUserInput(39);
    }

    /**
     * die Versteigerungsphase
     */
    private void betPhase(Property property) {
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
    /**
     * das Wuerfeln. Ergebnisse werden lokal in rollResult und doubletCounter gespeichert
     */
    private void roll(Player player) {

        // Erzeugen der Zufallszahl
        rollResult[0] = ((int) (Math.random() * 6)) + 1;
        rollResult[1] = ((int) (Math.random() * 6)) + 1;

        // Bei Pasch, erhöhe doubletCounter
        if (rollResult[0] == rollResult[1]) {
            doubletCounter++;
        }

        logger.log(Level.INFO, player.getName() + " würfelt " + rollResult[0] + " + " + rollResult[1]
                + " = " + (rollResult[0] + rollResult[1]));
    }

    /**
     * bewegt den Spieler zu einer neuen Position.
     *
     * @param player Spieler der bewegt wird
     */
    private void movePlayer(Player player) {
        /*
         * TODO brauchen wird noch eine Methode um den Spieler FREI zu bewegen? falls die Zahl 39 ueberschritten wird und damit
         * das Spielfeld dann geht der Spieler ueber Los (bekommt 200) und wird auf die Position aktuelles Feld + Wuerfelanzahl
         * minus der Gesamtfeldanzahl falls nicht dann geht der Spieler nicht ueber Los und wird einfach auf das aktuelle Feld
         * plus der Anzahl der Wuerfelanzahl gesetzt
         */
        if ((player.getPosition() + (rollResult[0] + rollResult[1])) > 39) {

            logger.log(Level.INFO, player.getName() + " ist ueber Los gegangen und erhaelt " + ((GoField) board.getFields()[0]).getAmount()
                    + " " + CURRENCY_TYPE + ".");

            giveMoney(player, ((GoField) board.getFields()[0]).getAmount());

            player.setPosition(((player.getPosition() + (rollResult[0] + rollResult[1])) - 39));
        } else {
            player.setPosition(player.getPosition() + (rollResult[0] + rollResult[1]));
        }
    }

    /**
     * bewegt den Spieler ins Gefängnis und setzt seine Attribute entsprechend
     *
     * @param player Spieler der ins Gefaengnis kommt
     */
    private void moveToJail(Player player) {
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
     *
     * @param player Spieler der die Strasse kauft
     * @param property Strasse die gekauft werden soll
     * @param price Preis der Strasse
     * @return ob die Strasse gekauft wurde
     */
    private boolean buyStreet(Player player, Property property, int price) {
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
     * ueberpruft ob der uebergebene Spieler mindestens soviel Geld besitzt, wie die Methode uebergeben bekommt.
     *
     * @param player Spieler der auf Liquiditaet geprueft wird
     * @param amount Geld was der Spieler besitzen muss
     */
    private boolean checkLiquidity(Player player, int amount) {

        logger.log(Level.INFO, "Es wird geprüft, ob " + player.getName() + " genug Geld hat für die Transaktion.");
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
    private void giveMoney(Player player, int amount) {

        player.setMoney(player.getMoney() + amount);
        logger.log(Level.INFO, player.getName() + " erhält " + amount + CURRENCY_TYPE + ".");
        logger.log(Level.INFO, "Der Kontostand von " + player.getName() + " beträgt nun: " + player.getMoney() + CURRENCY_TYPE + ".");
    }

    /**
     * Macht einen Spieler zum Beobachter und entfernt all seinen Besitz!
     *
     *
     * @param player Spieler der bankrott gegangen ist
     */
    private void bankrupt(Player player) {
        logger.log(Level.INFO, player.getName() + " ist Bankrott und ab jetzt nur noch Zuschauer. All sein Besitz geht zurück an die Bank.");

        // Spieler als Zuschauer festlegen
        player.setSpectator(true);

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
                if (((Property) field).getOwner() == player) {

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
     *
     * @return Anzahl der Spieler die nicht pleite sind
     */
    private int countActivePlayers() {
        return (int) Arrays.stream(players)
                .filter(p -> !(p.isSpectator()))
                .count();
    }

    /**
     * ermittelt anhand der Position des Spielers das Feld mit der ID auf dem GameBoard, welches der Variablen
     * <code> Field currField </code> uebergeben wird. Gibt folgenden fieldSwitch Wert zurück: <br>
     * 1 - Straße / Bahnhof / Werk (Property) <br>
     * 2 - LOS <br>
     * 3 - Frei-Parken <br>
     * 4 - Gefaengnis <br>
     * 5 - Steuer <br>
     * 6 - Ereignis- / Gemeinschaftskartenfeld <br>
     * 7 - Geh ins Gefaengnis
     *
     * @param player Spieler dessen Position ermittelt werden soll
     * @return fieldSwitch Wert
     */
    private int locate(Player player) {

        int fieldSwitch = 0;
        // Da es keine Implementierung des Gefängnisfeldes
        // und des Freiparkenfeldes gibt. Wurden die zwei
        // if Abfragen zunächst über die Spielerposition realisiert
        // Lösung ist korrekt so. GoToJail wurde hinzugefügt
        currField = board.getFields()[player.getPosition()];

        logger.log(Level.INFO, player.getName() + " befindet sich auf " + currField.getName() + ".");

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
        return fieldSwitch;
    }

    /**
     * Kauf von Haus/Hotel - wenn der aktive Spieler genügend Geld
     *
     * @param field Feld worauf ein Haus/Hotel gekauft/gebaut wird
     * @param player Spieler dem die Strasse gehoert
     */
    private void buyBuilding(Player player, StreetField field) {
        //@Eli, added Hausbau hinzugefuegt. TODO Spectator unmöglich

        if (!(player.isSpectator()) && checkLiquidity(player, field.getHousePrice())) {
            if (field.complete() && checkBalance(field, true)) {
                takeMoney(player, field.getHousePrice()); // enfernt: * field.getHouseCount());
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
    private void sellBuilding(Player player, StreetField field) {
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
    private boolean checkBalance(StreetField field, boolean buyIntend) {
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
    private void takeMortgage(Player player, Property field) {
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
    private void payMortgage(Player player, Property field) {
        int mortgageBack = field.getMortgageBack();
        if (checkLiquidity(player, mortgageBack)) {
            takeMoney(player, field.getMortgageBack());
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
    private void sumRenovation(Player player, int house_price, int hotel_price) {
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
        if (checkLiquidity(player, sum)) {
            takeMoney(player, sum);
        } else {
            bankrupt(player);
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
