package de.btu.monopoly.ui.controller;

import static de.btu.monopoly.menu.MainMenu.LOGGER;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.server.GameServer;
import de.btu.monopoly.ui.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 *
 * @author augat
 */
public class MenuController implements Initializable {

    @FXML
    private Button joinGameButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    // Button startGame
    @FXML
    private void startGameButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf startGame
        SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/startGame.fxml")));

        // Server und Client starten und verbinden
        GameServer server = new GameServer(59687);
        server.startServer();
        GameClient client = new GameClient(59687, 5000);
        String localHost = System.getProperty("myapp.ip");
        client.connect("localhost");
        LOGGER.info("Die ServerIP ist " + server.getServerIP());

        startGameController.client = client;

    }

    // Button joinGame
    @FXML
    private void joinGameButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf joinGame
        SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/joinGame.fxml")));
    }

    // Button Close
    @FXML
    private void closeButtonAction(ActionEvent event) {

        // Schließt die Anwendung
        Platform.exit();

    }
}
