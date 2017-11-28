/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly;

import de.btu.monopoly.controller.GameController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author Markus Uhlig (markus.uhlig@b-tu.de)
 */
public class Launcher extends Application {
    
    private static final String LOGGER_CONFIG_PATH = "data/config/logging.properties";
    
    /**
     * The main() method is ignored in correctly deployed JavaFX application. main() serves only as fallback in case the
     * application can not be launched through deployment artifacts, e.g., in IDEs with limited FX support. NetBeans ignores
     * main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        manageLogging();
        initGame();
        launch(args);
    }
    
    private static void initGame() {
        GameController ctrl = new GameController(2);
        ctrl.init();
        ctrl.startGame();
    }
    
    private static void manageLogging() {
        try {
            LogManager.getLogManager().readConfiguration(
                    Launcher.class.getClassLoader().getResourceAsStream(LOGGER_CONFIG_PATH));
        }
        catch (IOException ex) {
            System.err.println("Ooops! Logger configuration couldn't be read. Make sure the game files aren't modified!");
        }
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("Monopoly");
        stage.setScene(scene);
        stage.show();
    }
}
