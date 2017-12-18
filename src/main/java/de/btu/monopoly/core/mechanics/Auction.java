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
    private PropertyField property;
    private Player winner;
    private GameClient client;
    private int propPrice;
    private String playerName;

    /**
     *
     * @param property Die Strasse die versteigert werden soll
     * @param players Spieler die an der Auktion teilnehmen
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
     * @return the propPrice
     */
    public int getPropPrice() {
        return propPrice;
    }

    /**
     * @param propPrice the propPrice to set
     */
    public void setPropPrice(int propPrice) {
        this.propPrice = propPrice;
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

}
