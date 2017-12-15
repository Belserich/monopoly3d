package de.btu.monopoly.core.mechanics;

import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;

/**
 * Auktions Klasse Auktionen zum ersteigern von Grundstuecken, welche zuvor nicht gekauft wurden.
 *
 * @author Patrick Kalweit
 */
public class Auction {

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

}
