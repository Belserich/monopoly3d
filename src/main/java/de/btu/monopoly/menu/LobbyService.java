/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.service.PlayerService;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.input.InputHandler;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.networkClasses.BroadcastUsersRequest;
import de.btu.monopoly.net.networkClasses.GamestartRequest;
import de.btu.monopoly.net.networkClasses.IamHostRequest;
import de.btu.monopoly.net.networkClasses.JoinRequest;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class LobbyService {

    private static final Logger LOGGER = Logger.getLogger(LobbyService.class.getCanonicalName());
    private static Lobby lobby;

    public static void joinLobbyAsHost(GameClient client) {
        LOGGER.setLevel(Level.FINER);
        // Spielernamen abfragen
        LOGGER.fine("Geben sie einen Spielernamen ein");
        String name = InputHandler.askForString();

        // Lobby init
        lobby = new Lobby();
        lobby.setPlayerName(name);
        lobby.setPlayerClient(client);
        lobby.setPlayerId(0);
        String[] users = new String[1];

        // Player hinzufuegen
        users[0] = name;
        lobby.setUsers(users);

        // Sich als Host auf Server vermerken
        client.sendTCP(new IamHostRequest());

        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.WARNING, "Fehler: {0}", ex);
            Thread.currentThread().interrupt();
        }

        LOGGER.fine("klick auf 1 zum Starten");
        InputHandler.getUserInput(1);
        gamestartRequest();
    }

    public static void joinLobby(GameClient client) {
        LOGGER.setLevel(Level.FINER);
        // Spielernamen abfragen
        LOGGER.fine("Geben sie einen Spielernamen ein");
        String name = InputHandler.askForString();

        // Lobby init
        lobby = new Lobby();
        lobby.setPlayerName(name);
        lobby.setPlayerClient(client);

        joinRequest();
    }

    private static void startGame() {
        Player[] players = getPlayersArray();
        Game controller = new Game(players, lobby.getPlayerClient());
        controller.init();
        controller.start();
    }

    private static void joinRequest() {
        LOGGER.finer(lobby.getPlayerName() + " sendet Request");
        JoinRequest req = new JoinRequest();
        req.setName(lobby.getPlayerName());
        lobby.getPlayerClient().sendTCP(req);
    }

    public static void joinResponse(String name) {
        LOGGER.finer("JoinResponse wird verarbeitet");

        // neuen Spieler dem Array hinzufügen
        String[] olsers = lobby.getUsers();
        String[] newser = new String[olsers.length + 1];
        System.arraycopy(olsers, 0, newser, 0, olsers.length);
        newser[newser.length - 1] = name;
        lobby.setUsers(newser);

        // playerID festlegen, falls man selbst neu hinzugefuegt wurde
        // neues Array broadcasten (lassen)
        BroadcastUsersRequest req = new BroadcastUsersRequest();
        req.setUsers(newser);
        lobby.getPlayerClient().sendTCP(req);
    }

    private static void gamestartRequest() {
        lobby.getPlayerClient().sendTCP(new GamestartRequest());
    }

    public static void gamestartResponse() {
        startGame();
    }

    public static void setNewUsers(String[] users) {
        LOGGER.finer("BroadcastUsersResponse wird verarbeitet");
        lobby.setUsers(users);

        // ID festlegen, falls man der zuletzt hinzugefuegte User ist
        if (lobby.getPlayerId() == -1) {
            lobby.setPlayerId(users.length - 1);
        }
        LOGGER.fine("aktuelle Spieler in der Lobby:");
        for (String user : lobby.getUsers()) {
            LOGGER.fine(" - " + user);
        }
        LOGGER.fine("Meine ID ist: " + lobby.getPlayerId());
    }

    private static Player[] getPlayersArray() {
        String[] users = lobby.getUsers();
        Player[] players = new Player[users.length];
        for (int i = 0; i < players.length; i++) {
            Player newPlayer = new Player(users[i], i, 1500);
            players[i] = newPlayer;
            if (lobby.getPlayerId() == i) {
                lobby.getPlayerClient().setPlayerOnClient(newPlayer);
            }
        }
        LOGGER.info("Spielreihenfolge wird ausgewürfelt");
        Player temp;
        Player rand;
        Random random = PlayerService.getRng();
        for (int i = 0; i < players.length; i++) {
            temp = players[i];
            int r = random.nextInt(players.length);
            rand = players[r];
            players[i] = rand;
            players[r] = temp;
        }
        return players;
    }
}
