package de.btu.monopoly.controller;

import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.*;

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
     * Die zentrale Manager-Klasse für alles was ein Spiel betrifft.
     *
     * @param playerCount Anzahl Spieler
     * @param fields sämtliche Felder
     */
    public GameController(int playerCount, Field[] fields) {
        this.board = new GameBoard(fields);
        this.players = new Player[playerCount];
        init();
    }

    /**
     * Spielinitialisierung
     */
    public void init() {
        startGame();
        // TODO Maxi ist die Initialisierung komplett?
    }

    /**
     * Spielstart
     */
    public void startGame() {
        do {
            for (int i = 0; i < players.length; i++) {  // für alle Spieler
                currPlayer = players[i];		        // aktiven Spieler setzen
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
        doubletCounter = 0; 	        // PaschZaehler zuruecksetzen
        if (currPlayer.isInJail()) {	// Spieler im Gefaengnis
            prisonPhase();
        }
        do {		// bei Pasch wiederholen
            rollPhase();
            fieldPhase();
            actionPhase();
        } while (isDoublet);
    }

//-----------------------------------------------------------------------------
//----------- EINZELNE PHASEN -------------------------------------------------
//-----------------------------------------------------------------------------
    /**
     * Gefängnisphase
     */
    private void prisonPhase() {
        int prisonChoice = -1;
        switch (prisonChoice) { // @GUI
            //OPTION 1: Bezahlen
            case 1:
                if (checkLiquidity(currPlayer, 50)) {
                    takeMoney(currPlayer, 50);
                    currPlayer.setInJail(false);
                    currPlayer.setDaysInJail(0);
                } else { // muss in der GUI deaktiviert sein!!!
                    bankrupt(currPlayer);
                }
                break;

            //OPTION 2: Freikarte ausspielen
            case 2:
                if (currPlayer.getJailCardAmount() > 0) {
                    currPlayer.removeJailCard();
                    enqueueJailCard();
                    currPlayer.setInJail(false);
                    currPlayer.setDaysInJail(0);
                } else { // muss in der GUI deaktiviert sein!!!

                }
                break;

            //OPTION 3: 3mal wuerfeln
            case 3:
                roll();                 // wuerfeln
                if (!isDoublet) {       // kein Pasch > im Gefaengnis bleiben
                    currPlayer.addDayInJail();
                } else {                        // sonst frei
                    currPlayer.setInJail(false);
                    currPlayer.setDaysInJail(0);
                }
                // Wenn 3 mal kein Pasch dann bezahlen
                if (currPlayer.getDaysInJail() == 3) {
                    if (checkLiquidity(currPlayer, 50)) {
                        takeMoney(currPlayer, 50);
                        currPlayer.setInJail(false);
                    } else {        //wenn pleite game over
                        bankrupt(currPlayer);
                    }
                }
        }
    }

    /**
     * die Wurfphase (wuerfeln und ziehen)
     */
    private void rollPhase() {
        if (!(currPlayer.isInJail())) { //Gefaengnis hat eigenes Wuerfeln
            roll();
            if (doubletCounter == 3) {
                currPlayer.setInJail(true);
                moveToJail();
                currPlayer.setDaysInJail(0);
                doubletCounter = 0;
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

        switch (fieldSwitch) {

            case 1: // Strasse / Bahnhof

                if (currField instanceof Property) {
                    Property actualProperty = (Property) currField;
                    // wenn das Feld in eigenem Besitz ist
                    if (actualProperty.getOwner() == currPlayer) {
                        break;
                    } else if (actualProperty.getOwner() == null) { //wenn frei
                        int freeStreetChoice = 1;
                        switch (freeStreetChoice) { //@GUI
                            case 1: //Kaufen
                                if (checkLiquidity(currPlayer, actualProperty.getPrice())) {
                                    actualProperty.setOwner(currPlayer);
                                    takeMoney(currPlayer, actualProperty.getPrice());
                                } else { // muss in der GUI deaktiviert sein!

                                }
                                break;

                            case 2: //Auktion - NOCH DEAKTIVIERT @multiplayer
                                betPhase();
                                break;
                        }
                    } else { // wenn nicht in eigenem Besitz
                        Player owner = actualProperty.getOwner();
                        int rent = actualProperty.getRent();

                        // wenn es sich um ein Werk handelt:
                        if (actualProperty instanceof SupplyField) {
                            rent = rent * diceResult;
                        }
                        if (checkLiquidity(currPlayer, rent)) {
                            takeMoney(currPlayer, rent);
                            giveMoney(owner, rent);
                        } else {
                            bankrupt(currPlayer);
                        }
                    }

                } else { // kann nicht auftreten
                    break;
                }
                break;

            case 2: //LOS
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
                if (currField instanceof TaxField) {
                    TaxField taxField = (TaxField) currField;
                    if (checkLiquidity(currPlayer, taxField.getTax())) {
                        takeMoney(currPlayer, taxField.getTax());
                    } else {
                        bankrupt(currPlayer);
                    }
                    // spaeter kommt hier evtl. der Steuertopf zum Zuge @rules
                } else { // kann nicht auftreten

                }
                break;

            case 6: // Kartenfeld
                if (currField instanceof CardField) {
                    CardField cardField = (CardField) currField;
                    /*
                     * TODO @cards - Karten auslesen und co, wenn man hier auf ein anderes Feld gesetzt wird, muss in der
                     * Kartenmethode die Feldphase nochmal aufgerufen werden, oder ein in der Karte vorgegebener alternativer
                     * Feldphasenabruf stattfinden. FeldPhase muss so nicht "geschleift" werden.
                     */
                } else { // kann nicht auftreten

                }

                break;

            case 7: // GehInsGefaengnis-Feld
                moveToJail();
                break;

            default: // kann nicht auftreten!

                break;

        }

    }

    /**
     * @author Eli
     *
     * Die Aktionsphase (Bebauung, Hypothek, Handeln)
     */
    private void actionPhase() {
        if (currPlayer.isInJail() && (currPlayer.getDaysInJail() < 1)) {
            // actionPhase() entfaellt, wenn der Spieler grade ins Gefaengnis kam
        } else {
            // TODO Eli, hier noch Überlegungen zum input (2 Stellen), (Auflistung alle Straßen im Besitz?)
            int selectedField = 0; //speziell hier...
            this.currField = board.getFields()[selectedField];

            if (currField instanceof StreetField) { //wenn Feld eine Straße ist
                StreetField field = (StreetField) currField;
                int actionSwitch = 0; // dann genau hier von 1 - 4....
                switch (actionSwitch) {
                    case 1: // Haus bauen
                        // wenn im Besitz und nicht vollgebaut
                        if ((field.getOwner().equals(currPlayer)) && (field.getHouseCount() < 5)) {
                            buyBuilding(field);
                        } else {
                            //@output: Diese Straße gehört dir nicht, oder ist vollgebaut.
                        }
                        break;

                    case 2: // Haus verkaufen
                        // wenn im Besitz und nicht Hauslos
                        if ((field.getOwner().equals(currPlayer)) && (field.getHouseCount() > 0)) {
                            sellBuilding(field);
                        } else {
                            //@output: Diese Straße gehört dir nicht, oder hat keine Häuser zum verkaufen.
                        }
                        break;

                    case 3: // Hypothek aufnehmen
                        // wenn im Besitz und noch keine Hypothek aufgenommen
                        if (field.getOwner().equals(currPlayer) && (!(field.isMortgageTaken()))) {
                            takeMortgage(field);
                        } else {
                            //@output Diese Straße gehört dir nicht, oder hat schon eine Hypothek.
                        }
                        break;

                    case 4: // Hypothek abbezahlen
                        // wenn im Besitz und Hypothek aufgenommen
                        if (field.getOwner().equals(currPlayer) && (field.isMortgageTaken())) {
                            payMortgage(field);
                        } else {
                            //@output Diese Straße gehört dir nicht, oder hat keine Hypothek zum abzahlen.
                        }
                        break;

                    default:

                        break;
                }
            }

            if (currField instanceof Property) { //wenn Feld ein Bahnhof oder Werk ist
                Property field = (Property) currField;
                int actionSwitch = 0; // ...oder alternativ hier von 1 - 2
                switch (actionSwitch) {
                    case 1: // Hypothek aufnehmen
                        // wenn im Besitz und noch keine Hypothek aufgenommen
                        if (field.getOwner().equals(currPlayer) && (!(field.isMortgageTaken()))) {
                            takeMortgage(field);
                        } else {
                            //@output Dieses Grundstück gehört dir nicht, oder hat schon eine Hypothek.
                        }
                        break;

                    case 2: // Hypothek abbezahlen
                        // wenn im Besitz und Hypothek aufgenommen
                        if (field.getOwner().equals(currPlayer) && (field.isMortgageTaken())) {
                            payMortgage(field);
                        } else {
                            //@output Dieses Grundstück gehört dir nicht, oder hat keine Hypothek zum abzahlen.
                        }
                        break;

                    default:

                        break;
                }
            }
            // buyHouse(StreetField field),
            // sellHouse(StreetField field),
            // checkBalance(StreetField field) (Gleichgewicht der Haeuser)
            // takeMortgage(StreetField field)
            // payMortgage(StreetField field))
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
        if ((player.getMoney() - amount) < 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Einem Spieler wird der uebergebene Betrag von seinem Konto abgezogen.
     *
     * @param player Spieler dem der Betrag abgezogen wird
     * @param amount Betrag der dem Spieler abgezogen wird
     */
    private void takeMoney(Player player, int amount) {

        player.setMoney(player.getMoney() - amount);

    }

    /**
     * Einem Spieler wird der uebergebene Betrag auf dem Konto gutgeschrieben.
     *
     * @param player Spieler dem der Betrag gutgeschrieben wird
     * @param amount Betrag der dem Spieler gutgeschrieben wird
     */
    private void giveMoney(Player player, int amount) {

        player.setMoney(player.getMoney() + amount);

    }

    /**
     * Macht einen Spieler zum Beobachter und entfernt all seinen Besitz!
     *
     *
     * @param player Spieler der bankrott gegangen ist
     */
    private void bankrupt(Player player) {

        // Spieler auf Spectator setzen
        currPlayer.setSpectator(true);

        // Temporär das Feldarray zum Durchgehen zwischenspeichern
        Field[] fields = board.getFields();

        // Durchgehen des Array fields, ggf. Eigentum löschen
        for (int i = 0; i < fields.length; i++) {

            if (fields[i] instanceof Property) {

                // Löschen der Hypothek und des Eigentums
                if (((Property) fields[i]).getOwner() == currPlayer) {

                    ((Property) fields[i]).setOwner(null);
                    ((Property) fields[i]).setMortgageTaken(false);

                }

                // Löschen der Anzahl an Häusern
                if (fields[i] instanceof StreetField) {

                    ((StreetField) fields[i]).setHouseCount(0);

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

        // locate geloest:
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
        //@Eli, added Hausbau hinzugefuegt. Spectator unmöglich

        if (!(currPlayer.isSpectator()) && checkLiquidity(currPlayer, field.getHousePrice())) {
            if (field.complete() && checkBalance(field)) {
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
        //TODO Eli, added Bebauung, Spectator unmöglich. HIER MUSS NOCH EIN ANDERES CHECKBALANCE REIN!!! sagt Christian
        if (!(currPlayer.isSpectator())) {// anderes Checkbalance
            giveMoney(currPlayer, field.getHousePrice());
            field.setHouseCount(field.getHouseCount() - 1); // Haus abbauen
        } else {
            //@output Straßenzug würde unausgeglichen sein
        }
    }

    /**
     * @return true wenn eine Strasse gleiches Gewicht von Haeuser hat und false wenn nicht
     * @param field die auf Ausgeglichenheit im Strassenzug zu pruefende Strasse
     */
    private boolean checkBalance(StreetField field) {
        //@Eli, replaced. neighbours ist eine Liste
//        StreetField field = (StreetField) currField;  //als Parameter hinzugefuegt
//        StreetField neighbours = (StreetField) field.getNeighbours();
//        if ((neighbours.getHouseCount()) == (field.getHouseCount())) {
//            return true;
//        }
//        return false;

        for (Property nei : field.getNeighbours()) {  // Liste der Nachbarn durchgehen

            int housesHere = field.getHouseCount();      // Haueser auf der aktuellen Strasse
            int housesThere = ((StreetField) nei).getHouseCount();       // Haeuser auf der Nachbarstrasse
            if ((housesHere - housesThere) > 0) {        // Wenn die Nachbarn weniger Haueser haben
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
        //@Eli, ok
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
        takeMoney(currPlayer, field.getMortgageValue());
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
        //TODO
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
        //TODO Eli hier musst du den Houscount switchen 1-4 = houses, 5 = hotel
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

}
