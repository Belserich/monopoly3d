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
    private int highestBid;
    private int highestBidder;

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
