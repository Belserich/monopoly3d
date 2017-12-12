package de.btu.monopoly.ui.controller;

import de.btu.monopoly.menu.LobbyService;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.ui.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 *
 * @author augat
 */
public class startGameController implements Initializable {

    public static GameClient client;

    @FXML
    private Button backButton;

    @FXML
    private TextField nicknameHostTextView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    //-----------------------------------------------------------------------------------------
    // startGame.fxml
    //-----------------------------------------------------------------------------------------
    @FXML
    private void createLobbyButtonAction(ActionEvent event) throws IOException, InterruptedException {

        // JoinLobby und Namen übernehmen
        LobbyService.joinLobby(client, true);
        Thread.sleep(200);
        LobbyService.changeName(nicknameHostTextView.getText());

        // Wechselt die Scene auf Lobby
        SceneManager.changeSceneToLobby(new FXMLLoader(getClass().getResource("/fxml/Lobby.fxml")));

    }

    @FXML
    private void backButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf Menu
        SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/Menu.fxml")));
    }

}
