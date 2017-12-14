/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui.controller;

import de.btu.monopoly.core.Game;
import de.btu.monopoly.data.player.Player;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.Initializable;

import com.jfoenix.controls.JFXTextArea;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.ui.SceneManager;
import static de.btu.monopoly.ui.controller.startGameController.client;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;


/**
 *
 * @author Eleonora kostova
 */
public class mainSceneController implements Initializable {

    
    @FXML private Label playerMoney1;
    @FXML private Button player1;
    @FXML private ScrollPane scene2;
   public static GameClient client;
  
    @Override
    public void initialize(URL location, ResourceBundle resources) {

       
    }
    public void changeName(ActionEvent event) throws IOException{
      
        SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/mainSecene.fxml")));
       
        
        //playerMoney1.setText(""+client.getPlayerOnClient().getMoney());
        playerMoney1.setText("1200");
    }
    
    public void getPopUpAction() throws IOException{
        Stage primaryStage;
        Parent root;
        
       
            if(player1.isPressed()){
                System.out.println("You pressed mee");
            primaryStage = (Stage)player1.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("popUpPlayer1.fxml"));
            
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
            
        }
        
    }
    
}
