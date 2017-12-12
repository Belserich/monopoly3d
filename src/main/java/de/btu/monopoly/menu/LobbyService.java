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
import de.btu.monopoly.input.InputHandler;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.networkClasses.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
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

        if (lobby.isHost()) {
            generateRandomSeed();
        }

        joinRequest();

        //TODO kommt in GUI weg:
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            LOGGER.warning("Fehler: " + ex);
            Thread.currentThread().interrupt();
        }
        System.out.println("Name?:");
        changeName(InputHandler.askForString());

        if (lobby.isHost()) {
//            addKI("Gegner");
            System.out.println("Eingabe machen für Spielstart");
            InputHandler.askForString();
            gamestartRequest();
        }
    }

    public static void addKI(String name) { //TODO Funktioniert noch nicht (Threads)
        Thread t = new Thread() {
            @Override
            public void run() {
                kiLobby(name);
            }
        };
        t.setName("KI");
        t.start();
    }

    private static void kiLobby(String name) {
        // Client starten und verbinden
        GameClient client = new GameClient(59687, 5000);
        String localHost = System.getProperty("myapp.ip");
        client.connect(localHost);

        // Lobby init
        lobby = new Lobby();
        lobby.setHost(false);
        lobby.setKi(true);
        lobby.setPlayerName(name);
        lobby.setPlayerClient(client);

        joinRequest();

    }

    public static void changeName(String name) {
        lobby.setPlayerName(name);
        changeUsernameRequest();
    }

    public static void startGame() {//TODO
        Game controller = new Game(generatePlayerArray(), lobby.getPlayerClient(), lobby.getRandomSeed());
        controller.init();
        controller.start();
    }

    private static Player[] generatePlayerArray() {
        String[][] users = lobby.getUsers();
        Player[] players = new Player[users.length];
        for (int i = 0; i < users.length; i++) {
            int id = Integer.parseInt(users[i][0]);
            Player player = new Player(users[i][1], id, 1500);

            //wenn es sich um den aktuellen Spieler handelt
            if (id == lobby.getPlayerId()) {
                // wenn er eine KI ist
                if (lobby.isKi()) {
                    player.setKi(true);
                }
                lobby.getPlayerClient().setPlayerOnClient(player);
            }
            System.out.println(player.isKi());
            players[i] = player;
        }

        return players;
    }

    private static void generateRandomSeed() {
        long seed = new Random().nextLong();
        BroadcastRandomSeedRequest req = new BroadcastRandomSeedRequest();
        req.setSeed(seed);
        lobby.getPlayerClient().sendTCP(req);
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
        } else if (object instanceof JoinImpossibleResponse) {
            LOGGER.info("Spiel wurde bereits gestartet");
            Thread.interrupted();
        } else if (object instanceof JoinResponse) {
            LOGGER.finer("JoinResponse wird verarbeitet");
            JoinResponse joinres = (JoinResponse) object;
            lobby.setPlayerId(joinres.getId());
            lobby.setRandomSeed(joinres.getSeed());
        } else if (object instanceof RefreshLobbyResponse) {
            LOGGER.finer("RefreshLobbyResponse wird verarbeitet");
            RefreshLobbyResponse refres = (RefreshLobbyResponse) object;
            lobby.setUsers(refres.getUsers());

            //TODO kommt in GUI weg:
            System.out.println("Spieler in Lobby: (Meine ID: " + lobby.getPlayerId() + ")");
            for (int i = 0; i < lobby.getUsers().length; i++) {
                System.out.print("[" + lobby.getUsers()[i][0] + "] ");
                System.out.println(lobby.getUsers()[i][1]);
            }
            if (lobby.isHost()) {
                System.out.println("Eingabe machen für Spielstart");
            }

        } else if (object instanceof GamestartResponse) { //TODO elegantere Loesung
            LOGGER.finer("GamestartResponse wird verarbeitet");

            Thread t = new Thread() {
                @Override
                public void run() {
                    startGame();
                }
            };
            t.setName("Game");
            t.start();
        } else {
            LOGGER.log(Level.WARNING, "Falsches packet angekommen! {0}", object.getClass());
        }
    }

}
