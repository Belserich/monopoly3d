package de.btu.monopoly.ui.controller;

import de.btu.monopoly.ui.SceneManager;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
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
public class SettingsController implements Initializable {

    @FXML
    private GridPane grid;

    @FXML
    private StackPane stackPane;

    @FXML
    private CheckBox test1;

    @FXML
    private CheckBox test2;

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
        test1.setOpacity(0);
        test2.setOpacity(0);
        backButton.setOpacity(0);

        FadeTransition fadeInButton1
                = new FadeTransition(Duration.millis(500), test1);
        fadeInButton1.setFromValue(0);
        fadeInButton1.setToValue(1);
        fadeInButton1.playFromStart();

        FadeTransition fadeInButton2
                = new FadeTransition(Duration.millis(500), test2);
        fadeInButton2.setFromValue(0);
        fadeInButton2.setToValue(1);
        fadeInButton2.playFromStart();

        FadeTransition fadeInButton3
                = new FadeTransition(Duration.millis(500), backButton);
        fadeInButton3.setFromValue(0);
        fadeInButton3.setToValue(1);
        fadeInButton3.playFromStart();
    }

    // Button back
    @FXML
    private void backButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf Menu
        changeScene(new FXMLLoader(getClass().getResource("/fxml/menu_scene.fxml")));
    }

    private void changeScene(FXMLLoader loader) {
        FadeTransition fadeInButton1
                = new FadeTransition(Duration.millis(500), backButton);
        fadeInButton1.setFromValue(1);
        fadeInButton1.setToValue(0);
        fadeInButton1.playFromStart();

        FadeTransition fadeInButton2
                = new FadeTransition(Duration.millis(500), test1);
        fadeInButton2.setFromValue(1);
        fadeInButton2.setToValue(0);
        fadeInButton2.playFromStart();

        FadeTransition fadeInButton4
                = new FadeTransition(Duration.millis(500), test2);
        fadeInButton4.setFromValue(1);
        fadeInButton4.setToValue(0);
        fadeInButton4.playFromStart();
        fadeInButton4.setOnFinished((event) -> {
            try {
                SceneManager.changeScene(loader);
            } catch (IOException ex) {
                Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
