/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.btu.monopoly.Global;
import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.service.IOService;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.data.lobby.*;
import de.btu.monopoly.net.server.AuctionTable;
import de.btu.monopoly.util.Assets;
import javafx.scene.paint.Color;

import java.io.IOException;
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
    private static final boolean isRunAsTest = Global.RUN_AS_TEST;
    private static final boolean isRunInConsole = Global.RUN_IN_CONSOLE;
    private static Lobby lobby;

    /**
     * Methode die aufgerufen wird um der lobby beizutreten
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
            LOGGER.log(Level.WARNING, "Eigene IP konnte nicht ausgelesen werden {0}", ex);
        }

        // lobby init
        setLobby(new Lobby());
        lobby.setHost(host);
        lobby.setPlayerName(ipName);
        lobby.setPlayerClient(client);

        if (lobby.isHost()) {
            generateRandomSeed();
        }

        joinRequest();

        IOService.sleep(500);
        if (isRunInConsole && !isRunAsTest) { // nur fuer @Console
            System.out.println("Name?:");
            changeName(IOService.askForString());

            if (lobby.isHost()) {
                addKI("Computergegner", 1);
                System.out.println("Eingabe machen f??r Spielstart");
                IOService.askForString();
                gamestartRequest();
            }
        }
    }

    /**
     * Fuegt der lobby einen Computerspieler hinzu
     *
     * @param name des Computerspielers
     * @param kiLevel des Computerspielers [1,3]
     */
    public static void addKI(String name, int kiLevel) {
        if (kiLevel < 1 || kiLevel > 2) {
            LOGGER.warning("kein g??ltiges KI Level eingegeben!");
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

    public static void deleteUser(int id) {
        deleteUserRequest(id);
    }

    /**
     * erstellt eine Gameinstanz und startet das Spiel
     */
    public static void startGame() throws InterruptedException {
        lobby.getController().start();
    }

    private static void initGame() {
        Game controller = new Game(lobby.getPlayerClient(), generatePlayerArray(), lobby.getRandomSeed());
        lobby.setController(controller);
        lobby.getPlayerClient().setGame(controller);

        Global.ref().setGame(controller);
    }

    /**
     * erzeugt aus dem users[][] ein Player[], welches fuer das Spiel benoetigt wird
     *
     * @return Player[] fuer den Parameter der Game Instanz
     */
    public static Player[] generatePlayerArray() {
        String[][] users = lobby.getUsers();
        Player[] players = new Player[users.length];
        for (int i = 0; i < users.length; i++) {
            int id = Integer.parseInt(users[i][0]);
            int kilvl = Integer.parseInt(users[i][3]);
            Player player = new Player(users[i][1], i, Assets.START_MONEY);
            player.setAiLevel(kilvl);
            player.setColor(users[i][4]);

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

    /**
     * Generiert einen RandomSeed
     */
    private static void generateRandomSeed() {
        long seed = Global.ref().getRandomSeed();
        setRandomSeed(seed);
    }

    /**
     * Setzt den Seed, welcher fur das Spiel benoetigt wird
     */
    public static void setRandomSeed(long seed) {

        BroadcastRandomSeedRequest req = new BroadcastRandomSeedRequest();
        req.setSeed(seed);
        lobby.getPlayerClient().sendTCP(req);
    }

    public static Lobby getLobby() {
        return lobby;
    }

    // REQUESTS:__________________________________an LobbyTable
    public static void joinRequest() {
        JoinRequest req = new JoinRequest();
        req.setName(lobby.getPlayerName());
        lobby.getPlayerClient().sendTCP(req);

    }

    private static void changeUsernameRequest(String name, int id) {
        ChangeUsernameRequest req = new ChangeUsernameRequest();
        req.setUserName(name);
        req.setUserId(id);
        lobby.getPlayerClient().sendTCP(req);
    }

    private static void changeColorRequest(String colorString, int id) {
        ChangeUsercolorRequest req = new ChangeUsercolorRequest();
        req.setUserColor(colorString);
        req.setUserId(id);
        lobby.getPlayerClient().sendTCP(req);
    }

    private static void deleteUserRequest(int id) {
        DeleteUserRequest req = new DeleteUserRequest();
        req.setId(id);
        lobby.getPlayerClient().sendTCP(req);
    }

    public static void gamestartRequest() {
        GamestartRequest gaReq = new GamestartRequest();
        lobby.getPlayerClient().sendTCP(gaReq);
    }

    //LISTENER:______________________________________________________________
    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof JoinImpossibleResponse) {
            LOGGER.info("Spiel wurde bereits gestartet");
            Thread.interrupted();
        }
        else if (object instanceof JoinResponse) {
            JoinResponse joinres = (JoinResponse) object;
            lobby.setPlayerId(joinres.getId());
            lobby.setRandomSeed(joinres.getSeed());
        }
        else if (object instanceof RefreshLobbyResponse) {
            RefreshLobbyResponse refres = (RefreshLobbyResponse) object;
            lobby.setUsers(refres.getUsers());

            if (!Global.RUN_AS_TEST && !Global.RUN_IN_CONSOLE) {
                try {
                    // Lobby updaten
                    Global.ref().getMenuSceneManager().updateLobby();
                    // Kann sp??ter entfernt werden wenn Farben implementiert sind
                    Global.ref().getMenuSceneManager().updateLobbyColors();
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.WARNING, "Lobby konnte nicht geupdated werden{0}", ex);
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("Spieler in Lobby: (Meine ID: " + lobby.getPlayerId() + ")");
            for (int i = 0; i < lobby.getUsers().length; i++) {
                System.out.print("[" + i + "] ");
                System.out.println(lobby.getUsers()[i][1]);
            }
            if (lobby.isHost()) {
                System.out.println("Eingabe machen f??r Spielstart");
            }

        }
        else if (object instanceof GamestartResponse) {

            initGame();
            // Scene bei anderen Spielern ??ffnen
            try {
                Global.ref().getMenuSceneManager().openGameLayout(lobby);
                IOService.sleep(2000);

            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Scene konnte nicht geladen werden{0}", ex);
            }

            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        startGame();
                    } catch (InterruptedException ex) {
                        LOGGER.log(Level.WARNING, "Scene konnte nicht geladen werden{0}", ex);
                        Thread.currentThread().interrupt();
                    }
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
