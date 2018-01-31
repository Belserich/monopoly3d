package de.btu.monopoly.core.mechanics;

import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.client.GameClient;

/**
 * Auktions Klasse Auktionen zum ersteigern von Grundstuecken, welche zuvor nicht gekauft wurden.
 *
 * @author Patrick Kalweit
 */
public class Auction {

    private Player[] players;
    private Player winner;

    private PropertyField property;

    private int[][] aucPlayers;
    private int highestBidder;
    private int highestBid;

    private GameClient client;
    private String playerName;

    /**
     *
     * @param players Spieler die an der Auktion teilnehmen
     * @param client GameClient
     */
    public Auction(Player[] players, GameClient client) {
        this.players = players;
        this.client = client;
        this.playerName = client.getPlayerOnClient().getName();
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
     * gibt die Property zurueck
     *
     * @return
     */
    public PropertyField getProperty() {
        return this.property;
    }

    public void setProperty(PropertyField prop) {
        this.property = prop;
    }

    /**
     * @return the winner
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * @param winner the winner to set
     */
    public void setWinner(Player winner) {
        this.winner = winner;
    }

    /**
     * @return the highestBid
     */
    public int getHighestBid() {
        return highestBid;
    }

    /**
     * @param highestBid the highestBid to set
     */
    public void setHighestBid(int highestBid) {
        this.highestBid = highestBid;
    }

    /**
     * @return the client
     */
    public GameClient getClient() {
        return client;
    }

    /**
     * @return the playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return the aucPlayers
     */
    public int[][] getAucPlayers() {
        return aucPlayers;
    }

    /**
     * @param aucPlayers the aucPlayers to set
     */
    public void setAucPlayers(int[][] aucPlayers) {
        this.aucPlayers = aucPlayers;
    }

    /**
     * @return the highestBidder
     */
    public int getHighestBidder() {
        return highestBidder;
    }

    /**
     * @param highestBidder the highestBidder to set
     */
    public void setHighestBidder(int highestBidder) {
        this.highestBidder = highestBidder;
    }

}
