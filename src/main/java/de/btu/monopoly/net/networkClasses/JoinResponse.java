/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.networkClasses;

import de.btu.monopoly.data.Player;

/**
 *
 * @author Christian Prinz
 */
public class JoinResponse {

    private Player[] players;

    /**
     * @return the players
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * @param players the players to set
     */
    public void setPlayers(Player[] players) {
        this.players = players;
    }
}
