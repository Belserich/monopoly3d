/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import de.btu.monopoly.input.InputHandler;
import de.btu.monopoly.net.client.GameClient;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class MainMenu {

    public static final Logger LOGGER = Logger.getLogger(MainMenu.class.getCanonicalName());

    public void joinGame() {
        // Client starten und verbinden
        GameClient client = new GameClient(59687, 5000);
        LOGGER.fine("Geben sie die IP-Adresse des Servers ein");
        client.connect(InputHandler.askForString()); // while Schleife bis mit Server verbunden (evtl. begrenzte Versuche)

        // Lobby als Client joinen
        LobbyService.joinLobby(client, false);
    }
}
