package de.btu.monopoly.ui.controller;

import de.btu.monopoly.input.IOService;
import de.btu.monopoly.menu.LobbyService;
import de.btu.monopoly.net.client.GameClient;
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
import javafx.scene.control.Button;
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
public class StartGameController implements Initializable {

    private static GameClient client;

    @FXML
    private Button backButton;

    @FXML
    private TextField nicknameHostTextView;

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
        SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/Menu.fxml")));
    }

    private void joinLobby() {

        // JoinLobby und Namen übernehmen
        LobbyService.joinLobby(client, true);
        IOService.sleep(200);
        LobbyService.changeName(nicknameHostTextView.getText());

        try {
            // Wechselt die Scene auf Lobby
            SceneManager.changeSceneToLobby(new FXMLLoader(getClass().getResource("/fxml/Lobby.fxml")));
        } catch (IOException ex) {
            Logger.getLogger(StartGameController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
