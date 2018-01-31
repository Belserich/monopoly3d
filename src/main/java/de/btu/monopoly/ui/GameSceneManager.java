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
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.ui.CameraManager.WatchMode;
import de.btu.monopoly.ui.fx3d.Fx3dGameBoard;
import de.btu.monopoly.ui.fx3d.Fx3dPropertyField;
import de.btu.monopoly.util.Assets;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class GameSceneManager {

    private static final double DEFAULT_SCENE_WIDTH = 1280;
    private static final double DEFAULT_SCENE_HEIGHT = 720;

    private final Scene scene;
    private final SubScene gameSub;
    private final Fx3dGameBoard board3d;

    private final BorderPane uiPane;
    private final VBox popupWrapper;
    private final List<Popup> popupQueue;

    private final VBox gameInfoBox;
    private final VBox playerBox;
    private final VBox cardHandle;

    private CameraManager camMan;

    private Label auctionLabel = new Label("0 €");
    private Label hoechstgebotLabel = new Label("Höchstgebot:");
    private JFXTextField bidTextField = new JFXTextField();

    //Handelsspezifisch
    private boolean tradeOfferIsCreated = false;
    private boolean exitTrade = false;
    private boolean tradeAnswerIsGiven = false;
    private boolean tradeAnswer = false;
    private char currency = '€';

    public GameSceneManager(GameBoard board) {
        this.board3d = new Fx3dGameBoard(board);

        gameSub = new SubScene(board3d, 0, 0, true, SceneAntialiasing.DISABLED);
        gameSub.setCache(true);
        gameSub.setCacheHint(CacheHint.SPEED);

        uiPane = new BorderPane();
        popupWrapper = new VBox();

        popupQueue = new LinkedList<>();

        playerBox = new VBox();
        cardHandle = new VBox();
        gameInfoBox = new VBox(playerBox, cardHandle);

        StackPane uiStack = new StackPane(gameSub, uiPane, popupWrapper);
        uiStack.setAlignment(Pos.CENTER);
        uiStack.setPickOnBounds(false);

        scene = new Scene(
                uiStack,
                DEFAULT_SCENE_WIDTH, DEFAULT_SCENE_HEIGHT
        );

        Global.ref().getGame().addGameStateListener(new GameStateAdapterImpl(playerBox));
        Global.ref().getGame().addGameStateListener(board3d.gameStateAdapter());
        initScene();
    }

    private void initScene() {
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

        cardHandle.setPadding(new Insets(20, 0, 0, 20));

        board3d.getFields()
                .filter(Fx3dPropertyField.class::isInstance)
                .map(Fx3dPropertyField.class::cast)
                .forEach(prop -> {
                    prop.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> cardHandle.getChildren().add(0, prop.infoPane()));
                    prop.addEventHandler(MouseEvent.MOUSE_EXITED, event -> cardHandle.getChildren().remove(prop.infoPane()));
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

        uiPane.setLeft(gameInfoBox);

        uiPane.setPadding(new Insets(5, 5, 5, 5));
        uiPane.setPickOnBounds(false);
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
            if (!board3d.animatingProperty().get()) {
                displayPopups();
            }
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

        Popup pop = new Popup(gridPane);
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
                destroyPopup(pop);
                return fieldBox.getSelectionModel().getSelectedIndex() + 1;
            }
            if (exitButton.isPressed()) {
                destroyPopup(pop);
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

    protected class Popup {

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

    /*
     * Init Popup fuer den Handel
     */
    public void initTradePopup(Player player, GuiTrade tradeGui) {

        tradeGui.setTradeStarter(player);

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

        Popup pop = new Popup(initTradeGP);

        //Eventhandler
        EventHandler selectPlayer = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    /*
                     * Nimmt die Auswahl aus der Combobox und übergibt Sie dem Handel
                     */
                    String tradePartnersName = (String) choosePlayerBox.getSelectionModel().getSelectedItem();
                    GameBoard board = Lobby.getPlayerClient().getGame().getBoard();

                    for (int i = 0; i < Lobby.getPlayerClient().getGame().getBoard().getActivePlayers().size(); i++) {
                        if (tradePartnersName.equals(Lobby.getPlayerClient().getGame().getPlayers()[i].getName())) {
                            tradeGui.setTradePartner(Lobby.getPlayerClient().getGame().getPlayers()[i]);
                            //gibt an, ob sich fuer ein Tauschpartner entschieden wurde
                            break;
                        }
                    }

                    Task task = new Task() {
                        @Override
                        protected Object call() throws Exception {
                            showTradeInfoPopup(tradeGui);
                            destroyPopup(pop);
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

        Global.ref().getGameSceneManager().queuePopup(pop);

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

    public Player getTradePartner(GuiTrade tradeGui) {
        return tradeGui.getTradePartner();
    }

    public boolean getTradeOfferIsCreated() {
        return tradeOfferIsCreated;
    }

    public void resetTradeOfferIsCreated() {
        tradeOfferIsCreated = false;
    }

    private void showTradeInfoPopup(GuiTrade tradeGui) {

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

        Popup pop = new Popup(tradeInfoGridPane, Duration.millis(2500));
        Global.ref().getGameSceneManager().queuePopup(pop);

        selectTradeOfferPopup(tradeGui);
////        //Weiterleitung an das naechste Popup
////        Timeline timer = new Timeline(new KeyFrame(
////                Duration.millis(2500),
////                timeOver -> selectTradeOfferPopup(tradeGui)));
////
////        timer.play();

    }

    /**
     * Erzeugt das Gui-Fenster in dem man ein TradeOffer erstellt
     */
    private void selectTradeOfferPopup(GuiTrade tradeGui) {

        //Liste(n)
        List<CheckMenuItem> yourProps = tradePlayersProps(Lobby.getPlayerClient().getPlayerOnClient());
        List<CheckMenuItem> yourPropsForMenu = new LinkedList<>();
        List<CheckMenuItem> partnersProps = FXCollections.observableArrayList(tradePlayersProps(tradeGui.getTradePartner()));
        List<CheckMenuItem> partnersPropsForMenu = new LinkedList<>();
        //Listen mit CheckMenuItems fuellen
        while (!yourProps.isEmpty()) {
            CheckMenuItem item = yourProps.get(0);
            item.setOnAction(event -> {
                if (yourProps.contains(item)) {
                    yourProps.remove(item);
                }
                else {
                    yourProps.add(item);
                }
            });
            yourPropsForMenu.add(item);
            yourProps.remove(item);
        }

        while (!partnersProps.isEmpty()) {
            CheckMenuItem item = partnersProps.get(0);
            item.setOnAction(event -> {
                if (partnersProps.contains(item)) {
                    partnersProps.remove(item);
                }
                else {
                    partnersProps.add(item);
                }
            });
            partnersPropsForMenu.add(item);
            partnersProps.remove(item);
        }

        List<String> playersSelectedProps;
        List<String> partnersSelectedProps;

        //Initialisierung der benoetigten Objekte
        //GridPane(s)
        GridPane tradeOfferGridPane = new GridPane();
        //HBox(en)
        HBox tradeOfferHBox = new HBox();
        //VBox(en)
        VBox tradeOfferVBox = new VBox();
        VBox yourPropsOfferVBox = new VBox();
        VBox yourTradeOfferVBox = new VBox();
        VBox partnersTradeOfferVBox = new VBox();
        VBox partnersPropsOfferVBox = new VBox();
        //Label(s) mit Einstellung
        Label generallOfferLabel = new Label("Wähle dein Angebot!");
        generallOfferLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));

        Label yourSideLabel = new Label("Deine Seite");
        yourSideLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

        Label yourPropsLabel = new Label("Biete Grundstücke:");
        yourPropsLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

        Label yourOfferMoneyLabel = new Label("Wieviel " + currency + " bietest du:");
        yourOfferMoneyLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

        Label yourCardLabel = new Label("Gefängnisfreikarten: " + Lobby.getPlayerClient().getPlayerOnClient().getCardStack().countCardsOfAction(Card.Action.JAIL));
        yourCardLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

        Label yourOfferCardLabel = new Label("Biete Karten:");
        yourOfferCardLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

        Label partnersSideLabel = new Label("Tauschpartners Seite");
        partnersSideLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

        Label partnersPropsLabel = new Label("Verlange Grundstücke:");
        partnersPropsLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

        Label partnersOfferMoneyLabel = new Label("Verlange Geld:");
        partnersOfferMoneyLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

        Label partnersCardLabel = new Label("Gefängnisfreikarten: " + tradeGui.getTradePartner().getCardStack().countCardsOfAction(Card.Action.JAIL));
        partnersCardLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

        Label partnersOfferCardLabel = new Label("Verlange Karten:");
        partnersOfferCardLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

        //Button(s)
        JFXButton offerTradeButton = new JFXButton("Anbieten");
        //TextField(s)
        JFXTextField yourMoneyTextField = new JFXTextField("0");
        JFXTextField yourCardsTextField = new JFXTextField("0");
        JFXTextField partnersMoneyTextField = new JFXTextField("0");
        JFXTextField partnersCardsTextField = new JFXTextField("0");
        //MenuButton(s)
        MenuButton yourPropsMenu = new MenuButton("Wähle Grundstück");
        yourPropsMenu.setBackground(new Background(new BackgroundFill(Color.web("#dcedc8"), CornerRadii.EMPTY, Insets.EMPTY)));
        yourPropsMenu.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        MenuButton partnersPropsMenu = new MenuButton("Wähle Grundstück");
        partnersPropsMenu.setBackground(new Background(new BackgroundFill(Color.web("#dcedc8"), CornerRadii.EMPTY, Insets.EMPTY)));
        partnersPropsMenu.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        //Seperator(en)
        Separator separateLblFromTrade = new Separator();
        Separator separateYouFromPartner = new Separator(Orientation.VERTICAL);

        //Einstellung der benoetigten Objekte
        //MenuBotton(s) fuellen
        yourPropsMenu.getItems().addAll(yourPropsForMenu);
        partnersPropsMenu.getItems().addAll(partnersPropsForMenu);
        //GridPane(s)
        tradeOfferGridPane.setAlignment(Pos.CENTER);
        //Button(s)
        offerTradeButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));
        offerTradeButton.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

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
        tradeOfferHBox.setPadding(new Insets(5));
        tradeOfferHBox.getChildren().addAll(yourPropsOfferVBox, yourTradeOfferVBox, separateYouFromPartner, partnersPropsOfferVBox, partnersTradeOfferVBox);
        //VBox(en)
        //Groessen und Abstaende
        yourPropsOfferVBox.setPrefSize(199, 175);
        yourPropsOfferVBox.setSpacing(18);
        yourTradeOfferVBox.setPrefSize(199, 175);
        yourTradeOfferVBox.setSpacing(8);
        partnersPropsOfferVBox.setPrefSize(199, 175);
        partnersPropsOfferVBox.setSpacing(18);
        partnersTradeOfferVBox.setPrefSize(199, 175);
        partnersTradeOfferVBox.setSpacing(8);
        //Elemente hinzufuegen
        yourPropsOfferVBox.getChildren().addAll(yourSideLabel, yourPropsLabel, yourPropsMenu, offerTradeButton);
        yourTradeOfferVBox.getChildren().addAll(yourOfferMoneyLabel, yourMoneyTextField, yourCardLabel, yourOfferCardLabel, yourCardsTextField);
        partnersPropsOfferVBox.getChildren().addAll(partnersSideLabel, partnersPropsLabel, partnersPropsMenu);
        partnersTradeOfferVBox.getChildren().addAll(partnersOfferMoneyLabel, partnersMoneyTextField, partnersCardLabel, partnersOfferCardLabel, partnersCardsTextField);

        Popup pop = new Popup(tradeOfferGridPane);
        //Funktionen/Eventhandler
        offerTradeButton.setOnAction(event -> {

            try {
                try {
                    tradeGui.setYourMoney(Integer.parseInt(yourMoneyTextField.getText()));
                    tradeGui.setYourCardAmount(Integer.parseInt(yourCardsTextField.getText()));
                    tradeGui.setPartnersMoney(Integer.parseInt(partnersMoneyTextField.getText()));
                    tradeGui.setPartnersCardAmount(Integer.parseInt(partnersCardsTextField.getText()));
                } catch (NumberFormatException n) {
                    destroyPopup(pop);
                    showTradeWarningPopup(tradeGui);
                }

                if (checkIfInputIsOk(tradeGui.getYourMoney(), tradeGui.getYourCardAmount(), tradeGui.getTradeStarter())
                        && checkIfInputIsOk(tradeGui.getPartnersMoney(), tradeGui.getPartnersCardAmount(), tradeGui.getTradePartner())) {

                    tradeGui.setYourPropIds(collectPropertyIds(yourProps));
                    tradeGui.setPartnersPropIds(collectPropertyIds(partnersProps));
                    tradeGui.setYourCardIds(collectCardIds(tradeGui.getYourCardAmount()));
                    tradeGui.setPartnersCardIds(collectCardIds(tradeGui.getPartnersCardAmount()));

                    tradeOfferIsCreated = true;

                    destroyPopup(pop);
                    waitForResponsePopup(tradeGui);

                }

                else {
                    destroyPopup(pop);
                    showTradeWarningPopup(tradeGui);
                }
            } catch (InputMismatchException i) {
                //Fehler
            }

        });
        Global.ref().getGameSceneManager().queuePopup(pop);

    }

    /**
     * Erzeugt eine Liste von Namen aller Propertys des uebergebenen Spielers
     *
     * @param player
     * @return
     */
    private List<CheckMenuItem> tradePlayersProps(Player player) {

        //Benoetigte Objekte
        //Liste aller Propertys im Besitzt des Spielers
        List<PropertyField> playersProps = Lobby.getPlayerClient().getGame().getBoard().getFieldManager()
                .getOwnedPropertyFields(player).collect(Collectors.toList());
        //Liste fuer MenuButton Eintraege
        List<CheckMenuItem> items = new LinkedList<>();

        //Einer ArrayList werden alle Namen der Propertys mitgeteilt
        playersProps.forEach((field) -> {
            items.add(new CheckMenuItem(field.getName()));
        });

        return items;
    }

    /**
     * Erstellt ein int[] mit Property IDs, welches dem Trade uebergeben werden kann
     *
     * @param itemList
     * @return
     */
    private int[] collectPropertyIds(List<CheckMenuItem> itemList) {

        Field[] fields = Lobby.getPlayerClient().getGame().getBoard().getFieldManager().getFields();
        int[] propertyIds = new int[itemList.size()];

        for (int i = 0; i < itemList.size(); i++) {
            for (int j = 0; j < fields.length; j++) {

                if (itemList.get(i).getText().equals(fields[j].getName())) {
                    propertyIds[i] = Lobby.getPlayerClient().getGame().getBoard().getFieldManager().getFieldId(fields[j]);
                }

            }
        }
        return propertyIds;
    }

    /**
     * Erstellt ein int[] mit Card IDs, welches dem trade uebergeben werden kann
     *
     * @param amount
     * @return
     */
    private int[] collectCardIds(int amount) {

        int[] cardIds = new int[amount];

        for (int i = 0; i < amount; i++) {
            cardIds[i] = i;
        }

        return cardIds;
    }

    /**
     * Ueberprueft die Eingaben der Karten Anzahl und des Geldes
     *
     * @param moneyAmount
     * @param cardAmount
     * @param player
     * @return
     */
    private boolean checkIfInputIsOk(int moneyAmount, int cardAmount, Player player) {

        int playerMoney = player.getMoney();
        int playerCards = player.getCardStack().countCardsOfAction(Card.Action.JAIL);

        if (moneyAmount > -1 && moneyAmount < playerMoney) {
            if (cardAmount > -1 && cardAmount <= playerCards) {
                return true;
            }
        }
        return false;
    }

    /**
     * Zeigt fuer 3,5 Sekunden ein Warining Popup
     */
    private void showTradeWarningPopup(GuiTrade tradeGui) {

        //Initialisierung der benoetigten Objekte
        //Gridpane(s)
        GridPane tradeWarningGridPane = new GridPane();
        //Label(s)
        Label tradeWarningLabel = new Label("Es werden nur Werte innerhalb des Wertebereichs akzeptiert!"
                + "\n\nAnzahl Karten: 0 - Maximum"
                + "\n\nAnzahl Geld: 0 oder mehr"
                + "\nJedoch nicht so viel, wie der Spieler besitzt!");

        tradeWarningLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        //VBox(en)
        VBox tradeWarningVBox = new VBox();

        //Einstellung der Objekte
        String cssLayout = "-fx-background-color: #dcedc8;\n"
                + "-fx-border-color: black;\n"
                + "-fx-border-insets: 5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: double;\n";

        tradeWarningGridPane.setAlignment(Pos.CENTER);
        tradeWarningGridPane.add(tradeWarningVBox, 0, 0);

        tradeWarningVBox.setStyle(cssLayout);
        tradeWarningVBox.setSpacing(10);
        tradeWarningVBox.setPrefSize(800, 200);
        tradeWarningVBox.setAlignment(Pos.CENTER);
        tradeWarningVBox.getChildren().addAll(tradeWarningLabel);

        Popup pop = new Popup(tradeWarningGridPane, Duration.millis(3500));
        queuePopup(pop);

        //Weiterleitung an das naechste Popup
        selectTradeOfferPopup(tradeGui);
    }

    /**
     * Uebergibt das int[] mit den Property IDs dem Handel
     *
     * @param player
     * @return
     */
    public int[] getPropertyIdsForTrade(Player player, GuiTrade tradeGui) {

        if (player.equals(Lobby.getPlayerClient().getPlayerOnClient())) {
            return tradeGui.getYourPropIds();
        }
        else {
            return tradeGui.getPartnersPropIds();
        }
    }

    /**
     * Uebergibt das int[] mit den Card IDs dem Handel
     *
     * @param player
     * @return
     */
    public int[] getCardIdsForTrade(Player player, GuiTrade tradeGui) {

        if (player.equals(Lobby.getPlayerClient().getPlayerOnClient())) {
            return tradeGui.getYourCardIds();
        }
        else {
            return tradeGui.getPartnersCardIds();
        }
    }

    public int getMoneyForTrade(Player player, GuiTrade tradeGui) {

        if (player.equals(Lobby.getPlayerClient().getPlayerOnClient())) {
            return tradeGui.getYourMoney();
        }
        else {
            return tradeGui.getPartnersMoney();
        }
    }

    public void showOfferPopup(GuiTrade tradeGui) {
        //Initialisierung der benoetigten Objekte
        //GridPane(s)
        GridPane showOfferGridPane = new GridPane();
        //VBox(en)
        VBox mainOfferVBox = new VBox();
        VBox yourOfferVBox = new VBox();
        VBox partnersOfferVBox = new VBox();
        //HBox(en)
        HBox labelOfferHBox = new HBox();
        HBox showOfferHBox = new HBox();

        //Label(s) mit Einstellungen
        Label offerLabel = new Label("Angebot von " + tradeGui.getTradeStarter().getName());
        offerLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        Label yourSideLabel = new Label("Du bekommst:");
        yourSideLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        Label yourPropsLabel = new Label(generatePropertyString(tradeGui.getYourPropIds()));
        yourPropsLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        Label yourCardsLabel = new Label(tradeGui.getYourCardIds().length + " Gefängnisfreikarten");
        yourCardsLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        Label yourMoneyLabel = new Label(tradeGui.getYourMoney() + " " + currency);
        yourMoneyLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        Label partnersSideLabel = new Label("Du gibst:");
        partnersSideLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        Label partnersPropsLabel = new Label(generatePropertyString(tradeGui.getPartnersPropIds()));
        partnersPropsLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        Label partnersCardsLabel = new Label(tradeGui.getPartnersCardIds().length + " Gefängnisfreikarten");
        partnersCardsLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        Label partnersMoneyLabel = new Label(tradeGui.getPartnersMoney() + " " + currency);
        partnersMoneyLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));

        //Button(s) mit Einstellungen
        JFXButton acceptOfferButton = new JFXButton("Akzeptieren");
        acceptOfferButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));
        acceptOfferButton.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        JFXButton deniedOfferButton = new JFXButton("Ablehnen");
        deniedOfferButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));
        deniedOfferButton.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        //Seperator(en)
        Separator mainSeparator = new Separator(Orientation.HORIZONTAL);
        Separator offersSeparator = new Separator(Orientation.VERTICAL);
        Separator underYourPropsSeparator = new Separator(Orientation.HORIZONTAL);
        Separator underYourCardsSeparator = new Separator(Orientation.HORIZONTAL);
        Separator underPartnersPropsSeparator = new Separator(Orientation.HORIZONTAL);
        Separator underPartnersCardsSeparator = new Separator(Orientation.HORIZONTAL);

        //Einstellen des GUI Fensters
        String cssLayout = "-fx-background-color: #dcedc8;\n"
                + "-fx-border-color: black;\n"
                + "-fx-border-insets: 5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: double;\n";

        showOfferGridPane.add(mainOfferVBox, 0, 0);
        showOfferGridPane.setAlignment(Pos.CENTER);

        mainOfferVBox.setStyle(cssLayout);
        mainOfferVBox.setSpacing(10);
        mainOfferVBox.setPrefSize(800, 300);
        mainOfferVBox.setAlignment(Pos.CENTER);
        mainOfferVBox.getChildren().addAll(offerLabel, showOfferHBox);

        yourOfferVBox.setPrefSize(400, 275);
        yourOfferVBox.setAlignment(Pos.CENTER);
        yourOfferVBox.getChildren().addAll(yourSideLabel, yourPropsLabel, underYourPropsSeparator, yourCardsLabel, underYourCardsSeparator, yourMoneyLabel, acceptOfferButton);

        partnersOfferVBox.setPrefSize(400, 275);
        partnersOfferVBox.setAlignment(Pos.CENTER);
        partnersOfferVBox.getChildren().addAll(partnersSideLabel, partnersPropsLabel, underPartnersPropsSeparator, partnersCardsLabel, underPartnersCardsSeparator, partnersMoneyLabel, deniedOfferButton);

        showOfferHBox.setPadding(new Insets(2));
        showOfferHBox.getChildren().addAll(yourOfferVBox, offersSeparator, partnersOfferVBox);

        Popup pop = new Popup(showOfferGridPane);
        //Eventlistener(s)
        acceptOfferButton.setOnAction(event -> {
            tradeAnswer = true;
            tradeAnswerIsGiven = true;
            destroyPopup(pop);
            showAnswerPopup(tradeAnswer);
        });

        deniedOfferButton.setOnAction(event -> {
            tradeAnswer = false;
            tradeAnswerIsGiven = true;
            destroyPopup(pop);
            showAnswerPopup(tradeAnswer);
        });

        queuePopup(pop);

    }

    private String generatePropertyString(int[] propertyIds) {

        Field[] fields = Lobby.getPlayerClient().getGame().getBoard().getFields();
        String allPropertyString = "Grundstücke:\n";

        /*
         * Versuche die LinkedList auseinander zunehmen ;D
         */
        for (int j = 0; j < fields.length; j++) {
            for (int i = 0; i < propertyIds.length; i++) {
                if (j == propertyIds[i]) {
                    allPropertyString += fields[j].getName() + "\n";
                }
            }
        }

        if (allPropertyString.equals("Grundstücke:\n")) {
            allPropertyString = "Keine Grundstücke";
        }

        return allPropertyString;
    }

    public boolean getTradeAnswerIsGiven() {
        return tradeAnswerIsGiven;
    }

    public void resteTradeAnswerIsGiven() {
        tradeAnswerIsGiven = false;
    }

    public boolean getTradeAnswer() {
        return tradeAnswer;
    }

    public void waitForResponsePopup(GuiTrade tradeGui) {
        //Initialisierung der benoetigten Objekte
        //Gridpane(s)
        GridPane tradeResponseGridPane = new GridPane();
        //Label(s)
        Label tradeResponseLabel = new Label("Warte, bis dein Tauschpartner sich entschieden hat!");

        tradeResponseLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        //VBox(en)
        VBox tradeResponseVBox = new VBox();

        //Einstellung der Objekte
        String cssLayout = "-fx-background-color: #dcedc8;\n"
                + "-fx-border-color: black;\n"
                + "-fx-border-insets: 5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: double;\n";

        tradeResponseGridPane.setAlignment(Pos.CENTER);
        tradeResponseGridPane.add(tradeResponseVBox, 0, 0);

        tradeResponseVBox.setStyle(cssLayout);
        tradeResponseVBox.setSpacing(10);
        tradeResponseVBox.setPrefSize(800, 200);
        tradeResponseVBox.setAlignment(Pos.CENTER);
        tradeResponseVBox.getChildren().addAll(tradeResponseLabel);

        Popup pop = new Popup(tradeResponseGridPane);
        Global.ref().getGuiTrade().setWaitForResponsePopup(pop);
        queuePopup(pop);

    }

    public void showAnswerPopup(boolean choice) {

        destroyPopup(Global.ref().getGuiTrade().getWaitForResponsePopup());
        //Initialisierung der benoetigten Objekte
        //Gridpane(s)
        GridPane tradeAnswerGridPane = new GridPane();
        //Label(s)
        Label tradeAnswerLabel = new Label("");
        if (choice) {
            tradeAnswerLabel.setText("Der Tausch findet statt!");
        }
        else {
            tradeAnswerLabel.setText("Der Tausch findet nicht statt!");
        }

        tradeAnswerLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        //VBox(en)
        VBox tradeAnswerVBox = new VBox();

        //Einstellung der Objekte
        String cssLayout = "-fx-background-color: #dcedc8;\n"
                + "-fx-border-color: black;\n"
                + "-fx-border-insets: 5;\n"
                + "-fx-border-width: 1;\n"
                + "-fx-border-style: double;\n";

        tradeAnswerGridPane.setAlignment(Pos.CENTER);
        tradeAnswerGridPane.add(tradeAnswerVBox, 0, 0);

        tradeAnswerVBox.setStyle(cssLayout);
        tradeAnswerVBox.setSpacing(10);
        tradeAnswerVBox.setPrefSize(800, 200);
        tradeAnswerVBox.setAlignment(Pos.CENTER);
        tradeAnswerVBox.getChildren().addAll(tradeAnswerLabel);

        Popup pop = new Popup(tradeAnswerGridPane, Duration.millis(2000));
        Global.ref().getGameSceneManager().queuePopup(pop);

    }

    private class GameStateAdapterImpl extends GameStateAdapter {

        private VBox playerBox;

        public GameStateAdapterImpl(VBox playerBox) {
            this.playerBox = playerBox;
        }

        @Override
        public void onPlayerOnCardField(Player player, CardField cardField, Card card) {
            showCard(card, cardField.getStackType());
        }

        @Override
        public void onTurnEnd(Player oldPlayer, Player newPlayer) {
            Platform.runLater(() -> {
                ObservableList<Node> children = playerBox.getChildren();
                Pane firstChild = (Pane) children.get(oldPlayer.getId());

                int size = children.size();
                double newY = firstChild.getTranslateY() + size * firstChild.getHeight() + size * playerBox.getSpacing();

                TranslateTransition tt1 = new TranslateTransition(Duration.millis(200), firstChild);
                tt1.setByX(-firstChild.getWidth());
                tt1.setOnFinished(inv -> firstChild.setTranslateY(newY));

                ParallelTransition par = new ParallelTransition();
                ObservableList<Animation> parChildren = par.getChildren();
                children.stream()
                        .map(Pane.class::cast)
                        .forEach(pane -> {
                            TranslateTransition tt2 = new TranslateTransition(Duration.millis(200), pane);
                            tt2.setInterpolator(Interpolator.EASE_OUT);
                            tt2.setByY(-(firstChild.getHeight() + playerBox.getSpacing()));
                            parChildren.add(tt2);
                        });

                TranslateTransition tt3 = new TranslateTransition(Duration.millis(200), firstChild);
                tt3.setByX(firstChild.getWidth());

                SequentialTransition st = new SequentialTransition(tt1, par, tt3);
                st.play();
            });
        }
    }
}
