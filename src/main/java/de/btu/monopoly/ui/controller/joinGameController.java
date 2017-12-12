package de.btu.monopoly.ui.controller;

import de.btu.monopoly.menu.LobbyService;
import de.btu.monopoly.menu.MainMenu;
import de.btu.monopoly.ui.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 *
 * @author augat
 */
public class joinGameController implements Initializable {

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField ipAdressTextField;

    @FXML
    private Label errorLabel;

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
    private void searchButtonAction(ActionEvent event) throws IOException, InterruptedException {
        // Server initialisieren
        MainMenu menu = new MainMenu();
        menu.joinGame(ipAdressTextField.getText());

        // Namen wechseln
        Thread.sleep(200);
        LobbyService.changeName(nameTextField.getText());

        if (MessageControl.getConnectionError() == false) {
            // Wechselt die Scene auf Lobby
            SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/Lobby.fxml")));
        } else {
            errorLabel.setText("Die Verbindung konnte nicht hergestellt werden.");
        }

    }

}
