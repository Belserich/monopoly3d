/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui.controller;

import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.data.player.Player;
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
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Eleonora kostova
 */
public class mainSceneController implements Initializable {

    private Game game;
    private GameBoard board;
    private Player[] players;
    public static GameClient client;

    //Spieler ID
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

    //Felder ID
    @FXML
    private Pane goField;
    @FXML
    private Pane badStr;
    @FXML
    private Pane gemenschaft1;
    @FXML
    private Pane turmStr;
    @FXML
    private Pane einkommenSt;
    @FXML
    private Pane suedBahnhof;
    @FXML
    private Pane chauseeStr;
    @FXML
    private Pane ereignis1;
    @FXML
    private Pane postStr;
    @FXML
    private Pane besuch;
    @FXML
    private Pane seeStr;
    @FXML
    private Pane elWerk;
    @FXML
    private Pane hafenStr;
    @FXML
    private Pane neueStr;
    @FXML
    private Pane westBahnhof;
    @FXML
    private Pane muenchenerStr;
    @FXML
    private Pane gemeinschaft2;
    @FXML
    private Pane wienerStr;
    @FXML
    private Pane berlinerStr;
    @FXML
    private Pane parkplatz;
    @FXML
    private Pane theaterStr;
    @FXML
    private Pane museumStr;
    @FXML
    private Pane opernplatz;
    @FXML
    private Pane nordBahnhof;
    @FXML
    private Pane lessingStr;
    @FXML
    private Pane wasserWerk;
    @FXML
    private Pane schillerStr;
    @FXML
    private Pane goetheStr;
    @FXML
    private Pane jailField;
    @FXML
    private Pane rathhausPlatz;
    @FXML
    private Pane hauptStr;
    @FXML
    private Pane gemeinschat3;
    @FXML
    private Pane bahnhofStr;
    @FXML
    private Pane hauptBahnhof;
    @FXML
    private Pane ereignis3;
    @FXML
    private Pane parkStr;
    @FXML
    private Pane zusatzSt;
    @FXML
    private Pane schlossAllee;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        game = new Game(players, client, 0);
        board = game.getBoard(); 
        // User aus Lobby holen
        if (Lobby.getUsers() != null) {
            if (Lobby.getUsers().length >= 1) {
                player0Button.setText(Lobby.getUsers()[0][1]);
            }
            if (Lobby.getUsers().length >= 2) {
                player1Button.setText(Lobby.getUsers()[1][1]);
            } else {
                player1Button.setText("");
            }
            if (Lobby.getUsers().length >= 3) {
                player2Button.setText(Lobby.getUsers()[2][1]);
            } else {
                player2Button.setText("");
            }
            if (Lobby.getUsers().length >= 4) {
                player3Button.setText(Lobby.getUsers()[3][1]);
            } else {
                player3Button.setBlendMode(BlendMode.valueOf(""));
            }
            if (Lobby.getUsers().length >= 5) {
                player4Button.setText(Lobby.getUsers()[4][1]);
            } else {
                player4Button.setText("");
            }
            if (Lobby.getUsers().length >= 6) {
                player5Button.setText(Lobby.getUsers()[5][1]);
            } else {
                player5Button.setText("");
            }
        }

        // Farben festlegen
        if (Lobby.getUsers() != null) {
            player0Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[0][4]), CornerRadii.EMPTY, Insets.EMPTY)));
            player1Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[1][4]), CornerRadii.EMPTY, Insets.EMPTY)));
            player2Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[2][4]), CornerRadii.EMPTY, Insets.EMPTY)));
            player3Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[3][4]), CornerRadii.EMPTY, Insets.EMPTY)));
            player4Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[4][4]), CornerRadii.EMPTY, Insets.EMPTY)));
            player5Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[5][4]), CornerRadii.EMPTY, Insets.EMPTY)));

        }
        //TODO
//        if(players[0].getPosition()== Integer.parseInt(board.getFields()[0].toString())){
//       
//    }
//        goField.setId(board.getFields()[0].toString());
//        badStr.setId(board.getFields()[1].toString());
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
