/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui.controller;

import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.net.client.GameClient;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Eleonora Kostova
 */
public class MainSceneController implements Initializable {

    GameClient client;
    StackPane middlePane;

    @FXML
    private GridPane grid;
    @FXML
    private GridPane grid2;
    @FXML
    private GridPane gridMiddle;
    @FXML
    private GridPane PopupPane;

    //Spieler Figur
    @FXML
    private Circle player0;
    @FXML
    private Circle player1;
    @FXML
    private Circle player2;
    @FXML
    private Circle player3;
    @FXML
    private Circle player4;
    @FXML
    private Circle player5;

    //Spieler ID
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
    private Pane elisenStr;
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
    private Pane ereignis2;
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

    private Pane[] Felder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Felder = new Pane[40];
        Felder[0] = goField;
        Felder[1] = badStr;
        Felder[2] = gemenschaft1;
        Felder[3] = turmStr;
        Felder[4] = einkommenSt;
        Felder[5] = suedBahnhof;
        Felder[6] = chauseeStr;
        Felder[7] = ereignis1;
        Felder[8] = elisenStr;
        Felder[9] = postStr;
        Felder[10] = besuch;
        Felder[11] = seeStr;
        Felder[12] = elWerk;
        Felder[13] = hafenStr;
        Felder[14] = neueStr;
        Felder[15] = westBahnhof;
        Felder[16] = muenchenerStr;
        Felder[17] = gemeinschaft2;
        Felder[18] = wienerStr;
        Felder[19] = berlinerStr;
        Felder[20] = parkplatz;
        Felder[21] = theaterStr;
        Felder[22] = ereignis2;
        Felder[23] = museumStr;
        Felder[24] = opernplatz;
        Felder[25] = nordBahnhof;
        Felder[26] = lessingStr;
        Felder[27] = wasserWerk;
        Felder[28] = schillerStr;
        Felder[29] = goetheStr;
        Felder[30] = jailField;
        Felder[31] = rathhausPlatz;
        Felder[32] = hauptStr;
        Felder[33] = gemeinschat3;
        Felder[34] = bahnhofStr;
        Felder[35] = hauptBahnhof;
        Felder[36] = ereignis3;
        Felder[37] = parkStr;
        Felder[38] = zusatzSt;
        Felder[39] = schlossAllee;

        //Bilder hinzufuegen
        /*Background*/
        Image image = new Image(getClass().getResourceAsStream("/images/Lobby_Background.jpg"));
        grid.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        grid2.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        /*Mitte*/
        middlePane = new StackPane();
        Image image2 = new Image("/images/Monopoly_Logo.png");
        middlePane.setBackground(new Background(new BackgroundImage(image2, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        PopupPane.add(middlePane, 0, 1);

        // User aus Lobby holen
        client = Lobby.getPlayerClient();
        if (Lobby.getUsers() != null) {
            if (Lobby.getUsers().length >= 1) {
                player0Button.setText(Lobby.getUsers()[0][1]);
            }
            if (Lobby.getUsers().length >= 2) {
                player1Button.setText(Lobby.getUsers()[1][1]);
            }
            else {
                player1Button.setVisible(false);
                player1.setVisible(false);
            }
            if (Lobby.getUsers().length >= 3) {
                player2Button.setText(Lobby.getUsers()[2][1]);
            }
            else {
                player2Button.setVisible(false);
                player2.setVisible(false);
            }
            if (Lobby.getUsers().length >= 4) {
                player3Button.setText(Lobby.getUsers()[3][1]);
            }
            else {
                player3Button.setVisible(false);
                player3.setVisible(false);
            }
            if (Lobby.getUsers().length >= 5) {
                player4Button.setText(Lobby.getUsers()[4][1]);
            }
            else {
                player4Button.setVisible(false);
                player4.setVisible(false);
            }
            if (Lobby.getUsers().length >= 6) {
                player5Button.setText(Lobby.getUsers()[5][1]);
            }
            else {
                player5Button.setVisible(false);
                player5.setVisible(false);
            }
        }

        // Farben festlegen
        if (Lobby.getUsers() != null) {
            if (Lobby.getUsers().length >= 1) {
                player0Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[0][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                player0.setFill(Color.web(Lobby.getUsers()[0][4]));
            }
            if (Lobby.getUsers().length >= 2) {
                player1Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[1][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                player1.setFill(Color.web(Lobby.getUsers()[1][4]));
            }
            if (Lobby.getUsers().length >= 3) {
                player2Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[2][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                player2.setFill(Color.web(Lobby.getUsers()[2][4]));
            }
            if (Lobby.getUsers().length >= 4) {
                player3Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[3][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                player3.setFill(Color.web(Lobby.getUsers()[3][4]));
            }
            if (Lobby.getUsers().length >= 5) {
                player4Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[4][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                player4.setFill(Color.web(Lobby.getUsers()[4][4]));
            }
            if (Lobby.getUsers().length >= 6) {
                player5Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[5][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                player5.setFill(Color.web(Lobby.getUsers()[5][4]));
            }
        }

    }

    //TODO muss optimiert werden
    @FXML
    public void movePlayerAction() {

    }

    public void playerUpdate() {
        Player[] players = Lobby.getPlayerClient().getGame().getPlayers();
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                if (players.length >= 1) {
                    Felder[players[0].getPosition()].getChildren().add(player0);
                }
                if (players.length >= 2) {
                    Felder[players[1].getPosition()].getChildren().add(player1);
                }
                if (players.length >= 3) {
                    Felder[players[2].getPosition()].getChildren().add(player2);
                }
                if (players.length >= 4) {
                    Felder[players[3].getPosition()].getChildren().add(player3);
                }
                if (players.length >= 5) {
                    Felder[players[4].getPosition()].getChildren().add(player4);
                }
                if (players.length >= 6) {
                    Felder[players[5].getPosition()].getChildren().add(player5);
                }
                return null;
            }
        };
        Platform.runLater(task);

    }

    @FXML
    private void player0ButtonAction(ActionEvent event) throws IOException, InterruptedException {

    }

    @FXML
    private void player1ButtonAction(ActionEvent event) throws IOException, InterruptedException {

    }

    @FXML
    private void player2ButtonAction(ActionEvent event) throws IOException, InterruptedException {

    }

    @FXML
    private void player3ButtonAction(ActionEvent event) throws IOException, InterruptedException {

    }

    @FXML
    private void player4ButtonAction(ActionEvent event) throws IOException, InterruptedException {

    }

    @FXML
    private void player5ButtonAction(ActionEvent event) throws IOException, InterruptedException {

    }

}
