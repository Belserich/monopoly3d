package de.btu.monopoly.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.btu.monopoly.Global;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.service.AuctionService;
import de.btu.monopoly.core.service.IOService;
import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.card.Card.Action;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.CardField;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.ui.fx3d.Fx3dGameBoard;
import de.btu.monopoly.util.Assets;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.LinkedList;
import java.util.List;

import static de.btu.monopoly.ui.CameraManager.WatchMode;

public class GameSceneManager {
    
    private static final double DEFAULT_SCENE_WIDTH = 1280;
    private static final double DEFAULT_SCENE_HEIGHT = 720;
    
    private static final Pane EMPTY_POPUP_PANE = new Pane();
    
    private final Scene scene;
    private final Fx3dGameBoard board3d;
    private final SubScene gameSub;

    private final BorderPane uiPane;
    private final VBox popupWrapper;
    private final List<Pane> popupQueue;
    private VBox playerBox;
    
    private CameraManager camMan;
    
    private Label auctionLabel = new Label("0 €");
    private Label hoechstgebotLabel = new Label("Höchstgebot:");
    private JFXTextField bidTextField = new JFXTextField();
    
    public GameSceneManager(GameBoard board) {
        
        this.board3d = new Fx3dGameBoard(board);
        
        gameSub = new SubScene(board3d, 0, 0, true, SceneAntialiasing.DISABLED);
        gameSub.setCache(true);
        gameSub.setCacheHint(CacheHint.SPEED);
        
        uiPane = new BorderPane();
        popupWrapper = new VBox();
        popupQueue = new LinkedList<>();
        
        playerBox = new VBox();
    
        StackPane uiStack = new StackPane(gameSub, uiPane, popupWrapper);
        uiStack.setAlignment(Pos.CENTER);
        scene = new Scene(
                uiStack,
                DEFAULT_SCENE_WIDTH, DEFAULT_SCENE_HEIGHT
        );
        
        Global.ref().getGame().addGameStateListener(board3d.gameStateAdapter());
        initScene();
    }
    
    private void initScene()
    {
        gameSub.setFill(Color.LIGHTGRAY);
        
        gameSub.widthProperty().bind(scene.widthProperty());
        gameSub.heightProperty().bind(scene.heightProperty());
        
        initPopups();
        initUi();
        initCams();
    }
    
    private void initPopups() {
    
        board3d.animatingProperty().addListener((prop, oldB, newB) -> {
            if (!newB && !popupQueue.isEmpty()) {
                nextPopup();
            }
        });
    }
    
    private void initUi() {
        
        popupWrapper.setAlignment(Pos.CENTER);
        popupWrapper.setPickOnBounds(false);
        
        TextField chatField = new TextField();
        
        HBox chatInteractionBox = new HBox(chatField, new Button("Senden"));
        HBox.setHgrow(chatField, Priority.ALWAYS);
    
        TextArea chatArea = new TextArea();
        
        VBox wholeChatBox = new VBox(chatArea, chatInteractionBox);
        VBox.setVgrow(chatArea, Priority.ALWAYS);
        
        wholeChatBox.setVisible(false);
        wholeChatBox.setPrefWidth(400);
        
        uiPane.setRight(wholeChatBox);
        
        BorderPane topButtonPane = new BorderPane();
        topButtonPane.setPickOnBounds(false);
        
        ToggleButton viewButton = new ToggleButton(null, Assets.getIcon("3d_icon"));
        viewButton.setOnMousePressed(event -> {
            boolean selected = !viewButton.isSelected();
            camMan.watch(board3d, selected ? WatchMode.PERSPECTIVE : WatchMode.ORTHOGONAL);
        });
        viewButton.setPrefSize(50, 50);
    
        ToggleButton chatButton = new ToggleButton("Chat");
        chatButton.setOnMouseReleased(event -> wholeChatBox.setVisible(chatButton.isSelected()));
        chatButton.setPrefSize(50, 50);
        
        topButtonPane.setPadding(new Insets(0, 0, 5, 0));
        topButtonPane.setLeft(viewButton);
        topButtonPane.setRight(chatButton);
        
        uiPane.setTop(topButtonPane);
        
        playerBox = new VBox();
        playerBox.setPickOnBounds(false);
        playerBox.setPadding(new Insets(10, 0, 0, 0));
        playerBox.setSpacing(10);
        ObservableList<Node> children = playerBox.getChildren();
        board3d.getPlayers().forEach(p -> children.add(p.infoPane()));
        
        uiPane.setLeft(playerBox);
        
        uiPane.setPadding(new Insets(5, 5, 5, 5));
        uiPane.setPickOnBounds(false);
    }
    
    private void initCams() {
        camMan = new CameraManager(gameSub);
        camMan.watch(board3d, WatchMode.ORTHOGONAL);
    }
    
    private void nextPopup() {
        popupWrapper.getChildren().clear();
        Pane pop = popupQueue.remove(0);
        popupWrapper.getChildren().add(pop);
    }
    
    private void queuePopup(Pane pane) {
        Platform.runLater(() -> {
            popupQueue.add(pane);
            if (!board3d.animatingProperty().get())
                nextPopup();
        });
    }
    
    private void queueNullPopup() {
        queuePopup(EMPTY_POPUP_PANE);
    }
    
    public Scene getScene() {
        return scene;
    }
    
    public int buyPropertyPopup() {
        
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
                + "-fx-border-width: 1";
        
        box.setStyle(cssLayout);
        box.setSpacing(10);
        box.setPrefSize(300, 200);
        label.setFont(Font.font("Tahoma", 14));
        box.getChildren().addAll(label, buyButton, dontBuyButton);
        box.setAlignment(Pos.CENTER);
        
        queuePopup(gridpane);
        
        while (!buyButton.isPressed() || !dontBuyButton.isPressed()) {
            IOService.sleep(50);
            if (buyButton.isPressed()) {
                queueNullPopup();
                return 1;
            }
            if (dontBuyButton.isPressed()) {
                queueNullPopup();
                return 2;
            }
        }
        
        return -1;
    }
    
    public int jailChoicePopup() {
        
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
                + "-fx-border-width: 1";
        
        box.setStyle(cssLayout);
        box.setSpacing(10);
        box.setPrefSize(200, 300);
        box.getChildren().addAll(label, rollButton, payButton, cardButton);
        box.setAlignment(Pos.CENTER);
        
        queuePopup(gridpane);
        
        while (!rollButton.isPressed() || !payButton.isPressed() || !cardButton.isPressed()) {
            IOService.sleep(50);
            if (rollButton.isPressed()) {
                queueNullPopup();
                return 1;
            }
            if (payButton.isPressed()) {
                queueNullPopup();
                return 2;
            }
            if (cardButton.isPressed()) {
                queueNullPopup();
                return 3;
            }
        }
        
        return -1;
    }
    
    public int actionSequencePopup() {
        
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
        
        vbox.setStyle(
                "-fx-background-color: #b9f6ca; "
                        + "-fx-border-color: black; "
                        + "-fx-effect: dropshadow(gaussian, yellowgreen, 20, 0, 0, 0); "
                        + "-fx-border-insets: 5; "
                        + "-fx-border-width: 1"
        );
        
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
        
        queuePopup(gridpane);
        
        while (!nothingButton.isPressed() || !buyHouseButton.isPressed() || !removeHouseButton.isPressed() || !addMortgageButton.isPressed() || !removeMortgageButton.isPressed() || !tradeButton.isPressed()) {
            IOService.sleep(50);
            if (nothingButton.isPressed()) {
                queueNullPopup();
                return 1;
            }
            if (buyHouseButton.isPressed()) {
                queueNullPopup();
                return 2;
            }
            if (removeHouseButton.isPressed()) {
                queueNullPopup();
                return 3;
            }
            if (addMortgageButton.isPressed()) {
                queueNullPopup();
                return 4;
            }
            if (removeMortgageButton.isPressed()) {
                queueNullPopup();
                return 5;
            }
            if (tradeButton.isPressed()) {
                queueNullPopup();
                return 6;
            }
        }
        
        return -1;
    }
    
    public int askForFieldPopup(Player player, String[] fields) {
        
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
                + "-fx-border-width: 1";
        
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
        
        queuePopup(gridPane);
        
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
            queueNullPopup();
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
        
        queueNullPopup();
        
        return 0;
    }

    public void auctionPopup() {

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
                + "-fx-border-width: 1";
        
        auctionHBox.setStyle(cssLayout);
        auctionHBox.setSpacing(10);
        auctionHBox.setPrefSize(700, 200);
        auctionHBox.setCenterShape(true);
        auctionVBox.getChildren().addAll(bidButton, exitButton);
        auctionVBox.setSpacing(10);
        auctionVBox.setAlignment(Pos.CENTER);
        auctionHBox.getChildren().addAll(hoechstgebotLabel, auctionLabel, gebotsLabel, bidTextField, auctionVBox);
        auctionHBox.setAlignment(Pos.CENTER);
        
        queuePopup(auctionGP);
        
        //Verknuepfung mit EventHandler(n)
        bidTextField.setOnAction(bid);
        bidButton.setOnAction(bid);
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AuctionService.playerExit(Lobby.getPlayerClient().getPlayerOnClient().getId());
                queueNullPopup();
            }
        });
    }
    
    public void updateAuctionPopup(boolean stillActive, boolean noBidder) {
        
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

            queueNullPopup();

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
                    + "-fx-border-width: 1";

            endLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 10));
            resetBox.setStyle(cssLayout);
            resetBox.setSpacing(10);
            resetBox.setPrefSize(550, 150);
            resetBox.setCenterShape(true);
            resetBox.getChildren().addAll(endLabel);
            resetBox.setAlignment(Pos.CENTER);
            queuePopup(resetGridPane);
            IOService.sleep(3500);
            queueNullPopup();
            auctionLabel.setText("0 €");
        }

    }

    public void showCard() {

        if (Lobby.getPlayerClient().getGame() != null) {
            if (Lobby.getPlayerClient().getGame().getBoard() != null) {

                GridPane kartPane = new GridPane();
                VBox box = new VBox();

                kartPane.setAlignment(Pos.CENTER);
                kartPane.getChildren().add(box);

                //TODO Text der Karten
                Label text = new Label();

                box.setAlignment(Pos.CENTER);
                box.setPrefSize(250, 150);
                kartPane.getChildren().add(text);
                kartPane.setAlignment(Pos.CENTER);
                Player[] players = Lobby.getPlayerClient().getGame().getPlayers();
                Field[] fields = Lobby.getPlayerClient().getGame().getBoard().getFieldManager().getFields();

                for (Player p : players) {

                    Card card;
                    if (fields[p.getPosition()] instanceof CardField) {
                        
                        CardField cf = ((CardField) fields[p.getPosition()]);
                        CardStack stack = Lobby.getPlayerClient().getGame().getBoard().getCardManager().getStack(cf.getStackType());

                        if (p.getPosition() == 2 || p.getPosition() == 17 || p.getPosition() == 33) {
                            //Gemeinschaft
                            box.setStyle("-fx-background-color: #fff59d;\n"
                                    + "    -fx-border-color: #ff7043;\n"
                                    + "    -fx-border-insets: 5;\n"
                                    + "    -fx-border-width: 1;\n"
                                    + "    -fx-effect: dropshadow(gaussian, #aabb97, 20, 0, 0, 0);\n"
                            );
                        }
                        else {
                            //Ereignis
                            box.setStyle("-fx-background-color: #ff8a65;\n"
                                    + "    -fx-border-color: #ffd54f;\n"
                                    + "    -fx-border-insets: 5;\n"
                                    + "    -fx-border-width: 1;\n"
                                    + "    -fx-effect: dropshadow(gaussian, #e57373, 20, 0, 0, 0);\n");
                        }

                        card = stack.nextCard();
                        text.setText("\t" + card.getText());

                        queuePopup(kartPane);
                        System.out.println("ascsaklncl___1__");

                    }
                }

            }
            IOService.sleep(300);
            System.out.println("ascsaklncl___2__");
            queueNullPopup();
            System.out.println("ascsaklncl___3__");
        }
    }

    public void bidTextFieldFocus() {
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                bidTextField.requestFocus();
                return null;
            }
        };
        Platform.runLater(task);
        
    }
}
