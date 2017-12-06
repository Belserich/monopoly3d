/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import com.esotericsoftware.minlog.Log;
import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.InputHandler;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.networkClasses.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class LobbyService {

    private static Lobby lobby;

    public static void joinLobbyAsHost(String name, GameClient client) {
        // Lobby init
        lobby = new Lobby();
        lobby.setPlayerName(name);
        lobby.setPlayerClient(client);
        String[] users = new String[1];

        // Player hinzufuegen
        users[0] = name;
        lobby.setUsers(users);

        // Sich als Host auf Server vermerken
        client.sendTCP(new IamHostRequest());

        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(LobbyService.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (lobby.isInLobby()) {
            System.out.println("klick auf 1 zum Starten");
            InputHandler.getUserInput(1);
            gamestartRequest();
        }
    }

    public static void joinLobby(String name, GameClient client) {
        // Lobby init
        lobby = new Lobby();
        lobby.setPlayerName(name);
        lobby.setPlayerClient(client);

        joinRequest();
        while (lobby.isInLobby()) {

        }
    }

    private static void startGame() {
        Player[] players = getPlayersArray();
        Game controller = new Game(players);
        controller.init();
        controller.start();
    }

    private static void joinRequest() {
        Log.info(lobby.getPlayerName() + " sendet Request");
        JoinRequest req = new JoinRequest();
        req.setName(lobby.getPlayerName());
        lobby.getPlayerClient().sendTCP(req);
    }

    private static void gamestartRequest() {
        lobby.getPlayerClient().sendTCP(new GamestartRequest());
    }

    public static void joinResponse(String name) {
        Log.info("JoinResponse wird verarbeitet");

        // neuen Spieler dem Array hinzufügen
        String[] olsers = lobby.getUsers();
        String[] newser = new String[olsers.length + 1];
        System.arraycopy(olsers, 0, newser, 0, olsers.length);
        newser[newser.length - 1] = name;
        lobby.setUsers(newser);

        // neues Array broadcasten (lassen)
        BroadcastUsersRequest req = new BroadcastUsersRequest();
        req.setUsers(newser);
        lobby.getPlayerClient().sendTCP(req);
    }

    public static void gamestartResponse() {
        startGame();
    }

    public static void setNewUsers(String[] users) {
        Log.info("BroadcastUsersResponse wird verarbeitet");
        lobby.setUsers(users);
        System.out.println("aktuelle Spieler in der Lobby:");
        for (String user : lobby.getUsers()) {
            System.out.println(" - " + user);
        }
    }

    private static Player[] getPlayersArray() {
        String[] users = lobby.getUsers();
        Player[] players = new Player[users.length];
        for (int i = 0; i < players.length; i++) {
            players[i] = new Player(users[i], i, 1500);
        }
        return players;
    }
}
