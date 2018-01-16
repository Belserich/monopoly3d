package de.btu.monopoly.ui.controller;

import de.btu.monopoly.core.service.IOService;
import de.btu.monopoly.menu.LobbyService;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.ui.SceneManager;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author augat
 */
public class StartGameController implements Initializable {

    private static GameClient client;

    @FXML
    private Button backButton;

    @FXML
    private TextField nicknameHostTextView;

    @FXML
    private Label nameLabel;

    @FXML
    private Button createLobbyButton;

    @FXML
    private GridPane grid;

    @FXML
    private StackPane stackPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String image = " -fx-background-image: url(\"/images/Main_Background.png\") ;\n"
                + "    -fx-background-position: center;\n"
                + "    -fx-background-size: stretch;";
        grid.setStyle(image);
        stackPane.setBackground(new Background(new BackgroundImage(new Image(getClass().getResourceAsStream("/images/Lobby_Background.jpg")), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        // Animation
        backButton.setOpacity(0);
        nameLabel.setOpacity(0);
        nicknameHostTextView.setOpacity(0);
        createLobbyButton.setOpacity(0);

        FadeTransition fadeInButton1
                = new FadeTransition(Duration.millis(500), backButton);
        fadeInButton1.setFromValue(0);
        fadeInButton1.setToValue(1);
        fadeInButton1.playFromStart();

        FadeTransition fadeInButton2
                = new FadeTransition(Duration.millis(500), nicknameHostTextView);
        fadeInButton2.setFromValue(0);
        fadeInButton2.setToValue(1);
        fadeInButton2.playFromStart();

        FadeTransition fadeInButton3
                = new FadeTransition(Duration.millis(500), nameLabel);
        fadeInButton3.setFromValue(0);
        fadeInButton3.setToValue(1);
        fadeInButton3.playFromStart();

        FadeTransition fadeInButton4
                = new FadeTransition(Duration.millis(500), createLobbyButton);
        fadeInButton4.setFromValue(0);
        fadeInButton4.setToValue(1);
        fadeInButton4.playFromStart();
    }

    public static void setClient(GameClient client) {
        StartGameController.client = client;
    }

    //-----------------------------------------------------------------------------------------
    // startGame.fxml
    //-----------------------------------------------------------------------------------------
    @FXML
    private void enterStartsLobby(KeyEvent event) throws IOException {
        if (event.getCode().equals(KeyCode.ENTER)) {
            joinLobby();
        }
    }

    @FXML
    private void createLobbyButtonAction(ActionEvent event) {
        joinLobby();
    }

    @FXML
    private void backButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf Menu
        changeScene(new FXMLLoader(getClass().getResource("/fxml/Menu.fxml")), false);
    }

    private void joinLobby() {

        // JoinLobby und Namen übernehmen
        LobbyService.joinLobby(client, true);
        IOService.sleep(200);
        LobbyService.changeName(nicknameHostTextView.getText());

        // Wechselt die Scene auf lobby
        changeScene(new FXMLLoader(getClass().getResource("/fxml/Lobby.fxml")), true);

    }

    private void changeScene(FXMLLoader loader, boolean changeToLobby) {
        FadeTransition fadeInButton1
                = new FadeTransition(Duration.millis(500), backButton);
        fadeInButton1.setFromValue(1);
        fadeInButton1.setToValue(0);
        fadeInButton1.playFromStart();

        FadeTransition fadeInButton2
                = new FadeTransition(Duration.millis(500), createLobbyButton);
        fadeInButton2.setFromValue(1);
        fadeInButton2.setToValue(0);
        fadeInButton2.playFromStart();

        FadeTransition fadeInButton3
                = new FadeTransition(Duration.millis(500), nameLabel);
        fadeInButton3.setFromValue(1);
        fadeInButton3.setToValue(0);
        fadeInButton3.playFromStart();

        FadeTransition fadeInButton4
                = new FadeTransition(Duration.millis(500), nicknameHostTextView);
        fadeInButton4.setFromValue(1);
        fadeInButton4.setToValue(0);
        fadeInButton4.playFromStart();
        fadeInButton4.setOnFinished((ActionEvent event) -> {

            if (changeToLobby) {
                FadeTransition fadeGrid = new FadeTransition(Duration.millis(400), grid);
                fadeGrid.setFromValue(1);
                fadeGrid.setToValue(0);
                fadeGrid.playFromStart();
                fadeGrid.setOnFinished((ActionEvent event1) -> {
                    try {
                        SceneManager.changeSceneToLobby(loader);
                    } catch (IOException ex) {
                        Logger.getLogger(StartGameController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
            else {
                try {
                    SceneManager.changeScene(loader);
                } catch (IOException ex) {
                    Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

}
