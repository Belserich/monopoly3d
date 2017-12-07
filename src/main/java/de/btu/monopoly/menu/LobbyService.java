/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import com.esotericsoftware.minlog.Log;
import de.btu.monopoly.core.Game;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.input.InputHandler;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.networkClasses.GamestartRequest;
import de.btu.monopoly.net.networkClasses.JoinRequest;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class LobbyService {

    private static Lobby lobby;

    public static void joinLobbyAsHost(String name, GameClient client) { // @fix redundant ->
        lobby = new Lobby();
        lobby.setPlayerName(name);
        lobby.setPlayerClient(client);

        joinRequest();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(LobbyService.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (lobby.isInLobby()) {
            System.out.println("aktuelle Spieler in der Lobby:");
            for (Player player : lobby.getPlayers()) {
                System.out.println(" - " + player.getName());
            }
            System.out.println("\nAktionen:\n[1] aktualisieren\n[2] Spiel starten");
            switch (InputHandler.getUserInput(2)) {
                case 1:
                    break;
                case 2:
                    gamestartRequest();
                    break;
            }
        }
        startGame();
    }                                                                   // <- @fix redundant

    public static void joinLobby(String name, GameClient client) {
        lobby = new Lobby();
        lobby.setPlayerName(name);
        lobby.setPlayerClient(client);

        joinRequest();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(LobbyService.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (lobby.isInLobby()) {

        }
        startGame();
    }

    private static void startGame() {
        Game controller = new Game(lobby.getPlayers());
        controller.init();
        controller.start();
    }

    private static void joinRequest() {
        JoinRequest req = new JoinRequest();
        req.setName(lobby.getPlayerName());
        lobby.getPlayerClient().sendTCP(req);
    }

    private static void gamestartRequest() {
        lobby.getPlayerClient().sendTCP(new GamestartRequest());
    }

    public static void joinResponse(String name, int id) {
        Log.info("JoinResponse wird verarbeitet");
        //Player erzeugen
        Player player = new Player(name, id, 1500);

        //Players[] aktualisieren
        Player[] oldPlayers = lobby.getPlayers();
        Player[] newPlayers;
        if (oldPlayers != null) {

            newPlayers = new Player[oldPlayers.length + 1];
            for (int i = 0; i < lobby.getPlayers().length; i++) {
                newPlayers[i] = oldPlayers[i];
            }
            newPlayers[oldPlayers.length] = player;
        } else {
            newPlayers = new Player[1];
            newPlayers[0] = player;
        }
        lobby.setPlayers(newPlayers);
        Log.info("JoinResponse verarbeitet" + lobby.getPlayers().length);

    }

    public static void gamestartResponse() {
        lobby.setInLobby(false);
    }
}
