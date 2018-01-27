/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui;

import de.btu.monopoly.Global;
import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.ui.controller.LobbyController;
import de.btu.monopoly.ui.util.Assets;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 *
 * @author augat
 */
public class MenuSceneManager extends Stage {

    private Stage stage;
    private Scene scene;
    private GameSceneManager sceneData;
    
    private Parent lobbyRoot;
    private LobbyController LobbyController;

    public MenuSceneManager() throws IOException {
        stage = this;
        
        if (!Assets.loadedFxContent())
            Assets.loadFxContent();
        
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/menu_scene.fxml"));
        
        scene = new Scene(root);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) {
                stage.setFullScreen(true);
            }
        });
        
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Monopoly-Information");
            alert.setHeaderText("Du beendest gerade Monopoly!");
            alert.setContentText("Bist du sicher?");
    
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                System.exit(0);
            }
            else {
                event.consume();
            }
    
        });
    }

    public static void appendText(String message) {
        // TODO appendText
    }

    public void changeScene(FXMLLoader loader) throws IOException {

        Parent root = loader.load();
        scene.setRoot(root);
    }

    public void changeSceneToLobby(FXMLLoader loader) throws IOException {

        Parent root = loader.load();
        LobbyController = loader.getController();

        scene.setRoot(root);
        lobbyRoot = root;

    }

    public void changeSceneBackToLobby() {
        scene.setRoot(lobbyRoot);
        LobbyController.animation();
    }

    public void changeSceneToGame(Lobby lobby) throws IOException {
        
        sceneData = new GameSceneManager(lobby.getController().getBoard());
        Global.ref().setGameSceneManager(sceneData);
        
        Scene gameScene = sceneData.getScene();
        
        Platform.runLater(() -> {
            stage.setScene(gameScene);
        });
    }

    public void updateLobby() throws InterruptedException {
        if (LobbyController != null) {
            LobbyController.updateNames();
        }

    }

    public void updateLobbyColors() throws InterruptedException {
        if (LobbyController != null) {
            LobbyController.updateColors();
        }

    }

    public void openGameLayout(Lobby lobby) throws IOException {
        if (LobbyController != null) {
            LobbyController.loadGameLayout(lobby);
        }
    }

    public void playerUpdate() {
        // TODO playerInitialise
    }

    public void geldPlayerUpdate() {
        // TODO geldupdate()
    }

    public void movePlayerUpdate() {
        // TODO playerUpdate
    }

    public void propertyUpdate() {
        // TODO propertyUpdate()
    }

    public void propertyStateUpdate() {
        // TODO propertyState()
    }
}
