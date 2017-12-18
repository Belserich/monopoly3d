/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui.controller;

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
    Lobby lobby;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Bilder hinzufuegen
        /*Background*/
        Image image = new Image(
                "https://images-na.ssl-images-amazon.com/images/S/sgp-catalog-images/region_US/di3a2-ACJM5H51YKB-Full-Image_GalleryBackground-en-US-1489722831648._RI_SX940_.jpg");
        grid.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        grid2.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        /*Mitte*/
        middlePane = new StackPane();
        Image image2 = new Image("https://upload.wikimedia.org/wikipedia/en/f/f9/Monopoly_pack_logo.png");
        middlePane.setBackground(new Background(new BackgroundImage(image2, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        PopupPane.add(middlePane, 0, 1);

        // User aus Lobby holen
        if (Lobby.getUsers() != null) {
            if (Lobby.getUsers().length >= 1) {
                player0Button.setText(Lobby.getUsers()[0][1]);
            }
            if (Lobby.getUsers().length >= 2) {
                player1Button.setText(Lobby.getUsers()[1][1]);
            } else {
                player1Button.setVisible(false);
                player1.setVisible(false);
            }
            if (Lobby.getUsers().length >= 3) {
                player2Button.setText(Lobby.getUsers()[2][1]);
            } else {
                player2Button.setVisible(false);
                player2.setVisible(false);
            }
            if (Lobby.getUsers().length >= 4) {
                player3Button.setText(Lobby.getUsers()[3][1]);
            } else {
                player3Button.setVisible(false);
                player3.setVisible(false);
            }
            if (Lobby.getUsers().length >= 5) {
                player4Button.setText(Lobby.getUsers()[4][1]);
            } else {
                player4Button.setVisible(false);
                player4.setVisible(false);
            }
            if (Lobby.getUsers().length >= 6) {
                player5Button.setText(Lobby.getUsers()[5][1]);
            } else {
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
    public void movePlayerAction(ActionEvent event) throws Exception {
        Task task = new Task<Void>() {

            @Override
            protected Void call() throws Exception {
                if (client.getPlayerOnClient().getPosition() == 0) {
                    getPaneId(Lobby.getUsers(), goField, 0);
                }
                if (client.getPlayerOnClient().getPosition() == 1) {
                    getPaneId(Lobby.getUsers(), badStr, 1);
                }
                if (client.getPlayerOnClient().getPosition() == 2) {
                    getPaneId(Lobby.getUsers(), gemenschaft1, 2);
                }
                if (client.getPlayerOnClient().getPosition() == 3) {
                    getPaneId(Lobby.getUsers(), turmStr, 3);
                }
                if (client.getPlayerOnClient().getPosition() == 4) {
                    getPaneId(Lobby.getUsers(), einkommenSt, 4);
                }
                if (client.getPlayerOnClient().getPosition() == 5) {
                    getPaneId(Lobby.getUsers(), suedBahnhof, 5);
                }
                if (client.getPlayerOnClient().getPosition() == 6) {
                    getPaneId(Lobby.getUsers(), chauseeStr, 6);
                }
                if (client.getPlayerOnClient().getPosition() == 7) {
                    getPaneId(Lobby.getUsers(), ereignis1, 7);
                }
                if (client.getPlayerOnClient().getPosition() == 8) {
                    getPaneId(Lobby.getUsers(), elisenStr, 8);
                }
                if (client.getPlayerOnClient().getPosition() == 9) {
                    getPaneId(Lobby.getUsers(), postStr, 9);
                }
                if (client.getPlayerOnClient().getPosition() == 10) {
                    getPaneId(Lobby.getUsers(), besuch, 10);
                }
                if (client.getPlayerOnClient().getPosition() == 11) {
                    getPaneId(Lobby.getUsers(), seeStr, 11);
                }
                if (client.getPlayerOnClient().getPosition() == 12) {
                    getPaneId(Lobby.getUsers(), elWerk, 12);
                }
                if (client.getPlayerOnClient().getPosition() == 13) {
                    getPaneId(Lobby.getUsers(), hafenStr, 13);
                }
                if (client.getPlayerOnClient().getPosition() == 14) {
                    getPaneId(Lobby.getUsers(), neueStr, 14);
                }
                if (client.getPlayerOnClient().getPosition() == 15) {
                    getPaneId(Lobby.getUsers(), westBahnhof, 15);
                }
                if (client.getPlayerOnClient().getPosition() == 16) {
                    getPaneId(Lobby.getUsers(), muenchenerStr, 16);
                }
                if (client.getPlayerOnClient().getPosition() == 17) {
                    getPaneId(Lobby.getUsers(), gemeinschaft2, 17);
                }
                if (client.getPlayerOnClient().getPosition() == 18) {
                    getPaneId(Lobby.getUsers(), wienerStr, 18);
                }
                if (client.getPlayerOnClient().getPosition() == 19) {
                    getPaneId(Lobby.getUsers(), berlinerStr, 19);
                }
                if (client.getPlayerOnClient().getPosition() == 20) {
                    getPaneId(Lobby.getUsers(), parkplatz, 20);
                }
                if (client.getPlayerOnClient().getPosition() == 21) {
                    getPaneId(Lobby.getUsers(), theaterStr, 21);
                }
                if (client.getPlayerOnClient().getPosition() == 22) {
                    getPaneId(Lobby.getUsers(), ereignis2, 22);
                }
                if (client.getPlayerOnClient().getPosition() == 23) {
                    getPaneId(Lobby.getUsers(), museumStr, 23);
                }
                if (client.getPlayerOnClient().getPosition() == 24) {
                    getPaneId(Lobby.getUsers(), opernplatz, 24);
                }
                if (client.getPlayerOnClient().getPosition() == 25) {
                    getPaneId(Lobby.getUsers(), nordBahnhof, 25);
                }
                if (client.getPlayerOnClient().getPosition() == 26) {
                    getPaneId(Lobby.getUsers(), lessingStr, 26);
                }
                if (client.getPlayerOnClient().getPosition() == 27) {
                    getPaneId(Lobby.getUsers(), schillerStr, 27);
                }
                if (client.getPlayerOnClient().getPosition() == 28) {
                    getPaneId(Lobby.getUsers(), wasserWerk, 28);
                }
                if (client.getPlayerOnClient().getPosition() == 29) {
                    getPaneId(Lobby.getUsers(), goetheStr, 29);
                }
                if (client.getPlayerOnClient().getPosition() == 30) {
                    getPaneId(Lobby.getUsers(), jailField, 30);
                }
                if (client.getPlayerOnClient().getPosition() == 31) {
                    getPaneId(Lobby.getUsers(), rathhausPlatz, 31);
                }
                if (client.getPlayerOnClient().getPosition() == 32) {
                    getPaneId(Lobby.getUsers(), hauptStr, 32);
                }
                if (client.getPlayerOnClient().getPosition() == 33) {
                    getPaneId(Lobby.getUsers(), gemeinschat3, 33);
                }
                if (client.getPlayerOnClient().getPosition() == 34) {
                    getPaneId(Lobby.getUsers(), bahnhofStr, 34);
                }
                if (client.getPlayerOnClient().getPosition() == 35) {
                    getPaneId(Lobby.getUsers(), hauptBahnhof, 35);
                }
                if (client.getPlayerOnClient().getPosition() == 36) {
                    getPaneId(Lobby.getUsers(), ereignis3, 36);
                }
                if (client.getPlayerOnClient().getPosition() == 37) {
                    getPaneId(Lobby.getUsers(), parkStr, 37);
                }
                if (client.getPlayerOnClient().getPosition() == 38) {
                    getPaneId(Lobby.getUsers(), zusatzSt, 38);
                }
                if (client.getPlayerOnClient().getPosition() == 39) {
                    getPaneId(Lobby.getUsers(), schlossAllee, 39);
                }

                return null;
            }
        };
        Platform.runLater(task);

    }

    public void getPaneId(String[][] user, Pane pane, int id) {
        if (user[0][1] == client.getPlayerOnClient().getName()) {
            pane.setShape(player0);
        } else if (user[1][1] == client.getPlayerOnClient().getName()) {
            pane.setShape(player1);
        } else if (user[2][1] == client.getPlayerOnClient().getName()) {
            pane.setShape(player2);
        } else if (user[3][1] == client.getPlayerOnClient().getName()) {
            pane.setShape(player3);
        } else if (user[4][1] == client.getPlayerOnClient().getName()) {
            pane.setShape(player4);
        } else if (user[5][1] == client.getPlayerOnClient().getName()) {
            pane.setShape(player5);
        }
    }

    @FXML
    private void player0ButtonAction(ActionEvent event) throws IOException, InterruptedException {

    }
}
