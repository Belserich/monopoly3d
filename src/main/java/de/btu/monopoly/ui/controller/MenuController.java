package de.btu.monopoly.ui.controller;

import de.btu.monopoly.menu.MainMenu;
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
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;

/**
 *
 * @author augat
 */
public class MenuController implements Initializable {

    @FXML
    private Button joinGameButton;

    @FXML
    private GridPane grid;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Image image = new Image("https://cdn.vox-cdn.com/thumbor/LuyPPsKiSwRkL0i87Ur-8GDhSDM=/0x0:1144x566/1200x800/filters:focal(481x192:663x374)/cdn.vox-cdn.com/uploads/chorus_image/image/52679863/Screen_Shot_2017_01_10_at_10.41.40_AM.0.png");
        grid.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
    }

    // Button startGame
    @FXML
    private void startGameButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf startGame
        SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/startGame.fxml")));

        // Server initialisieren
        MainMenu menu = new MainMenu();
        menu.createGame();

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
        System.exit(0);

    }
}
