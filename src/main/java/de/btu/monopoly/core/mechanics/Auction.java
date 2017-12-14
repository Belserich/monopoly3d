package de.btu.monopoly.core.mechanics;

import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;

/**
 * Auktions Klasse Auktionen zum ersteigern von Grundstuecken, welche zuvor nicht gekauft wurden.
 *
 * @author Patrick Kalweit
 */
public class Auction {

    private int price;

    private Player[] players;

    private PropertyField property;

    /**
     *
     * @param property Die Strasse die versteigert werden soll
     * @param players Spieler die an der Auktion teilnehmen
     */
    public Auction(PropertyField property, Player[] players) {
        this.property = property;
        this.players = players;
    }

    /**
     * gibt das Player[] zurueck
     *
     * @return
     */
    public Player[] getPlayers() {
        return this.players;
    }

    /**
     * Setzt das Gebot eines Spielers
     *
     * @param activePlayers
     * @param i
     * @param bid
     */
    private void setBid(int[][] activePlayers, int i, int bid) {

        activePlayers[i][1] = bid;
    }

    /**
     * Diese Methode ermoeglicht es einem Spieler, die Auktion zu verlassen.
     *
     * @param activePlayers
     * @return
     */
    private int[][] playerExit(int[][] activePlayers, int i) {

        activePlayers[i][2] = 0;

        return activePlayers;
    }

    /**
     * Das hoechste Gebot aller Bieter wird ermittelt. Es wird die ID des Spielers mit dem hoechsten Gebot zurueck gegeben
     *
     * @param activePlayers
     * @return playerID
     */
    private int getHighestBid(int[][] activePlayers) {

        int playerID = -1;
        int highestBid = -1;

        for (int i = 0; i < activePlayers.length; i++) {
            if (highestBid < activePlayers[i][1] && activePlayers[i][1] != 0) {
                playerID = activePlayers[i][0];
                highestBid = activePlayers[i][1];
            }
        }

        return playerID;
    }

}
