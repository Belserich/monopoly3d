/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import de.btu.monopoly.GlobalSettings;
import de.btu.monopoly.core.service.IOService;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.server.GameServer;
import de.btu.monopoly.ui.controller.StartGameController;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class MainMenu {

    protected static final int CREATE_GAME = 1;
    protected static final int JOIN_GAME = 2;
    private final int PORT = 55555;

    public static final Logger LOGGER = Logger.getLogger(MainMenu.class.getCanonicalName());

    public void start() { //@GUI wird nicht verwendet
        LOGGER.setLevel(Level.FINER);
        LOGGER.fine("HAUPTMENÜ\n[1] Spiel starten\n[2] Spiel beitreten");
        int choice = IOService.getUserInput(2);
        if (choice == CREATE_GAME) {
            createGame();
        }
        else if (choice == JOIN_GAME) {
            joinGame("localhost");
        }
    }

    public void createGame() {
        // Server und Client starten und verbinden
        GameServer server = new GameServer(PORT);
        server.startServer();
        GameClient client = new GameClient(PORT, 5000);
        String localHost = System.getProperty("myapp.ip");
        client.connect(localHost);
        LOGGER.info("Die ServerIP ist " + server.getServerIP());

        if (GlobalSettings.RUN_IN_CONSOLE) {
            LobbyService.joinLobby(client, true);
        }
        else {
            StartGameController.setClient(client);
        }
    }

    public void joinGame(String ip) {
        // Client starten und verbinden
        GameClient client = new GameClient(PORT, 5000);
        if (GlobalSettings.RUN_IN_CONSOLE) {
            LOGGER.fine("Geben sie die IP-Adresse des Servers ein");
            client.connect(IOService.askForString());
        }
        else {
            client.connect(ip); // while Schleife bis mit Server verbunden (evtl. begrenzte Versuche)
        }

        // lobby als Client joinen
        LobbyService.joinLobby(client, false);
    }
}
