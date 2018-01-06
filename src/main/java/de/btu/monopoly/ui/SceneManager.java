/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.input.IOService;
import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.ui.controller.LobbyController;
import de.btu.monopoly.ui.controller.MainSceneController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 *
 * @author augat
 */
public class SceneManager extends Stage {

    private Stage stage;
    private static Scene scene;
    private static LobbyController LobbyController;
    private static MainSceneController GameController;

    public SceneManager() throws IOException {
        stage = this;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Menu.fxml"));

        scene = new Scene(root);

        stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();
    }

    public static void appendText(String message) {
        GameController.appendText(message);
    }

    public static void changeScene(FXMLLoader loader) throws IOException {

        Parent root = loader.load();
        scene.setRoot(root);
    }

    public static void changeSceneToLobby(FXMLLoader loader) throws IOException {

        Parent root = loader.load();
        LobbyController = loader.getController();

        scene.setRoot(root);

    }

    public static void changeSceneToGame(FXMLLoader loader) throws IOException {

        Parent root = loader.load();
        GameController = loader.getController();

        scene.setRoot(root);

    }

    public static void updateLobby() throws InterruptedException {
        if (LobbyController != null) {
            LobbyController.updateNames();
        }

    }

    public static void updateLobbyColors() throws InterruptedException {
        if (LobbyController != null) {
            LobbyController.updateColors();
        }

    }

    public static void openGameLayout() throws IOException {
        if (LobbyController != null) {
            LobbyController.loadGameLayout();
        }
    }

    
    public static void playerUpdate() {
        GameController.playerInitialise();
    }

    public static void geldPlayerUpdate() {
        GameController.geldUpdate();
    }

    public static void movePlayerUpdate() {
        GameController.playerUpdate();
    }

    public static void propertyUpdate() {
        GameController.propertyUpdate();
    }

    // -----------------------------------------------------------------------
    // Popups
    // -----------------------------------------------------------------------
    public static int buyPropertyPopup() {

        GridPane gridpane = new GridPane();
        Label label = new Label("Möchtest du die " + Lobby.getPlayerClient().getGame().getBoard().getFields()[Lobby.getPlayerClient().getPlayerOnClient().getPosition()].getName() + " kaufen?");

        JFXButton buyButton = new JFXButton();
        JFXButton dontBuyButton = new JFXButton();

        buyButton.setText("Kaufen");

        dontBuyButton.setText("Nicht kaufen");

        gridpane.add(label, 0, 0);
        gridpane.add(buyButton, 1, 0);
        gridpane.add(dontBuyButton, 1, 1);

        GameController.setPopup(gridpane);

        while (!buyButton.isPressed() || !dontBuyButton.isPressed()) {
            IOService.sleep(50);
            if (buyButton.isPressed()) {
                GameController.resetPopup(gridpane);
                return 1;
            }
            if (dontBuyButton.isPressed()) {
                GameController.resetPopup(gridpane);
                return 2;
            }
        }

        return -1;
    }

    public static int jailChoicePopup() {

        GridPane gridpane = new GridPane();

        Label label = new Label("Du bist im Gefängnis. Was möchtest du tun?");

        JFXButton rollButton = new JFXButton();
        JFXButton payButton = new JFXButton();
        JFXButton cardButton = new JFXButton();

        rollButton.setText("Würfeln");
        payButton.setText("Bezahlen");
        cardButton.setText("Frei-Karte nutzen");

        gridpane.add(label, 0, 0);
        gridpane.add(rollButton, 1, 0);
        gridpane.add(payButton, 1, 1);
        gridpane.add(cardButton, 1, 2);

        GameController.setPopup(gridpane);

        while (!rollButton.isPressed() || !payButton.isPressed() || !cardButton.isPressed()) {
            IOService.sleep(50);
            if (rollButton.isPressed()) {
                GameController.resetPopup(gridpane);
                return 1;
            }
            if (payButton.isPressed()) {
                GameController.resetPopup(gridpane);
                return 2;
            }
            if (cardButton.isPressed()) {
                GameController.resetPopup(gridpane);
                return 3;
            }
        }

        return -1;
    }

    public static int actionSequencePopup() {

        GridPane gridpane = new GridPane();

        Label label = new Label("Was möchtest du noch tun?");

        JFXButton nothingButton = new JFXButton();
        JFXButton buyHouseButton = new JFXButton();
        JFXButton removeHouseButton = new JFXButton();
        JFXButton addMortgageButton = new JFXButton();
        JFXButton removeMortgageButton = new JFXButton();
        JFXButton tradeButton = new JFXButton();

        nothingButton.setText("Nichts");
        buyHouseButton.setText("Haus kaufen");
        removeHouseButton.setText("Haus verkaufen");
        addMortgageButton.setText("Hypothek aufnehmen");
        removeMortgageButton.setText("Hypothek abbezahlen");
        tradeButton.setText("Handeln");

        gridpane.add(label, 0, 0);
        gridpane.add(nothingButton, 1, 0);
        gridpane.add(buyHouseButton, 1, 1);
        gridpane.add(removeHouseButton, 1, 2);
        gridpane.add(addMortgageButton, 1, 3);
        gridpane.add(removeMortgageButton, 1, 4);
        gridpane.add(tradeButton, 1, 5);

        GameController.setPopup(gridpane);

        while (!nothingButton.isPressed() || !buyHouseButton.isPressed() || !removeHouseButton.isPressed() || !addMortgageButton.isPressed() || !removeMortgageButton.isPressed() || !tradeButton.isPressed()) {
            IOService.sleep(50);
            if (nothingButton.isPressed()) {
                GameController.resetPopup(gridpane);
                return 1;
            }
            if (buyHouseButton.isPressed()) {
                GameController.resetPopup(gridpane);
                return 2;
            }
            if (removeHouseButton.isPressed()) {
                GameController.resetPopup(gridpane);
                return 3;
            }
            if (addMortgageButton.isPressed()) {
                GameController.resetPopup(gridpane);
                return 4;
            }
            if (removeMortgageButton.isPressed()) {
                GameController.resetPopup(gridpane);
                return 5;
            }
            if (tradeButton.isPressed()) {
                GameController.resetPopup(gridpane);
                return 6;
            }
        }

        return -1;
    }

    public static int askForFieldPopup(Player player, Field[] fields) {

        GridPane gridPane = new GridPane();

        Label label = new Label("Wähle ein Feld:");
        JFXComboBox fieldBox = new JFXComboBox();
        Button button = new Button();

        button.setText("Eingabe");

        for (Field field : fields) {
            fieldBox.getItems().add(field.getName());
        }

        fieldBox.getSelectionModel().selectFirst();

        gridPane.add(label, 0, 0);
        gridPane.add(fieldBox, 2, 0);
        gridPane.add(button, 1, 0);

        GameController.setPopup(gridPane);

        while (!button.isPressed()) {
            IOService.sleep(50);
        }

        GameController.resetPopup(gridPane);

        int id = 0;
        for (Field field : Lobby.getPlayerClient().getGame().getBoard().getFieldManager().getFields()) {

            id++;
            if (field == fields[fieldBox.getSelectionModel().getSelectedIndex()]) {
                return id;

            }
        }

        return -1;
    }

    public static int askForAuctionPopup() {
        // TODO Patrick

        // GameController.setPopup(gridpane);
        // GameController.resetPopup(gridpane);
        return -1;
    }

}
