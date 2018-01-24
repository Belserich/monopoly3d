/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import de.btu.monopoly.core.service.IOService;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.net.client.GameClient;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.util.Duration;

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
    private GridPane mainGrid;
    @FXML
    private GridPane grid;
    @FXML
    private GridPane grid2;
    @FXML
    private GridPane PopupPane;
    @FXML
    private GridPane PopupPane2;
    @FXML
    private GridPane centerPane;

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
    private Pane gemeinschaft1;
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
    private Pane gemeinschaft3;
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

    // Besitzanzeigen
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

    // Straßennamen (Labels)
    @FXML
    private Label label0;
    @FXML
    private Label label1;
    @FXML
    private Label label2;
    @FXML
    private Label label3;
    @FXML
    private Label label4;
    @FXML
    private Label label5;
    @FXML
    private Label label6;
    @FXML
    private Label label7;
    @FXML
    private Label label8;
    @FXML
    private Label label9;
    @FXML
    private Label label10;
    @FXML
    private Label label11;
    @FXML
    private Label label12;
    @FXML
    private Label label13;
    @FXML
    private Label label14;
    @FXML
    private Label label15;
    @FXML
    private Label label16;
    @FXML
    private Label label17;
    @FXML
    private Label label18;
    @FXML
    private Label label19;
    @FXML
    private Label label20;
    @FXML
    private Label label21;
    @FXML
    private Label label22;
    @FXML
    private Label label23;
    @FXML
    private Label label24;
    @FXML
    private Label label25;
    @FXML
    private Label label26;
    @FXML
    private Label label27;
    @FXML
    private Label label28;
    @FXML
    private Label label29;
    @FXML
    private Label label30;
    @FXML
    private Label label31;
    @FXML
    private Label label32;
    @FXML
    private Label label33;
    @FXML
    private Label label34;
    @FXML
    private Label label35;
    @FXML
    private Label label36;
    @FXML
    private Label label37;
    @FXML
    private Label label38;
    @FXML
    private Label label39;
    private boolean initToggle = true;

    //Häser/Hypothek Anzeige
    @FXML
    private Pane haus1;
    @FXML
    private Pane haus3;
    @FXML
    private Pane haus5;
    @FXML
    private Pane haus6;
    @FXML
    private Pane haus8;
    @FXML
    private Pane haus9;
    @FXML
    private Pane haus11;
    @FXML
    private Pane haus12;
    @FXML
    private Pane haus13;
    @FXML
    private Pane haus14;
    @FXML
    private Pane haus15;
    @FXML
    private Pane haus16;
    @FXML
    private Pane haus18;
    @FXML
    private Pane haus19;
    @FXML
    private Pane haus21;
    @FXML
    private Pane haus23;
    @FXML
    private Pane haus24;
    @FXML
    private Pane haus25;
    @FXML
    private Pane haus26;
    @FXML
    private Pane haus27;
    @FXML
    private Pane haus28;
    @FXML
    private Pane haus29;
    @FXML
    private Pane haus31;
    @FXML
    private Pane haus32;
    @FXML
    private Pane haus34;
    @FXML
    private Pane haus35;
    @FXML
    private Pane haus37;
    @FXML
    private Pane haus39;

    private Pane[] Felder;
    private Pane[] BesitzanzeigeFelder;
    private Pane[] RentanzeigeFelder;

    // Speichert die letzte Position für das Vorrücken
    private int lastPosPlayer0 = 0;
    private int lastPosPlayer1 = 0;
    private int lastPosPlayer2 = 0;
    private int lastPosPlayer3 = 0;
    private int lastPosPlayer4 = 0;
    private int lastPosPlayer5 = 0;

    // Protokollfenster
    @FXML
    private JFXTextArea textArea;

    // Speichern der ID welche das ButtonPopup anzeigen soll
    private int player0ButtonID;
    private int player1ButtonID;
    private int player2ButtonID;
    private int player3ButtonID;
    private int player4ButtonID;
    private int player5ButtonID;

    // -------------------------------------------------------------------------
    // Initialisierung von Buttons und interner Variablen
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Felder = new Pane[40];
        Felder[0] = goField;
        Felder[1] = badStr;
        Felder[2] = gemeinschaft1;
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
        Felder[27] = schillerStr;
        Felder[28] = wasserWerk;
        Felder[29] = goetheStr;
        Felder[30] = jailField;
        Felder[31] = rathhausPlatz;
        Felder[32] = hauptStr;
        Felder[33] = gemeinschaft3;
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

        RentanzeigeFelder = new Pane[40];
        String style = "-fx-rotate:90";

        RentanzeigeFelder[1] = haus1;
        RentanzeigeFelder[3] = haus3;
        RentanzeigeFelder[5] = haus5;
        RentanzeigeFelder[6] = haus6;
        RentanzeigeFelder[8] = haus8;
        RentanzeigeFelder[9] = haus9;
        RentanzeigeFelder[11] = haus11;
        //haus11.setStyle(style);
        RentanzeigeFelder[12] = haus12;
        // haus12.setStyle(style);
        RentanzeigeFelder[13] = haus13;
        //haus13.setStyle(style);
        RentanzeigeFelder[14] = haus14;
        // haus14.setStyle(style);
        RentanzeigeFelder[15] = haus15;
        //haus15.setStyle(style);
        RentanzeigeFelder[16] = haus16;
        // haus16.setStyle(style);
        RentanzeigeFelder[18] = haus18;
        // haus18.setStyle(style);
        RentanzeigeFelder[19] = haus19;
        // haus19.setStyle(style);
        RentanzeigeFelder[21] = haus21;
        RentanzeigeFelder[23] = haus23;
        RentanzeigeFelder[24] = haus24;
        RentanzeigeFelder[25] = haus25;
        RentanzeigeFelder[26] = haus26;
        RentanzeigeFelder[27] = haus27;
        RentanzeigeFelder[28] = haus28;
        RentanzeigeFelder[29] = haus29;
        RentanzeigeFelder[31] = haus31;
        //haus31.setStyle(style);
        RentanzeigeFelder[32] = haus32;
        //haus32.setStyle(style);
        RentanzeigeFelder[34] = haus34;
        //haus34.setStyle(style);
        RentanzeigeFelder[35] = haus35;
        // haus35.setStyle(style);
        RentanzeigeFelder[37] = haus37;
        // haus37.setStyle(style);
        RentanzeigeFelder[39] = haus39;
        //haus39.setStyle(style);

        //Bilder hinzufuegen
        /*Background*/
        Image image = new Image(getClass().getResourceAsStream("/images/Lobby_Background.jpg"));
        grid.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        grid2.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        /*Mitte*/
        middlePane = new StackPane();
        Image image2 = new Image("/images/Monopoly_Logo.png");
        middlePane.setBackground(new Background(new BackgroundImage(image2, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
        PopupPane.add(middlePane, 0, 0);

        // User aus lobby holen und Farben setzen
        client = Lobby.getPlayerClient();

        // Animation
        mainGrid.setOpacity(0);
        FadeTransition fadeGrid = new FadeTransition(Duration.millis(800), mainGrid);
        fadeGrid.setFromValue(0);
        fadeGrid.setToValue(1);
        fadeGrid.playFromStart();
    }

    /**
     * Initialisierung der Spielerbuttons und Spielfiguren
     */
    public void playerInitialise() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                if (Lobby.getUsers() != null) {
                    for (int i = 0; i < Lobby.getUsers().length; i++) {
                        if (Lobby.getPlayerClient().getPlayerOnClient().getName().equals(Lobby.getUsers()[i][1])) {
                            FadeTransition fadeButton = new FadeTransition(Duration.millis(500), player0Button);
                            fadeButton.setFromValue(0);
                            fadeButton.setToValue(1);
                            fadeButton.playFromStart();
                            player0.setVisible(true);
                            if (Lobby.getUsers()[i][1].contains("(")) {
                                player0Button.setText(Lobby.getUsers()[i][1].substring(0, Lobby.getUsers()[i][1].indexOf('(') - 1));
                            }
                            else {
                                player0Button.setText(Lobby.getUsers()[i][1]);
                            }
                            player0Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[i][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                            if (Color.web(Lobby.getUsers()[i][4]).getBrightness() <= 0.8) {
                                player0Button.setTextFill(Color.WHITE);
                            }
                            player0.setFill(Color.web(Lobby.getUsers()[0][4]));
                            player0ButtonID = i;
                            player0Button.setOnMouseEntered((event) -> {
                                playerButtonPopup(player0ButtonID);
                            });
                            player0Button.setOnMouseExited((event) -> {
                                resetPopupAbove();
                            });
                        }
                        else {

                            if (Lobby.getUsers().length >= 2) {
                                if ("frei".equals(player1Button.getText())) {
                                    FadeTransition fadeButton = new FadeTransition(Duration.millis(500), player1Button);
                                    fadeButton.setFromValue(0);
                                    fadeButton.setToValue(1);
                                    fadeButton.playFromStart();
                                    player1.setVisible(true);
                                    if (Lobby.getUsers()[i][1].contains("(")) {
                                        player1Button.setText(Lobby.getUsers()[i][1].substring(0, Lobby.getUsers()[i][1].indexOf('(') - 1));
                                    }
                                    else {
                                        player1Button.setText(Lobby.getUsers()[i][1]);
                                    }
                                    player1Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[i][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                                    if (Color.web(Lobby.getUsers()[i][4]).getBrightness() <= 0.8) {
                                        player1Button.setTextFill(Color.WHITE);
                                    }
                                    player1.setFill(Color.web(Lobby.getUsers()[1][4]));

                                    player1ButtonID = i;
                                    player1Button.setOnMouseEntered((event) -> {
                                        playerButtonPopup(player1ButtonID);
                                    });
                                    player1Button.setOnMouseExited((event) -> {
                                        resetPopupAbove();
                                    });
                                    continue;
                                }
                            }
                            if (Lobby.getUsers().length >= 3) {
                                if ("frei".equals(player2Button.getText())) {
                                    FadeTransition fadeButton = new FadeTransition(Duration.millis(500), player2Button);
                                    fadeButton.setFromValue(0);
                                    fadeButton.setToValue(1);
                                    fadeButton.playFromStart();
                                    player2.setVisible(true);
                                    if (Lobby.getUsers()[i][1].contains("(")) {
                                        player2Button.setText(Lobby.getUsers()[i][1].substring(0, Lobby.getUsers()[i][1].indexOf('(') - 1));
                                    }
                                    else {
                                        player2Button.setText(Lobby.getUsers()[i][1]);
                                    }
                                    player2Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[i][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                                    if (Color.web(Lobby.getUsers()[i][4]).getBrightness() <= 0.8) {
                                        player2Button.setTextFill(Color.WHITE);
                                    }
                                    player2.setFill(Color.web(Lobby.getUsers()[2][4]));
                                    player2ButtonID = i;
                                    player2Button.setOnMouseEntered((event) -> {
                                        playerButtonPopup(player2ButtonID);
                                    });
                                    player2Button.setOnMouseExited((event) -> {
                                        resetPopupAbove();
                                    });
                                    continue;
                                }
                            }
                            if (Lobby.getUsers().length >= 4) {
                                if ("frei".equals(player3Button.getText())) {
                                    FadeTransition fadeButton = new FadeTransition(Duration.millis(500), player3Button);
                                    fadeButton.setFromValue(0);
                                    fadeButton.setToValue(1);
                                    fadeButton.playFromStart();
                                    player3.setVisible(true);
                                    if (Lobby.getUsers()[i][1].contains("(")) {
                                        player3Button.setText(Lobby.getUsers()[i][1].substring(0, Lobby.getUsers()[i][1].indexOf('(') - 1));
                                    }
                                    else {
                                        player3Button.setText(Lobby.getUsers()[i][1]);
                                    }
                                    player3Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[i][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                                    if (Color.web(Lobby.getUsers()[i][4]).getBrightness() <= 0.8) {
                                        player3Button.setTextFill(Color.WHITE);
                                    }
                                    player3.setFill(Color.web(Lobby.getUsers()[3][4]));
                                    player3ButtonID = i;
                                    player3Button.setOnMouseEntered((event) -> {
                                        playerButtonPopup(player3ButtonID);
                                    });
                                    player3Button.setOnMouseExited((event) -> {
                                        resetPopupAbove();
                                    });
                                    continue;
                                }
                            }
                            if (Lobby.getUsers().length >= 5) {
                                if ("frei".equals(player4Button.getText())) {
                                    FadeTransition fadeButton = new FadeTransition(Duration.millis(500), player4Button);
                                    fadeButton.setFromValue(0);
                                    fadeButton.setToValue(1);
                                    fadeButton.playFromStart();
                                    player4.setVisible(true);
                                    if (Lobby.getUsers()[i][1].contains("(")) {
                                        player4Button.setText(Lobby.getUsers()[i][1].substring(0, Lobby.getUsers()[i][1].indexOf('(') - 1));
                                    }
                                    else {
                                        player4Button.setText(Lobby.getUsers()[i][1]);
                                    }
                                    player4Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[i][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                                    if (Color.web(Lobby.getUsers()[i][4]).getBrightness() <= 0.8) {
                                        player4Button.setTextFill(Color.WHITE);
                                    }
                                    player4.setFill(Color.web(Lobby.getUsers()[4][4]));
                                    player4ButtonID = i;
                                    player4Button.setOnMouseEntered((event) -> {
                                        playerButtonPopup(player4ButtonID);
                                    });
                                    player4Button.setOnMouseExited((event) -> {
                                        resetPopupAbove();
                                    });
                                    continue;
                                }
                            }
                            if (Lobby.getUsers().length >= 6) {
                                if ("frei".equals(player5Button.getText())) {
                                    FadeTransition fadeButton = new FadeTransition(Duration.millis(500), player5Button);
                                    fadeButton.setFromValue(0);
                                    fadeButton.setToValue(1);
                                    fadeButton.playFromStart();
                                    player5.setVisible(true);
                                    if (Lobby.getUsers()[i][1].contains("(")) {
                                        player5Button.setText(Lobby.getUsers()[i][1].substring(0, Lobby.getUsers()[i][1].indexOf('(') - 1));
                                    }
                                    else {
                                        player5Button.setText(Lobby.getUsers()[i][1]);
                                    }
                                    player5Button.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[i][4]), CornerRadii.EMPTY, Insets.EMPTY)));
                                    if (Color.web(Lobby.getUsers()[i][4]).getBrightness() <= 0.8) {
                                        player5Button.setTextFill(Color.WHITE);
                                    }
                                    player5.setFill(Color.web(Lobby.getUsers()[5][4]));
                                    player5ButtonID = i;
                                    player5Button.setOnMouseEntered((event) -> {
                                        playerButtonPopup(player5ButtonID);
                                    });
                                    player5Button.setOnMouseExited((event) -> {
                                        resetPopupAbove();
                                    });
                                }
                            }
                        }
                    }
                }

                return null;
            }
        };
        Platform.runLater(task);

        // Initialisierung für FeldPopups (Miete, etc.)
        for (Pane field : Felder) {
            field.setOnMouseEntered((event) -> {
                fieldPopup(field);
            });

            field.setOnMouseMoved((event) -> {
                fieldPopup(field);
            });

            field.setOnMouseExited((event) -> {
                resetPopupAbove();

            });
        }
    }

    public void propertyState() {
        if (Lobby.getPlayerClient().getGame().getBoard() != null) {
            Field[] currentField = Lobby.getPlayerClient().getGame().getBoard().getFields();

            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    int hausAnzahl = 0;
                    HBox hauses;

                    for (int i = 0; i < Felder.length; i++) {

                        if (currentField[i] instanceof SupplyField || currentField[i] instanceof StationField) {
                            if (((PropertyField) currentField[i]).isMortgageTaken()) {

                                Label hypothek = new Label("Hypothek");

                                if ((i > 9 && i < 21)) {
                                    hypothek.setStyle("-fx-rotate:90;"
                                            + "-fx-background-color:brown;");
                                    hypothek.setLayoutY(25);
                                    hypothek.setLayoutX(-10);
                                }
                                if (i > 29 && i < 40) {
                                    hypothek.setStyle("-fx-rotate:-90;"
                                            + "-fx-background-color:brown;");
                                    hypothek.setLayoutY(25);
                                    hypothek.setLayoutX(-10);
                                }
                                else {
                                    hypothek.relocate(5, 5);
                                    hypothek.setStyle("-fx-background-color:brown;");
                                }

                                RentanzeigeFelder[i].getChildren().add(hypothek);
                            }
                            if (!((PropertyField) currentField[i]).isMortgageTaken()) {
                                RentanzeigeFelder[i].getChildren().clear();

                            }
                        }

                        if (currentField[i] instanceof StreetField) {
                            hausAnzahl = ((StreetField) currentField[i]).getHouseCount();

                            if (hausAnzahl == 0) {
                                RentanzeigeFelder[i].getChildren().clear();
                                if (((PropertyField) currentField[i]).isMortgageTaken()) {
                                    Label hypothek = new Label("Hypothek");
                                    if ((i > 9 && i < 21)) {
                                        hypothek.setStyle("-fx-rotate:90;"
                                                + "-fx-background-color:brown;");
                                        hypothek.setLayoutY(25);
                                        hypothek.setLayoutX(-10);

                                    }
                                    if (i > 29 && i < 40) {
                                        hypothek.setStyle("-fx-rotate:-90;"
                                                + "-fx-background-color:brown;");
                                        hypothek.setLayoutY(25);
                                        hypothek.setLayoutX(-10);
                                    }
                                    else {
                                        hypothek.relocate(5, 5);
                                        hypothek.setStyle("-fx-background-color:brown;");
                                    }

                                    RentanzeigeFelder[i].getChildren().add(hypothek);
                                }
                            }
                            //TODO Refactoring und createHaus.. Optimierung
                            if (hausAnzahl == 1) {
                                RentanzeigeFelder[i].getChildren().clear();
                                hauses = createHaus1(10, 10);
                                if ((i > 9 && i < 21)) {
                                    hauses.setStyle("-fx-rotate:90;");
                                    hauses.setLayoutY(25);
                                    hauses.setLayoutX(-10);
                                }
                                if (i > 29 && i < 40) {
                                    hauses.setStyle("-fx-rotate:-90;");
                                    hauses.setLayoutY(25);
                                    hauses.setLayoutX(-10);
                                }
                                else {
                                    hauses.relocate(5, 5);
                                }
                                RentanzeigeFelder[i].getChildren().add(hauses);

                            }
                            if (hausAnzahl == 2) {
                                RentanzeigeFelder[i].getChildren().clear();
                                hauses = createHaus2(10, 10);
                                if ((i > 9 && i < 21)) {
                                    hauses.setStyle("-fx-rotate:90;");
                                    hauses.setLayoutY(25);
                                    hauses.setLayoutX(-10);
                                }
                                if (i > 29 && i < 40) {
                                    hauses.setStyle("-fx-rotate:-90;");
                                    hauses.setLayoutY(25);
                                    hauses.setLayoutX(-10);
                                }
                                else {
                                    hauses.relocate(5, 5);
                                }
                                RentanzeigeFelder[i].getChildren().add(hauses);

                            }
                            if (hausAnzahl == 3) {
                                RentanzeigeFelder[i].getChildren().clear();
                                hauses = createHaus3(10, 10);
                                if ((i > 9 && i < 21)) {
                                    hauses.setStyle("-fx-rotate:90;");
                                    hauses.setLayoutY(25);
                                    hauses.setLayoutX(-10);
                                }
                                if (i > 29 && i < 40) {
                                    hauses.setStyle("-fx-rotate:-90;");
                                    hauses.setLayoutY(25);
                                    hauses.setLayoutX(-10);
                                }
                                else {
                                    hauses.relocate(5, 5);
                                }
                                RentanzeigeFelder[i].getChildren().add(hauses);

                            }
                            if (hausAnzahl == 4) {
                                RentanzeigeFelder[i].getChildren().clear();
                                hauses = createHaus4(10, 10);
                                if ((i > 9 && i < 21)) {
                                    hauses.setStyle("-fx-rotate:90;");
                                    hauses.setLayoutY(25);
                                    hauses.setLayoutX(-10);
                                }
                                if (i > 29 && i < 40) {
                                    hauses.setStyle("-fx-rotate:-90;");
                                    hauses.setLayoutY(25);
                                    hauses.setLayoutX(-10);
                                }
                                else {
                                    hauses.relocate(5, 5);
                                }
                                RentanzeigeFelder[i].getChildren().add(hauses);

                            }
                            if (hausAnzahl == 5) {
                                RentanzeigeFelder[i].getChildren().clear();
                                hauses = createHotel(12, 30);
                                if ((i > 9 && i < 21)) {
                                    hauses.setStyle("-fx-rotate:90;");
                                    hauses.setLayoutY(25);
                                    hauses.setLayoutX(-10);
                                }
                                if (i > 29 && i < 40) {
                                    hauses.setStyle("-fx-rotate:-90;");
                                    hauses.setLayoutY(25);
                                    hauses.setLayoutX(-10);
                                }
                                else {
                                    hauses.relocate(5, 5);
                                }
                                RentanzeigeFelder[i].getChildren().add(hauses);

                            }
                        }

                    }

                    return null;
                }

            };
            Platform.runLater(task);

        }
    }

    /**
     * Erstellt die Figur von Haus
     *
     * @param width
     * @param heigth
     * @param anzahl der Hauser
     * @return
     */
    public HBox createHaus1(int width, int heigth) {
        HBox box = new HBox();

        HBox haus = new HBox();
        Rectangle half1 = new Rectangle(width / 2, heigth, Color.web("#82ada9"));
        Rectangle half2 = new Rectangle(width / 2, heigth, Color.web("#b2dfdb"));
        haus.getChildren().addAll(half1, half2);
        haus.setStyle("-fx-effect: dropshadow(gaussian, yellowgreen, 3, 0, 0, 0);");
        box.getChildren().add(haus);

        box.setSpacing(5);

        return box;

    }

    public HBox createHaus2(int width, int heigth) {
        HBox box = new HBox();
        HBox haus;
        for (int i = 0; i < 2; i++) {
            haus = new HBox();
            Rectangle half1 = new Rectangle(width / 2, heigth, Color.web("#82ada9"));
            Rectangle half2 = new Rectangle(width / 2, heigth, Color.web("#b2dfdb"));
            haus.getChildren().addAll(half1, half2);
            haus.setStyle("-fx-effect: dropshadow(gaussian, yellowgreen, 3, 0, 0, 0);");
            box.getChildren().addAll(haus);
        }
        box.setSpacing(5);

        return box;

    }

    public HBox createHaus3(int width, int heigth) {
        HBox box = new HBox();
        HBox haus;
        for (int i = 0; i < 3; i++) {
            haus = new HBox();
            Rectangle half1 = new Rectangle(width / 2, heigth, Color.web("#82ada9"));
            Rectangle half2 = new Rectangle(width / 2, heigth, Color.web("#b2dfdb"));
            haus.getChildren().addAll(half1, half2);
            haus.setStyle("-fx-effect: dropshadow(gaussian, yellowgreen, 3, 0, 0, 0);");
            box.getChildren().addAll(haus);
        }
        box.setSpacing(5);

        return box;

    }

    public HBox createHaus4(int width, int heigth) {
        HBox box = new HBox();
        HBox haus;
        for (int i = 0; i < 4; i++) {
            haus = new HBox();
            Rectangle half1 = new Rectangle(width / 2, heigth, Color.web("#82ada9"));
            Rectangle half2 = new Rectangle(width / 2, heigth, Color.web("#b2dfdb"));
            haus.getChildren().addAll(half1, half2);
            haus.setStyle("-fx-effect: dropshadow(gaussian, yellowgreen, 3, 0, 0, 0);");
            box.getChildren().addAll(haus);
        }
        box.setSpacing(5);

        return box;

    }

    public HBox createHotel(int width, int heigth) {
        HBox box = new HBox();
        HBox haus;
        haus = new HBox();
        Rectangle half1 = new Rectangle(width, heigth / 2, Color.web("#c97b63"));
        Rectangle half2 = new Rectangle(width, heigth / 2, Color.web("#ffab91"));
        haus.getChildren().addAll(half1, half2);
        haus.setStyle("-fx-effect: dropshadow(gaussian, brown, 3, 0, 0, 0);");
        box.getChildren().addAll(haus);

        return box;

    }

    // -------------------------------------------------------------------------
    // Interne Popups in der GUI (permanent angezeigte Buttons)
    /**
     * Öffnen des jeweiligen SpielerPopups
     *
     * @param id
     */
    private void playerButtonPopup(int id) {
        Player[] players = Lobby.getPlayerClient().getGame().getPlayers();
        FieldManager manager = Lobby.getPlayerClient().getGame().getBoard().getFieldManager();
        Player playeronbutton = players[id];

        String property = "";
        List<PropertyField> ownedFields = manager.getOwnedPropertyFields(playeronbutton)
                .collect(Collectors.toList());      //Liste der besessenen Strassen
        for (PropertyField field : ownedFields) {
            property += "\n" + field.getName() + " [ " + field.getRent() + " €]";
        }

        GridPane player0Pane = new GridPane();
        ScrollPane scroll = new ScrollPane();
        VBox box = new VBox();
        player0Pane.setAlignment(Pos.CENTER);
        scroll.setCenterShape(true);
        player0Pane.add(scroll, 0, 0);
        scroll.setContent(box);

        Label fields = new Label(property + "\n");
        fields.setFont(Font.font("Tahoma", FontPosture.ITALIC, 10));
        fields.setTextFill(Color.MIDNIGHTBLUE);

        JFXButton player = new JFXButton();
        Label geld = new Label(" hat in Konto : " + playeronbutton.getMoney());
        Label jail = new Label(" ist in Gefängnis seit : " + playeronbutton.getDaysInJail());
        Label idNummer = new Label("ID : " + id);
        Label karten = new Label("" + playeronbutton.getCardStack());

        player.setBackground(new Background(new BackgroundFill(Color.web(Lobby.getUsers()[id][4]), CornerRadii.EMPTY, Insets.EMPTY)));
        player.setText(Lobby.getUsers()[id][1]);
        if (Color.web(Lobby.getUsers()[id][4]).getBrightness() <= 0.8) {
            player.setTextFill(Color.WHITE);
        }
        player.setPrefSize(150, 10);

        box.getChildren().addAll(player, geld, jail, idNummer, karten, fields);
        box.setAlignment(Pos.CENTER);

        if (PopupPane.getChildren().contains(middlePane)) {
            setPopupAbove(player0Pane);
        }
    }

    /**
     * Öffnen des Feldpopups
     *
     * @param feld
     */
    public void fieldPopup(Pane feld) {
        if (Lobby.getPlayerClient().getGame() != null) {
            if (Lobby.getPlayerClient().getGame().getBoard() != null) {
                Field[] currentField = Lobby.getPlayerClient().getGame().getBoard().getFields();

                GridPane gp = new GridPane();
                VBox box = new VBox();
                gp.setAlignment(Pos.CENTER);
                gp.getChildren().add(box);

                String text = "";
                Label owner = new Label();
                Label rent = new Label();
                for (int i = 0; i < Felder.length; i++) {
                    if (Felder[i] == feld) {
                        if (currentField[i] instanceof PropertyField) {
                            owner.setText(("\tBesitzer : " + ((PropertyField) currentField[i]).getOwner()));
                            rent.setText("\n Aktuelle Miete : " + ((PropertyField) currentField[i]).getRent());
                            rent.setTextFill(Color.BROWN);
                            owner.setTextFill(Color.DARKBLUE);

                            text = "\n\t" + currentField[i];

                            Label info = new Label(text);
                            box.getChildren().addAll(owner, info, rent);
                            // box.setBackground(new Background(new BackgroundFill(Color.web(feld.getStyle()), CornerRadii.EMPTY, Insets.EMPTY)));
                            box.setStyle(feld.getStyle());
                            box.setPrefWidth(200);
                            box.setAlignment(Pos.CENTER);

                            if (PopupPane.getChildren().contains(middlePane)) {
                                setPopupAbove(gp);
                            }
                            break;
                        }
                        break;
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Update Funktionen für Steuerung aktiver Inhalte des Spielbretts
    /**
     * Updaten des Geldes der Spieler
     */
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

    private void geldUpdate0(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                geld0.setText("Geld: " + players[client.getPlayerOnClient().getId()].getMoney());
                return null;
            }
        };
        Platform.runLater(task);
    }

    private void geldUpdate1(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                geld1.setText("Geld: " + players[player1ButtonID].getMoney());
                return null;
            }
        };
        Platform.runLater(task);
    }

    private void geldUpdate2(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                geld2.setText("Geld: " + players[player2ButtonID].getMoney());
                player2Geld.add(geld2, 0, 0);
                return null;
            }
        };
        Platform.runLater(task);
    }

    private void geldUpdate3(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                geld3.setText("Geld: " + players[player3ButtonID].getMoney());
                return null;
            }
        };
        Platform.runLater(task);
    }

    private void geldUpdate4(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                geld4.setText("Geld: " + players[player4ButtonID].getMoney());
                return null;
            }
        };
        Platform.runLater(task);
    }

    private void geldUpdate5(Player[] players) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                geld5.setText("Geld: " + players[player5ButtonID].getMoney());
                return null;
            }
        };
        Platform.runLater(task);
    }

    /**
     * Updaten der Position der Spielfigur
     */
    public void playerUpdate() {
        Player[] players = Lobby.getPlayerClient().getGame().getPlayers();

        if (players.length >= 1) {
            while (lastPosPlayer0 != players[0].getPosition()) {
                lastPosPlayer0 = ++lastPosPlayer0 % 40;
                playerUpdate0(players, lastPosPlayer0);
                IOService.sleep(300);
            }
        }
        if (players.length >= 2) {
            while (lastPosPlayer1 != players[1].getPosition()) {
                lastPosPlayer1 = ++lastPosPlayer1 % 40;
                playerUpdate1(players, lastPosPlayer1);
                IOService.sleep(300);
            }
        }
        if (players.length >= 3) {
            while (lastPosPlayer2 != players[2].getPosition()) {
                lastPosPlayer2 = ++lastPosPlayer2 % 40;
                playerUpdate2(players, lastPosPlayer2);
                IOService.sleep(300);
            }
        }
        if (players.length >= 4) {
            while (lastPosPlayer3 != players[3].getPosition()) {
                lastPosPlayer3 = ++lastPosPlayer3 % 40;
                playerUpdate3(players, lastPosPlayer3);
                IOService.sleep(300);
            }
        }
        if (players.length >= 5) {
            while (lastPosPlayer4 != players[4].getPosition()) {
                lastPosPlayer4 = ++lastPosPlayer4 % 40;
                playerUpdate4(players, lastPosPlayer4);
                IOService.sleep(300);
            }
        }
        if (players.length >= 6) {
            while (lastPosPlayer5 != players[5].getPosition()) {
                lastPosPlayer5 = ++lastPosPlayer5 % 40;
                playerUpdate5(players, lastPosPlayer5);
                IOService.sleep(300);
            }
        }

    }

    private void playerUpdate0(Player[] players, int nextPos) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                player0.layoutXProperty().bind(Felder[nextPos].widthProperty().subtract(player0.centerXProperty()).divide(2));
                player0.layoutYProperty().bind(Felder[nextPos].heightProperty().subtract(player0.centerYProperty()).divide(2));
                Felder[nextPos].getChildren().add(player0);
                return null;
            }
        };
        Platform.runLater(task);
    }

    private void playerUpdate1(Player[] players, int nextPos) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                player1.layoutXProperty().bind(Felder[nextPos].widthProperty().subtract(player1.centerXProperty()).divide(2));
                player1.layoutYProperty().bind(Felder[nextPos].heightProperty().subtract(player1.centerYProperty()).divide(2));
                Felder[nextPos].getChildren().add(player1);
                return null;
            }
        };
        Platform.runLater(task);
    }

    private void playerUpdate2(Player[] players, int nextPos) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                player2.layoutXProperty().bind(Felder[nextPos].widthProperty().subtract(player2.centerXProperty()).divide(2));
                player2.layoutYProperty().bind(Felder[nextPos].heightProperty().subtract(player2.centerYProperty()).divide(2));
                Felder[nextPos].getChildren().add(player2);
                return null;
            }
        };
        Platform.runLater(task);
    }

    private void playerUpdate3(Player[] players, int nextPos) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                player3.layoutXProperty().bind(Felder[nextPos].widthProperty().subtract(player3.centerXProperty()).divide(2));
                player3.layoutYProperty().bind(Felder[nextPos].heightProperty().subtract(player3.centerYProperty()).divide(2));
                Felder[nextPos].getChildren().add(player3);
                return null;
            }
        };
        Platform.runLater(task);
    }

    private void playerUpdate4(Player[] players, int nextPos) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                player4.layoutXProperty().bind(Felder[nextPos].widthProperty().subtract(player4.centerXProperty()).divide(2));
                player4.layoutYProperty().bind(Felder[nextPos].heightProperty().subtract(player4.centerYProperty()).divide(2));
                Felder[nextPos].getChildren().add(player4);
                return null;
            }
        };
        Platform.runLater(task);
    }

    private void playerUpdate5(Player[] players, int nextPos) {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                player5.layoutXProperty().bind(Felder[nextPos].widthProperty().subtract(player5.centerXProperty()).divide(2));
                player5.layoutYProperty().bind(Felder[nextPos].heightProperty().subtract(player5.centerYProperty()).divide(2));
                Felder[nextPos].getChildren().add(player5);
                return null;
            }
        };
        Platform.runLater(task);
    }

    /**
     * Update der Anzeigen für Spielerbesitz
     */
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

    // -------------------------------------------------------------------------
    // Grundlegende Steuerung der Popups
    /**
     * Übergebenes Popup öffnen
     *
     * @param gridpane
     */
    public void setPopupAbove(GridPane gridpane) {

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                PopupPane.getChildren().remove(middlePane);
                PopupPane.add(gridpane, 0, 0);
                return null;
            }
        };
        Platform.runLater(task);

    }

    public void setPopupBellow(GridPane gridpane) {

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                PopupPane2.getChildren().remove(middlePane);
                PopupPane2.add(gridpane, 0, 0);
                return null;
            }
        };
        Platform.runLater(task);

    }

    /**
     * Reset des letzten Popups
     */
    public void resetPopupAbove() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                PopupPane.getChildren().clear();
                PopupPane.add(middlePane, 0, 0);
                return null;
            }
        };
        Platform.runLater(task);
    }

    public void resetPopupBelow() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                PopupPane2.getChildren().clear();
                return null;
            }
        };
        Platform.runLater(task);
    }

    // -------------------------------------------------------------------------
    // Steuerung des Protokollfensters
    /**
     * Anfügen eines Texts in das Protokollfenster
     *
     * @param message
     */
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

    // -------------------------------------------------------------------------
    // Initialisierung der Felder
    public void initStreets() {

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                if (Lobby.getPlayerClient().getGame().getBoard() != null) {
                    if (initToggle) {
                        // Los Feld, da Bild eingefügt wurde
                        label0.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[0].getName());
                        label1.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[1].getName());
                        label2.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[2].getName());
                        label3.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[3].getName());
                        label4.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[4].getName());
                        label5.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[5].getName());
                        label6.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[6].getName());
                        label7.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[7].getName());
                        label8.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[8].getName());
                        label9.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[9].getName());
                        label10.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[10].getName());
                        label11.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[11].getName());
                        label12.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[12].getName());
                        label13.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[13].getName());
                        label14.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[14].getName());
                        label15.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[15].getName());
                        label16.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[16].getName());
                        label17.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[17].getName());
                        label18.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[18].getName());
                        label19.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[19].getName());
                        label20.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[20].getName());
                        label21.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[21].getName());
                        label22.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[22].getName());
                        label23.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[23].getName());
                        label24.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[24].getName());
                        label25.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[25].getName());
                        label26.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[26].getName());
                        label27.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[27].getName());
                        label28.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[28].getName());
                        label29.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[29].getName());
                        label30.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[30].getName());
                        label31.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[31].getName());
                        label32.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[32].getName());
                        label33.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[33].getName());
                        label34.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[34].getName());
                        label35.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[35].getName());
                        label36.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[36].getName());
                        label37.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[37].getName());
                        label38.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[38].getName());
                        label39.setText(Lobby.getPlayerClient().getGame().getBoard().getFields()[39].getName());
                        initToggle = false;
                    }
                }
                return null;
            }
        };
        Platform.runLater(task);

    }

}
