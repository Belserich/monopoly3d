/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.InputHandler;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.networkClasses.GamestartRequest;
import de.btu.monopoly.net.networkClasses.JoinRequest;

/**
 *
 * @author Christian Prinz
 */
public class LobbyService {

    private static Lobby lobby;

    public static void joinLobbyAsHost(String name, GameClient client) {
        lobby = new Lobby();
        lobby.setPlayerName(name);
        lobby.setPlayerClient(client);

        joinRequest();

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
    }

    public static void joinLobby(String name, GameClient client) {
        lobby = new Lobby();
        lobby.setPlayerName(name);
        lobby.setPlayerClient(client);

        joinRequest();

        while (lobby.isInLobby()) {
            System.out.println("aktuelle Spieler in der Lobby:");
            for (Player player : lobby.getPlayers()) {
                System.out.println(" - " + player.getName());
            }
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

    public static void joinResponse(Player[] players) {
        lobby.setPlayers(players);
    }

    public static void gamestartResponse() {
        lobby.setInLobby(false);
    }
}
