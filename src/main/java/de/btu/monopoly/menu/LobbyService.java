/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import de.btu.monopoly.core.Game;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.networkClasses.*;
import de.btu.monopoly.ui.SceneManager;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class LobbyService extends Listener {

    private static final Logger LOGGER = Logger.getLogger(LobbyService.class.getCanonicalName());
    private static Lobby lobby;

    public static void joinLobby(GameClient client, boolean host) {
        LOGGER.setLevel(Level.FINER);
        // Spielernamen voreintragen
        String ipName = "";
        try {
            ipName = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            LOGGER.warning("Eigene IP konnte nicht ausgelesen werden " + ex);
        }

        // Lobby init
        lobby = new Lobby();
        lobby.setHost(host);
        lobby.setPlayerName(ipName);
        lobby.setPlayerClient(client);

        joinRequest();

        // Methode für Spielstart
        // Muss dann in den Button
        //gamestartRequest();
    }

    public static void changeName(String name) {
        lobby.setPlayerName(name);
        changeUsernameRequest();
    }

    public static void startGame() {//TODO
        Game controller = new Game(generatePlayerArray(), lobby.getPlayerClient());
        controller.init();
        controller.start();
    }

    private static Player[] generatePlayerArray() {
        String[][] users = lobby.getUsers();
        Player[] players = new Player[users.length];
        for (int i = 0; i < users.length; i++) {
            int id = Integer.parseInt(users[i][0]);
            Player player = new Player(users[i][1], id, 1500);
            players[i] = player;

            //PlayerOnClient setzen
            if (id == lobby.getPlayerId()) {
                lobby.getPlayerClient().setPlayerOnClient(player);
            }
        }

        return players;
    }

    // REQUESTS:__________________________________an LobbyTable
    private static void joinRequest() {
        LOGGER.finer(lobby.getPlayerName() + " sendet JoinRequest");
        JoinRequest req = new JoinRequest();
        req.setName(lobby.getPlayerName());
        lobby.getPlayerClient().sendTCP(req);

    }

    private static void changeUsernameRequest() {
        LOGGER.finer(lobby.getPlayerName() + " sendet RefreshRequest");
        ChangeUsernameRequest req = new ChangeUsernameRequest();
        req.setUserName(lobby.getPlayerName());
        req.setUserId(lobby.getPlayerId());
        lobby.getPlayerClient().sendTCP(req);
    }

    private static void gamestartRequest() {
        LOGGER.finer(lobby.getPlayerName() + " sendet GamestartRequest");
        lobby.getPlayerClient().sendTCP(new GamestartRequest());
    }

    //LISTENER:______________________________________________________________
    public void received(Connection connection, Object object) {

        if (object instanceof FrameworkMessage) {
            // TODO LOG
        } else if (object instanceof JoinResponse) {
            LOGGER.finer("JoinResponse wird verarbeitet");
            JoinResponse joinres = (JoinResponse) object;
            lobby.setPlayerId(joinres.getId());
        } else if (object instanceof RefreshLobbyResponse) {
            LOGGER.finer("RefreshLobbyResponse wird verarbeitet");
            RefreshLobbyResponse refres = (RefreshLobbyResponse) object;
            lobby.setUsers(refres.getUsers());

            try {
                // Lobby updaten
                SceneManager.updateLobby();
            } catch (InterruptedException ex) {
                Logger.getLogger(LobbyService.class.getName()).log(Level.SEVERE, null, ex);
            }

            //TODO kommt in GUI weg:
            System.out.println("Spieler in Lobby");
            for (int i = 0; i < lobby.getUsers().length; i++) {
                System.out.println("- " + lobby.getUsers()[i][1]);
            }

        } else if (object instanceof GamestartResponse) { //TODO elegantere Loesung
            LOGGER.finer("GamestartResponse wird verarbeitet");
//            startGame();
            Thread t = new Thread() {
                @Override
                public void run() {
                    startGame();
                }
            };
            t.start();
        } else {
            LOGGER.log(Level.WARNING, "Falsches packet angekommen! {0}", object.getClass());
        }
    }

}
