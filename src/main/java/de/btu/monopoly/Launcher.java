/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly;

import de.btu.monopoly.core.Game;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.menu.MainMenu;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.server.AuctionTable;
import de.btu.monopoly.net.server.GameServer;
import de.btu.monopoly.ui.GameSceneManager;
import de.btu.monopoly.ui.MenuSceneManager;
import de.btu.monopoly.util.Assets;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * @author Markus Uhlig (markus.uhlig@b-tu.de)
 */
public class Launcher extends Application {

    public static final Logger LOGGER = Logger.getLogger(Launcher.class.getCanonicalName());

    public static void main(String[] args) throws Exception {
        configureLoggers();
        loadResources();
        initGame();
    }

    private static void configureLoggers() throws IOException {
        LogManager.getLogManager().readConfiguration(Launcher.class.getResourceAsStream("/data/config/logging.properties"));
        //        Logger.getLogger(FieldDataParser.class.getCanonicalName()).setLevel(Level.OFF);
        //        Logger.getLogger(Game.class.getCanonicalName()).setLevel(Level.OFF);
    }

    private static void loadResources() {
        Assets.loadData();
        Assets.loadFx();
    }

    private static void initGame() {
        if (Global.RUN_IN_CONSOLE) {
            MainMenu mainMenu = new MainMenu();
            mainMenu.start();
        }
        else {
            launch();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

        if (!Global.FX_3D_TEST) {
            MenuSceneManager menuMan = new MenuSceneManager();
            Global.ref().setMenuSceneManager(menuMan);
        }
        else {

            Player[] players = new Player[]{
                new Player("Patrick", 0, 1500, "#4286f4ff"),
                new Player("Christian", 1, 1500, "#f44242ff")
            };
            players[1].setAiLevel(2);

            GameClient client = new GameClient(7777, 1000);

            Lobby.setPlayerClient(client);
            client.setPlayerOnClient(players[0]);

            Global.ref().setClient(client);
            Game game = new Game(client, players, 2);

            client.setGame(game);

            Global.ref().setGame(game);

            GameSceneManager man = new GameSceneManager(game.getBoard());
            Global.ref().setGameSceneManager(man);

            GameServer server = new GameServer(7777);
            server.startServer();
            client.connect("localhost");
            AuctionTable.setPlayers(players);

            stage.setScene(man.getScene());
            stage.show();
            stage.setOnCloseRequest(ev -> System.exit(0));

            Thread thread = new Thread(game::start);
            thread.start();
        }
    }
}
