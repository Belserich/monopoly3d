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
import javafx.scene.layout.StackPane;

/**
 *
 * @author augat
 */
public class MenuController implements Initializable {

    @FXML
    private Button joinGameButton;

    @FXML
    private GridPane grid;

    @FXML
    private StackPane stackPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Image image = new Image("https://i.imgur.com/tiR1cea.jpg");
        grid.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
        stackPane.setBackground(new Background(new BackgroundImage(new Image("https://images-na.ssl-images-amazon.com/images/S/sgp-catalog-images/region_US/di3a2-ACJM5H51YKB-Full-Image_GalleryBackground-en-US-1489722831648._RI_SX940_.jpg"), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
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
