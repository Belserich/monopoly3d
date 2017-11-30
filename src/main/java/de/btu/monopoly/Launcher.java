/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly;

import de.btu.monopoly.controller.GameController;
import de.btu.monopoly.data.parser.GameBoardParser;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Markus Uhlig (markus.uhlig@b-tu.de)
 */
public class Launcher extends Application {

    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setTitle("Monopoly");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application. main() serves only as fallback in case the
     * application can not be launched through deployment artifacts, e.g., in IDEs with limited FX support. NetBeans ignores
     * main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        configLoggers();
        initGame();
        launch(args);
    }

    private static void initGame() {
        GameController controller = new GameController(2);
        controller.init();
    }

    private static void configLoggers() {
        try {
            LogManager.getLogManager().readConfiguration(Launcher.class.getResourceAsStream("/data/config/logging.properties"));
        } catch (IOException ex) {
            System.err.println("Couldn't configure loggers!");
            ex.printStackTrace();
        }

        Logger.getLogger(GameBoardParser.class.getCanonicalName()).setLevel(Level.OFF);
        Logger.getLogger(GameController.class.getCanonicalName()).setLevel(Level.OFF);
    }
}
