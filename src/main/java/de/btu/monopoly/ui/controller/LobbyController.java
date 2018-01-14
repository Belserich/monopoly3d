package de.btu.monopoly.ui.controller;

import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.menu.LobbyService;
import de.btu.monopoly.ui.SceneManager;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 *
 * @author augat
 */
public class LobbyController implements Initializable {

    @FXML
    private Label lobbyLabelIp;

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
    private Button deleteKi1;
    @FXML
    private Button deleteKi2;
    @FXML
    private Button deleteKi3;
    @FXML
    private Button deleteKi4;
    @FXML
    private Button deleteKi5;

    @FXML
    private GridPane grid;

    @FXML
    private StackPane stackPane;

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

    // ID des eigenen Spielers
    private int id = -1;

    @FXML
    private Button playButton;

    @FXML
    private ComboBox difficultyComboBox;

    @FXML
    private Button kiButton;

    @FXML
    private TextField kiNameTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        difficultyComboBox.setStyle("-fx-font: 22px \"System\";");

        String image = " -fx-background-image: url(\"/images/Lobby_Background.jpg\") ;\n"
                + "    -fx-background-position: center;\n"
                + "    -fx-background-size: stretch;";
        grid.setStyle(image);
        try {
            // Anzeigen der IP Adresse
            lobbyLabelIp.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(LobbyController.class.getName()).log(Level.SEVERE, null, ex);
        }

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

        // Deaktivieren der KI Steuerung
        if (id != 0) {
            kiButton.setDisable(true);
            kiNameTextField.setDisable(true);
            difficultyComboBox.setDisable(true);
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
                    }
                    else {
                        name1Label.setText("frei");
                    }
                    if (Lobby.getUsers().length >= 2) {
                        name2Label.setText(Lobby.getUsers()[1][1]);
                        if (Integer.valueOf(Lobby.getUsers()[1][3]) > 0) {
                            if (id == 0) {
                                deleteKi1.setVisible(true);
                            }
                        }
                        else {
                            deleteKi1.setVisible(false);
                        }
                    }
                    else {
                        name2Label.setText("frei");
                        deleteKi1.setVisible(false);
                    }
                    if (Lobby.getUsers().length >= 3) {
                        name3Label.setText(Lobby.getUsers()[2][1]);
                        if (Integer.valueOf(Lobby.getUsers()[2][3]) > 0) {
                            if (id == 0) {
                                deleteKi2.setVisible(true);
                            }
                        }
                        else {
                            deleteKi2.setVisible(false);
                        }
                    }
                    else {
                        name3Label.setText("frei");
                        deleteKi2.setVisible(false);
                    }
                    if (Lobby.getUsers().length >= 4) {
                        name4Label.setText(Lobby.getUsers()[3][1]);
                        if (Integer.valueOf(Lobby.getUsers()[3][3]) > 0) {
                            if (id == 0) {
                                deleteKi3.setVisible(true);
                            }
                        }
                        else {
                            deleteKi3.setVisible(false);
                        }
                    }
                    else {
                        name4Label.setText("frei");
                        deleteKi3.setVisible(false);
                    }
                    if (Lobby.getUsers().length >= 5) {
                        name5Label.setText(Lobby.getUsers()[4][1]);
                        if (Integer.valueOf(Lobby.getUsers()[4][3]) > 0) {
                            if (id == 0) {
                                deleteKi4.setVisible(true);
                            }
                        }
                        else {
                            deleteKi4.setVisible(false);
                        }
                    }
                    else {
                        name5Label.setText("frei");
                        deleteKi4.setVisible(false);
                    }
                    if (Lobby.getUsers().length >= 6) {
                        name6Label.setText(Lobby.getUsers()[5][1]);
                        if (Integer.valueOf(Lobby.getUsers()[5][3]) > 0) {
                            if (id == 0) {
                                deleteKi5.setVisible(true);
                            }
                        }
                        else {
                            deleteKi5.setVisible(false);
                        }
                    }
                    else {
                        deleteKi5.setVisible(false);
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
                    if (name1Label.getText() != "frei") {
                        playerColor1.setValue(Color.web(Lobby.getUsers()[0][4]));

                    }
                    else {
                        playerColor1.setValue(Color.web("FFFFFF"));
                        playerColor1.setDisable(true);
                    }
                    if (name2Label.getText() != "frei") {
                        playerColor2.setValue(Color.web(Lobby.getUsers()[1][4]));

                    }
                    else {
                        playerColor2.setValue(Color.web("FFFFFF"));
                        playerColor2.setDisable(true);
                    }
                    if (name3Label.getText() != "frei") {
                        playerColor3.setValue(Color.web(Lobby.getUsers()[2][4]));

                    }
                    else {
                        playerColor3.setValue(Color.web("FFFFFF"));
                        playerColor3.setDisable(true);
                    }
                    if (name4Label.getText() != "frei") {
                        playerColor4.setValue(Color.web(Lobby.getUsers()[3][4]));

                    }
                    else {
                        playerColor4.setValue(Color.web("FFFFFF"));
                        playerColor4.setDisable(true);
                    }
                    if (name5Label.getText() != "frei") {
                        playerColor5.setValue(Color.web(Lobby.getUsers()[4][4]));

                    }
                    else {
                        playerColor5.setValue(Color.web("FFFFFF"));
                        playerColor5.setDisable(true);
                    }
                    if (name6Label.getText() != "frei") {
                        playerColor6.setValue(Color.web(Lobby.getUsers()[5][4]));

                    }
                    else {
                        playerColor6.setValue(Color.web("FFFFFF"));
                        playerColor6.setDisable(true);
                    }

                }
                return null;
            }
        };
        Platform.runLater(task);
    }

    // Spiel starten
    @FXML
    private void playButtonAction(ActionEvent event) throws IOException, InterruptedException {

        // Spiel starten
        LobbyService.gamestartRequest();

    }

    // Lobby verlassen
    @FXML
    private void leaveLobbyButtonAction(ActionEvent event) throws IOException, InterruptedException {

        // Schließt die Anwendung
        Platform.exit();
        System.exit(0); //NOSONAR

        // Wechselt die Scene auf Menu
        // SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/Menu.fxml")));
    }

    @FXML
    private void enterSetsKi(KeyEvent event) throws IOException {
        if (event.getCode().equals(KeyCode.ENTER)) {
            setKi();
        }
    }

    @FXML
    private void kiButtonAction(ActionEvent event) {
        setKi();
    }

    private void setKi() {
        if (difficultyComboBox.getSelectionModel().getSelectedItem() != null) {
            if (Lobby.getUsers().length < 6) {
                if (kiNameTextField.getText().length() > 0) {
                    // Colorpicker aktivieren
                    switch (Lobby.getUsers().length) {
                        case 1: {
                            playerColor2.setDisable(false);
                            deleteKi1.setVisible(true);
                            deleteKi1.setOnAction((event) -> {
                                LobbyService.deleteUser(1);
                            });
                            break;
                        }
                        case 2: {
                            playerColor3.setDisable(false);
                            deleteKi2.setVisible(true);
                            deleteKi2.setOnAction((event) -> {
                                LobbyService.deleteUser(2);
                            });
                            break;
                        }
                        case 3: {
                            playerColor4.setDisable(false);
                            deleteKi3.setVisible(true);
                            deleteKi3.setOnAction((event) -> {
                                LobbyService.deleteUser(3);
                            });
                            break;
                        }
                        case 4: {
                            playerColor5.setDisable(false);
                            deleteKi4.setVisible(true);
                            deleteKi4.setOnAction((event) -> {
                                LobbyService.deleteUser(4);
                            });
                            break;
                        }
                        case 5: {
                            playerColor6.setDisable(false);
                            deleteKi5.setVisible(true);
                            deleteKi5.setOnAction((event) -> {
                                LobbyService.deleteUser(5);
                            });
                            break;
                        }

                        default: {

                        }

                    }

                    // Schwierigkeit auslesen
                    int difficulty;

                    switch ((String) difficultyComboBox.getSelectionModel().getSelectedItem()) {
                        case "Einfach": {
                            difficulty = 1;
                            break;
                        }
                        case "Mittel": {
                            difficulty = 1;
                            break;
                        }
                        case "Schwer": {
                            difficulty = 1;
                            break;
                        }
                        default: {
                            difficulty = 1;
                        }
                    }

                    // KI hinzufügen
                    LobbyService.addKI(kiNameTextField.getText() + " ("
                            + (String) difficultyComboBox.getSelectionModel().getSelectedItem()
                            + ")", difficulty);
                    kiNameTextField.setText("");
                }
                else {
                    // Fehlermeldung in ComboBox
                    kiNameTextField.setPromptText("Bitte einen Namen eingeben!");
                }
            }
            else {
                kiNameTextField.setPromptText("Maximale KI Anzahl!");
            }

        }
        else {
            // Fehlermeldung in ComboBox
            difficultyComboBox.setPromptText("Bitte auswählen!");
        }
    }

    // Farben aktualisieren
    @FXML
    private void pushColorPick0(ActionEvent event) throws IOException, InterruptedException {

        LobbyService.changeColor(playerColor1.getValue());

    }

    @FXML
    private void pushColorPick1(ActionEvent event) throws IOException, InterruptedException {

        LobbyService.changeColor(playerColor2.getValue(), Integer.valueOf(Lobby.getUsers()[1][0]));

    }

    @FXML
    private void pushColorPick2(ActionEvent event) throws IOException, InterruptedException {

        LobbyService.changeColor(playerColor3.getValue(), Integer.valueOf(Lobby.getUsers()[2][0]));

    }

    @FXML
    private void pushColorPick3(ActionEvent event) throws IOException, InterruptedException {

        LobbyService.changeColor(playerColor4.getValue(), Integer.valueOf(Lobby.getUsers()[3][0]));

    }

    @FXML
    private void pushColorPick4(ActionEvent event) throws IOException, InterruptedException {

        LobbyService.changeColor(playerColor5.getValue(), Integer.valueOf(Lobby.getUsers()[4][0]));

    }

    @FXML
    private void pushColorPick5(ActionEvent event) throws IOException, InterruptedException {

        LobbyService.changeColor(playerColor6.getValue(), Integer.valueOf(Lobby.getUsers()[5][0]));

    }

    public void loadGameLayout() throws IOException {
        // Wechselt die Scene auf Game
        SceneManager.changeSceneToGame(new FXMLLoader(getClass().getResource("/fxml/mainScene.fxml")));
    }

}
