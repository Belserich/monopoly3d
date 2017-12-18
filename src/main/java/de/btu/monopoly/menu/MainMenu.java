/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import de.btu.monopoly.input.InputHandler;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.server.GameServer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class MainMenu {

    protected static final int CREATE_GAME = 1;
    protected static final int JOIN_GAME = 2;

    public static final Logger LOGGER = Logger.getLogger(MainMenu.class.getCanonicalName());

    public void start() { //@GUI wird nicht verwendet
        LOGGER.setLevel(Level.FINER);
        LOGGER.fine("HAUPTMENÜ\n[1] Spiel starten\n[2] Spiel beitreten");
        int choice = InputHandler.getUserInput(2);
        if (choice == CREATE_GAME) {
            createGame();
        } else if (choice == JOIN_GAME) {
            joinGame();
        }
    }

    public void createGame() {
        // Server und Client starten und verbinden
        GameServer server = new GameServer(59687);
        server.startServer();
        GameClient client = new GameClient(59687, 5000);
        String localHost = System.getProperty("myapp.ip");
        client.connect(localHost);
        LOGGER.info("Die ServerIP ist " + server.getServerIP());

        // Lobby als Host joinen @GUI kommt weg
        LobbyService.joinLobby(client, true);
    }

    public void joinGame() {
        // Client starten und verbinden
        GameClient client = new GameClient(59687, 5000);
        LOGGER.fine("Geben sie die IP-Adresse des Servers ein");
        client.connect(InputHandler.askForString()); // while Schleife bis mit Server verbunden (evtl. begrenzte Versuche)

        // Lobby als Client joinen
        LobbyService.joinLobby(client, false);
    }
}
