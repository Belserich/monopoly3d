/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui;

import de.btu.monopoly.ui.controller.LobbyController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author augat
 */
public class SceneManager extends Stage {

    private Stage stage;
    private static Scene scene;
    private static LobbyController controller;

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
        System.out.println("Lustig");
        scene.setRoot(root);

    }

    public static void changeSceneToLobby(FXMLLoader loader) throws IOException {

        Parent root = loader.load();
        controller = loader.getController();

        scene.setRoot(root);

    }

    public static void updateLobby() throws InterruptedException {
        if (controller != null) {
            controller.updateNames();
        }

    }

    public static void updateLobbyColors() throws InterruptedException {
        if (controller != null) {
            controller.updateColors();
        }

    }

    public static void openGameLayout() throws IOException {
        if (controller != null) {
            controller.loadGameLayout();
        }
    }

}
