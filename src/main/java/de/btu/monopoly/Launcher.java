/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly;

import de.btu.monopoly.menu.MainMenu;
import de.btu.monopoly.ui.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Markus Uhlig (markus.uhlig@b-tu.de)
 */
public class Launcher extends Application {

    public static final Logger LOGGER = Logger.getLogger(Launcher.class.getCanonicalName());

    @Override
    public void start(Stage stage) throws Exception {
        new SceneManager();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application. main() serves only as fallback in case the
     * application can not be launched through deployment artifacts, e.g., in IDEs with limited FX support. NetBeans ignores
     * main().
     *
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        configLoggers();
        if (GlobalSettings.RUN_IN_CONSOLE) initGame();
        else launch(args);
    }

    public static void initGame() {
        MainMenu mainMenu = new MainMenu();
        mainMenu.start();
    }

    private static void configLoggers() {
        try {
            LogManager.getLogManager().readConfiguration(Launcher.class.getResourceAsStream("/data/config/logging.properties"));
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "unable to configure loggers{0}", ex);
        }

//        Logger.getLogger(FieldDataParser.class.getCanonicalName()).setLevel(Level.OFF);
//        Logger.getLogger(Game.class.getCanonicalName()).setLevel(Level.OFF);
    }
}
