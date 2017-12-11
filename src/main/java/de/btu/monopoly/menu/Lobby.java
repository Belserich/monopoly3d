package de.btu.monopoly.menu;

import de.btu.monopoly.net.client.GameClient;

/**
 *
 * @author Christian Prinz
 */
public class Lobby {

    private boolean host = true;
    private static String[][] users;
    private String playerName;
    private GameClient playerClient;
    private int playerID = -1;

    /**
     * @return the host
     */
    protected boolean isHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    protected void setHost(boolean host) {
        this.host = host;
    }

    /**
     * @return the users
     */
    public static String[][] getUsers() {
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
}
