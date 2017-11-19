package de.btu.monopoly.controller;

import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;

/**
 *
 * @author Christian Prinz
 */
public class GameController {
    
    /**
     * Pfad zur Datensammlung
     */
    private static final String BOARD_DATA_PATH = "field_data.config";
    
    private GameBoard board;
    private final Player[] players;
    private Player activePlayer;
    private boolean gameOver;
    private boolean repeatFieldPhase;
    private int doubletCounter;
    private boolean isDoublet;
    private int diceResult;
    private Field actualField;

    // Steuerung:
    /**
     * Optionen im Gefaengnis: (1 Bezahlen, 2 Karte, 3 Wuerfeln)
     */
    private int prisonChoice;

    public GameController(int playerCount) {
        this.players = new Player[playerCount];
        init();
    }

    /**
     * Spielinitialisierung
     */
    public void init() {
        startGame();
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
            do {
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
        switch (prisonChoice) {
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
                    returnJailCard(); //TODO
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
        //TODO
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
     * das Wuerfeln. Summe in rollResult speichern. Pasch nicht vergessen
     */
    private void roll() {
        //TODO
    }

    /**
     * bewegt den Spieler (activePlayer) zu einer neuen Position.
     * ueber-LOS-gehen beachten!
     */
    private void movePlayer() {
        //TODO
    }

    /**
     * analog zu movePlayer(), nur dass kein Geld beim ueber-LOS-gehen
     * ueberwiesen wird!
     */
    private void moveToJail() {

    }

    /**
     * ueberpruft ob der uebergebene Spieler mindestens soviel Geld besitzt, wie
     * die Methode uebergeben bekommt. (Hierbei ist der Spieler noch variabel,
     * da ich noch nicht weiss, ob man die Methode fuer eine Karte eventuell auf
     * andere nicht aktive Spieler anwenden muss. Ist dies nicht der Fall, kann
     * die Methodenuebergabe fuer Player player wegfallen und in der Methode nur
     * der activePlayer implementiert werden)
     *
     * @param player Spieler der auf Liquiditaet geprueft wird
     * @param amount Geld was der Spieler besitzen muss
     */
    private boolean checkLiquidity(Player player, int amount) {
        //TODO
        return false; //Platzhalter
    }

    /**
     * Einem Spieler wird der uebergebene Betrag von seinem Konto abgezogen.
     *
     * @param player Spieler dem der Betrag abgezogen wird
     * @param amount Betrag der dem Spieler abgezogen wird
     */
    private void takeMoney(Player player, int amount) {
        //TODO
    }

    /**
     * Einem Spieler wird der uebergebene Betrag auf dem Konto gutgeschrieben.
     *
     * @param player Spieler dem der BEtrag gutgeschrieben wird
     * @param amount Betrag der dem Spieler gutgeschrieben wird
     */
    private void giveMoney(Player player, int amount) {
        //TODO
    }

    /**
     * Macht einen Spieler zum Beobachter (fertig). Entfernt all seinen Besitz!
     * HIER MUESST IHR EUCH WAS CLEVERES UEBERLEGEN
     *
     * @param player Spieler der bankrott gegangen ist
     */
    private void bankrupt(Player player) {
        activePlayer.setSpectator(true);
        //TODO
    }

    /**
     * fügt dem Kartenstapel wieder eine Gefaengnis-frei-Karte hinzu. WIE MACHEN
     * WIR DAS, WENN DIE KARTEN IDs HABEN? Wenn diese Methode nur eine Zeile
     * hat, dann entfernt sie bitte und fügt diese Zeile beim Case 2 in der
     * prisonPhase hinzu
     */
    private void returnJailCard() {
        //TODO
    }

    /**
     * ermittelt anhand der Position des Spielers das Feld mit der ID auf dem
     * GameBoard, welches der Variablen actualField uebergeben wird. Bitte mit
     * Maxi absprechen, wie er das mit der Datenstruktur un der Initialisierung
     * der Felder auf dem Spielbrett geloest hat.
     *
     * @param player Spieler dessen Position ermittelt werden soll
     */
    private void locate(Player player) {
        //TODO
    }
}
