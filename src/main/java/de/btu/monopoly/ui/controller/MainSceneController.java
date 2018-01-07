/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.field.PropertyField;
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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Eleonora Kostova
 */
public class MainSceneController implements Initializable {

    GameClient client;
    StackPane middlePane;

    //Geld Labels
    @FXML
    private Label geld0;
    @FXML
    private Label geld1;
    @FXML
    private Label geld2;
    @FXML
    private Label geld3;
    @FXML
    private Label geld4;
    @FXML
    private Label geld5;

    @FXML
    private GridPane grid;
    @FXML
    private GridPane grid2;
    @FXML
    private GridPane PopupPane;
    @FXML
    private GridPane player0Geld;
    @FXML
    private GridPane player1Geld;
    @FXML
    private GridPane player2Geld;
    @FXML
    private GridPane player3Geld;
    @FXML
    private GridPane player4Geld;
    @FXML
    private GridPane player5Geld;
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

    @FXML
    private Pane besitz1;
    @FXML
    private Pane besitz2;
    @FXML
    private Pane besitz3;
    @FXML
    private Pane besitz4;
    @FXML
    private Pane besitz5;
    @FXML
    private Pane besitz6;
    @FXML
    private Pane besitz7;
    @FXML
    private Pane besitz8;
    @FXML
    private Pane besitz9;
    @FXML
    private Pane besitz11;
    @FXML
    private Pane besitz12;
    @FXML
    private Pane besitz13;
    @FXML
    private Pane besitz14;
    @FXML
    private Pane besitz15;
    @FXML
    private Pane besitz16;
    @FXML
    private Pane besitz17;
    @FXML
    private Pane besitz18;
    @FXML
    private Pane besitz19;
    @FXML
    private Pane besitz21;
    @FXML
    private Pane besitz22;
    @FXML
    private Pane besitz23;
    @FXML
    private Pane besitz24;
    @FXML
    private Pane besitz25;
    @FXML
    private Pane besitz26;
    @FXML
    private Pane besitz27;
    @FXML
    private Pane besitz28;
    @FXML
    private Pane besitz29;
    @FXML
    private Pane besitz31;
    @FXML
    private Pane besitz32;
    @FXML
    private Pane besitz33;
    @FXML
    private Pane besitz34;
    @FXML
    private Pane besitz35;
    @FXML
    private Pane besitz36;
    @FXML
    private Pane besitz37;
    @FXML
    private Pane besitz38;
    @FXML
    private Pane besitz39;

    @FXML
    private JFXTextArea textArea;

    private Pane[] Felder;
    private Pane[] BesitzanzeigeFelder;

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

        BesitzanzeigeFelder = new Pane[40];

        BesitzanzeigeFelder[1] = besitz1;
        BesitzanzeigeFelder[2] = besitz2;
        BesitzanzeigeFelder[3] = besitz3;
        BesitzanzeigeFelder[4] = besitz4;
        BesitzanzeigeFelder[5] = besitz5;
        BesitzanzeigeFelder[6] = besitz6;
        BesitzanzeigeFelder[7] = besitz7;
        BesitzanzeigeFelder[8] = besitz8;
        BesitzanzeigeFelder[9] = besitz9;
        BesitzanzeigeFelder[11] = besitz11;
        BesitzanzeigeFelder[12] = besitz12;
        BesitzanzeigeFelder[13] = besitz13;
        BesitzanzeigeFelder[14] = besitz14;
        BesitzanzeigeFelder[15] = besitz15;
        BesitzanzeigeFelder[16] = besitz16;
        BesitzanzeigeFelder[17] = besitz17;
        BesitzanzeigeFelder[18] = besitz18;
        BesitzanzeigeFelder[19] = besitz19;
        BesitzanzeigeFelder[21] = besitz21;
        BesitzanzeigeFelder[22] = besitz22;
        BesitzanzeigeFelder[23] = besitz23;
        BesitzanzeigeFelder[24] = besitz24;
        BesitzanzeigeFelder[25] = besitz25;
        BesitzanzeigeFelder[26] = besitz26;
        BesitzanzeigeFelder[27] = besitz27;
        BesitzanzeigeFelder[28] = besitz28;
        BesitzanzeigeFelder[29] = besitz29;
        BesitzanzeigeFelder[31] = besitz31;
        BesitzanzeigeFelder[32] = besitz32;
        BesitzanzeigeFelder[33] = besitz33;
        BesitzanzeigeFelder[34] = besitz34;
        BesitzanzeigeFelder[35] = besitz35;
        BesitzanzeigeFelder[36] = besitz36;
        BesitzanzeigeFelder[37] = besitz37;
        BesitzanzeigeFelder[38] = besitz38;
        BesitzanzeigeFelder[39] = besitz39;

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

        // User aus Lobby holen ud Farben setzen
        //TODO Bugfixes
        client = Lobby.getPlayerClient();

    }

    public void playerInitialise() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                if (Lobby.getUsers() != null) {
                    for (int i = 0; i < Lobby.getUsers().length; i++) {
                        if (Lobby.getPlayerClient().getPlayerOnClient().getName().equals(Lobby.getUsers()[i][1])) {
                            player0Button.setText(Lobby.getUsers()[i][1]);
                            player0Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[i][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                            player0.setFill(Color.web(Lobby.getUsers()[i][4]));
                            player0Button.setVisible(true);
                            player0.setVisible(true);
                        }
                        else {

                            if (Lobby.getUsers().length >= 2) {
                                if (player1Button.getText().equals("frei")) {
                                    player1Button.setText(Lobby.getUsers()[i][1]);
                                    player1Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[i][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                                    player1.setFill(Color.web(Lobby.getUsers()[i][4]));
                                    player1Button.setVisible(true);
                                    player1.setVisible(true);
                                    continue;
                                }
                            }
                            if (Lobby.getUsers().length >= 3) {
                                if (player2Button.getText().equals("frei")) {
                                    player2Button.setText(Lobby.getUsers()[i][1]);
                                    player2Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[i][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                                    player2.setFill(Color.web(Lobby.getUsers()[i][4]));
                                    player2Button.setVisible(true);
                                    player2.setVisible(true);
                                    continue;
                                }
                            }
                            if (Lobby.getUsers().length >= 4) {
                                if (player3Button.getText().equals("frei")) {
                                    player3Button.setText(Lobby.getUsers()[i][1]);
                                    player3Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[i][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                                    player3.setFill(Color.web(Lobby.getUsers()[i][4]));
                                    player3Button.setVisible(true);
                                    player3.setVisible(true);
                                    continue;
                                }
                            }
                            if (Lobby.getUsers().length >= 5) {
                                if (player4Button.getText().equals("frei")) {
                                    player4Button.setText(Lobby.getUsers()[i][1]);
                                    player4Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[i][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                                    player4.setFill(Color.web(Lobby.getUsers()[i][4]));
                                    player4Button.setVisible(true);
                                    player4.setVisible(true);
                                    continue;
                                }
                            }
                            if (Lobby.getUsers().length >= 6) {
                                if (player5Button.getText().equals("frei")) {
                                    player5Button.setText(Lobby.getUsers()[i][1]);
                                    player5Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[i][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                                    player5.setFill(Color.web(Lobby.getUsers()[i][4]));
                                    player5Button.setVisible(true);
                                    player5.setVisible(true);
                                }
                            }
                        }
                    }
                }

                return null;
            }
        };
        Platform.runLater(task);

    }

    public void appendText(String message) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                textArea.appendText(message);
                return null;
            }
        };
        Platform.runLater(task);
    }

    public void geldUpdate0(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {

                geld0.setText("Geld: " + players[client.getPlayerOnClient().getId()].getMoney());
                player0Geld.add(geld0, 0, 0);
                return null;
            }

        };
        Platform.runLater(task);

    }

    public void geldUpdate1(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                for (int i = 0; i < players.length; i++) {
                    if (player1Button.getText().equals(players[i].getName())) {
                        geld1.setText("Geld: " + players[i].getMoney());
                        player1Geld.add(geld1, 0, 0);
                    }
                }
                return null;
            }

        };
        Platform.runLater(task);

    }

    public void geldUpdate2(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                for (int i = 0; i < players.length; i++) {
                    if (player2Button.getText().equals(players[i].getName())) {
                        geld2.setText("Geld: " + players[i].getMoney());
                        player2Geld.add(geld2, 0, 0);
                    }
                }
                return null;
            }

        };
        Platform.runLater(task);

    }

    public void geldUpdate3(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                for (int i = 0; i < players.length; i++) {
                    if (player3Button.getText().equals(players[i].getName())) {
                        geld3.setText("Geld: " + players[i].getMoney());
                        player3Geld.add(geld3, 0, 0);
                    }
                }
                return null;
            }

        };
        Platform.runLater(task);

    }

    public void geldUpdate4(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                for (int i = 0; i < players.length; i++) {
                    if (player4Button.getText().equals(players[i].getName())) {
                        geld4.setText("Geld: " + players[i].getMoney());
                        player4Geld.add(geld4, 0, 0);
                    }
                }
                return null;
            }

        };
        Platform.runLater(task);

    }

    public void geldUpdate5(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                for (int i = 0; i < players.length; i++) {
                    if (player5Button.getText().equals(players[i].getName())) {
                        geld5.setText("Geld: " + players[i].getMoney());
                        player5Geld.add(geld5, 0, 0);
                    }
                }
                return null;
            }

        };
        Platform.runLater(task);

    }

    public void geldUpdate() {
        Player[] players = Lobby.getPlayerClient().getGame().getPlayers();
        if (players.length >= 1) {
            geldUpdate0(players);
        }
        if (players.length >= 2) {
            geldUpdate1(players);
        }
        if (players.length >= 3) {
            geldUpdate2(players);
        }
        if (players.length >= 4) {
            geldUpdate3(players);
        }
        if (players.length >= 5) {
            geldUpdate4(players);
        }
        if (players.length >= 6) {
            geldUpdate5(players);
        }

    }

    public void playerUpdate() {
        Player[] players = Lobby.getPlayerClient().getGame().getPlayers();

        if (players.length >= 1) {
            playerUpdate0(players);
        }
        if (players.length >= 2) {
            playerUpdate1(players);
        }
        if (players.length >= 3) {
            playerUpdate2(players);
        }
        if (players.length >= 4) {
            playerUpdate3(players);
        }
        if (players.length >= 5) {
            playerUpdate4(players);
        }
        if (players.length >= 6) {
            playerUpdate5(players);
        }

    }

    public void playerUpdate0(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                player0.layoutXProperty().bind(Felder[players[0].getPosition()].widthProperty().subtract(player0.centerXProperty()).divide(2));
                player0.layoutYProperty().bind(Felder[players[0].getPosition()].heightProperty().subtract(player0.centerYProperty()).divide(2));
                Felder[players[0].getPosition()].getChildren().add(player0);
                return null;
            }
        };
        Platform.runLater(task);
    }

    public void playerUpdate1(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                player1.layoutXProperty().bind(Felder[players[1].getPosition()].widthProperty().subtract(player1.centerXProperty()).divide(2));
                player1.layoutYProperty().bind(Felder[players[1].getPosition()].heightProperty().subtract(player1.centerYProperty()).divide(2));
                Felder[players[1].getPosition()].getChildren().add(player1);
                return null;
            }
        };
        Platform.runLater(task);
    }

    public void playerUpdate2(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                player2.layoutXProperty().bind(Felder[players[2].getPosition()].widthProperty().subtract(player2.centerXProperty()).divide(2));
                player2.layoutYProperty().bind(Felder[players[2].getPosition()].heightProperty().subtract(player2.centerYProperty()).divide(2));
                Felder[players[2].getPosition()].getChildren().add(player2);
                return null;
            }
        };
        Platform.runLater(task);
    }

    public void playerUpdate3(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                player3.layoutXProperty().bind(Felder[players[3].getPosition()].widthProperty().subtract(player3.centerXProperty()).divide(2));
                player3.layoutYProperty().bind(Felder[players[3].getPosition()].heightProperty().subtract(player3.centerYProperty()).divide(2));
                Felder[players[3].getPosition()].getChildren().add(player3);
                return null;
            }
        };
        Platform.runLater(task);
    }

    public void playerUpdate4(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                player4.layoutXProperty().bind(Felder[players[4].getPosition()].widthProperty().subtract(player4.centerXProperty()).divide(2));
                player4.layoutYProperty().bind(Felder[players[4].getPosition()].heightProperty().subtract(player4.centerYProperty()).divide(2));
                Felder[players[4].getPosition()].getChildren().add(player4);
                return null;
            }
        };
        Platform.runLater(task);
    }

    public void playerUpdate5(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                player5.layoutXProperty().bind(Felder[players[5].getPosition()].widthProperty().subtract(player5.centerXProperty()).divide(2));
                player5.layoutYProperty().bind(Felder[players[5].getPosition()].heightProperty().subtract(player5.centerYProperty()).divide(2));
                Felder[players[5].getPosition()].getChildren().add(player5);
                return null;
            }
        };
        Platform.runLater(task);
    }

    public void propertyUpdate() {
        if (Lobby.getPlayerClient().getGame().getBoard() != null) {
            Field[] fields = Lobby.getPlayerClient().getGame().getBoard().getFieldManager().getFields();
            int counter = 0;

            for (Field field : fields) {
                if (field instanceof PropertyField) {
                    if (((PropertyField) field).getOwner() != null) {
                        if (BesitzanzeigeFelder[counter] != null) {
                            BesitzanzeigeFelder[counter].setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[((PropertyField) field).getOwner().getId()][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                        }
                    }
                }
                counter++;
            }
        }
    }

    //TODO Testen
    @FXML
    private void player0ButtonAction(ActionEvent event) throws IOException, InterruptedException {
        Player[] players = Lobby.getPlayerClient().getGame().getPlayers();
        //  String fields =  Lobby.getPlayerClient().getGame().getBoard().getFieldManager().toStringOwned(players[client.getPlayerOnClient().getId()]);
        GridPane player0Pane = new GridPane();
        ScrollPane scroll = new ScrollPane();
        VBox box = new VBox();
        player0Pane.setAlignment(Pos.CENTER);
        scroll.setCenterShape(true);
        player0Pane.add(scroll, 0, 0);
        scroll.setContent(box);
        Label geld = new Label(" hat in Konto: " + players[client.getPlayerOnClient().getId()].getMoney());
        JFXButton player = new JFXButton();
        JFXButton exit = new JFXButton("Exit");
        //Label properties = new Label("besitzt: " + fields);
        Label jail = new Label(" ist in Gefängnis seit : " + players[client.getPlayerOnClient().getId()].getDaysInJail());
        player.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[client.getPlayerOnClient().getId()][4]), CornerRadii.EMPTY, Insets.EMPTY)));
        player.setText(Lobby.getUsers()[client.getPlayerOnClient().getId()][1]);
        player.setPrefSize(150, 10);
        box.getChildren().addAll(player, geld, jail, exit);
        box.setAlignment(Pos.CENTER);

        setPopup(player0Pane);

        exit.setOnAction(e -> {
            resetPopup(player0Pane);
        });
    }

    @FXML
    private void player1ButtonAction(ActionEvent event) throws IOException, InterruptedException {
        Player[] players = Lobby.getPlayerClient().getGame().getPlayers();
        GridPane player1Pane = new GridPane();
        ScrollPane scroll = new ScrollPane();
        VBox box = new VBox();
        player1Pane.setAlignment(Pos.CENTER);
        scroll.setCenterShape(true);
        player1Pane.add(scroll, 0, 0);
        scroll.setContent(box);
        Label geld;
        JFXButton player = new JFXButton();
        JFXButton exit = new JFXButton("Exit");
        Label jail;
        if (client.getPlayerOnClient().getId() == 0) {
            geld = new Label(" hat in Konto: " + players[1].getMoney());
            jail = new Label(" ist in Gefängnis seit : " + players[1].getDaysInJail());
            player.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[1][4]), CornerRadii.EMPTY, Insets.EMPTY)));
            player.setText(Lobby.getUsers()[1][1]);
        }
        else {
            geld = new Label(" hat in Konto: " + players[0].getMoney());
            jail = new Label(" ist in Gefängnis seit : " + players[0].getDaysInJail());

            player.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[0][4]), CornerRadii.EMPTY, Insets.EMPTY)));
            player.setText(Lobby.getUsers()[0][1]);
        }
        player.setPrefSize(150, 10);
        box.getChildren().addAll(player, geld, jail, exit);
        box.setAlignment(Pos.CENTER);

        setPopup(player1Pane);

        exit.setOnAction(e -> {
            resetPopup(player1Pane);
        });
    }

    @FXML
    private void player2ButtonAction(ActionEvent event) throws IOException, InterruptedException {
        Player[] players = Lobby.getPlayerClient().getGame().getPlayers();
        GridPane player2Pane = new GridPane();
        ScrollPane scroll = new ScrollPane();
        VBox box = new VBox();
        player2Pane.setAlignment(Pos.CENTER);
        scroll.setCenterShape(true);
        player2Pane.add(scroll, 0, 0);
        scroll.setContent(box);
        Label geld;
        JFXButton player = new JFXButton();
        JFXButton exit = new JFXButton("Exit");
        Label jail;
        if (client.getPlayerOnClient().getId() == 2) {
            geld = new Label(" hat in Konto: " + players[1].getMoney());
            jail = new Label(" ist in Gefängnis seit : " + players[1].getDaysInJail());
            player.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[1][4]), CornerRadii.EMPTY, Insets.EMPTY)));
            player.setText(Lobby.getUsers()[1][1]);
        }
        else {
            geld = new Label(" hat in Konto: " + players[2].getMoney());
            jail = new Label(" ist in Gefängnis seit : " + players[2].getDaysInJail());
            player.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[2][4]), CornerRadii.EMPTY, Insets.EMPTY)));
            player.setText(Lobby.getUsers()[2][1]);
        }

        player.setPrefSize(150, 10);
        box.getChildren().addAll(player, geld, jail, exit);
        box.setAlignment(Pos.CENTER);
        setPopup(player2Pane);

        exit.setOnAction(e -> {
            resetPopup(player2Pane);
        });
    }

    @FXML
    private void player3ButtonAction(ActionEvent event) throws IOException, InterruptedException {
        Player[] players = Lobby.getPlayerClient().getGame().getPlayers();
        GridPane player3Pane = new GridPane();
        ScrollPane scroll = new ScrollPane();
        VBox box = new VBox();
        player3Pane.setAlignment(Pos.CENTER);
        scroll.setCenterShape(true);
        player3Pane.add(scroll, 0, 0);
        scroll.setContent(box);
        Label geld;
        JFXButton player = new JFXButton();
        JFXButton exit = new JFXButton("Exit");
        Label jail;
        if (client.getPlayerOnClient().getId() == 3) {
            geld = new Label(" hat in Konto: " + players[2].getMoney());
            jail = new Label(" ist in Gefängnis seit : " + players[2].getDaysInJail());
            player.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[2][4]), CornerRadii.EMPTY, Insets.EMPTY)));
            player.setText(Lobby.getUsers()[2][1]);
        }
        else {
            geld = new Label(" hat in Konto: " + players[3].getMoney());
            jail = new Label(" ist in Gefängnis seit : " + players[3].getDaysInJail());
            player.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[3][4]), CornerRadii.EMPTY, Insets.EMPTY)));
            player.setText(Lobby.getUsers()[3][1]);
        }
        player.setPrefSize(150, 10);
        box.getChildren().addAll(player, geld, jail, exit);
        box.setAlignment(Pos.CENTER);

        setPopup(player3Pane);

        exit.setOnAction(e -> {
            resetPopup(player3Pane);
        });
    }

    @FXML
    private void player4ButtonAction(ActionEvent event) throws IOException, InterruptedException {
        Player[] players = Lobby.getPlayerClient().getGame().getPlayers();
        GridPane player4Pane = new GridPane();
        ScrollPane scroll = new ScrollPane();
        VBox box = new VBox();
        player4Pane.setAlignment(Pos.CENTER);
        scroll.setCenterShape(true);
        player4Pane.add(scroll, 0, 0);
        scroll.setContent(box);
        Label geld;
        JFXButton player = new JFXButton();
        JFXButton exit = new JFXButton("Exit");
        Label jail;
        if (client.getPlayerOnClient().getId() == 4) {
            geld = new Label(" hat in Konto: " + players[3].getMoney());
            jail = new Label(" ist in Gefängnis seit : " + players[3].getDaysInJail());
            player.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[3][4]), CornerRadii.EMPTY, Insets.EMPTY)));
            player.setText(Lobby.getUsers()[3][1]);
        }
        else {
            geld = new Label(" hat in Konto: " + players[4].getMoney());
            jail = new Label(" ist in Gefängnis seit : " + players[4].getDaysInJail());
            player.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[4][4]), CornerRadii.EMPTY, Insets.EMPTY)));
            player.setText(Lobby.getUsers()[4][1]);
        }
//        Label geld = new Label(" hat in Konto: " + players[4].getMoney());
//        JFXButton player = new JFXButton();
//        JFXButton exit = new JFXButton("Exit");
//        player.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[4][4]), CornerRadii.EMPTY, Insets.EMPTY)));
//        player.setText(Lobby.getUsers()[4][1]);
        player.setPrefSize(150, 10);
        box.getChildren().addAll(player, geld, jail, exit);
        box.setAlignment(Pos.CENTER);

        setPopup(player4Pane);

        exit.setOnAction(e -> {
            resetPopup(player4Pane);
        });
    }

    @FXML
    private void player5ButtonAction(ActionEvent event) throws IOException, InterruptedException {
        Player[] players = Lobby.getPlayerClient().getGame().getPlayers();
        GridPane player5Pane = new GridPane();
        ScrollPane scroll = new ScrollPane();
        VBox box = new VBox();
        player5Pane.setAlignment(Pos.CENTER);
        scroll.setCenterShape(true);
        player5Pane.add(scroll, 0, 0);
        scroll.setContent(box);
        Label geld;
        JFXButton player = new JFXButton();
        JFXButton exit = new JFXButton("Exit");
        Label jail;
        if (client.getPlayerOnClient().getId() == 5) {
            geld = new Label(" hat in Konto: " + players[4].getMoney());
            jail = new Label(" ist in Gefängnis seit : " + players[4].getDaysInJail());
            player.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[4][4]), CornerRadii.EMPTY, Insets.EMPTY)));
            player.setText(Lobby.getUsers()[4][1]);
        }
        else {
            geld = new Label(" hat in Konto: " + players[5].getMoney());
            jail = new Label(" ist in Gefängnis seit : " + players[5].getDaysInJail());
            player.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[5][4]), CornerRadii.EMPTY, Insets.EMPTY)));
            player.setText(Lobby.getUsers()[5][1]);
        }
//        Label geld = new Label(players[5].getName() + " hat in Konto: " + players[5].getMoney());
//        JFXButton player = new JFXButton();
//        JFXButton exit = new JFXButton("Exit");
//        player.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[5][4]), CornerRadii.EMPTY, Insets.EMPTY)));
//        player.setText(Lobby.getUsers()[5][1]);
        player.setPrefSize(150, 10);
        box.getChildren().addAll(player, geld, jail, exit);
        box.setAlignment(Pos.CENTER);

        setPopup(player5Pane);

        exit.setOnAction(e -> {
            resetPopup(player5Pane);
        });

    }

    public void setPopup(GridPane gridpane) {

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                PopupPane.getChildren().remove(middlePane);
                PopupPane.add(gridpane, 0, 1);
                return null;
            }
        };
        Platform.runLater(task);

    }

    public void resetPopup(GridPane gridpane) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                PopupPane.getChildren().remove(gridpane);
                PopupPane.add(middlePane, 0, 1);
                return null;
            }
        };
        Platform.runLater(task);
    }

}
