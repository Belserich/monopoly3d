package de.btu.monopoly.menu;

import de.btu.monopoly.core.Game;
import de.btu.monopoly.net.client.GameClient;
import javafx.scene.paint.Color;

/**
 *
 * @author Christian Prinz
 */
public class Lobby {

    private boolean host = true;
    private boolean ki = false;
    private static String[][] users;
    private String playerName;
    private String playerColor = (new Color(Math.random(), Math.random(), Math.random(), 1)).toString();
    private static GameClient playerClient;
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
    public static GameClient getPlayerClient() {
        return playerClient;
    }

    /**
     * @param aPlayerClient the playerClient to set
     */
    public static void setPlayerClient(GameClient aPlayerClient) {
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
     * @return the playerColor
     */
    public String getPlayerColor() {
        return playerColor;
    }

    /**
     * @param playerColor the playerColor to set
     */
    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
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
