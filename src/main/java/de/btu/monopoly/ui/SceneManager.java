/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.btu.monopoly.core.service.AuctionService;
import de.btu.monopoly.core.service.IOService;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.ui.controller.LobbyController;
import de.btu.monopoly.ui.fx3d.MonopolySceneData;
import de.btu.monopoly.ui.util.Assets;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 *
 * @author augat
 */
public class SceneManager extends Stage {

    private static Stage stage;
    private static Scene scene;
    private static MonopolySceneData sceneData;
    
    private static Parent lobbyRoot;
    private static LobbyController LobbyController;
    private static Label auctionLabel = new Label("0 €");
    private static Label hoechstgebotLabel = new Label("Höchstgebot:");
    private static JFXTextField bidTextField = new JFXTextField();

    public SceneManager() throws IOException {
        stage = this;
        
        if (!Assets.loaded()) {
            Assets.load();
        }
        
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/menu_scene.fxml"));
        
        scene = new Scene(root);
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) {
                fullscreen();
            }
        });
        
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> {
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
    
        });
    }

    public static void appendText(String message) {
        // TODO appendText
    }

    public static void changeScene(FXMLLoader loader) throws IOException {

        Parent root = loader.load();
        scene.setRoot(root);
    }

    public static void changeSceneToLobby(FXMLLoader loader) throws IOException {

        Parent root = loader.load();
        LobbyController = loader.getController();

        scene.setRoot(root);
        lobbyRoot = root;

    }

    public static void changeSceneBackToLobby() {
        scene.setRoot(lobbyRoot);
        LobbyController.animation();
    }

    public static void changeSceneToGame(Lobby lobby) throws IOException {
        
        sceneData = new MonopolySceneData(lobby.getController().getBoard());
        Scene gameScene = sceneData.getScene();
        
        Platform.runLater(() -> {
            stage.setScene(gameScene);
        });
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

    public static void openGameLayout(Lobby lobby) throws IOException {
        if (LobbyController != null) {
            LobbyController.loadGameLayout(lobby);
        }
    }

    public static void playerUpdate() {
        // TODO playerInitialise
    }

    public static void geldPlayerUpdate() {
        // TODO geldupdate()
    }

    public static void movePlayerUpdate() {
        // TODO playerUpdate
    }

    public static void propertyUpdate() {
        // TODO propertyUpdate()
    }

    public static void propertyStateUpdate() {
        // TODO propertyState()
    }
    
    public static void addPopup(Pane popupPane) {
        Platform.runLater(() -> sceneData.addPopupPane(popupPane));
    }
    
    public static void removePopup(Pane popupPane) {
        Platform.runLater(() -> sceneData.removePopupPane(popupPane));
    }
    
    public static void clearPopups() {
        Platform.runLater(() -> sceneData.clearPopups());
    }
    
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
        
        addPopup(gridpane);

        while (!buyButton.isPressed() || !dontBuyButton.isPressed()) {
            IOService.sleep(50);
            if (buyButton.isPressed()) {
                removePopup(gridpane);
                return 1;
            }
            if (dontBuyButton.isPressed()) {
                removePopup(gridpane);
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

        addPopup(gridpane);

        while (!rollButton.isPressed() || !payButton.isPressed() || !cardButton.isPressed()) {
            IOService.sleep(50);
            if (rollButton.isPressed()) {
                removePopup(gridpane);
                return 1;
            }
            if (payButton.isPressed()) {
                removePopup(gridpane);
                return 2;
            }
            if (cardButton.isPressed()) {
                removePopup(gridpane);
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
                + "-fx-effect: dropshadow(gaussian, yellowgreen, 20, 0, 0, 0);\n"
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

        addPopup(gridpane);

        while (!nothingButton.isPressed() || !buyHouseButton.isPressed() || !removeHouseButton.isPressed() || !addMortgageButton.isPressed() || !removeMortgageButton.isPressed() || !tradeButton.isPressed()) {
            IOService.sleep(50);
            if (nothingButton.isPressed()) {
                removePopup(gridpane);
                return 1;
            }
            if (buyHouseButton.isPressed()) {
                removePopup(gridpane);
                return 2;
            }
            if (removeHouseButton.isPressed()) {
                removePopup(gridpane);
                return 3;
            }
            if (addMortgageButton.isPressed()) {
                removePopup(gridpane);
                return 4;
            }
            if (removeMortgageButton.isPressed()) {
                removePopup(gridpane);
                return 5;
            }
            if (tradeButton.isPressed()) {
                removePopup(gridpane);
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
        Button eingabeButton = new Button();
        Button exitButton = new Button();

        String cssLayout = "-fx-background-color: #b2dfdb;\n"
                + "-fx-border-color: black;\n"
                + "-fx-border-insets: 5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: double;\n";

        box.setStyle(cssLayout);
        box.setSpacing(7);
        box.setPrefSize(200, 250);
        box.setCenterShape(true);
        eingabeButton.setText("Eingabe");
        eingabeButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));
        exitButton.setText("Schließen");
        exitButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));
        label.setFont(Font.font("Tahoma", 14));

        for (String fieldName : fields) {
            fieldBox.getItems().add(fieldName);
        }

        fieldBox.getSelectionModel().selectFirst();

        box.getChildren().addAll(label, fieldBox, eingabeButton, exitButton);
        box.setAlignment(Pos.CENTER);

        addPopup(gridPane);

        if (fields.length == 0) {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    fieldBox.setPromptText("Du besitzt keine Straßen!");
                    return null;
                }
            };
            Platform.runLater(task);
            IOService.sleep(3000);
            removePopup(gridPane);
            return 0;
        }

        while (!eingabeButton.isPressed() || !exitButton.isPressed()) {
            if (eingabeButton.isPressed()) {
                return fieldBox.getSelectionModel().getSelectedIndex() + 1;
            }
            if (exitButton.isPressed()) {
                return 0;
            }
            IOService.sleep(50);
        }
        
        removePopup(gridPane);
        
        return 0;
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
                    bidTextField.setText("");
                } catch (NumberFormatException e) {
                    bidTextField.setText("");
                    bidTextField.setPromptText("Nur Zahlen eingeben!");
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
        auctionHBox.setPrefSize(700, 200);
        auctionHBox.setCenterShape(true);
        auctionVBox.getChildren().addAll(bidButton, exitButton);
        auctionVBox.setSpacing(10);
        auctionVBox.setAlignment(Pos.CENTER);
        auctionHBox.getChildren().addAll(hoechstgebotLabel, auctionLabel, gebotsLabel, bidTextField, auctionVBox);
        auctionHBox.setAlignment(Pos.CENTER);
        
        addPopup(auctionGP);

        //Verknuepfung mit EventHandler(n)
        bidTextField.setOnAction(bid);
        bidButton.setOnAction(bid);
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AuctionService.playerExit(Lobby.getPlayerClient().getPlayerOnClient().getId());
                clearPopups();
            }
        });
    }

    public static void updateAuctionPopup(boolean stillActive, boolean noBidder) {

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                auctionLabel.setText(String.valueOf(AuctionService.getHighestBid()) + " €");
                hoechstgebotLabel.setText("Höchstgebot von \n" + AuctionService.getPlayer(AuctionService.getHighestBidder()).getName() + ":");
                //bidTextField.requestFocus();
                return null;
            }
        };
        Platform.runLater(task);

        IOService.sleep(500);
        if (!stillActive) {

            clearPopups();

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
                        + " hat die Auktion gewonnen und muss " + AuctionService.getHighestBid() + "€ für das Grundstück "
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
            addPopup(resetGridPane);
            IOService.sleep(3500);
            clearPopups();
            auctionLabel.setText("0 €");
        }

    }

    public static void bidTextFieldFocus() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                bidTextField.requestFocus();
                return null;
            }
        };
        Platform.runLater(task);

    }

    public static void fullscreen() {
        stage.setFullScreen(true);
    }

}
