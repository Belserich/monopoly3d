/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import de.btu.monopoly.Global;
import de.btu.monopoly.core.service.AuctionService;
import de.btu.monopoly.core.service.IOService;
import de.btu.monopoly.ui.GameSceneManager.Popup;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author Christian Prinz
 */
public class AuctionUI {

    private static final Logger LOGGER = Logger.getLogger(AuctionUI.class.getCanonicalName());

    private Popup popup;
    private final GridPane gridPane = new GridPane();
    private final HBox hBox = new HBox();
    private final VBox vBox = new VBox();
    private final JFXButton bidButton = new JFXButton("Bieten");
    private final JFXButton exitButton = new JFXButton("Aussteigen");
    private final JFXTextField bidTextField = new JFXTextField();
    private final Label gebotsLabel = new Label("Dein Gebot für \n" + AuctionService.getPropertyString() + ":");
    private final Label auctionLabel = new Label("0 €");
    private final Label hoechstgebotLabel = new Label("Höchstgebot:");

    private void initAuctionGridpane() {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(hBox, 0, 0);
        gridPane.getStylesheets().add(getClass().getResource("/styles/game.css").toExternalForm());

        hoechstgebotLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        gebotsLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        bidTextField.setAlignment(Pos.CENTER);
        bidTextField.setPromptText(" ");
        bidTextField.setOnAction(bidButtonHandler());

        bidButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));
        bidButton.setOnAction(bidButtonHandler());
        exitButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));
        exitButton.setOnAction((ActionEvent event) -> {
            hBox.getChildren().removeAll(gebotsLabel, bidTextField, vBox);
            hBox.setPrefSize(300, 100);
            AuctionService.playerExit(Global.ref().getClient().getPlayerOnClient().getId());
        });

        hBox.setId("greenPopup");
        hBox.setSpacing(10);
        hBox.setPrefSize(600, 200);
        hBox.setCenterShape(true);
        vBox.getChildren().addAll(bidButton, exitButton);
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(hoechstgebotLabel, auctionLabel);
        if (Global.ref().getClient().getPlayerOnClient().getBank().isLiquid()) {
            hBox.getChildren().addAll(gebotsLabel, bidTextField, vBox);
        }
        else {
            AuctionService.playerExit(Global.ref().getClient().getPlayerOnClient().getId());
        }

        hBox.setAlignment(Pos.CENTER);

        Platform.runLater(() -> bidTextField.requestFocus());
    }

    public void revertPopup() {
        Platform.runLater(() -> {
            hBox.getChildren().addAll(gebotsLabel, bidTextField, vBox);
            hBox.setPrefSize(600, 200);
        });

    }

    private EventHandler bidButtonHandler() {
        return (EventHandler<ActionEvent>) (ActionEvent event) -> {
            try {
                AuctionService.setBid(Global.ref().getClient().getPlayerOnClient().getId(), Integer.parseInt(bidTextField.getText()));
                bidTextField.setText("");
            } catch (NumberFormatException e) {
                bidTextField.setText("");
                bidTextField.setPromptText("Nur Zahlen eingeben!");
                LOGGER.log(Level.WARNING, "FEHLER in auctionPopup: {0}", e);
            }
        };
    }

    public void updatePopup(boolean stillActive, boolean noBidder) {
        if (!noBidder) {
            Platform.runLater(() -> {
                auctionLabel.setText(String.valueOf(AuctionService.getHighestBid()) + " €");
                hoechstgebotLabel.setText("Höchstgebot von \n" + AuctionService.getPlayer(AuctionService.getHighestBidder()).getName() + ":");
            });
        }

        IOService.sleep(500);
        if (!stillActive) {

            // TODO
            GridPane resetGridPane = new GridPane();
            VBox resetBox = new VBox();
            Label endLabel = new Label();

            resetGridPane.setAlignment(Pos.CENTER);
            resetGridPane.add(resetBox, 0, 0);

            if (noBidder) {
                endLabel.setText("Das Grundstück " + AuctionService.getPropertyString() + " wurde nicht verkauft!");
            }
            else {
                endLabel.setText(Global.ref().getGame().getPlayers()[AuctionService.getHighestBidder()].getName()
                        + " hat die Auktion gewonnen und muss " + AuctionService.getHighestBid() + "€ \nfür das Grundstück "
                        + AuctionService.getPropertyString() + " zahlen!");
            }

            endLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
            resetBox.setId("greenPopup");
            resetBox.setSpacing(10);
            resetBox.setPrefSize(450, 100);
            resetBox.setCenterShape(true);
            resetBox.getChildren().addAll(endLabel);
            resetBox.setAlignment(Pos.CENTER);

            Global.ref().getGameSceneManager().endAuctionPopup(popup, resetGridPane);
            Platform.runLater(() -> auctionLabel.setText("0 €"));
        }
    }

//_________________GETTER_UND_SETTER_______________________________________________________________
    public GridPane getGridPane() {
        return gridPane;
    }

    public void setPopup(Popup popup) {
        this.popup = popup;
    }

//_________________INSTANCE________________________________________________________________________
    private AuctionUI() {
        initAuctionGridpane();
    }

    public static AuctionUI getInstance() {
        return AuctionUIHolder.INSTANCE;
    }

    private static class AuctionUIHolder {

        private static final AuctionUI INSTANCE = new AuctionUI();
    }
}
