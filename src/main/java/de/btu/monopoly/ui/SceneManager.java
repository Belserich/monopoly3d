/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui;

import com.jfoenix.controls.JFXButton;
import de.btu.monopoly.ui.controller.LobbyController;
import de.btu.monopoly.ui.controller.MainSceneController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    public static void movePlayerUpdate() {
        GameController.playerUpdate();
    }

    public static int buyPropertyPopup() throws InterruptedException {

        GridPane gridpane = new GridPane();

        Label label = new Label("Möchtest du die Straße kaufen?");

        JFXButton buyButton = new JFXButton();
        JFXButton dontBuyButton = new JFXButton();

        buyButton.setText("Kaufen");

        dontBuyButton.setText("Nicht kaufen");

        gridpane.add(label, 0, 0);
        gridpane.add(buyButton, 1, 0);
        gridpane.add(dontBuyButton, 1, 1);

        GameController.setPopup(gridpane);

        while (!buyButton.isPressed() || !dontBuyButton.isPressed()) {
            Thread.sleep(50);
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

    public static int jailChoicePopup() throws InterruptedException {

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
            Thread.sleep(50);
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

}
