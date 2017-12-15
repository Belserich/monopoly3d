package de.btu.monopoly.core.service;

import de.btu.monopoly.core.mechanics.Auction;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.input.IOService;

/**
 *
 * @author patrick
 */
public class AuctionService {

    private static Auction auc;
    private static int[][] aucPlayers;

    /**
     * Initialisierung des "Auktionshauses"
     *
     * @param players
     */
    public static void initAuction(Player[] players) {
        auc = new Auction(players);
    }

    /**
     * Startet die Auktion, ermittelt den Höchstbietenden und übergibt den Gewinner, sowie den Preis an deren Klassenvariablen
     *
     * @param prop
     */
    public static void startAuction(PropertyField prop) {

        Player[] players = auc.getPlayers();
        int playerNumb = players.length;
        auc.setProperty(prop);

        /*
        Erstelle ein int[][] welches die ID's und Gebote der Spieler speichert
         */
        int[][] aucPlayers = new int[playerNumb][3];
        for (int i = 0; i < playerNumb; i++) {
            aucPlayers[i][0] = players[i].getId();      //Speicher Spieler ID
            aucPlayers[i][1] = 0;                       //Speichert Spieler Gebot
            aucPlayers[i][2] = 1;                       //Spieler noch aktiv? 1 = ja, 0 = nein
        }

        while (auctionStillActive()) {
            IOService.sleep(500);
        }

        FieldService.buyPropertyField(auc.getWinner(), auc.getProperty(), auc.getPropPrice());

    }

    /**
     * Setzt das Gebot eines Spielers, falls dieses hoeher ist als das aktuell hoechste Gebot
     *
     * @param aucPlayers
     * @param i
     * @param bid
     */
    private boolean setBid(int i, int bid) {

        boolean isBidOk = true;

        if (bid > getHighestBid()) {
            aucPlayers[i][1] = bid;
        } else {
            isBidOk = false;
        }

        return isBidOk;
    }

    /**
     * Diese Methode ermoeglicht es einem Spieler, die Auktion zu verlassen.
     *
     * @param aucPlayers
     * @return
     */
    private int[][] playerExit(int i) {

        aucPlayers[i][2] = 0;

        return aucPlayers;
    }

    /**
     * Das hoechste Gebot aller Bieter wird ermittelt. Es wird die ID des Spielers mit dem hoechsten Gebot zurueck gegeben
     *
     * @param aucPlayers
     * @return playerID
     */
    private int getHighestBid() {

        int highestBid = -1;

        for (int i = 0; i < aucPlayers.length; i++) {
            if (highestBid < aucPlayers[i][1] && aucPlayers[i][1] != 0) {
                highestBid = aucPlayers[i][1];
            }
        }

        return highestBid;
    }

    /**
     * Das hoechste Gebot aller Bieter wird ermittelt. Es wird die ID des Spielers mit dem hoechsten Gebot zurueck gegeben
     *
     * @param aucPlayers
     * @return playerID
     */
    private int getHighestBidder() {

        int playerID = -1;
        int highestBid = -1;

        for (int i = 0; i < aucPlayers.length; i++) {
            if (highestBid < aucPlayers[i][1] && aucPlayers[i][1] != 0) {
                playerID = aucPlayers[i][0];
            }
        }

        return playerID;
    }

    /**
     * Ueberprueft ob die Auktion noch genug Bieter hat
     *
     * @return stillActive
     */
    public static boolean auctionStillActive() {

        int activCount = 0;
        boolean stillActive = false;
        for (int i = 0; i < aucPlayers.length; i++) {
            activCount += aucPlayers[i][2];
        }

        if (activCount > 1) {
            stillActive = true;
        }

        return stillActive;
    }

}
