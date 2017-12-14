package de.btu.monopoly.ui.controller;

import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.menu.LobbyService;
import de.btu.monopoly.ui.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
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

    private int id = -1;

    @FXML
    private Button playButton;

    @FXML
    private ComboBox difficultyComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Festlegen der Optionen in der Combobox
        difficultyComboBox.getItems().addAll("Einfach", "Mittel", "Schwer");

        // Updatet die Spieler in der Lobby
        updateNames();

        // playButton kann nur der Host drücken
        if (Lobby.getUsers() != null) {
            if (Lobby.getUsers().length != 1) {
                playButton.setDisable(true);
            }
        }

        // Funktion des eigenen Colorpickers aktivieren und ID im Controller festlegen
        if (Lobby.getUsers().length == 1) {
            playerColor1.setDisable(false);
            id = 0;
        }
        if (Lobby.getUsers().length == 2) {
            playerColor2.setDisable(false);
            id = 1;
        }
        if (Lobby.getUsers().length == 3) {
            playerColor3.setDisable(false);
            id = 2;
        }
        if (Lobby.getUsers().length == 4) {
            playerColor4.setDisable(false);
            id = 3;
        }
        if (Lobby.getUsers().length == 5) {
            playerColor5.setDisable(false);
            id = 4;
        }
        if (Lobby.getUsers().length == 6) {
            playerColor6.setDisable(false);
            id = 5;
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
                if (Lobby.getUsers() != null) {
                    playerColor1.setValue(Color.web(Lobby.getUsers()[0][4]));
                    playerColor2.setValue(Color.web(Lobby.getUsers()[1][4]));
                    playerColor3.setValue(Color.web(Lobby.getUsers()[2][4]));
                    playerColor4.setValue(Color.web(Lobby.getUsers()[3][4]));
                    playerColor5.setValue(Color.web(Lobby.getUsers()[4][4]));
                    playerColor6.setValue(Color.web(Lobby.getUsers()[5][4]));

                }
                return null;
            }
        };
        Platform.runLater(task);
    }

    // Spiel starten
    @FXML
    private void playButtonAction(ActionEvent event) throws IOException, InterruptedException {

        // Wechselt die Scene auf Game
        SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/mainScene.fxml")));

        // Spiel starten
//        LobbyService.startGame();
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

    @FXML
    private void KiButtonAction(ActionEvent event) {

        int difficulty;

        LobbyService.addKI("horst", 1);
    }

    // Farben aktualisieren
    @FXML
    private void pushColorPick(ActionEvent event) throws IOException, InterruptedException {

        switch (id) {
            case 0: {
                LobbyService.changeColor(playerColor1.getValue());
            }
            case 1: {
                LobbyService.changeColor(playerColor1.getValue());
            }
            case 2: {
                LobbyService.changeColor(playerColor1.getValue());
            }
            case 3: {
                LobbyService.changeColor(playerColor1.getValue());
            }
            case 4: {
                LobbyService.changeColor(playerColor1.getValue());
            }
            case 5: {
                LobbyService.changeColor(playerColor1.getValue());
            }
            default: {

            }
        }

    }
}
