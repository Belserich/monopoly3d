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
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 *
 * @author augat
 */
public class LobbyController implements Initializable {

    @FXML
    private Label lobbyLabelIp;

    @FXML
    private Label lobbyLabel;

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
    private Button leaveLobbyButton;

    @FXML
    private Button optionButton;

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
        difficultyComboBox.getItems().addAll("Anfänger", "Experte");

        // Updatet die Spieler in der lobby
        updateNames();

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

        // Animation
        lobbyLabelIp.setOpacity(0);
        lobbyLabel.setOpacity(0);
        name1Label.setOpacity(0);
        name2Label.setOpacity(0);
        name3Label.setOpacity(0);
        name4Label.setOpacity(0);
        name5Label.setOpacity(0);
        name6Label.setOpacity(0);
        playerColor1.setOpacity(0);
        playerColor2.setOpacity(0);
        playerColor3.setOpacity(0);
        playerColor4.setOpacity(0);
        playerColor5.setOpacity(0);
        playerColor6.setOpacity(0);
        difficultyComboBox.setOpacity(0);
        kiNameTextField.setOpacity(0);
        kiButton.setOpacity(0);
        playButton.setOpacity(0);
        leaveLobbyButton.setOpacity(0);
        grid.setOpacity(0);
        optionButton.setOpacity(0);

        // Deaktivieren der KI Steuerung
        if (id != 0) {
            playButton.setDisable(true);
            kiButton.setDisable(true);
            kiNameTextField.setDisable(true);
            difficultyComboBox.setDisable(true);
            optionButton.setDisable(true);
        }

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {

                FadeTransition fadeGrid = new FadeTransition(Duration.millis(800), grid);
                fadeGrid.setFromValue(0);
                fadeGrid.setToValue(1);
                fadeGrid.playFromStart();
                fadeGrid.setOnFinished((event) -> {

                    if (id != 0) {
                        double value = 0.5;
                    }
                    else {
                        double value = 1;
                    }

                    FadeTransition fadeInButton1
                            = new FadeTransition(Duration.millis(800), lobbyLabel);
                    fadeInButton1.setFromValue(0);
                    fadeInButton1.setToValue(1);
                    fadeInButton1.playFromStart();

                    FadeTransition fadeInButton2
                            = new FadeTransition(Duration.millis(800), name1Label);
                    fadeInButton2.setFromValue(0);
                    fadeInButton2.setToValue(1);
                    fadeInButton2.playFromStart();

                    FadeTransition fadeInButton3
                            = new FadeTransition(Duration.millis(800), name2Label);
                    fadeInButton3.setFromValue(0);
                    fadeInButton3.setToValue(1);
                    fadeInButton3.playFromStart();

                    FadeTransition fadeInButton4
                            = new FadeTransition(Duration.millis(800), name3Label);
                    fadeInButton4.setFromValue(0);
                    fadeInButton4.setToValue(1);
                    fadeInButton4.playFromStart();

                    FadeTransition fadeInButton5
                            = new FadeTransition(Duration.millis(800), name4Label);
                    fadeInButton5.setFromValue(0);
                    fadeInButton5.setToValue(1);
                    fadeInButton5.playFromStart();

                    FadeTransition fadeInButton6
                            = new FadeTransition(Duration.millis(800), name5Label);
                    fadeInButton6.setFromValue(0);
                    fadeInButton6.setToValue(1);
                    fadeInButton6.playFromStart();

                    FadeTransition fadeInButton7
                            = new FadeTransition(Duration.millis(800), name6Label);
                    fadeInButton7.setFromValue(0);
                    fadeInButton7.setToValue(1);
                    fadeInButton7.playFromStart();

                    FadeTransition fadeInButton8
                            = new FadeTransition(Duration.millis(800), lobbyLabelIp);
                    fadeInButton8.setFromValue(0);
                    fadeInButton8.setToValue(1);
                    fadeInButton8.playFromStart();

                    FadeTransition fadeInButton9
                            = new FadeTransition(Duration.millis(800), playerColor1);
                    fadeInButton9.setFromValue(0);
                    fadeInButton9.setToValue(1);
                    fadeInButton9.playFromStart();

                    FadeTransition fadeInButton10
                            = new FadeTransition(Duration.millis(800), playerColor2);
                    fadeInButton10.setFromValue(0);
                    fadeInButton10.setToValue(1);
                    fadeInButton10.playFromStart();

                    FadeTransition fadeInButton11
                            = new FadeTransition(Duration.millis(800), playerColor3);
                    fadeInButton11.setFromValue(0);
                    fadeInButton11.setToValue(1);
                    fadeInButton11.playFromStart();

                    FadeTransition fadeInButton12
                            = new FadeTransition(Duration.millis(800), playerColor4);
                    fadeInButton12.setFromValue(0);
                    fadeInButton12.setToValue(1);
                    fadeInButton12.playFromStart();

                    FadeTransition fadeInButton13
                            = new FadeTransition(Duration.millis(800), playerColor5);
                    fadeInButton13.setFromValue(0);
                    fadeInButton13.setToValue(1);
                    fadeInButton13.playFromStart();

                    FadeTransition fadeInButton14
                            = new FadeTransition(Duration.millis(800), playerColor6);
                    fadeInButton14.setFromValue(0);
                    fadeInButton14.setToValue(1);
                    fadeInButton14.playFromStart();
                    if (id == 0) {
                        FadeTransition fadeInButton15
                                = new FadeTransition(Duration.millis(800), difficultyComboBox);
                        fadeInButton15.setFromValue(0);
                        fadeInButton15.setToValue(1);
                        fadeInButton15.playFromStart();

                        FadeTransition fadeInButton16
                                = new FadeTransition(Duration.millis(800), kiNameTextField);
                        fadeInButton16.setFromValue(0);
                        fadeInButton16.setToValue(1);
                        fadeInButton16.playFromStart();

                        FadeTransition fadeInButton17
                                = new FadeTransition(Duration.millis(800), kiButton);
                        fadeInButton17.setFromValue(0);
                        fadeInButton17.setToValue(1);
                        fadeInButton17.playFromStart();

                        FadeTransition fadeInButton18
                                = new FadeTransition(Duration.millis(800), playButton);
                        fadeInButton18.setFromValue(0);
                        fadeInButton18.setToValue(1);
                        fadeInButton18.playFromStart();

                        FadeTransition fadeInButton19
                                = new FadeTransition(Duration.millis(800), optionButton);
                        fadeInButton19.setFromValue(0);
                        fadeInButton19.setToValue(1);
                        fadeInButton19.playFromStart();
                    }
                    else {
                        FadeTransition fadeInButton15
                                = new FadeTransition(Duration.millis(800), difficultyComboBox);
                        fadeInButton15.setFromValue(0);
                        fadeInButton15.setToValue(0.5);
                        fadeInButton15.playFromStart();

                        FadeTransition fadeInButton16
                                = new FadeTransition(Duration.millis(800), kiNameTextField);
                        fadeInButton16.setFromValue(0);
                        fadeInButton16.setToValue(0.5);
                        fadeInButton16.playFromStart();

                        FadeTransition fadeInButton17
                                = new FadeTransition(Duration.millis(800), kiButton);
                        fadeInButton17.setFromValue(0);
                        fadeInButton17.setToValue(0.5);
                        fadeInButton17.playFromStart();

                        FadeTransition fadeInButton18
                                = new FadeTransition(Duration.millis(800), playButton);
                        fadeInButton18.setFromValue(0);
                        fadeInButton18.setToValue(0.5);
                        fadeInButton18.playFromStart();

                        FadeTransition fadeInButton19
                                = new FadeTransition(Duration.millis(800), optionButton);
                        fadeInButton19.setFromValue(0);
                        fadeInButton19.setToValue(0.5);
                        fadeInButton19.playFromStart();
                    }

                    FadeTransition fadeInButton19
                            = new FadeTransition(Duration.millis(800), leaveLobbyButton);
                    fadeInButton19.setFromValue(0);
                    fadeInButton19.setToValue(1);
                    fadeInButton19.playFromStart();
                });

                return null;
            }
        };
        Platform.runLater(task);

        updateColors();
    }

    /**
     * Anzeigen der Spielernamen in der lobby
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
     * Anzeigen der Spielerfarben in der lobby
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

    // lobby verlassen
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
                        case "Anfänger": {
                            difficulty = 1;
                            break;
                        }
                        case "Experte": {
                            difficulty = 2;
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
                    kiNameTextField.setText("");
                }
            }
            else {
                kiNameTextField.setPromptText("Maximale KI Anzahl!");
                kiNameTextField.setText("");
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
        FadeTransition fadeGrid = new FadeTransition(Duration.millis(400), grid);
        fadeGrid.setFromValue(1);
        fadeGrid.setToValue(0);
        fadeGrid.playFromStart();

        try {
            // Wechselt die Scene auf Game
            SceneManager.changeSceneToGame(new FXMLLoader(getClass().getResource("/fxml/mainScene_1.fxml")));
        } catch (IOException ex) {
            Logger.getLogger(LobbyController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void optionButtonAction(ActionEvent event) throws IOException {
        FadeTransition fadeGrid = new FadeTransition(Duration.millis(400), grid);
        fadeGrid.setFromValue(1);
        fadeGrid.setToValue(0);
        fadeGrid.playFromStart();
        fadeGrid.setOnFinished((event1) -> {
            try {
                SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/settings.fxml")));
            } catch (IOException ex) {
                Logger.getLogger(LobbyController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void animation() {
        FadeTransition fadeGrid = new FadeTransition(Duration.millis(800), grid);
        fadeGrid.setFromValue(0);
        fadeGrid.setToValue(1);
        fadeGrid.playFromStart();
    }
}
