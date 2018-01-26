/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly;

import de.btu.monopoly.menu.MainMenu;
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
        MenuSceneManager menuMan = new MenuSceneManager();
        Global.ref().setMenuSceneManager(menuMan);
    }
}
