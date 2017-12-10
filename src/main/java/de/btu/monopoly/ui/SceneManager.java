/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui;

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
    private Scene scene;

    public SceneManager() throws IOException {
        stage = this;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Menu.fxml"));

        scene = new Scene(root);

        // stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();
    }

    public void changeScene(FXMLLoader loader) throws IOException {

        Parent root = loader.load();

        scene.setRoot(root);

    }
}
