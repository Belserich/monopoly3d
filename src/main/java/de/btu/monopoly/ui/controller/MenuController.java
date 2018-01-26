package de.btu.monopoly.ui.controller;

import de.btu.monopoly.menu.MainMenu;
import de.btu.monopoly.ui.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.util.Duration;

/**
 *
 * @author augat
 */
public class MenuController implements Initializable {

    @FXML
    private Button joinGameButton;

    @FXML
    private Button startGameButton;

    @FXML
    private Button ruleButton;

    @FXML
    private Button closeButton;

    @FXML
    private GridPane grid;

    @FXML
    private StackPane stackPane;

    @FXML
    private StackPane testPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //Image image = new Image(getClass().getResourceAsStream("/images/Main_Background.png"), 1200, 800, false, false);
        String image = " -fx-background-image: url('/images/Main_Background.png\') ;\n"
                + "    -fx-background-position: center;\n"
                + "    -fx-background-size: stretch;";
        grid.setStyle(image);
        //grid.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        stackPane.setBackground(new Background(new BackgroundImage(new Image(getClass().getResourceAsStream("/images/Lobby_Background.jpg")), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        startGameButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    startGameButtonAction(new ActionEvent());
                } catch (IOException ex) {
                    Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        joinGameButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    joinGameButtonAction(new ActionEvent());
                } catch (IOException ex) {
                    Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        closeButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                closeButtonAction(new ActionEvent());
            }
        });

        ruleButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    ruleButtonAction(new ActionEvent());
                } catch (IOException ex) {
                    Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        // Animation
        closeButton.setOpacity(0);
        joinGameButton.setOpacity(0);
        startGameButton.setOpacity(0);
        ruleButton.setOpacity(0);

        FadeTransition fadeInButton1
                = new FadeTransition(Duration.millis(1000), joinGameButton);
        fadeInButton1.setFromValue(0);
        fadeInButton1.setToValue(1);
        fadeInButton1.playFromStart();

        FadeTransition fadeInButton2
                = new FadeTransition(Duration.millis(1000), startGameButton);
        fadeInButton2.setFromValue(0);
        fadeInButton2.setToValue(1);
        fadeInButton2.playFromStart();

        FadeTransition fadeInButton3
                = new FadeTransition(Duration.millis(1000), ruleButton);
        fadeInButton3.setFromValue(0);
        fadeInButton3.setToValue(1);
        fadeInButton3.playFromStart();

        FadeTransition fadeInButton4
                = new FadeTransition(Duration.millis(1000), closeButton);
        fadeInButton4.setFromValue(0);
        fadeInButton4.setToValue(1);
        fadeInButton4.playFromStart();

    }

    // Button startGame
    @FXML
    private void startGameButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf startGame
        changeScene(new FXMLLoader(getClass().getResource("/fxml/start_game_scene.fxml")));

        // Server initialisieren
        MainMenu menu = new MainMenu();
        menu.createGame();

    }

    // Button joinGame
    @FXML
    private void joinGameButtonAction(ActionEvent event) throws IOException {
        // Wechselt die Scene auf joinGame
        changeScene(new FXMLLoader(getClass().getResource("/fxml/join_scene.fxml")));
    }

    // Button Einstellungen
    @FXML
    private void ruleButtonAction(ActionEvent event) throws IOException {

        changeScene(new FXMLLoader(getClass().getResource("/fxml/rules_scene.fxml")));
        // Wechselt die Scene auf Einstellungen

    }

    // Button Close
    @FXML
    private void closeButtonAction(ActionEvent event) {

        // Schließt die Anwendung
        Platform.exit();
        System.exit(0); //NOSONAR

    }

    private void changeScene(FXMLLoader loader) {
        FadeTransition fadeInButton1
                = new FadeTransition(Duration.millis(500), joinGameButton);
        fadeInButton1.setFromValue(1);
        fadeInButton1.setToValue(0);
        fadeInButton1.playFromStart();

        FadeTransition fadeInButton2
                = new FadeTransition(Duration.millis(500), startGameButton);
        fadeInButton2.setFromValue(1);
        fadeInButton2.setToValue(0);
        fadeInButton2.playFromStart();

        FadeTransition fadeInButton3
                = new FadeTransition(Duration.millis(500), ruleButton);
        fadeInButton3.setFromValue(1);
        fadeInButton3.setToValue(0);
        fadeInButton3.playFromStart();

        FadeTransition fadeInButton4
                = new FadeTransition(Duration.millis(500), closeButton);
        fadeInButton4.setFromValue(1);
        fadeInButton4.setToValue(0);
        fadeInButton4.playFromStart();
        fadeInButton4.setOnFinished((event) -> {
            try {
                SceneManager.changeScene(loader);
            } catch (IOException ex) {
                Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

}
