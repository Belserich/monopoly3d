package de.btu.monopoly.ui.controller;

import de.btu.monopoly.menu.MainMenu;
import de.btu.monopoly.ui.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 *
 * @author augat
 */
public class MenuController implements Initializable {

    @FXML
    private Button joinGameButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    // Button startGame
    @FXML
    private void startGameButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf startGame
        SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/startGame.fxml")));

        MainMenu menu = new MainMenu();
        menu.createGame();

    }

    // Button joinGame
    @FXML
    private void joinGameButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf joinGame
        SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/joinGame.fxml")));
    }

    // Button Close
    @FXML
    private void closeButtonAction(ActionEvent event) {

        // Schließt die Anwendung
        Platform.exit();

    }
}
