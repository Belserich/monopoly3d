package de.btu.monopoly.ui.controller;

import de.btu.monopoly.menu.LobbyService;
import de.btu.monopoly.menu.MainMenu;
import de.btu.monopoly.ui.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
        Image image = new Image("https://cdn.vox-cdn.com/thumbor/LuyPPsKiSwRkL0i87Ur-8GDhSDM=/0x0:1144x566/1200x800/filters:focal(481x192:663x374)/cdn.vox-cdn.com/uploads/chorus_image/image/52679863/Screen_Shot_2017_01_10_at_10.41.40_AM.0.png");
        grid.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        stackPane.setBackground(new Background(new BackgroundImage(new Image("https://images-na.ssl-images-amazon.com/images/S/sgp-catalog-images/region_US/di3a2-ACJM5H51YKB-Full-Image_GalleryBackground-en-US-1489722831648._RI_SX940_.jpg"), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
    }

    // Button back
    @FXML
    private void backButtonAction(ActionEvent event) throws IOException {

        // Wechselt die Scene auf Menu
        SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/Menu.fxml")));
    }

    // Button joinLobby
    @FXML
    private void searchButtonAction(ActionEvent event) throws IOException, InterruptedException {
        // Server initialisieren
        MainMenu menu = new MainMenu();
        menu.joinGame(ipAdressTextField.getText());

        // Namen wechseln
        Thread.sleep(200);
        LobbyService.changeName(nameTextField.getText());

        if (GuiMessages.getConnectionError() == false) {
            // Wechselt die Scene auf Lobby
            SceneManager.changeSceneToLobby(new FXMLLoader(getClass().getResource("/fxml/Lobby.fxml")));
        } else {
            errorLabel.setText("Keine Verbindung möglich.");
        }

    }

}
