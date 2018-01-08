package de.btu.monopoly.ui.controller;

import de.btu.monopoly.input.IOService;
import de.btu.monopoly.menu.LobbyService;
import de.btu.monopoly.menu.MainMenu;
import de.btu.monopoly.ui.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

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
    private Label errorLabel;

    @FXML
    private GridPane grid;

    @FXML
    private StackPane stackPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        Image image = new Image(getClass().getResourceAsStream("/images/Main_Background.png"), 1200, 800, false, false);
//        grid.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        String image = " -fx-background-image: url(\"/images/Main_Background.png\") ;\n"
                + "    -fx-background-position: center;\n"
                + "    -fx-background-size: stretch;";
        grid.setStyle(image);
        stackPane.setBackground(new Background(new BackgroundImage(new Image(getClass().getResourceAsStream("/images/Lobby_Background.jpg")), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
    }

    // Button back
    @FXML
    private void backButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf Menu
        SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/Menu.fxml")));
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

        if (GuiMessages.getConnectionError() == false) {
            try {
                // Wechselt die Scene auf Lobby
                SceneManager.changeSceneToLobby(new FXMLLoader(getClass().getResource("/fxml/Lobby.fxml")));
            } catch (IOException ex) {
                Logger.getLogger(JoinGameController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else {
            errorLabel.setText("Keine Verbindung möglich.");
        }

    }

}
