package de.btu.monopoly.controller;

import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.CardField;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.field.GoField;
import de.btu.monopoly.data.field.Property;
import de.btu.monopoly.data.field.StreetField;
import de.btu.monopoly.data.field.SupplyField;
import de.btu.monopoly.data.field.TaxField;

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
     * Gibt an, ob die Feldphase wiederholt werden soll.
     */
    private boolean repeatFieldPhase;

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
        // TODO (Maxi)
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
            /*
             * bekommt der Spieler eine Karte, die ihn Zwingt auf ein neues Feld
             * zu gehen, muss die Feldphase erneut stattfinden
             */
            do { // TODO Christian -- eventuell weg, wenn rekursiv
                fieldPhase();
            } while (repeatFieldPhase);
            actionPhase();
        } while (isDoublet);
    }

//-----------------------------------------------------------------------------
//----------- EINZELNE PHASEN -------------------------------------------------
//-----------------------------------------------------------------------------
    /**
     * Gefängnisphase TODO
     */
    private void prisonPhase() {
        int prisonChoice = -1;
        switch (prisonChoice) { // @GUI
            //OPTION 1: Bezahlen
            case 1:
                if (checkLiquidity(currPlayer, 50)) {
                    takeMoney(currPlayer, 50);
                    currPlayer.setInJail(false);
                } else { // muss in der GUI deaktiviert sein!!!
                    bankrupt(currPlayer);
                }
                break;

            //OPTION 2: Freikarte ausspielen
            case 2:
                if (currPlayer.getJailCardAmount() > 0) {
                    currPlayer.removeJailCard();
                    enqueueJailCard(); //TODO Einsetzen
                    currPlayer.setInJail(false);
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
                    // TODO Christian & Maxi -- hier ist zu klären wie wir das endgueltig lösen mit den Karten und den Stapeln
                } else { // kann nicht auftreten

                }

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
        // buyHouse(StreetField field),
        // sellHouse(StreetField field),
        // checkBalance() (Gleichgewicht der Haeuser)
        // takeMortgage(StreetField field)
        // payMortgage(StreetField field))
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
        falls die Zahl 39 ueberschritten wird und damit das Spielfeld
        dann geht der Spieler ueber Los (bekommt 200) und wird auf die Position
        aktuelles Feld + Wuerfelanzahl minus der Gesamtfeldanzahl
        falls nicht
        dann geht der Spieler nicht ueber Los und wird einfach auf das
        aktuelle Feld plus der Anzahl der Wuerfelanzahl gesetzt
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
         Spieler wird auf Position 10 gesetzt
         setInJail wird true, damit der Spieler nicht "nur zu Besuch" ist
         Die Tage im Gefängnis werden auf 0 gesetzt
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

        // TODO - Gefängnisfreikarten müssen zurück in den Stapel
    }

    /**
     * fuegt dem Kartenstapel wieder eine Gefaengnis-frei-Karte hinzu.
     */
    private void enqueueJailCard() {
        /*
         * TODO Patrick & John --
         * Methode ist fertig mit: "CardQueue".addCard(JailCard);
         * Nur wissen wir noch nicht, wie der Kartenstapel initialisiert wird..
         */

    }

    /**
     * ermittelt anhand der Position des Spielers das Feld mit der ID auf dem GameBoard, welches der Variablen currField
     * uebergeben wird, zudem wird fieldSwitch festgelegt, zur GameController Steuerung
     *
     * @param player Spieler dessen Position ermittelt werden soll
     */
    private void locate(Player player) {

        // TODO - locate:
        // Da es keine Implementierung des Gefängnisfeldes
        // und des Freiparkenfeldes gibt. Wurden die zwei
        // if Abfragen zunächst über die Spielerposition realisiert
        // Frage: Was ist mit GoToJail???? -> FieldSwitch
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
        }

        // fieldSwitch Belegung
        // 1 - Straße / Bahnhof / Werk (Property)
        // 2 - LOS
        // 3 - Frei-Parken
        // 4 - Gefaengnis
        // 5 - Steuer
        // 6 - Ereignis- / Gemeinschaftskartenfeld
    }

    /**
     * Kauf von Haus/Hotel
     *
     * @param field - das Feld worauf ein Haus/Hotel gekauft/gebaut wird
     */
    private void buyBuilding(StreetField field) {
        //TODO Eli
        if (!(currPlayer.isSpectator()) && checkLiquidity(currPlayer, field.getHousePrice())) {
            if (field.complete() && checkBalance()) {
                takeMoney(currPlayer, field.getHousePrice() * field.getHouseCount());
            }
        }
    }

    /**
     * Verkauf von Haus/Hotel
     *
     * @param field - das Feld wovon ein haus/Hotel verkauft/abbebaut wird
     */
    private void sellBuilding(StreetField field) {
        //TODO Eli
        if (!(currPlayer.isSpectator())) {
            giveMoney(currPlayer, field.getHousePrice());
        }
    }

    /**
     * @return true wenn eine Strasse gleiches Gewicht von Haeuser hat und false wenn nicht
     */
    private boolean checkBalance() {
        //TODO Eli
        StreetField field = (StreetField) currField;
        StreetField neighbours = (StreetField) field.getNeighbours();
        if ((neighbours.getHouseCount()) == (field.getHouseCount())) {
            return true;
        }
        return false;
    }

    /**
     * Hypotheke aufnehmen
     *
     * @param filed - das Feld, dessen Hypotheke aufgenommen wurde
     */
    private void takeMortgage(StreetField field) {
        //TODO Eli
        giveMoney(currPlayer, field.getMortgageValue());
        field.setMortgageTaken(true);
    }

    /**
     * Hypotheke zurueck zahlen
     *
     * @param field - das Feld ist wieder aktiv und hat Rent
     */
    private void payMortgage(StreetField field) {
        //TODO Eli
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
        //TODO Eli
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
