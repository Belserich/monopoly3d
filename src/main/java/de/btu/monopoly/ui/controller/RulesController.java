package de.btu.monopoly.ui.controller;

import de.btu.monopoly.ui.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 *
 * @author augat
 */
public class RulesController implements Initializable {

    @FXML
    private GridPane grid;

    @FXML
    private StackPane stackPane;

    @FXML
    private Button backButton;

    @FXML
    private Label labelRegeln;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String image = " -fx-background-image: url(\"/images/Main_Background.png\") ;\n"
                + "    -fx-background-position: center;\n"
                + "    -fx-background-size: stretch;";
        grid.setStyle(image);
        stackPane.setBackground(new Background(new BackgroundImage(new Image(getClass().getResourceAsStream("/images/Lobby_Background.jpg")), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        // Animation
        backButton.setOpacity(0);
        labelRegeln.setOpacity(0);

        FadeTransition fadeInButton3
                = new FadeTransition(Duration.millis(500), backButton);
        fadeInButton3.setFromValue(0);
        fadeInButton3.setToValue(1);
        fadeInButton3.playFromStart();

        FadeTransition fadeInButton2 = new FadeTransition(Duration.millis(500), labelRegeln);
        fadeInButton2.setFromValue(0);
        fadeInButton2.setToValue(1);
        fadeInButton2.playFromStart();
    }

    // Button back
    @FXML
    private void backButtonAction(ActionEvent event) throws IOException {

        changeScene(new FXMLLoader(getClass().getResource("/fxml/Menu.fxml")));

    }

    private void changeScene(FXMLLoader loader) {
        FadeTransition fadeInButton4
                = new FadeTransition(Duration.millis(500), labelRegeln);
        fadeInButton4.setFromValue(1);
        fadeInButton4.setToValue(0);
        fadeInButton4.playFromStart();

        FadeTransition fadeInButton1
                = new FadeTransition(Duration.millis(500), backButton);
        fadeInButton1.setFromValue(1);
        fadeInButton1.setToValue(0);
        fadeInButton1.playFromStart();
        fadeInButton1.setOnFinished((event) -> {
            try {
                SceneManager.changeScene(loader);
            } catch (IOException ex) {
                Logger.getLogger(MenuController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
}
