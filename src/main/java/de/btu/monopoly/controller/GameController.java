package de.btu.monopoly.controller;

import de.btu.monopoly.data.CardField;
import de.btu.monopoly.data.Field;
import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.Property;
import de.btu.monopoly.data.SupplyField;
import de.btu.monopoly.data.TaxField;

/**
 *
 * @author Christian Prinz
 */
public class GameController {

    private GameBoard board;
    private final Player[] players;
    /**
     * der aktive Spieler, welcher gerade dran ist
     */
    private Player activePlayer;
    /**
     * wenn alle bis auf einen Spieler pleite sind
     */
    private boolean gameOver;
    /**
     * while-boolean für die Wdh. der Feldphase
     */
    private boolean repeatFieldPhase;
    /**
     * Anzahl Pasche
     */
    private int doubletCounter;
    /**
     * wurde ein Pasch gewuerfelt?
     */
    private boolean isDoublet;
    /**
     * Ergebnis des letzten mal wuerfelns
     */
    private int diceResult;
    /**
     * das Feld auf dem sich der Spieler soeben befindet
     */
    private Field actualField;
    /**
     * ein int fuer die unterschiedlichen Typen von Feldern 1-
     */
    private int fieldSwitch;

    // Steuerung:
    /**
     * Optionen im Gefaengnis: (1 Bezahlen, 2 Karte, 3 Wuerfeln)
     */
    private int prisonChoice;
    /**
     * Optionen beim betreten einer freien Straße (1 Kaufen, 2 Auktion)
     */
    private int freeStreetChoice = 1; //@editInMultiplayer "=1" muss weg

    public GameController(int playerCount) {
        this.board = new GameBoard();
        this.players = new Player[playerCount];
        init();
    }

    /**
     * Spielinitialisierung
     */
    public void init() {
        startGame();
        // TODO Maxi
    }

    /**
     * das gestartete Spiel mit seiner phasenbasierte Grundstruktur
     */
    public void startGame() {
        /*
         * Schleife für Spieler
         */
        for (Player p : players) {
            /*
             * Rundenphase
             */
            activePlayer = p;		// aktiven Spieler setzen
            if (!(p.isSpectator())) {	// Beobachter ist nicht am Zug
                turnPhase();
            }
        }
    }

    /**
     * die Rundenphase
     */
    private void turnPhase() {
        doubletCounter = 0;		// PaschZaehler zuruecksetzen

        if (activePlayer.isInJail()) {	// Spieler im Gefaengnis?
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
     * @todo die Gefängnisphase
     */
    private void prisonPhase() {
        switch (prisonChoice) { // @GUI
            //OPTION 1: Bezahlen
            case 1:
                if (checkLiquidity(activePlayer, 50)) {
                    takeMoney(activePlayer, 50);
                    activePlayer.setInJail(false);
                } else { // muss in der GUI deaktiviert sein!!!
                    bankrupt(activePlayer);
                }
                break;

            //OPTION 2: GfKarte ausspielen
            case 2:
                if (activePlayer.getJailCardAmount() > 0) {
                    activePlayer.removeJailCard();
                    enqueueJailCard(); //TODO Einsetzen
                    activePlayer.setInJail(false);
                } else { // muss in der GUI deaktiviert sein!!!

                }
                break;

            //OPTION 3: 3mal wuerfeln
            case 3:
                roll();             //wuerfeln
                if (!isDoublet) {   //kein Pasch > im Gefaengnis bleiben
                    activePlayer.setDaysInJail(activePlayer.getDaysInJail() + 1);
                } else {            //sonst frei
                    activePlayer.setInJail(false);
                    activePlayer.setDaysInJail(0);
                }
                // Wenn 3 mal kein Pasch dann bezahlen
                if (activePlayer.getDaysInJail() == 3) {
                    if (checkLiquidity(activePlayer, 50)) {
                        takeMoney(activePlayer, 50);
                        activePlayer.setInJail(false);
                    } else {        //wenn pleite game over
                        bankrupt(activePlayer);
                    }
                }

        }
    }

    /**
     * die Wurfphase (würfeln und ziehen)
     */
    private void rollPhase() {
        if (!(activePlayer.isInJail())) { //Gefaengnis hat eigenes Wuerfeln
            roll();
            if (doubletCounter == 3) {
                activePlayer.setInJail(true);
                moveToJail();
                activePlayer.setDaysInJail(0);
                doubletCounter = 0;
            }
        }
        if (!(activePlayer.isInJail())) { //kann sich nach wuerfeln aendern
            movePlayer();
        }
    }

    /**
     * die Feldphase (Feldaktionen)
     */
    private void fieldPhase() {
        locate(activePlayer);

        switch (fieldSwitch) {

            case 1: // Strasse / Bahnhof

                if (actualField instanceof Property) {
                    Property actualProperty = (Property) actualField;
                    // wenn das Feld in eigenem Besitz ist
                    if (actualProperty.getOwner() == activePlayer) {
                        break;
                    } else if (actualProperty.getOwner() == null) { //wenn frei
                        switch (freeStreetChoice) { //@GUI
                            case 1: //Kaufen
                                if (checkLiquidity(activePlayer, actualProperty.getPrice())) {
                                    actualProperty.setOwner(activePlayer);
                                    takeMoney(activePlayer, actualProperty.getPrice());
                                } else { // muss in der GUI deaktiviert sein!

                                }
                                break;

                            case 2: //Auktion - NOCH DEAKTIVIERT @editInMultiplayer
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
                        checkLiquidity(activePlayer, rent);
                        takeMoney(activePlayer, rent);
                        giveMoney(owner, rent);
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
                    checkLiquidity(activePlayer, taxField.getTax());
                    takeMoney(activePlayer, taxField.getTax());
                    // spaeter kommt hier evtl. der Steuertopf zum Zuge @rules
                } else { // kann nicht auftreten

                }
                break;

            case 6: // Kartenfeld
                if (actualField instanceof CardField) {
                    CardField cardField = (CardField) actualField;
                    // TODO Christian & Maxi -- hier ist zu klären wie wir das endgültig lösen mit den Karten und den Stapeln
                } else { // kann nicht auftreten

                }

                break;

            default: // kann nicht auftreten!

                break;

        }

    }

    /**
     * Die Aktionsphase (Handeln, Bebauung, Hypothek)
     */
    private void actionPhase() {
        // Bebauung

        // Hypothek
        // Handeln NOCH NICHT BENOETIGT!
    }

    /**
     * die Versteigerungsphase
     */
    private void betPhase() {
        // FUER DIE SINGLEPLAYER-IMPLEMENTIERUNG NICHT BENOETIGT!!
    }

//-----------------------------------------------------------------------------
//----------- EINZELNE METHODEN------------------------------------------------
//-----------------------------------------------------------------------------
    /**
     * das Wuerfeln. Summe in diceResult speichern. Pasch nicht vergessen
     */
    private void roll() {

        // 2 interne Würfel
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
     * bewegt den Spieler (activePlayer) zu einer neuen Position.
     *
     */
    private void movePlayer() {

        //TODO Patrick & John - eventuell warten auf Implementierung des Gameboard
    }

    /**
     * analog zu movePlayer(), nur dass kein Geld beim ueber-LOS-gehen ueberwiesen wird!
     */
    private void moveToJail() {
        /*
         * TODO Patrick & John - eventuell warten auf Implementierung des Gameboard
         */
    }

    /**
     * ueberpruft ob der uebergebene Spieler mindestens soviel Geld besitzt, wie die Methode uebergeben bekommt.
     *
     * @param player Spieler der auf Liquiditaet geprueft wird
     * @param amount Geld was der Spieler besitzen muss
     */
    private boolean checkLiquidity(Player player, int amount) {

        // Die Methode benötigt die Übergabe des Spielers, da bei einem Ereignisfeld, eine Karte vorkommt, bei der der
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
        activePlayer.setSpectator(true);
        /*
         * TODO Patrick & John -- HIER MUESST IHR EUCH WAS CLEVERES UEBERLEGEN
         * WIE MAN DEN BESITZ ENTFERNT
         */
    }

    /**
     * fügt dem Kartenstapel wieder eine Gefaengnis-frei-Karte hinzu.
     */
    private void enqueueJailCard() {
        /*
         * TODO Patrick & John -- WIE MACHEN WIR DAS, WENN DIE KARTEN IDs HABEN?
         * macht die Methode erst später, wenn Maxi seinen branch fertig hat
         * muss der Methode evtl. noch der Stapel übergeben werden?
         */
    }

    /**
     * entfernt eine Gefaengnis-frei-Karte von einem Stapel
     */
    private void dequeueJailCard() {
        /*
         * TODO Patrick & John -- Kommentar analog zu enqueue
         */
    }

    /**
     * ermittelt anhand der Position des Spielers das Feld mit der ID auf dem GameBoard, welches der Variablen actualField
     * uebergeben wird.
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
        //TODO
    }

}
