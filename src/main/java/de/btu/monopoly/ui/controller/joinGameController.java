package de.btu.monopoly.ui.controller;

import de.btu.monopoly.ui.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 *
 * @author augat
 */
public class joinGameController implements Initializable {

    public Button backButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    // Button back
    @FXML
    private void backButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf Menu
        SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/Menu.fxml")));
    }

    // Button joinLobby
    @FXML
    private void searchButtonAction(ActionEvent event) throws IOException {

        //TODO searchButtonAction
        System.out.println("joinLobbyButton ausgeführt! (Noch nicht implementiert!)");

    }
}
