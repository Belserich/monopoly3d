/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import de.btu.monopoly.input.InputHandler;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.server.GameServer;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class MainMenu {

    public static final Logger LOGGER = Logger.getLogger(MainMenu.class.getCanonicalName());

    public void start() {
        System.out.println("HAUPTMENÜ\n[1] Spiel starten\n[2] Spiel beitreten");
        switch (InputHandler.getUserInput(2)) {
            case 1:
                createGame();
                break;
            case 2:
                joinGame();
                break;
        }
    }

    private void createGame() {
        // Server und Client starten und verbinden
        GameServer server = new GameServer(59687);
        server.startServer();
        GameClient client = new GameClient(59687, 5000);    // @fix redundant ->
        String localHost = "127.0.0.1";
        client.connect(localHost);
        System.out.println("Die ServerIP ist " + server.getServerIP());

        // Lobby als Host joinen
        LobbyService.joinLobbyAsHost(client);
    }

    private void joinGame() {
        // Client starten und verbinden
        GameClient client = new GameClient(59687, 5000);
        System.out.println("Geben sie die IP-Adresse des Servers ein");
        client.connect(InputHandler.askForString()); // while Schleife bis mit Server verbunden (evtl. begrenzte Versuche)

        // Lobby als Client joinen
        LobbyService.joinLobby(client);
    }
}
