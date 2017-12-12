package de.btu.monopoly.ui.controller;

import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.menu.LobbyService;
import de.btu.monopoly.ui.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 *
 * @author augat
 */
public class LobbyController implements Initializable {

    @FXML
    private Label name1Label;
    @FXML
    private Label name2Label;
    @FXML
    private Label name3Label;
    @FXML
    private Label name4Label;
    @FXML
    private Label name5Label;
    @FXML
    private Label name6Label;

    @FXML
    private Button playButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Updatet die Spieler in der Lobby
        update();

        // playButton kann nur der Host drücken
        if (Lobby.getUsers() != null) {
            if (Lobby.getUsers().length != 1) {
                playButton.setDisable(true);
            }
        }
    }

    /**
     * Anzeigen der Spieler in der Lobby
     */
    public void update() {
        if (Lobby.getUsers() != null) {
            if (Lobby.getUsers().length >= 1) {
                name1Label.setText(Lobby.getUsers()[0][1]);
            } else {
                name1Label.setText("frei");
            }
            if (Lobby.getUsers().length >= 2) {
                name2Label.setText(Lobby.getUsers()[1][1]);
            } else {
                name2Label.setText("frei");
            }
            if (Lobby.getUsers().length >= 3) {
                name3Label.setText(Lobby.getUsers()[2][1]);
            } else {
                name3Label.setText("frei");
            }
            if (Lobby.getUsers().length >= 4) {
                name4Label.setText(Lobby.getUsers()[3][1]);
            } else {
                name4Label.setText("frei");
            }
            if (Lobby.getUsers().length >= 5) {
                name5Label.setText(Lobby.getUsers()[4][1]);
            } else {
                name5Label.setText("frei");
            }
            if (Lobby.getUsers().length >= 6) {
                name6Label.setText(Lobby.getUsers()[5][1]);
            } else {
                name6Label.setText("frei");
            }

        }
    }

    // Spiel starten - Button
    @FXML
    private void playButtonAction(ActionEvent event) throws IOException, InterruptedException {
        LobbyService.startGame();

        // Wechselt die Scene auf Game
        // SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/Game.fxml")));
    }

    // Lobby verlassen - Button
    @FXML
    private void leaveLobbyButtonAction(ActionEvent event) throws IOException, InterruptedException {

        // Wechselt die Scene auf Game
        SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/Menu.fxml")));
    }
}
