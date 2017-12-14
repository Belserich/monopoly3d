/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui.controller;

import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.ui.SceneManager;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.stage.Stage;

/**
 *
 * @author Eleonora kostova
 */
public class mainSceneController implements Initializable {

    @FXML
    private Label playerMoney1;
    @FXML
    private Button player0Button;
    @FXML
    private Button player1Button;
    @FXML
    private Button player2Button;
    @FXML
    private Button player3Button;
    @FXML
    private Button player4Button;
    @FXML
    private Button player5Button;

    public static GameClient client;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // User aus Lobby holen
        if (Lobby.getUsers() != null) {
            if (Lobby.getUsers().length >= 1) {
                player0Button.setText(Lobby.getUsers()[0][1]);
            }
            if (Lobby.getUsers().length >= 2) {
                player1Button.setText(Lobby.getUsers()[1][1]);
            }
            if (Lobby.getUsers().length >= 3) {
                player2Button.setText(Lobby.getUsers()[2][1]);
            }
            if (Lobby.getUsers().length >= 4) {
                player3Button.setText(Lobby.getUsers()[3][1]);
            }
            if (Lobby.getUsers().length >= 5) {
                player4Button.setText(Lobby.getUsers()[4][1]);
            }
            if (Lobby.getUsers().length >= 6) {
                player5Button.setText(Lobby.getUsers()[5][1]);
            }
        }

        // Farben festlegen
        if (GuiMessages.getPlayerColors() != null) {
            player0Button.setBackground(new Background(new BackgroundFill(GuiMessages.getPlayerColors()[0], CornerRadii.EMPTY, Insets.EMPTY)));
            player1Button.setBackground(new Background(new BackgroundFill(GuiMessages.getPlayerColors()[1], CornerRadii.EMPTY, Insets.EMPTY)));
            player2Button.setBackground(new Background(new BackgroundFill(GuiMessages.getPlayerColors()[2], CornerRadii.EMPTY, Insets.EMPTY)));
            player3Button.setBackground(new Background(new BackgroundFill(GuiMessages.getPlayerColors()[3], CornerRadii.EMPTY, Insets.EMPTY)));
            player4Button.setBackground(new Background(new BackgroundFill(GuiMessages.getPlayerColors()[4], CornerRadii.EMPTY, Insets.EMPTY)));
            player5Button.setBackground(new Background(new BackgroundFill(GuiMessages.getPlayerColors()[5], CornerRadii.EMPTY, Insets.EMPTY)));

        }
    }

    public void changeName(ActionEvent event) throws IOException {

        SceneManager.changeScene(new FXMLLoader(getClass().getResource("/fxml/mainSecene.fxml")));

        //playerMoney1.setText(""+client.getPlayerOnClient().getMoney());
        playerMoney1.setText("1200");
    }

    public void getPopUpAction() throws IOException {
        Stage primaryStage;
        Parent root;

        System.out.println("You pressed mee");
        primaryStage = new Stage();
        root = FXMLLoader.load(getClass().getResource("popUpPlayer1.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}
