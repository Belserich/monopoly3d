/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.server.GameServer;
import de.btu.monopoly.ui.controller.StartGameController;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class MainMenu {

    protected static final int CREATE_GAME = 1;
    protected static final int JOIN_GAME = 2;
    
    public static final Logger LOGGER = Logger.getLogger(MainMenu.class.getCanonicalName());

    public void createGame() {
        // Server und Client starten und verbinden
        GameServer server = new GameServer(59687);
        server.startServer();
        GameClient client = new GameClient(59687, 5000);
        String localHost = System.getProperty("myapp.ip");
        client.connect(localHost);
        LOGGER.info("Die ServerIP ist " + server.getServerIP());

        StartGameController.client = client;
    }

    public void joinGame(String ip) {
        // Client starten und verbinden
        GameClient client = new GameClient(59687, 5000);
        client.connect(ip); // while Schleife bis mit Server verbunden (evtl. begrenzte Versuche)

        // Lobby als Client joinen
        LobbyService.joinLobby(client, false);
    }
}
