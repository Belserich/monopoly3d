/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import de.btu.monopoly.core.Game;
import de.btu.monopoly.net.client.GameClient;

/**
 *
 * @author Christian Prinz
 */
public class Lobby {

    private boolean host = true;
    private boolean ki = false;
    private String[][] users;
    private String playerName;
    private GameClient playerClient;
    private int playerID = -1;
    private long randomSeed;
    private Game controller;

    /**
     * @return the host
     */
    public boolean isHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(boolean host) {
        this.host = host;
    }

    /**
     * @return the users
     */
    public String[][] getUsers() {
        return users;
    }

    /**
     * @param players the users to set
     */
    public void setUsers(String[][] players) {
        this.users = players;
    }

    /**
     * @return the playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @param aPlayerName the playerName to set
     */
    public void setPlayerName(String aPlayerName) {
        playerName = aPlayerName;
    }

    /**
     * @return the playerClient
     */
    public GameClient getPlayerClient() {
        return playerClient;
    }

    /**
     * @param aPlayerClient the playerClient to set
     */
    public void setPlayerClient(GameClient aPlayerClient) {
        playerClient = aPlayerClient;
    }

    /**
     * @return the playerID
     */
    public int getPlayerId() {
        return playerID;
    }

    /**
     * @param id the playerID to set
     */
    public void setPlayerId(int id) {
        this.playerID = id;
    }

    /**
     * @return the ki
     */
    public boolean isKi() {
        return ki;
    }

    /**
     * @param ki the ki to set
     */
    public void setKi(boolean ki) {
        this.ki = ki;
    }

    /**
     * @return the randomSeed
     */
    public long getRandomSeed() {
        return randomSeed;
    }

    /**
     * @param randomSeed the randomSeed to set
     */
    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }

    /**
     * @return the controller
     */
    public Game getController() {
        return controller;
    }

    /**
     * @param controller the controller to set
     */
    public void setController(Game controller) {
        this.controller = controller;
    }
}
