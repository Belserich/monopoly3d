package de.btu.monopoly.controller;

import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.CardField;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.field.Property;
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
    private Field actualField;
    
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
        do
        {
            for (int i = 0; i < players.length; i++) {  // für alle Spieler
                currPlayer = players[i];		        // aktiven Spieler setzen
                if (!(currPlayer.isSpectator())) {	    // Beobachter ist nicht am Zug
                    turnPhase();
                }
            }
        }
        while (!gameOver);
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
     * Gefängnisphase
     * TODO
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

                if (actualField instanceof Property) {
                    Property actualProperty = (Property) actualField;
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
                if (actualField instanceof TaxField) {
                    TaxField taxField = (TaxField) actualField;
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
                if (actualField instanceof CardField) {
                    CardField cardField = (CardField) actualField;
                    // TODO Christian & Maxi -- hier ist zu klären wie wir das endgueltig lösen mit den Karten und den Stapeln
                } else { // kann nicht auftreten

                }

                break;

            default: // kann nicht auftreten!

                break;

        }

    }

    /**
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
        //
        //TODO Patrick & John - eventuell warten auf Implementierung des Gameboard
    }

    /**
     * analog zu movePlayer(), nur dass kein Geld beim ueber-LOS-gehen
     * ueberwiesen wird!
     */
    private void moveToJail() {
        /*
         * TODO Patrick & John - eventuell warten auf Implementierung des
         * Gameboard
         */
    }

    /**
     * ueberpruft ob der uebergebene Spieler mindestens soviel Geld besitzt, wie
     * die Methode uebergeben bekommt.
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
     * @param player Spieler dem der BEtrag gutgeschrieben wird
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
        currPlayer.setSpectator(true);
        /*
         * TODO Patrick & John -- HIER MUESST IHR EUCH WAS CLEVERES UEBERLEGEN
         * WIE MAN DEN BESITZ ENTFERNT
         */
    }

    /**
     * fuegt dem Kartenstapel wieder eine Gefaengnis-frei-Karte hinzu.
     */
    private void enqueueJailCard() {
        /*
         * TODO Patrick & John -- WIE MACHEN WIR DAS, WENN DIE KARTEN IDs HABEN?
         * macht die Methode erst später, wenn Maxi seinen branch fertig hat
         * muss der Methode evtl. noch der Stapel uebergeben werden?
         */
    }

    /**
     * ermittelt anhand der Position des Spielers das Feld mit der ID auf dem
     * GameBoard, welches der Variablen actualField uebergeben wird.
     *
     * @param player Spieler dessen Position ermittelt werden soll
     */
    private void locate(Player player) {

        /*
         * TODO Patrick & John -- Bitte mit Maxi absprechen, wie er das mit der
         * Datenstruktur und der Initialisierung der Felder auf dem Spielbrett
         * geloest hat. ausserdem wird hier der int fieldSwitch je nach
         * ermitteltem Feld angepasst. Folgende Liste fuer den Fieldswitch
         * beachten:
         */
        // 1 - Straße / Bahnhof / Werk (Property)
        // 2 - LOS
        // 3 - Frei-Parken
        // 4 - Gefaengnis
        // 5 - Steuer
        // 6 - Ereignis- / Gemeinschaftskartenfeld
    }

    //-------------------------------------------------------------------------
    //------------ Karten Methoden --------------------------------------------
    //-------------------------------------------------------------------------
    /**
     * Doppelte Miete -- Diese Methode wird verwendet, wenn in einer Karte
     * gefordert ist die doppelte Miete zu zahlen.
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
     * Die Preise fuer Renovierung werden von dem entsprechenden Karte bekannt
     * und dies wird mit der Anzahl von Haeuser/Hotels multipliziert und am Ende
     * addiert = Summe
     *
     * @param house_price
     * @param hotel_price
     */
    private void sumRenovation(int house_price, int hotel_price) {
        //TODO
    }

}
