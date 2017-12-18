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
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

/**
 *
 * @author Christian Prinz
 */
public class LobbyService extends Listener {

    private static final Logger LOGGER = Logger.getLogger(LobbyService.class.getCanonicalName());
    private static Lobby lobby;

    /**
     * Methode die aufgerufen wird um der Lobby beizutreten
     *
     * @param client des Spielers der beitreten will
     * @param host gibt an, ob der Spieler der Host ist
     */
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
    }

    /**
     * Fuegt der Lobby einen Computerspieler hinzu
     *
     * @param name des Computerspielers
     * @param kiLevel des Computerspielers [1,3]
     */
    public static void addKI(String name, int kiLevel) {
        if (kiLevel < 1 || kiLevel > 3) {
            LOGGER.warning("kein gültiges KI Level eingegeben!");
        }
        else {
            AddKiRequest req = new AddKiRequest();
            req.setKiLevel(kiLevel);
            req.setName(name);
            lobby.getPlayerClient().sendTCP(req);
        }

    }

    /**
     * aendert den Namen eines Spielers (nur fuer KI vorgesehen)
     *
     * @param name neuer Name des Spielers (KI)
     * @param id die der Spieler besitzt (users[i][0])
     */
    public static void changeName(String name, int id) {
        changeUsernameRequest(name, id);
    }

    /**
     * aendert den Namen des Spielers
     *
     * @param name neuer Name des Spielers
     */
    public static void changeName(String name) {
        lobby.setPlayerName(name);
        changeName(name, lobby.getPlayerId());
    }

    /**
     * aendert die Farbe eines Spielers (nur fuer KI vorgesehen)
     *
     * @param color neue Farbe des Spielers
     * @param id die der Spieler besitzt (users[i][0])
     */
    public static void changeColor(Color color, int id) {
        String colString = color.toString();
        lobby.setPlayerColor(colString);
        changeColorRequest(colString, id);
    }

    /**
     * aendert die Farbe des Spielers
     *
     * @param color neue Farbe des Spielers
     */
    public static void changeColor(Color color) {
        changeColor(color, lobby.getPlayerId());
    }

    /**
     * erstellt eine Gameinstanz und startet das Spiel
     */
    public static void startGame() {

        Game controller = new Game(generatePlayerArray(), lobby.getPlayerClient(), lobby.getRandomSeed());
        lobby.getPlayerClient().setGame(controller);
        controller.init();
        controller.start();
    }

    /**
     * erzeugt aus dem users[][] ein Player[], welches fuer das Spiel benoetigt
     * wird
     *
     * @return Player[] fuer den Parameter der Game Instanz
     */
    private static Player[] generatePlayerArray() {
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
        return players;
    }

    /**
     * erzeugt den Randomseed, welcher fur das Spiel benoetigt wird
     */
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

    private static void changeUsernameRequest(String name, int id) {
        LOGGER.finer(lobby.getPlayerName() + " sendet ChangeUsernameRequest");
        ChangeUsernameRequest req = new ChangeUsernameRequest();
        req.setUserName(name);
        req.setUserId(id);
        lobby.getPlayerClient().sendTCP(req);
    }

    private static void changeColorRequest(String colorString, int id) {
        LOGGER.finer(lobby.getPlayerName() + " sendet ChangeUsercolorRequest");
        ChangeUsercolorRequest req = new ChangeUsercolorRequest();
        req.setUserColor(colorString);
        req.setUserId(id);
        lobby.getPlayerClient().sendTCP(req);
    }

    public static void gamestartRequest() {
        LOGGER.finer(lobby.getPlayerName() + " sendet GamestartRequest");
        lobby.getPlayerClient().sendTCP(new GamestartRequest());
    }

    //LISTENER:______________________________________________________________
    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof FrameworkMessage) {
            // TODO LOG
        }
        else if (object instanceof JoinImpossibleResponse) {
            LOGGER.info("Spiel wurde bereits gestartet");
            Thread.interrupted();
        }
        else if (object instanceof JoinResponse) {
            LOGGER.finer("JoinResponse wird verarbeitet");
            JoinResponse joinres = (JoinResponse) object;
            lobby.setPlayerId(joinres.getId());
            lobby.setRandomSeed(joinres.getSeed());
        }
        else if (object instanceof RefreshLobbyResponse) {
            LOGGER.finer("RefreshLobbyResponse wird verarbeitet");
            RefreshLobbyResponse refres = (RefreshLobbyResponse) object;
            lobby.setUsers(refres.getUsers());

            try {
                // Lobby updaten
                SceneManager.updateLobby();
                // Kann später entfernt werden wenn Farben implementiert sind
                SceneManager.updateLobbyColors();
            } catch (InterruptedException ex) {
                LOGGER.warning("Lobby konnte nicht geupdated werden" + ex);
                Thread.interrupted();
            }

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
            LOGGER.finer("GamestartResponse wird verarbeitet");

            // Scene bei anderen Spielern öffnen
            try {
                SceneManager.openGameLayout();
                System.out.print("Testitestitest");
            } catch (IOException ex) {
                LOGGER.warning("Scene konnte nicht geladen werden" + ex);
            }

            Thread t = new Thread() {
                @Override
                public void run() {
                    startGame();
                }
            };
            t.setName("Game");
            t.start();
        }
        else {
            LOGGER.log(Level.WARNING, "Falsches packet angekommen! {0}", object.getClass());
        }
    }

}
