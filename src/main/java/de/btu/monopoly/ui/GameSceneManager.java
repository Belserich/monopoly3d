package de.btu.monopoly.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.btu.monopoly.Global;
import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.GameStateAdapter;
import de.btu.monopoly.core.service.AuctionService;
import de.btu.monopoly.core.service.IOService;
import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.CardField;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.field.FieldManager;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.net.chat.GUIChat;
import de.btu.monopoly.ui.CameraManager.WatchMode;
import de.btu.monopoly.ui.fx3d.Fx3dGameBoard;
import de.btu.monopoly.ui.fx3d.Fx3dPropertyField;
import de.btu.monopoly.util.Assets;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class GameSceneManager {

    private static final double DEFAULT_SCENE_WIDTH = 1280;
    private static final double DEFAULT_SCENE_HEIGHT = 720;

    private static final Pane EMPTY_POPUP_PANE = new Pane();

    private final Scene scene;
    private final SubScene gameSub;
    private final Fx3dGameBoard board3d;

    private final BorderPane uiPane;
    private final VBox popupWrapper;
    private final List<Popup> popupQueue;
    
    private final VBox playerBox;
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
        uiStack.setPickOnBounds(false);
        
        scene = new Scene(
                uiStack,
                DEFAULT_SCENE_WIDTH, DEFAULT_SCENE_HEIGHT
        );
        
        Global.ref().getGame().addGameStateListener(new GameStateAdapterImpl());
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
                displayPopups();
            }
        });
    }

    private void initUi() {
        
        ObservableList<Node> children1 = popupWrapper.getChildren();
        board3d.getFields()
                .filter(Fx3dPropertyField.class::isInstance)
                .map(Fx3dPropertyField.class::cast)
                .forEach(prop -> {
                    prop.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> children1.add(0, prop.infoPane()));
                    prop.addEventHandler(MouseEvent.MOUSE_EXITED, event -> children1.remove(prop.infoPane()));
                });
        
        popupWrapper.setAlignment(Pos.CENTER);
        popupWrapper.setPickOnBounds(false);

        VBox wholeChatBox = ChatUi.getInstance().getWholeChatBox();
        HBox chatToggleBox = ChatUi.getInstance().getChatToggleBox();

        uiPane.setRight(wholeChatBox);

        BorderPane topButtonPane = new BorderPane();
        topButtonPane.setPickOnBounds(false);

        ToggleButton viewButton = new ToggleButton(null, Assets.getIcon("3d_icon"));
        viewButton.setOnMousePressed(event -> {
            boolean selected = !viewButton.isSelected();
            camMan.watch(board3d, selected ? WatchMode.PERSPECTIVE : WatchMode.ORTHOGONAL);
        });
        viewButton.setPrefSize(50, 50);

        topButtonPane.setPadding(new Insets(0, 0, 5, 0));
        topButtonPane.setLeft(viewButton);
        topButtonPane.setRight(chatToggleBox);

        uiPane.setTop(topButtonPane);
        
        playerBox.setPickOnBounds(false);
        playerBox.setPadding(new Insets(10, 0, 0, 0));
        playerBox.setSpacing(10);
        ObservableList<Node> children = playerBox.getChildren();
        board3d.getPlayers().forEach(p -> children.add(p.infoPane()));

        uiPane.setLeft(playerBox);

        uiPane.setPadding(new Insets(5, 5, 5, 5));
        uiPane.setPickOnBounds(false);
    }

    private void clickSendMessage(TextField chatField) {
        if (!chatField.getText().isEmpty()) {
            GUIChat.getInstance().msg(Global.ref().playerOnClient(), chatField.getText());
        }
        chatField.clear();

    }

    private class ChatObserver implements Observer {

        private final TextFlow area;

        ChatObserver(TextFlow textFlow) {
            area = textFlow;
        }

        TextFlow getTextFlow() {
            return area;
        }

        @Override
        public void update(Observable o, Object arg) {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    for (int i = 0; i < 5; i++) {
                        Text[] message = (Text[]) arg;
                        area.getChildren().addAll(message[0], message[1]);
                    }
                    return null;
                }
            };
            Platform.runLater(task);
        }

    }

    private void initCams() {
        camMan = new CameraManager(gameSub);
        camMan.watch(board3d, WatchMode.ORTHOGONAL);
    }
    
    private void displayPopup(Popup pop) {
        pop.pane.setPickOnBounds(false);
        popupWrapper.getChildren().add(pop.pane);
    }
    
    private void displayPopups() {
        
        popupQueue.forEach(pop -> {
            displayPopup(pop);
    
            Duration dur = pop.duration;
            if (!dur.isIndefinite()) {
                PauseTransition pause = new PauseTransition(dur);
                pause.setOnFinished(inv -> popupWrapper.getChildren().remove(pop.pane));
                pause.play();
            }
        });
        popupQueue.clear();
    }
    
    private void queuePopup(Popup pop) {
        Platform.runLater(() -> {
            popupQueue.add(pop);
            if (!board3d.animatingProperty().get())
                displayPopups();
        });
    }
    
    private void destroyPopup(Popup pop) {
        Platform.runLater(() -> {
            popupWrapper.getChildren().remove(pop.pane);
        });
    }

    public Scene getScene() {
        return scene;
    }

    public int buyPropertyPopup() {
        
        GridPane gridpane = new GridPane();
        gridpane.setPickOnBounds(false);
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
        
        Popup pop = new Popup(gridpane);
        queuePopup(pop);
        
        while (!buyButton.isPressed() || !dontBuyButton.isPressed()) {
            IOService.sleep(50);
            if (buyButton.isPressed()) {
                destroyPopup(pop);
                return 1;
            }
            if (dontBuyButton.isPressed()) {
                destroyPopup(pop);
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
        
        Popup pop = new Popup(gridpane);
        queuePopup(pop);
        
        while (!rollButton.isPressed() || !payButton.isPressed() || !cardButton.isPressed()) {
            IOService.sleep(50);
            if (rollButton.isPressed()) {
                destroyPopup(pop);
                return 1;
            }
            if (payButton.isPressed()) {
                destroyPopup(pop);
                return 2;
            }
            if (cardButton.isPressed()) {
                destroyPopup(pop);
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
        
        Popup pop = new Popup(gridpane);
        queuePopup(pop);
        
        while (!nothingButton.isPressed() || !buyHouseButton.isPressed() || !removeHouseButton.isPressed() || !addMortgageButton.isPressed() || !removeMortgageButton.isPressed() || !tradeButton.isPressed()) {
            IOService.sleep(50);
            if (nothingButton.isPressed()) {
                destroyPopup(pop);
                return 1;
            }
            if (buyHouseButton.isPressed()) {
                destroyPopup(pop);
                return 2;
            }
            if (removeHouseButton.isPressed()) {
                destroyPopup(pop);
                return 3;
            }
            if (addMortgageButton.isPressed()) {
                destroyPopup(pop);
                return 4;
            }
            if (removeMortgageButton.isPressed()) {
                destroyPopup(pop);
                return 5;
            }
            if (tradeButton.isPressed()) {
                destroyPopup(pop);
                return 6;
            }
        }

        return -1;
    }
    
    public int askForFieldPopup() {
        
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
        
        Game game = Global.ref().getGame();
        Player currPlayer = game.getCurrentPlayer();
        FieldManager fima = game.getBoard().getFieldManager();
        fima.getOwnedPropertyFields(currPlayer).forEach(prop -> fieldBox.getItems().add(prop.getName()));
        
        fieldBox.getSelectionModel().selectFirst();

        box.getChildren().addAll(label, fieldBox, eingabeButton, exitButton);
        box.setAlignment(Pos.CENTER);
        
        Popup pop = new Popup(gridPane, Duration.seconds(2));
        queuePopup(pop);
        
        if (fima.getOwnedPropertyFields(currPlayer).count() == 0) {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    fieldBox.setPromptText("Du besitzt keine Straßen!");
                    return null;
                }
            };
            Platform.runLater(task);
            destroyPopup(pop);
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
        
        destroyPopup(pop);
        
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
        
        Popup pop = new Popup(auctionGP);
        queuePopup(pop);
        
        //Verknuepfung mit EventHandler(n)
        bidTextField.setOnAction(bid);
        bidButton.setOnAction(bid);
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AuctionService.playerExit(Lobby.getPlayerClient().getPlayerOnClient().getId());
                destroyPopup(pop);
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
            
            Popup pop = new Popup(resetGridPane, Duration.seconds(3));
            queuePopup(pop);
            auctionLabel.setText("0 €");
        }

    }

    public void showCard(Card card, CardStack.Type type) {

        if (Lobby.getPlayerClient().getGame() != null) {
            if (Lobby.getPlayerClient().getGame().getBoard() != null) {

                GridPane kartPane = new GridPane();
                VBox box = new VBox();

                kartPane.setAlignment(Pos.CENTER);
                kartPane.getChildren().add(box);
                
                Label text = new Label("\t" + card.getText());

                box.setAlignment(Pos.CENTER);
                box.setPrefSize(250, 150);
                kartPane.getChildren().add(text);
                kartPane.setAlignment(Pos.CENTER);
                Player[] players = Lobby.getPlayerClient().getGame().getPlayers();
                Field[] fields = Lobby.getPlayerClient().getGame().getBoard().getFieldManager().getFields();
    
                if (type == CardStack.Type.COMMUNITY) {
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
                
                Popup pop = new Popup(kartPane, Duration.seconds(3));
                queuePopup(pop);
            }
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
    
    private class Popup {
        
        private Pane pane;
        private Duration duration;
        
        private Popup(Pane pane, Duration duration) {
            this.pane = pane;
            this.duration = duration;
        }
        
        private Popup(Pane pane) {
            this(pane, Duration.INDEFINITE);
        }
    }
    
    private class GameStateAdapterImpl extends GameStateAdapter {
    
        @Override
        public void onPlayerOnCardField(Player player, CardField cardField, Card card) {
            showCard(card, cardField.getStackType());
        }
    }
}
