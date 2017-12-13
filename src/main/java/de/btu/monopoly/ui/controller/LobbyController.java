package de.btu.monopoly.ui.controller;

import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.menu.LobbyService;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

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
    private ColorPicker playerColor1;
    @FXML
    private ColorPicker playerColor2;
    @FXML
    private ColorPicker playerColor3;
    @FXML
    private ColorPicker playerColor4;
    @FXML
    private ColorPicker playerColor5;
    @FXML
    private ColorPicker playerColor6;

    @FXML
    private Button playButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Updatet die Spieler in der Lobby
        updateNames();

        // playButton kann nur der Host drücken
        if (Lobby.getUsers() != null) {
            if (Lobby.getUsers().length != 1) {
                playButton.setDisable(true);
            }
        }
    }

    /**
     * Anzeigen der Spielernamen in der Lobby
     */
    public void updateNames() {

        Task task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
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
                return null;
            }

        };
        Platform.runLater(task);
    }

    /**
     * Anzeigen der Spielerfarben in der Lobby
     */
    public void updateColors() {
        Task task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                if (GuiMessages.getPlayerColors() != null) {
                    playerColor1.setValue(GuiMessages.getPlayerColors()[0]);
                    playerColor2.setValue(GuiMessages.getPlayerColors()[1]);
                    playerColor3.setValue(GuiMessages.getPlayerColors()[2]);
                    playerColor4.setValue(GuiMessages.getPlayerColors()[3]);
                    playerColor5.setValue(GuiMessages.getPlayerColors()[4]);
                    playerColor6.setValue(GuiMessages.getPlayerColors()[5]);

                }
                return null;
            }
        };
        Platform.runLater(task);
    }

    // Spiel starten
    @FXML
    private void playButtonAction(ActionEvent event) throws IOException, InterruptedException {
        LobbyService.startGame();

        // Wechselt die Scene auf Game
        // SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/Game.fxml")));
    }

    // Lobby verlassen
    @FXML
    private void leaveLobbyButtonAction(ActionEvent event) throws IOException, InterruptedException {

        // Schließt die Anwendung
        Platform.exit();
        System.exit(0);

        // Wechselt die Scene auf Menu
        // SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/Menu.fxml")));
    }

    // Farben aktualisieren
    @FXML
    private void pushColorPick(ActionEvent event) throws IOException, InterruptedException {
        Color[] colors = {playerColor1.getValue(), playerColor2.getValue(), playerColor3.getValue(), playerColor4.getValue(), playerColor5.getValue(), playerColor6.getValue()};
        GuiMessages.setPlayerColors(colors);
    }
}
