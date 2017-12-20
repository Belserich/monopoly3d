/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.service.NetworkService;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.input.IOService;
import de.btu.monopoly.input.InputHandler;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.networkClasses.Lobby.*;
import de.btu.monopoly.net.server.AuctionTable;
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
            LOGGER.log(Level.WARNING, "Eigene IP konnte nicht ausgelesen werden {0}", ex);
        }

        // Lobby init
        setLobby(new Lobby());
        lobby.setHost(host);
        lobby.setPlayerName(ipName);
        lobby.setPlayerClient(client);

        if (lobby.isHost()) {
            generateRandomSeed();
        }

        joinRequest();

        //TODO @GUI kommt weg:  (@Console Z.54-63)
        IOService.sleep(500);
        System.out.println("Name?:");
        changeName(InputHandler.askForString());

        if (lobby.isHost()) {
            addKI("Computergegner", 1);
            System.out.println("Eingabe machen für Spielstart");
            InputHandler.askForString();
            gamestartRequest();
        }
    }

    public static void addKI(String name, int kiLevel) {
        if (kiLevel < 1 || kiLevel > 3) {
            LOGGER.warning("kein gültiges KI Level eingegeben!");
        }
        else {
            AddKiRequest req = new AddKiRequest();
            req.setKiLevel(kiLevel);
            req.setName(name);
            lobby.getPlayerClient().sendTCP(req);
            NetworkService.logClientSendMessage(req, lobby.getPlayerName());
        }

    }

    public static void changeName(String name) {
        lobby.setPlayerName(name);
        changeUsernameRequest();
    }

    public static void startGame() {
        Game controller = new Game(generatePlayerArray(), lobby.getPlayerClient(), lobby.getRandomSeed());
        lobby.setController(controller);
        lobby.getPlayerClient().setGame(controller);

        controller.init();
        controller.start();
    }

    public static Player[] generatePlayerArray() {
        String[][] users = lobby.getUsers();
        Player[] players = new Player[users.length];
        for (int i = 0; i < users.length; i++) {
            int id = Integer.parseInt(users[i][0]);
            int kilvl = Integer.parseInt(users[i][3]);
            Player player = new Player(users[i][1], i, 1500);
            player.setKiLevel(kilvl);

            //wenn es sich um den aktuellen Spieler handelt
            if (id == lobby.getPlayerId()) {
                lobby.getPlayerClient().setPlayerOnClient(player);
            }
            players[i] = player;
        }
        // AuctionTable bekommt Player[]
        if (lobby.isHost()) {
            AuctionTable.setPlayers(players);
        }
        return players;
    }

    public static void generateRandomSeed() {
        long seed = new Random().nextLong();
        BroadcastRandomSeedRequest req = new BroadcastRandomSeedRequest();
        req.setSeed(seed);
        lobby.getPlayerClient().sendTCP(req);
        NetworkService.logClientSendMessage(req, lobby.getPlayerName());
    }

    public static Lobby getLobby() {
        return lobby;
    }

    // REQUESTS:__________________________________an LobbyTable
    public static void joinRequest() {
        JoinRequest req = new JoinRequest();
        req.setName(lobby.getPlayerName());
        NetworkService.logClientSendMessage(req, lobby.getPlayerName());
        lobby.getPlayerClient().sendTCP(req);

    }

    private static void changeUsernameRequest() {
        ChangeUsernameRequest req = new ChangeUsernameRequest();
        req.setUserName(lobby.getPlayerName());
        req.setUserId(lobby.getPlayerId());
        NetworkService.logClientSendMessage(req, lobby.getPlayerName());
        lobby.getPlayerClient().sendTCP(req);
    }

    public static void gamestartRequest() {
        GamestartRequest gaReq = new GamestartRequest();
        NetworkService.logClientSendMessage(gaReq, lobby.getPlayerName());
        lobby.getPlayerClient().sendTCP(gaReq);
    }

    //LISTENER:______________________________________________________________
    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof JoinImpossibleResponse) {
            NetworkService.logClientReceiveMessage(object, lobby.getPlayerName());
            LOGGER.info("Spiel wurde bereits gestartet");
            Thread.interrupted();
        }
        else if (object instanceof JoinResponse) {
            NetworkService.logClientReceiveMessage(object, lobby.getPlayerName());
            JoinResponse joinres = (JoinResponse) object;
            lobby.setPlayerId(joinres.getId());
            lobby.setRandomSeed(joinres.getSeed());
        }
        else if (object instanceof RefreshLobbyResponse) {
            NetworkService.logClientReceiveMessage(object, lobby.getPlayerName());
            RefreshLobbyResponse refres = (RefreshLobbyResponse) object;
            lobby.setUsers(refres.getUsers());

            //TODO kommt in GUI weg:
            System.out.println("Spieler in Lobby: (Meine ID: " + lobby.getPlayerId() + ")");
            for (int i = 0; i < lobby.getUsers().length; i++) {
                System.out.print("[" + i + "] ");
                System.out.println(lobby.getUsers()[i][1]);
            }
            if (lobby.isHost()) {
                System.out.println("Eingabe machen für Spielstart");
            }

        }
        else if (object instanceof GamestartResponse) {
            NetworkService.logClientReceiveMessage(object, lobby.getPlayerName());

            Thread t = new Thread() {
                @Override
                public void run() {
                    startGame();
                }
            };
            t.setName("Game");
            t.start();
        }

    }

    /**
     * @param aLobby the lobby to set
     */
    public static void setLobby(Lobby aLobby) {
        lobby = aLobby;
    }

}
