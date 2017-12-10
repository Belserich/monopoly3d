package de.btu.monopoly.ui.controller;

import de.btu.monopoly.menu.MainMenu;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 *
 * @author augat
 */
public class MenuController implements Initializable {

    @FXML
    private Button joinGameButton;

    MainMenu menu = new MainMenu();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    // Button startGame
    @FXML
    private void startGameButtonAction(ActionEvent event) throws IOException {

    }

    // Button joinGame
    @FXML
    private void joinGameButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf joinGame
    }

    // Button Close
    @FXML
    private void closeButtonAction(ActionEvent event) {

        // Schließt die Anwendung
        Platform.exit();

    }
}
