/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly;

import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.service.AuctionService;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.menu.MainMenu;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.ui.GameSceneManager;
import de.btu.monopoly.ui.MenuSceneManager;
import de.btu.monopoly.ui.util.Assets;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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
        Assets.loadGeneral();
    }
    
    private static void initGame() {
        if (GlobalSettings.RUN_IN_CONSOLE) {
            MainMenu mainMenu = new MainMenu();
            mainMenu.start();
        }
        else launch();
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        
        if (!GlobalSettings.FX_3D_TEST) {
            MenuSceneManager menuMan = new MenuSceneManager();
            Global.ref().setMenuSceneManager(menuMan);
        }
        else {
            Assets.load();
            Player[] players = new Player[]{ new Player("Peti", 0, 1500), new Player("Tom", 1, 1500) };
            players[1].setAiLevel(1);
            
            GameClient client = new GameClient(55556, 1000);
            
            Lobby.setPlayerClient(client);
            client.setPlayerOnClient(players[0]);
            AuctionService.initAuction(players, client);
    
            Game game = new Game(client, players, 1);
            client.setGame(game);
            
            Global.ref().setGame(game);
            Global.ref().setClient(client);
            
            GameSceneManager man = new GameSceneManager(game.getBoard());
            Global.ref().setGameSceneManager(man);
    
            stage.setScene(man.getScene());
            stage.show();
            stage.setOnCloseRequest(ev -> System.exit(0));
    
            Thread thread = new Thread(() -> {
                try { game.start(); } catch (InterruptedException ex) { ex.printStackTrace(); }
            });
            thread.start();
        }
    }
}
