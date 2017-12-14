/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui.controller;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 *
 * @author Eleonora Kostova
 */
public class mainSceneTest extends Application {

    Button player1;
    Stage stage;
    Scene scene1, scene2;
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
       
        
           FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("/fxml/mainScene.fxml"));
    
        Parent root = loader.load();
       
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        
         }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
