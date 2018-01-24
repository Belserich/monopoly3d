/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.service.AuctionService;
import de.btu.monopoly.core.service.IOService;
import de.btu.monopoly.data.card.CardAction;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.ui.controller.LobbyController;
import de.btu.monopoly.ui.controller.MainSceneController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 *
 * @author augat
 */
public class SceneManager extends Stage {

    private Stage stage;
    private static Scene scene;
    private static LobbyController LobbyController;
    private static MainSceneController GameController;
    private static char currency = '€';
    //Auktionsspezifisch
    private static Label auctionLabel = new Label("0 €");
    private static Label hoechstgebotLabel = new Label("Höchstgebot:");
    private static JFXTextField bidTextField = new JFXTextField();
    //Handelsspezifisch
    private static Player tradePartner;
    private static boolean partnerIsChoosen = false;
    private static boolean exitTrade = false;

    public SceneManager() throws IOException {
        stage = this;
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Menu.fxml"));

        scene = new Scene(root);

        stage.setFullScreen(true);
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
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

            }
        });
    }

    public static void appendText(String message) {
        if (GameController != null) {
            GameController.appendText(message);
        }
    }

    public static void changeScene(FXMLLoader loader) throws IOException {

        Parent root = loader.load();
        scene.setRoot(root);
    }

    public static void changeSceneToLobby(FXMLLoader loader) throws IOException {

        Parent root = loader.load();
        LobbyController = loader.getController();

        scene.setRoot(root);

    }

    public static void changeSceneToGame(FXMLLoader loader) throws IOException {

        Parent root = loader.load();
        GameController = loader.getController();

        scene.setRoot(root);

    }

    public static void updateLobby() throws InterruptedException {
        if (LobbyController != null) {
            LobbyController.updateNames();
        }

    }

    public static void updateLobbyColors() throws InterruptedException {
        if (LobbyController != null) {
            LobbyController.updateColors();
        }

    }

    public static void openGameLayout() throws IOException {
        if (LobbyController != null) {
            LobbyController.loadGameLayout();
        }
    }

    public static void playerUpdate() {
        if (GameController != null) {
            GameController.playerInitialise();
        }
    }

    public static void geldPlayerUpdate() {
        if (GameController != null) {
            GameController.geldUpdate();
        }
    }

    public static void movePlayerUpdate() {
        if (GameController != null) {
            GameController.playerUpdate();
        }
    }

    public static void propertyUpdate() {
        if (GameController != null) {
            GameController.propertyUpdate();
        }
    }

    public static void propertyStateUpdate() {
        if (GameController != null) {
            GameController.propertyState();
        }
    }

    // -----------------------------------------------------------------------
    // Popups
    // -----------------------------------------------------------------------
    public static int buyPropertyPopup() {

        GridPane gridpane = new GridPane();
        // ScrollPane scroll = new ScrollPane();
        VBox box = new VBox();
        gridpane.setAlignment(Pos.CENTER);
        // scroll.setCenterShape(true);
        gridpane.add(box, 0, 0);
        // box.setContent(box);

        Label label = new Label("Möchtest du die " + Lobby.getPlayerClient().getGame().getBoard().getFields()[Lobby.getPlayerClient().getPlayerOnClient().getPosition()].getName() + " kaufen?");

        JFXButton buyButton = new JFXButton();
        JFXButton dontBuyButton = new JFXButton();

        buyButton.setText("Kaufen");
        buyButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        dontBuyButton.setText("Nicht kaufen");
        dontBuyButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        String cssLayout = "-fx-background-color: #fbe9e7;\n"
                + "-fx-border-color: black;\n"
                + "-fx-border-insets: 5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: double;\n";

        box.setStyle(cssLayout);
        box.setSpacing(10);
        box.setPrefSize(300, 200);
        label.setFont(Font.font("Tahoma", 14));
        box.getChildren().addAll(label, buyButton, dontBuyButton);
        box.setAlignment(Pos.CENTER);

        GameController.setPopupBellow(gridpane);

        while (!buyButton.isPressed() || !dontBuyButton.isPressed()) {
            IOService.sleep(50);
            if (buyButton.isPressed()) {
                GameController.resetPopupBelow();
                return 1;
            }
            if (dontBuyButton.isPressed()) {
                GameController.resetPopupBelow();
                return 2;
            }
        }

        return -1;
    }

    public static int jailChoicePopup() {

        GridPane gridpane = new GridPane();
        // ScrollPane scroll = new ScrollPane();
        VBox box = new VBox();
        gridpane.setAlignment(Pos.CENTER);
        //scroll.setCenterShape(true);
        gridpane.add(box, 0, 0);
        //scroll.setContent(box);

        Label label = new Label("Du bist im Gefängnis. Was möchtest du tun?");

        JFXButton rollButton = new JFXButton();
        JFXButton payButton = new JFXButton();
        JFXButton cardButton = new JFXButton();

        rollButton.setText("Würfeln");
        rollButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        payButton.setText("Bezahlen");
        payButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        cardButton.setText("Frei-Karte nutzen");
        cardButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        String cssLayout = "-fx-background-color: #ffccbc;\n"
                + "-fx-border-color: black;\n"
                + "-fx-border-insets: 5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: double;\n";

        box.setStyle(cssLayout);
        box.setSpacing(10);
        box.setPrefSize(200, 300);
        box.getChildren().addAll(label, rollButton, payButton, cardButton);
        box.setAlignment(Pos.CENTER);

        GameController.setPopupBellow(gridpane);

        while (!rollButton.isPressed() || !payButton.isPressed() || !cardButton.isPressed()) {
            IOService.sleep(50);
            if (rollButton.isPressed()) {
                GameController.resetPopupBelow();
                return 1;
            }
            if (payButton.isPressed()) {
                GameController.resetPopupBelow();
                return 2;
            }
            if (cardButton.isPressed()) {
                GameController.resetPopupBelow();
                return 3;
            }
        }

        return -1;
    }

    public static int actionSequencePopup() {

        GridPane gridpane = new GridPane();
        //ScrollPane scroll = new ScrollPane();
        VBox vbox = new VBox();
        VBox vbox1 = new VBox();
        VBox vbox2 = new VBox();

        HBox box = new HBox();

        gridpane.setAlignment(Pos.CENTER);
        //scroll.setCenterShape(true);

        // scroll.setContent(box);
        Label label = new Label("Was möchtest du noch tun?");

        gridpane.getChildren().add(vbox);
        JFXButton nothingButton = new JFXButton();
        JFXButton buyHouseButton = new JFXButton();
        JFXButton removeHouseButton = new JFXButton();
        JFXButton addMortgageButton = new JFXButton();
        JFXButton removeMortgageButton = new JFXButton();
        JFXButton tradeButton = new JFXButton();

        nothingButton.setText("Nichts");
        nothingButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));
        buyHouseButton.setText("Haus kaufen");
        buyHouseButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        removeHouseButton.setText("Haus verkaufen");
        removeHouseButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        addMortgageButton.setText("Hypothek aufnehmen");
        addMortgageButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        removeMortgageButton.setText("Hypothek abbezahlen");
        removeMortgageButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        tradeButton.setText("Handeln");
        tradeButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        label.setFont(Font.font("Tahoma", 14));

        // scroll.add(label, 0, 0);
        String cssLayout = "-fx-background-color: #b9f6ca;\n"
                + "-fx-border-color: black;\n"
                + "-fx-effect: dropshadow(gaussian, yellowgreen, 20, 0, 0, 0);"
                + "-fx-border-insets: 5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: double;\n";

        vbox.setStyle(cssLayout);
        box.setSpacing(10);
        box.setPrefSize(500, 200);
        vbox1.getChildren().addAll(nothingButton, buyHouseButton, removeHouseButton);
        vbox1.setAlignment(Pos.CENTER);
        vbox1.setSpacing(5);
        vbox2.getChildren().addAll(tradeButton, addMortgageButton, removeMortgageButton);
        vbox2.setSpacing(5);
        vbox2.setAlignment(Pos.CENTER);
        box.setSpacing(40);
        box.getChildren().addAll(vbox1, vbox2);
        vbox.getChildren().addAll(label, box);
        box.setAlignment(Pos.CENTER);
        vbox.setAlignment(Pos.CENTER);

        GameController.setPopupBellow(gridpane);

        while (!nothingButton.isPressed() || !buyHouseButton.isPressed() || !removeHouseButton.isPressed() || !addMortgageButton.isPressed() || !removeMortgageButton.isPressed() || !tradeButton.isPressed()) {
            IOService.sleep(50);
            if (nothingButton.isPressed()) {
                GameController.resetPopupBelow();
                return 1;
            }
            if (buyHouseButton.isPressed()) {
                GameController.resetPopupBelow();
                return 2;
            }
            if (removeHouseButton.isPressed()) {
                GameController.resetPopupBelow();
                return 3;
            }
            if (addMortgageButton.isPressed()) {
                GameController.resetPopupBelow();
                return 4;
            }
            if (removeMortgageButton.isPressed()) {
                GameController.resetPopupBelow();
                return 5;
            }
            if (tradeButton.isPressed()) {
                GameController.resetPopupBelow();
                return 6;
            }
        }

        return -1;
    }

    public static int askForFieldPopup(Player player, String[] fields) {

        GridPane gridPane = new GridPane();
        VBox box = new VBox();

        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(box, 0, 0);

        Label label = new Label("Wähle ein Feld:");
        JFXComboBox fieldBox = new JFXComboBox();
        Button button = new Button();

        String cssLayout = "-fx-background-color: #b2dfdb;\n"
                + "-fx-border-color: black;\n"
                + "-fx-border-insets: 5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: double;\n";

        box.setStyle(cssLayout);
        box.setSpacing(10);
        box.setPrefSize(200, 250);
        box.setCenterShape(true);
        button.setText("Eingabe");
        button.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));
        label.setFont(Font.font("Tahoma", 14));

        for (String fieldName : fields) {
            fieldBox.getItems().add(fieldName);
        }

        fieldBox.getSelectionModel().selectFirst();

        box.getChildren().addAll(label, fieldBox, button);
        box.setAlignment(Pos.CENTER);

        GameController.setPopupBellow(gridPane);

        while (!button.isPressed()) {
            IOService.sleep(50);
        }

        GameController.resetPopupBelow();

        int id = 0;
        for (Field field : Lobby.getPlayerClient().getGame().getBoard().getFieldManager().getFields()) {

            id++;
            if (field.getName().equals(fields[fieldBox.getSelectionModel().getSelectedIndex()])) {
                return id;

            }
        }

        return -1;
    }

    public static void auctionPopup() {

        //initialisierung der benoetigten Objekte
        HBox auctionHBox = new HBox();
        VBox auctionVBox = new VBox();
        GridPane auctionGP = new GridPane();
        Label gebotsLabel = new Label("Dein Gebot für \n" + AuctionService.getPropertyString() + ":");

        JFXButton bidButton = new JFXButton("Bieten");
        JFXButton exitButton = new JFXButton("Aussteigen");

        auctionGP.setAlignment(Pos.CENTER);
        hoechstgebotLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        //Eventhandler(n)
        EventHandler bid = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    AuctionService.setBid(Lobby.getPlayerClient().getPlayerOnClient().getId(), Integer.parseInt(bidTextField.getText()));
                } catch (NumberFormatException e) {
                    bidTextField.setText("");
                    bidTextField.setPromptText("Bitte nur Zahlen eingeben!");
                }
            }
        };

        //Einstellung der benoetigten Objekte
        auctionGP.setAlignment(Pos.CENTER);
        auctionGP.add(auctionHBox, 0, 0);
        hoechstgebotLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        gebotsLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        bidTextField.setAlignment(Pos.CENTER);

        bidButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));
        exitButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        bidTextField.setPromptText(" ");

        String cssLayout = "-fx-background-color: #dcedc8;\n"
                + "-fx-border-color: black;\n"
                + "-fx-border-insets: 5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: double;\n";

        auctionHBox.setStyle(cssLayout);
        auctionHBox.setSpacing(10);
        auctionHBox.setPrefSize(800, 200);
        auctionHBox.setCenterShape(true);
        auctionVBox.getChildren().addAll(bidButton, exitButton);
        auctionVBox.setSpacing(10);
        auctionVBox.setAlignment(Pos.CENTER);
        auctionHBox.getChildren().addAll(hoechstgebotLabel, auctionLabel, gebotsLabel, bidTextField, auctionVBox);
        auctionHBox.setAlignment(Pos.CENTER);
        if (GameController != null) {
            GameController.setPopupBellow(auctionGP);
        }

        //Verknuepfung mit EventHandler(n)
        bidTextField.setOnAction(bid);
        bidButton.setOnAction(bid);
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AuctionService.playerExit(Lobby.getPlayerClient().getPlayerOnClient().getId());
                GameController.resetPopupBelow();
            }
        });
    }

    public static void updateAuctionPopup(boolean stillActive, boolean noBidder) {

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                auctionLabel.setText(String.valueOf(AuctionService.getHighestBid()) + currency);
                hoechstgebotLabel.setText("Höchstgebot von \n" + AuctionService.getPlayer(AuctionService.getHighestBidder()).getName() + ":");
                return null;
            }
        };
        Platform.runLater(task);

        IOService.sleep(500);
        if (!stillActive) {

            GameController.resetPopupBelow();

            GridPane resetGridPane = new GridPane();
            VBox resetBox = new VBox();
            Label endLabel = new Label();

            resetGridPane.setAlignment(Pos.CENTER);
            resetGridPane.add(resetBox, 0, 0);

            if (noBidder) {
                endLabel.setText("Das Grundstück " + AuctionService.getPropertyString() + " wurde nicht verkauft!");
            }
            else {
                endLabel.setText(Lobby.getPlayerClient().getGame().getPlayers()[AuctionService.getHighestBidder()].getName()
                        + " hat die Auktion gewonnen und muss " + AuctionService.getHighestBid() + currency + " für das Grundstück "
                        + AuctionService.getPropertyString() + " zahlen!");
            }
            String cssLayout = "-fx-background-color: #dcedc8;\n"
                    + "-fx-border-color: black;\n"
                    + "-fx-border-insets: 5;\n"
                    + "-fx-border-width: 1;\n"
                    + "-fx-border-style: double;\n";

            endLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 10));
            resetBox.setStyle(cssLayout);
            resetBox.setSpacing(10);
            resetBox.setPrefSize(550, 150);
            resetBox.setCenterShape(true);
            resetBox.getChildren().addAll(endLabel);
            resetBox.setAlignment(Pos.CENTER);
            GameController.setPopupBellow(resetGridPane);
            IOService.sleep(3500);
            GameController.resetPopupBelow();
            auctionLabel.setText("0 " + currency);
        }

    }

    public static void bidTextFieldFocus() {
        bidTextField.requestFocus();
    }

    public static void initStreets() {
        GameController.initStreets();
    }

    /*
    Popups fuer den Handel
     */
    public static void initTradePopup() {

        //Liste(n)
        ObservableList<String> choosePlayerOptions = FXCollections.observableArrayList(tradePlayersNames());

        //initialisierung der benoetigten Objekte
        //GridPane(s)
        GridPane initTradeGP = new GridPane();
        //HBox(en)
        HBox initTradeHBox = new HBox();
        //VBox(en)
        VBox initTradeVBox = new VBox();
        //Label(s)
        Label initTradeLabel = new Label("Wähle einen Spieler, mit dem du handeln möchtest.");
        //Button(s)
        JFXButton acceptPlayerButton = new JFXButton("Handeln");
        //ComboBox(en)
        JFXComboBox choosePlayerBox = new JFXComboBox(choosePlayerOptions);

        //Eventhandler
        EventHandler selectPlayer = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    /*
                    Nimmt die Auswahl aus der Combobox und übergibt Sie dem Handel
                     */
                    String tradePartnersName = (String) choosePlayerBox.getSelectionModel().getSelectedItem();
                    GameBoard board = Lobby.getPlayerClient().getGame().getBoard();

                    for (int i = 0; i < Lobby.getPlayerClient().getGame().getBoard().getActivePlayers().size(); i++) {
                        if (tradePartnersName.equals(Lobby.getPlayerClient().getGame().getPlayers()[i].getName())) {
                            tradePartner = Lobby.getPlayerClient().getGame().getPlayers()[i];

                            //selectTradeOfferPopup();
                            //TradeService.trade.setSupply(TradeService.createTradeOffer(Lobby.getPlayerClient().getPlayerOnClient(), board));
                            //TradeService.trade.setDemand(TradeService.createTradeOffer(tradePartner, board));
                            partnerIsChoosen = true;
                            break;
                        }
                    }

                    Task task = new Task() {
                        @Override
                        protected Object call() throws Exception {
                            showTradeInfoPopup();
                            return null;
                        }
                    };
                    Platform.runLater(task);

                } catch (NullPointerException e) {
                    //Fehler
                }
            }
        };

        //Einstellung der benoetigten Objekte
        initTradeGP.setAlignment(Pos.CENTER);
        initTradeGP.add(initTradeVBox, 0, 0);
        initTradeLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        acceptPlayerButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        String cssLayout = "-fx-background-color: #dcedc8;\n"
                + "-fx-border-color: black;\n"
                + "-fx-border-insets: 5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: double;\n";

        //Einstellen HBox
        initTradeHBox.getChildren().addAll(acceptPlayerButton);
        initTradeHBox.setSpacing(10);
        initTradeHBox.setAlignment(Pos.CENTER);

        //Einstellen VBox
        initTradeVBox.setStyle(cssLayout);
        initTradeVBox.setSpacing(10);
        initTradeVBox.setPrefSize(800, 200);
        initTradeVBox.setCenterShape(true);
        initTradeVBox.getChildren().addAll(initTradeLabel, choosePlayerBox, initTradeHBox);
        initTradeVBox.setAlignment(Pos.CENTER);
        if (GameController != null) {
            GameController.setPopupBellow(initTradeGP);
        }

        //Verkünpfung mit Eventhandler(n)
        acceptPlayerButton.setOnAction(selectPlayer);

    }

    /**
     * Erzeugt eine Liste von Namen aller Spieler ausser des aktiven Spielers
     *
     * @return
     */
    private static ArrayList<String> tradePlayersNames() {

        int playerCount = Lobby.getPlayerClient().getGame().getBoard().getActivePlayers().size();
        ArrayList<String> tradePlayers = new ArrayList<>();

        for (int i = 0; i < playerCount; i++) {
            if (Lobby.getPlayerClient().getGame().getPlayers()[i] != Lobby.getPlayerClient().getPlayerOnClient()) {
                tradePlayers.add(Lobby.getPlayerClient().getGame().getPlayers()[i].getName());
            }
        }

        return tradePlayers;
    }

    public static boolean getPartnerIsChoosen() {
        return partnerIsChoosen;
    }

    public static void resetPartnerIsChoosen() {
        partnerIsChoosen = false;
    }

    private static void showTradeInfoPopup() {

        GameController.resetPopupBelow();

        //Initialisierung der benoetigten Objekte
        //Gridpane(s)
        GridPane tradeInfoGridPane = new GridPane();
        //Label(s)
        Label tradeInfoLabel = new Label("Es können mehrere Grundstücke\ngeboten und verlangt werden!");
        tradeInfoLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        //VBox(en)
        VBox tradeInfoVBox = new VBox();

        //Einstellung der Objekte
        String cssLayout = "-fx-background-color: #dcedc8;\n"
                + "-fx-border-color: black;\n"
                + "-fx-border-insets: 5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: double;\n";

        tradeInfoGridPane.setAlignment(Pos.CENTER);
        tradeInfoGridPane.add(tradeInfoVBox, 0, 0);

        tradeInfoVBox.setStyle(cssLayout);
        tradeInfoVBox.setSpacing(10);
        tradeInfoVBox.setPrefSize(800, 200);
        tradeInfoVBox.setAlignment(Pos.CENTER);
        tradeInfoVBox.getChildren().addAll(tradeInfoLabel);

        if (GameController != null) {
            GameController.setPopupBellow(tradeInfoGridPane);
        }

        //Weiterleitung an das naechste Popup
        Timeline timer = new Timeline(new KeyFrame(
                Duration.millis(2500),
                timeOver -> selectTradeOfferPopup()));

        timer.play();

    }

    public static void selectTradeOfferPopup() {

        GameController.resetPopupBelow();

        //Liste(n)
        List<CheckMenuItem> playersProps = tradePlayersProps(Lobby.getPlayerClient().getPlayerOnClient());
        List<CheckMenuItem> rivalsProps = FXCollections.observableArrayList(tradePlayersProps(tradePartner));
        //Initialisierung der benoetigten Objekte
        //GridPane(s)
        GridPane tradeOfferGridPane = new GridPane();
        //HBox(en)
        HBox tradeOfferHBox = new HBox();
        //VBox(en)
        VBox tradeOfferVBox = new VBox();
        VBox yourPropsOfferVBox = new VBox();
        VBox yourTradeOfferVBox = new VBox();
        VBox rivalsTradeOfferVBox = new VBox();
        VBox rivalsPropsOfferVBox = new VBox();
        //Label(s) mit Einstellung
        Label generallOfferLabel = new Label("Wähle dein Angebot!");
        generallOfferLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        Label yourSideLabel = new Label("Deine Seite");
        yourSideLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        Label yourPropsLabel = new Label("Biete Grundstücke:");
        yourPropsLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        Label yourOfferMoneyLabel = new Label("Wieviel " + currency + " bietetst du:");
        yourOfferMoneyLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        Label yourCardLabel = new Label("Gefängnisfreikarten: " + Lobby.getPlayerClient().getPlayerOnClient().getCardStack().countCardsOfAction(CardAction.JAIL));
        yourCardLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        Label yourOfferCardLabel = new Label("Biete Karten:");
        yourOfferCardLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        Label rivalsSideLabel = new Label("Tauschpartners Seite");
        rivalsSideLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        Label rivalsPropsLabel = new Label("Verlange Grundstücke:");
        rivalsPropsLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        Label rivalsOfferMoneyLabel = new Label("Verlange Geld:");
        rivalsOfferMoneyLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        Label rivalsCardLabel = new Label("Gefängnisfreikarten: " + tradePartner.getCardStack().countCardsOfAction(CardAction.JAIL));
        rivalsCardLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        Label rivalsOfferCardLabel = new Label("Verlange Karten:");
        rivalsOfferCardLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        //Button(s)
        JFXButton offerTradeButton = new JFXButton("Anbieten");
        //TextField(s)
        JFXTextField yourMoneyTextField = new JFXTextField("0");
        JFXTextField yourCardsTextField = new JFXTextField("0");
        JFXTextField rivalsMoneyTextField = new JFXTextField("0");
        JFXTextField rivalsCardsTextField = new JFXTextField("0");
        //MenuButton(s)
        MenuButton yourPropsMeButton = new MenuButton();
        MenuButton rivalsPropsMeButton = new MenuButton();
        //Seperator(en)
        Separator separateLblFromTrade = new Separator();
        Separator separateYouFromRival = new Separator(Orientation.VERTICAL);

        //Einstellung der benoetigten Objekte
        //MenuBotton(s) fuellen
        yourPropsMeButton
                .getItems().addAll(playersProps);
        rivalsPropsMeButton.getItems().addAll(rivalsProps);
        //GridPane(s)
        tradeOfferGridPane.setAlignment(Pos.CENTER);
        //Button(s)
        offerTradeButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        String cssLayout = "-fx-background-color: #dcedc8;\n"
                + "-fx-border-color: black;\n"
                + "-fx-border-insets: 5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: double;\n";

        //Anordnen des GUI
        tradeOfferGridPane.add(tradeOfferVBox, 0, 0);
        //Main VBox
        tradeOfferVBox.setStyle(cssLayout);
        tradeOfferVBox.setSpacing(10);
        tradeOfferVBox.setPrefSize(800, 200);
        tradeOfferVBox.setAlignment(Pos.CENTER);
        tradeOfferVBox.getChildren().addAll(generallOfferLabel, separateLblFromTrade, tradeOfferHBox);
        //MainHBox
        tradeOfferHBox.getChildren().addAll(yourPropsOfferVBox, yourTradeOfferVBox, separateYouFromRival, rivalsPropsOfferVBox, rivalsTradeOfferVBox);
        //VBox(en)
        //Groessen und Abstaende
        yourPropsOfferVBox.setPrefSize(199, 175);
        yourPropsOfferVBox.setSpacing(18);
        yourTradeOfferVBox.setPrefSize(199, 175);
        yourTradeOfferVBox.setSpacing(8);
        rivalsPropsOfferVBox.setPrefSize(199, 175);
        rivalsPropsOfferVBox.setSpacing(18);
        rivalsTradeOfferVBox.setPrefSize(199, 175);
        rivalsTradeOfferVBox.setSpacing(8);
        //Elemente hinzufuegen
        yourPropsOfferVBox.getChildren().addAll(yourSideLabel, yourPropsLabel, yourPropsMeButton, offerTradeButton);
        yourTradeOfferVBox.getChildren().addAll(yourOfferMoneyLabel, yourMoneyTextField, yourCardLabel, yourOfferCardLabel, yourCardsTextField);
        rivalsPropsOfferVBox.getChildren().addAll(rivalsSideLabel, rivalsPropsLabel, rivalsPropsMeButton);
        rivalsTradeOfferVBox.getChildren().addAll(rivalsOfferMoneyLabel, rivalsMoneyTextField, rivalsCardLabel, rivalsOfferCardLabel, rivalsCardsTextField);

        if (GameController != null) {
            GameController.setPopupBellow(tradeOfferGridPane);
        }

    }

    /**
     * Erzeugt eine Liste von Namen aller Propertys des uebergebenen Spielers
     *
     * @param player
     * @return
     */
    private static List<CheckMenuItem> tradePlayersProps(Player player) {

        //Benoetigte Objekte
        //Liste aller Propertys im Besitzt des Spielers
        List<PropertyField> playersProps = Lobby.getPlayerClient().getGame().getBoard().getFieldManager()
                .getOwnedPropertyFields(player).collect(Collectors.toList());
        //Liste fuer MenuButton Eintraege
        List<CheckMenuItem> items = new LinkedList<>();

        //Einer ArrayList werden alle Namen der Propertys mitgeteilt
        for (PropertyField field : playersProps) {
            items.add(new CheckMenuItem(field.getName()));
        }

        return items;
    }

}
