package de.btu.monopoly.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 *
 * @author augat
 */
public class startGameController implements Initializable {

    @FXML
    private Button backButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    //-----------------------------------------------------------------------------------------
    // startGame.fxml
    //-----------------------------------------------------------------------------------------
    @FXML
    private void createLobbyButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf Lobby
    }

    @FXML
    private void backButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf Menu
    }

}
