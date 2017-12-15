/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.networkClasses;

/**
 *
 * @author Christian Prinz
 */
public class BroadcastAuctionResponse {

    private int[][] aucPlayers;

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

}
