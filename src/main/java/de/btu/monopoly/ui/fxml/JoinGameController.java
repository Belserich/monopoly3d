package de.btu.monopoly.ui.fxml;

import de.btu.monopoly.Global;
import de.btu.monopoly.core.service.IOService;
import de.btu.monopoly.menu.LobbyService;
import de.btu.monopoly.menu.MainMenu;
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
public class JoinGameController implements Initializable {

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField ipAdressTextField;

    @FXML
    private Label nameLabel;

    @FXML
    private Label ipLabel;

    @FXML
    private Button backButton;

    @FXML
    private Button searchButton;

    @FXML
    private Label errorLabel;

    @FXML
    private GridPane grid;

    @FXML
    private StackPane stackPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String image = " -fx-background-image: url('/images/Main_Background.png');\n"
                + "    -fx-background-position: center;\n"
                + "    -fx-background-size: stretch;";
        grid.setStyle(image);
        stackPane.setBackground(new Background(new BackgroundImage(new Image(getClass().getResourceAsStream("/images/Lobby_Background.jpg")), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        backButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    backButtonAction(new ActionEvent());
                } catch (IOException ex) {
                    Logger.getLogger(JoinGameController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        searchButton.setOnKeyPressed((event) -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                searchButtonAction(new ActionEvent());
            }
        });

        // Animation
        nameTextField.setOpacity(0);
        ipAdressTextField.setOpacity(0);
        nameLabel.setOpacity(0);
        ipLabel.setOpacity(0);
        backButton.setOpacity(0);
        searchButton.setOpacity(0);

        FadeTransition fadeInButton1
                = new FadeTransition(Duration.millis(500), backButton);
        fadeInButton1.setFromValue(0);
        fadeInButton1.setToValue(1);
        fadeInButton1.playFromStart();

        FadeTransition fadeInButton2
                = new FadeTransition(Duration.millis(500), ipLabel);
        fadeInButton2.setFromValue(0);
        fadeInButton2.setToValue(1);
        fadeInButton2.playFromStart();

        FadeTransition fadeInButton3
                = new FadeTransition(Duration.millis(500), nameLabel);
        fadeInButton3.setFromValue(0);
        fadeInButton3.setToValue(1);
        fadeInButton3.playFromStart();

        FadeTransition fadeInButton4
                = new FadeTransition(Duration.millis(500), ipAdressTextField);
        fadeInButton4.setFromValue(0);
        fadeInButton4.setToValue(1);
        fadeInButton4.playFromStart();

        FadeTransition fadeInButton5
                = new FadeTransition(Duration.millis(500), searchButton);
        fadeInButton5.setFromValue(0);
        fadeInButton5.setToValue(1);
        fadeInButton5.playFromStart();

        FadeTransition fadeInButton6
                = new FadeTransition(Duration.millis(500), nameTextField);
        fadeInButton6.setFromValue(0);
        fadeInButton6.setToValue(1);
        fadeInButton6.playFromStart();
    }

    // Button back
    @FXML
    private void backButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf Menu
        changeScene(new FXMLLoader(getClass().getResource("/fxml/menu_scene.fxml")), false);
    }

    @FXML
    private void enterJoinsLobby(KeyEvent event) {

        if (event.getCode().equals(KeyCode.ENTER)) {
            joinLobby();
        }
    }

    // Button joinLobby
    @FXML
    private void searchButtonAction(ActionEvent event) {
        joinLobby();
    }

    private void joinLobby() {
        // Server initialisieren
        MainMenu menu = new MainMenu();
        menu.joinGame(ipAdressTextField.getText());

        // Namen wechseln
        IOService.sleep(200);
        LobbyService.changeName(nameTextField.getText());
    
        changeScene(new FXMLLoader(getClass().getResource("/fxml/lobby_scene.fxml")), true);
    }

    private void changeScene(FXMLLoader loader, boolean changeToLobby) {
        FadeTransition fadeInButton1
                = new FadeTransition(Duration.millis(500), backButton);
        fadeInButton1.setFromValue(1);
        fadeInButton1.setToValue(0);
        fadeInButton1.playFromStart();

        FadeTransition fadeInButton2
                = new FadeTransition(Duration.millis(500), ipAdressTextField);
        fadeInButton2.setFromValue(1);
        fadeInButton2.setToValue(0);
        fadeInButton2.playFromStart();

        FadeTransition fadeInButton3
                = new FadeTransition(Duration.millis(500), nameLabel);
        fadeInButton3.setFromValue(1);
        fadeInButton3.setToValue(0);
        fadeInButton3.playFromStart();

        FadeTransition fadeInButton5
                = new FadeTransition(Duration.millis(500), searchButton);
        fadeInButton5.setFromValue(1);
        fadeInButton5.setToValue(0);
        fadeInButton5.playFromStart();

        FadeTransition fadeInButton6
                = new FadeTransition(Duration.millis(500), nameTextField);
        fadeInButton6.setFromValue(1);
        fadeInButton6.setToValue(0);
        fadeInButton6.playFromStart();

        FadeTransition fadeInButton4
                = new FadeTransition(Duration.millis(500), ipLabel);
        fadeInButton4.setFromValue(1);
        fadeInButton4.setToValue(0);
        fadeInButton4.playFromStart();
        fadeInButton4.setOnFinished((event) -> {
            if (changeToLobby) {
                FadeTransition fadeGrid = new FadeTransition(Duration.millis(400), grid);
                fadeGrid.setFromValue(1);
                fadeGrid.setToValue(0);
                fadeGrid.playFromStart();
                fadeGrid.setOnFinished((ActionEvent event1) -> {
                    try {
                        Global.ref().getMenuSceneManager().changeSceneToLobby(loader);
                    } catch (IOException ex) {
                        Logger.getLogger(de.btu.monopoly.ui.fxml.StartGameController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            }
            else {
                try {
                    Global.ref().getMenuSceneManager().changeScene(loader);
                } catch (IOException ex) {
                    Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

}
