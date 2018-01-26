package de.btu.monopoly.ui.controller;

import com.jfoenix.controls.JFXTextField;
import de.btu.monopoly.ui.MenuSceneManager;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 *
 * @author augat
 */
public class SettingsController implements Initializable {

    @FXML
    private GridPane grid;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField seedField;

    @FXML
    private Button backButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String image = " -fx-background-image: url('/images/Main_Background.png');\n"
                + "    -fx-background-position: center;\n"
                + "    -fx-background-size: stretch;";
        grid.setStyle(image);
        stackPane.setBackground(new Background(new BackgroundImage(new Image(getClass().getResourceAsStream("/images/Lobby_Background.jpg")), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        // Animation
        grid.setOpacity(0);

        FadeTransition fadeGrid = new FadeTransition(Duration.millis(800), grid);
        fadeGrid.setFromValue(0);
        fadeGrid.setToValue(1);
        fadeGrid.playFromStart();
    }

    // Button back
    @FXML
    private void backButtonAction(ActionEvent event) throws IOException {

//        LobbyService.setRandomSeed(Long.parseLong(seedField.getText()));
        FadeTransition fadeGrid = new FadeTransition(Duration.millis(400), grid);
        fadeGrid.setFromValue(1);
        fadeGrid.setToValue(0);
        fadeGrid.playFromStart();
        fadeGrid.setOnFinished((event1) -> {
            MenuSceneManager.changeSceneBackToLobby();
        });
    }
}
