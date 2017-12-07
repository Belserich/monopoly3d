/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import de.btu.monopoly.net.client.GameClient;

/**
 *
 * @author Christian Prinz
 */
public class Lobby {

    private boolean inLobby = true;
    private static String[] users;
    private String playerName;
    private GameClient playerClient;

    /**
     * @return the inLobby
     */
    protected boolean isInLobby() {
        return inLobby;
    }

    /**
     * @param inLobby the inLobby to set
     */
    protected void setInLobby(boolean inLobby) {
        this.inLobby = inLobby;
    }

    /**
     * @return the users
     */
    protected String[] getUsers() {
        return users;
    }

    /**
     * @param players the users to set
     */
    protected void setUsers(String[] players) {
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
}
