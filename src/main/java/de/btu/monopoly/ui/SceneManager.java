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
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.input.IOService;
import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.ui.controller.LobbyController;
import de.btu.monopoly.ui.controller.MainSceneController;
import java.io.IOException;
import java.util.Optional;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author augat
 */
public class SceneManager extends Stage {

    private Stage stage;
    private static Scene scene;
    private static LobbyController LobbyController;
    private static MainSceneController GameController;
    private static Label auctionLabel = new Label("0 €");
    private static GridPane auctionGP = new GridPane();
    private static Label hoechstgebotLabel = new Label("Höchstgebot:");

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
                } else {
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

    public static void hausUpdate() {
        if (GameController != null) {
            GameController.hausAnzeigen();
        }
    }

    public static void hypothekUpdate() {
        if (GameController != null) {
            GameController.hypothekState();
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

        GameController.setPopupAbove(gridpane);

        while (!buyButton.isPressed() || !dontBuyButton.isPressed()) {
            IOService.sleep(50);
            if (buyButton.isPressed()) {
                GameController.resetPopupAbove();
                return 1;
            }
            if (dontBuyButton.isPressed()) {
                GameController.resetPopupAbove();
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

        GameController.setPopupAbove(gridpane);

        while (!rollButton.isPressed() || !payButton.isPressed() || !cardButton.isPressed()) {
            IOService.sleep(50);
            if (rollButton.isPressed()) {
                GameController.resetPopupAbove();
                return 1;
            }
            if (payButton.isPressed()) {
                GameController.resetPopupAbove();
                return 2;
            }
            if (cardButton.isPressed()) {
                GameController.resetPopupAbove();
                return 3;
            }
        }

        return -1;
    }

    public static int actionSequencePopup() {

        GridPane gridpane = new GridPane();
        //ScrollPane scroll = new ScrollPane();
        VBox box = new VBox();

        gridpane.setAlignment(Pos.CENTER);
        //scroll.setCenterShape(true);
        gridpane.add(box, 0, 0);
        // scroll.setContent(box);
        Label label = new Label("Was möchtest du noch tun?");

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

        box.setStyle(cssLayout);
        box.setSpacing(10);
        box.setPrefSize(200, 300);
        box.getChildren().addAll(label, nothingButton, buyHouseButton, removeHouseButton, addMortgageButton, removeMortgageButton, tradeButton);
        box.setAlignment(Pos.CENTER);

        GameController.setPopupAbove(gridpane);

        while (!nothingButton.isPressed() || !buyHouseButton.isPressed() || !removeHouseButton.isPressed() || !addMortgageButton.isPressed() || !removeMortgageButton.isPressed() || !tradeButton.isPressed()) {
            IOService.sleep(50);
            if (nothingButton.isPressed()) {
                GameController.resetPopupAbove();
                return 1;
            }
            if (buyHouseButton.isPressed()) {
                GameController.resetPopupAbove();
                return 2;
            }
            if (removeHouseButton.isPressed()) {
                GameController.resetPopupAbove();
                return 3;
            }
            if (addMortgageButton.isPressed()) {
                GameController.resetPopupAbove();
                return 4;
            }
            if (removeMortgageButton.isPressed()) {
                GameController.resetPopupAbove();
                return 5;
            }
            if (tradeButton.isPressed()) {
                GameController.resetPopupAbove();
                return 6;
            }
        }

        return -1;
    }

    public static int askForFieldPopup(Player player, Field[] fields) {

        GridPane gridPane = new GridPane();

        //ScrollPane scroll = new ScrollPane();
        VBox box = new VBox();
        gridPane.setAlignment(Pos.CENTER);
        //scroll.setCenterShape(true);
        gridPane.add(box, 0, 0);
        // scroll.setContent(box);
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

        for (Field field : fields) {
            fieldBox.getItems().add(field.getName());
        }

        fieldBox.getSelectionModel().selectFirst();

        box.getChildren().addAll(label, fieldBox, button);
        box.setAlignment(Pos.CENTER);

        GameController.setPopupAbove(gridPane);

        while (!button.isPressed()) {
            IOService.sleep(50);
        }

        GameController.resetPopupAbove();

        int id = 0;
        for (Field field : Lobby.getPlayerClient().getGame().getBoard().getFieldManager().getFields()) {

            id++;
            if (field == fields[fieldBox.getSelectionModel().getSelectedIndex()]) {
                return id;

            }
        }

        return -1;
    }

    public static void AuctionPopup() {

        //initialisierung der benoetigten Objekte
        //ScrollPane scroll = new ScrollPane();
        VBox box = new VBox();
        auctionGP.setAlignment(Pos.CENTER);
        // scroll.setCenterShape(true);
        auctionGP.add(box, 0, 0);
        // scroll.setContent(box);

        hoechstgebotLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        Label label2 = new Label("Dein Gebot für \n" + AuctionService.getPropertyString() + ":");
        label2.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        JFXTextField tf = new JFXTextField();
        tf.setAlignment(Pos.CENTER);
        JFXButton bidBut = new JFXButton("Bieten");
        bidBut.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        JFXButton exitBut = new JFXButton("Aussteigen");
        exitBut.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        tf.setPromptText(" ");
        String cssLayout = "-fx-background-color: #dcedc8;\n"
                + "-fx-border-color: black;\n"
                + "-fx-border-insets: 5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: double;\n";

        box.setStyle(cssLayout);
        box.setSpacing(10);
        box.setPrefSize(200, 300);
        box.setCenterShape(true);
        box.getChildren().addAll(hoechstgebotLabel, auctionLabel, label2, tf, bidBut, exitBut);
        box.setAlignment(Pos.CENTER);
        if (GameController != null) {
            GameController.setPopupAbove(auctionGP);
        }

        bidBut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    AuctionService.setBid(Lobby.getPlayerClient().getPlayerOnClient().getId(), Integer.parseInt(tf.getText()));
                    // GameController.resetPopup(auctionGP);
                } catch (NumberFormatException e) {
                    tf.setText("");
                    tf.setPromptText("Bitte nur Zahlen eingeben!");
                }
            }
        });

        exitBut.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AuctionService.playerExit(Lobby.getPlayerClient().getPlayerOnClient().getId());
                GameController.resetPopupAbove();
            }
        });
    }

    public static void updateAuctionPopup(boolean stillActive, boolean noBidder) {

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                auctionLabel.setText(String.valueOf(AuctionService.getHighestBid()));
                hoechstgebotLabel.setText("Höchstgebot von \n" + AuctionService.getPlayer(AuctionService.getHighestBidder()).getName() + ":");
                return null;
            }
        };
        Platform.runLater(task);

        IOService.sleep(500);

        if (!stillActive) {

            GameController.resetPopupAbove();

            GridPane gp = new GridPane();
            VBox box = new VBox();
            Label lbl = new Label();

            gp.setAlignment(Pos.CENTER);
            gp.add(box, 0, 0);

            if (noBidder) {
                lbl.setText("Das Grundstück " + AuctionService.getPropertyString() + " wurde nicht verkauft!");
            } else {
                lbl.setText(Lobby.getPlayerClient().getGame().getPlayers()[AuctionService.getHighestBidder()].getName()
                        + " hat die Auktion gewonnen und muss " + AuctionService.getHighestBid() + "€ für das Grundstück "
                        + AuctionService.getPropertyString() + " zahlen!");
            }
            String cssLayout = "-fx-background-color: #dcedc8;\n"
                    + "-fx-border-color: black;\n"
                    + "-fx-border-insets: 5;\n"
                    + "-fx-border-width: 1;\n"
                    + "-fx-border-style: double;\n";

            lbl.setFont(Font.font("Tahoma", FontWeight.BOLD, 10));
            box.setStyle(cssLayout);
            box.setSpacing(10);
            box.setPrefSize(550, 150);
            box.setCenterShape(true);
            box.getChildren().addAll(lbl);
            box.setAlignment(Pos.CENTER);
            GameController.setPopupAbove(gp);
            IOService.sleep(3500);
            GameController.resetPopupAbove();
        }

    }

    public static void initStreets() {
        GameController.initStreets();
    }

}
