package de.btu.monopoly.ui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
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

    @FXML
    private JFXButton allgemeinButton;

    @FXML
    private JFXButton vorDemSpielButton;

    @FXML
    private JFXButton gefaengnisphaseButton;

    @FXML
    private JFXButton wurfphaseButton;

    @FXML
    private JFXButton feldphaseButton;

    @FXML
    private JFXButton aktionsphaseButton;

    @FXML
    private JFXButton haeuserButton;

    @FXML
    private JFXButton hypothekButton;

    @FXML
    private JFXButton auktionenButton;

    @FXML
    private StackPane dialogPane;

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
        allgemeinButton.setOpacity(0);
        vorDemSpielButton.setOpacity(0);
        gefaengnisphaseButton.setOpacity(0);
        wurfphaseButton.setOpacity(0);
        feldphaseButton.setOpacity(0);
        aktionsphaseButton.setOpacity(0);
        haeuserButton.setOpacity(0);
        hypothekButton.setOpacity(0);
        auktionenButton.setOpacity(0);

        FadeTransition fadeInButton3
                = new FadeTransition(Duration.millis(500), backButton);
        fadeInButton3.setFromValue(0);
        fadeInButton3.setToValue(1);
        fadeInButton3.playFromStart();

        FadeTransition fadeInButton2 = new FadeTransition(Duration.millis(500), labelRegeln);
        fadeInButton2.setFromValue(0);
        fadeInButton2.setToValue(1);
        fadeInButton2.playFromStart();

        FadeTransition fadeInButton4 = new FadeTransition(Duration.millis(500), allgemeinButton);
        fadeInButton4.setFromValue(0);
        fadeInButton4.setToValue(1);
        fadeInButton4.playFromStart();

        FadeTransition fadeInButton5 = new FadeTransition(Duration.millis(500), vorDemSpielButton);
        fadeInButton5.setFromValue(0);
        fadeInButton5.setToValue(1);
        fadeInButton5.playFromStart();

        FadeTransition fadeInButton6 = new FadeTransition(Duration.millis(500), gefaengnisphaseButton);
        fadeInButton6.setFromValue(0);
        fadeInButton6.setToValue(1);
        fadeInButton6.playFromStart();

        FadeTransition fadeInButton7 = new FadeTransition(Duration.millis(500), wurfphaseButton);
        fadeInButton7.setFromValue(0);
        fadeInButton7.setToValue(1);
        fadeInButton7.playFromStart();

        FadeTransition fadeInButton8 = new FadeTransition(Duration.millis(500), feldphaseButton);
        fadeInButton8.setFromValue(0);
        fadeInButton8.setToValue(1);
        fadeInButton8.playFromStart();

        FadeTransition fadeInButton9 = new FadeTransition(Duration.millis(500), aktionsphaseButton);
        fadeInButton9.setFromValue(0);
        fadeInButton9.setToValue(1);
        fadeInButton9.playFromStart();

        FadeTransition fadeInButton10 = new FadeTransition(Duration.millis(500), haeuserButton);
        fadeInButton10.setFromValue(0);
        fadeInButton10.setToValue(1);
        fadeInButton10.playFromStart();

        FadeTransition fadeInButton11 = new FadeTransition(Duration.millis(500), hypothekButton);
        fadeInButton11.setFromValue(0);
        fadeInButton11.setToValue(1);
        fadeInButton11.playFromStart();

        FadeTransition fadeInButton12 = new FadeTransition(Duration.millis(500), auktionenButton);
        fadeInButton12.setFromValue(0);
        fadeInButton12.setToValue(1);
        fadeInButton12.playFromStart();

    }

    // Button back
    @FXML
    private void backButtonAction(ActionEvent event) throws IOException {
        changeScene(new FXMLLoader(getClass().getResource("/fxml/Menu.fxml")));
    }

    @FXML
    private void allgemeinButtonAction(ActionEvent event) throws IOException {
        JFXDialog dialog = new JFXDialog(dialogPane, null, JFXDialog.DialogTransition.TOP);
    }

    private void changeScene(FXMLLoader loader) {
        FadeTransition fadeInButton2
                = new FadeTransition(Duration.millis(500), labelRegeln);
        fadeInButton2.setFromValue(1);
        fadeInButton2.setToValue(0);
        fadeInButton2.playFromStart();

        FadeTransition fadeInButton4 = new FadeTransition(Duration.millis(500), allgemeinButton);
        fadeInButton4.setFromValue(1);
        fadeInButton4.setToValue(0);
        fadeInButton4.playFromStart();

        FadeTransition fadeInButton5 = new FadeTransition(Duration.millis(500), vorDemSpielButton);
        fadeInButton5.setFromValue(1);
        fadeInButton5.setToValue(0);
        fadeInButton5.playFromStart();

        FadeTransition fadeInButton6 = new FadeTransition(Duration.millis(500), gefaengnisphaseButton);
        fadeInButton6.setFromValue(1);
        fadeInButton6.setToValue(0);
        fadeInButton6.playFromStart();

        FadeTransition fadeInButton7 = new FadeTransition(Duration.millis(500), wurfphaseButton);
        fadeInButton7.setFromValue(1);
        fadeInButton7.setToValue(0);
        fadeInButton7.playFromStart();

        FadeTransition fadeInButton8 = new FadeTransition(Duration.millis(500), feldphaseButton);
        fadeInButton8.setFromValue(1);
        fadeInButton8.setToValue(0);
        fadeInButton8.playFromStart();

        FadeTransition fadeInButton9 = new FadeTransition(Duration.millis(500), aktionsphaseButton);
        fadeInButton9.setFromValue(1);
        fadeInButton9.setToValue(0);
        fadeInButton9.playFromStart();

        FadeTransition fadeInButton10 = new FadeTransition(Duration.millis(500), haeuserButton);
        fadeInButton10.setFromValue(1);
        fadeInButton10.setToValue(0);
        fadeInButton10.playFromStart();

        FadeTransition fadeInButton11 = new FadeTransition(Duration.millis(500), hypothekButton);
        fadeInButton11.setFromValue(1);
        fadeInButton11.setToValue(0);
        fadeInButton11.playFromStart();

        FadeTransition fadeInButton12 = new FadeTransition(Duration.millis(500), auktionenButton);
        fadeInButton12.setFromValue(1);
        fadeInButton12.setToValue(0);
        fadeInButton12.playFromStart();

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
