package de.btu.monopoly.ui;

import com.jfoenix.controls.*;
import de.btu.monopoly.Global;
import de.btu.monopoly.core.*;
import de.btu.monopoly.core.service.*;
import de.btu.monopoly.data.card.*;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.ui.CameraManager.WatchMode;
import de.btu.monopoly.ui.fx3d.*;
import de.btu.monopoly.util.Assets;
import de.btu.monopoly.util.TextUtils;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.*;
import java.util.stream.Collectors;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.util.Duration;

public class GameSceneManager implements AnimationQueuer {

    private static final Logger LOGGER = Logger.getLogger(GameSceneManager.class.getCanonicalName());

    private static final double PLAYER_ZOOM = -1200;
    private static final int DEFAULT_ACTION_DELAY_MILLIS = 1000;

    private final StackPane rootPane;

    private final SubScene gameSub;
    private final Fx3dGameBoard board3d;

    private final BorderPane uiPane;
    private final VBox popupWrapper;

    private final VBox gameInfoBox;
    private final VBox playerBox;
    private final VBox cardHandle;

    private CameraManager camMan;

    private List<Animation> visualQueue;
    private BooleanProperty isPlayingAnim;

    //Handelsspezifisch
    private boolean tradeOfferIsCreated = false;
    private boolean tradeAnswerIsGiven = false;
    private boolean tradeAnswer = false;
    private char currency = '€';

    public GameSceneManager(GameBoard board) {
        this.board3d = new Fx3dGameBoard(board, this);

        visualQueue = new LinkedList<>();
        isPlayingAnim = new SimpleBooleanProperty(false);
        isPlayingAnim.addListener((prop, oldB, newB) -> tryNextAnim());

        gameSub = new SubScene(board3d, 0, 0, true, SceneAntialiasing.BALANCED);
        gameSub.setCache(true);
        gameSub.setCacheHint(CacheHint.SPEED);

        uiPane = new BorderPane();
        popupWrapper = new VBox();

        playerBox = new VBox();
        cardHandle = new VBox();
        gameInfoBox = new VBox(playerBox, cardHandle);

        rootPane = new StackPane(gameSub, uiPane, popupWrapper);
        rootPane.setAlignment(Pos.CENTER);
        rootPane.setPickOnBounds(false);
        rootPane.getStylesheets().add(getClass().getResource("/styles/game.css").toExternalForm());

        Game game = Global.ref().getGame();
        game.addGameStateListener(new GameStateAdapterImpl());
        game.addGameStateListener(board3d.stateListener());
        initScene();
    }

    private void initScene() {
        gameSub.setFill(Color.LIGHTGRAY);

        gameSub.widthProperty().bind(rootPane.widthProperty());
        gameSub.heightProperty().bind(rootPane.heightProperty());

        initUi();
        initCams();
    }

    private void initUi() {

        uiPane.setPadding(new Insets(5, 5, 5, 5));
        uiPane.setPickOnBounds(false);

        popupWrapper.setAlignment(Pos.CENTER);
        popupWrapper.setPickOnBounds(false);

        StackPane wholeChatBox = ChatUi.getInstance().getWholeChatBox();
        wholeChatBox.setPickOnBounds(false);

        HBox toolBox = ChatUi.getInstance().getChatToggleBox();
        toolBox.setPickOnBounds(false);

        uiPane.setRight(wholeChatBox);

        BorderPane botButtonPane = new BorderPane();
        botButtonPane.setPickOnBounds(false);

        Label viewButton = new Label(null, Assets.getIcon("3d_icon"));
        viewButton.setOnMousePressed(event -> {
            WatchMode mode = camMan.getWatchMode();
            camMan.watch(board3d, mode == WatchMode.ORTHOGONAL ? WatchMode.PERSPECTIVE : WatchMode.ORTHOGONAL);
        });
        viewButton.setOnMouseEntered(event -> viewButton.setGraphic(Assets.getIcon("3d_icon_rollover")));
        viewButton.setOnMouseExited(event -> viewButton.setGraphic(Assets.getIcon("3d_icon")));

        botButtonPane.setLeft(viewButton);
        uiPane.setBottom(botButtonPane);

        playerBox.setPickOnBounds(false);
        playerBox.setPadding(new Insets(10, 0, 0, 0));
        playerBox.setSpacing(10);
        ObservableList<Node> children = playerBox.getChildren();
        board3d.getPlayers().forEach(p -> children.add(p.infoPane()));

        gameInfoBox.setPickOnBounds(false);
        uiPane.setLeft(gameInfoBox);

        cardHandle.setPadding(new Insets(20, 0, 0, 20));
        cardHandle.setPickOnBounds(false);

        board3d.getFields()
                .filter(Fx3dPropertyField.class::isInstance)
                .map(Fx3dPropertyField.class::cast)
                .forEach(prop -> {
                    prop.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> cardHandle.getChildren().add(0, prop.infoPane()));
                    prop.addEventHandler(MouseEvent.MOUSE_EXITED, event -> cardHandle.getChildren().remove(prop.infoPane()));
                });
    }

    private void initCams() {

        camMan = new CameraManager(gameSub);
        camMan.watch(board3d, WatchMode.ORTHOGONAL);

        board3d.getPlayers().forEach(fxPlayer
                -> fxPlayer.setOnMouseReleased(event -> watchNode(fxPlayer)));
    }

    private void changeFirstPlayer() {

        ObservableList<Node> children = playerBox.getChildren();
        Pane firstChild = (Pane) children.get(0);

        int size = children.size();
        double newY = firstChild.getTranslateY() + size * firstChild.getHeight() + size * playerBox.getSpacing();

        TranslateTransition tt1 = new TranslateTransition(Duration.millis(200), firstChild);
        tt1.setByX(-firstChild.getWidth());
        tt1.setOnFinished(inv -> {
            children.remove(0);
            firstChild.setTranslateY(newY);
        });

        TranslateTransition tt2 = new TranslateTransition(Duration.millis(200), firstChild);
        tt2.setByX(firstChild.getWidth());
        tt2.setOnFinished(inv -> {
            firstChild.setTranslateY(0);
            children.add(firstChild);
        });

        SequentialTransition st = new SequentialTransition(tt1, tt2);
        st.play();
    }

    private PauseTransition taskAnim(Runnable runnable, int initialDelayMillis) {
        PauseTransition pause = new PauseTransition(Duration.millis(initialDelayMillis));
        pause.setOnFinished(inv -> runnable.run());
        return pause;
    }

    private PauseTransition taskAnim(Runnable runnable) {
        return taskAnim(runnable, DEFAULT_ACTION_DELAY_MILLIS);
    }

    private void queueTask(Runnable runnable, int initalDelayMillis) {
        PauseTransition pause = taskAnim(runnable, initalDelayMillis);
        queueAnimation(pause);
    }

    private void queueTask(Runnable runnable) {
        queueTask(runnable, DEFAULT_ACTION_DELAY_MILLIS);
    }

    private void safelyQueueTask(Runnable runnable) {
        safelyQueueTask(runnable, DEFAULT_ACTION_DELAY_MILLIS);
    }

    private void safelyQueueTask(Runnable runnable, int initialDelayMillis) {
        Platform.runLater(() -> queueTask(runnable, initialDelayMillis));
    }

    private void displayPopup(Popup pop) {
        pop.pane.setPickOnBounds(false);
        popupWrapper.getChildren().add(pop.pane);

        Duration dur = pop.duration;
        if (!dur.isIndefinite()) {
            PauseTransition pause = new PauseTransition(dur);
            pause.setOnFinished(inv -> popupWrapper.getChildren().remove(pop.pane));
            pause.play();
        }
    }

    private void queuePopup(Popup pop) {
        queueTask(() -> displayPopup(pop));
    }

    private void safelyQueuePopup(Popup pop) {
        Platform.runLater(() -> queuePopup(pop));
    }

    void destroyPopup(Popup pop) {
        Platform.runLater(() -> popupWrapper.getChildren().remove(pop.pane));
    }

    @Override
    public void queueAnimation(Animation anim) {
        visualQueue.add(anim);
        tryNextAnim();
    }

    private void tryNextAnim() {
        if (!visualQueue.isEmpty() && !isPlayingAnim.get()) {
            nextAnim();
        }
    }

    private void nextAnim() {

        isPlayingAnim.set(true);
        Animation anim = visualQueue.remove(0);
        EventHandler<ActionEvent> oldHandler = anim.getOnFinished();
        anim.setOnFinished(event -> finishAnim(oldHandler, event));
        anim.play();
    }

    private void finishAnim(EventHandler<ActionEvent> handler, ActionEvent event) {

        if (handler != null) {
            handler.handle(event);
        }

        if (!visualQueue.isEmpty()) {
            nextAnim();
        }
        else {
            isPlayingAnim.set(false);
        }
    }

    private void watchNode(Node node) {
        camMan.watch(node, PLAYER_ZOOM);
    }

    public Pane getSceneRoot() {
        return rootPane;
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

        System.out.println("HIAHJIA");
        String fieldname = TextUtils.format(Global.ref().getGame().getBoard().getFields()[Global.ref().getClient().getPlayerOnClient().getPosition()].getName());
        Label label = new Label("Möchtest du " + fieldname + " kaufen?");
        System.out.println("STATIC");

        JFXButton buyButton = new JFXButton();
        JFXButton dontBuyButton = new JFXButton();

        buyButton.setText("Kaufen");
        buyButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        dontBuyButton.setText("Nicht kaufen");
        dontBuyButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        box.setId("rosePopup");
        box.setSpacing(10);
        box.setPrefSize(300, 200);
        label.setFont(Font.font("Tahoma", 14));
        box.getChildren().addAll(label, buyButton, dontBuyButton);
        box.setAlignment(Pos.CENTER);

        Popup pop = new Popup(gridpane);
        safelyQueuePopup(pop);

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

    private Popup winnerPopup(Player winner) {

        ImageView winnerView = new ImageView(Assets.getImage("game_won"));
        Text winnerText = new Text(String.format("%s hat das Spiel gewonnen.\n Herzlichen Glückwunsch!", winner.getName()));
        winnerText.setFont(Font.font("Tahoma", 23));
        winnerText.setTextAlignment(TextAlignment.CENTER);

        VBox winnerBox = new VBox(winnerView, winnerText);
        winnerBox.setPadding(new Insets(20, 20, 20, 20));
        winnerBox.setAlignment(Pos.CENTER);
        winnerBox.setStyle("-fx-background-color: #d50000aa; -fx-background-radius: 10px;");

        HBox wrapper = new HBox(winnerBox);
        wrapper.setAlignment(Pos.CENTER);
        return new Popup(wrapper);
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

        box.setId("rosePopup");
        box.setSpacing(10);
        box.setPrefSize(200, 300);
        box.getChildren().addAll(label, rollButton, payButton, cardButton);
        box.setAlignment(Pos.CENTER);

        Popup pop = new Popup(gridpane);
        safelyQueuePopup(pop);

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
        VBox vbox = new VBox();
        VBox vbox1 = new VBox();
        VBox vbox2 = new VBox();

        HBox box = new HBox();

        gridpane.setAlignment(Pos.CENTER);

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

        vbox.setId("greenPopup");

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
        safelyQueuePopup(pop);

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

    public int askForFieldPopup(String fieldNames[]) {

        for (int i = 0; i < fieldNames.length; i++) {
            fieldNames[i] = TextUtils.format(fieldNames[i]);
        }

        GridPane gridPane = new GridPane();
        VBox box = new VBox();

        gridPane.setAlignment(Pos.CENTER);
        gridPane.add(box, 0, 0);

        Label label = new Label("Wähle ein Feld:");
        JFXComboBox fieldBox = new JFXComboBox();
        Button eingabeButton = new Button();
        Button exitButton = new Button();

        box.setId("greenPopup");

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
        for (String name : fieldNames) {
            fieldBox.getItems().add(name);
        }

        fieldBox.getSelectionModel().selectFirst();

        box.getChildren().addAll(label, fieldBox, eingabeButton, exitButton);
        box.setAlignment(Pos.CENTER);

        Popup pop = new Popup(gridPane);

        if (fima.getOwnedPropertyFields(currPlayer).count() == 0) {
            Platform.runLater(() -> {
                fieldBox.setPromptText("Du besitzt keine Straßen!");
                destroyPopup(pop);
            });
            return 0;
        }
        safelyQueuePopup(pop);

        while (!eingabeButton.isPressed() || !exitButton.isPressed()) {
            if (eingabeButton.isPressed()) {
                destroyPopup(pop);
                int selection = fieldBox.getSelectionModel().getSelectedIndex() + 1;
                return selection;
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

    public void beginAuctionPopup() {
        Popup pop = new Popup(AuctionUI.getInstance().getGridPane());
        AuctionUI.getInstance().setPopup(pop);
        safelyQueuePopup(pop);
    }

    public void endAuctionPopup(Popup oldOne, GridPane newOne) {
        destroyPopup(oldOne);
        AuctionUI.getInstance().revertPopup();
        Popup pop = new Popup(newOne, Duration.seconds(4));
        safelyQueuePopup(pop);
    }

    private void showCard(Card card, CardStack.Type type) {

        GridPane kartPane = new GridPane();
        HBox box = new HBox();

        kartPane.add(box, 0, 0);
        kartPane.setAlignment(Pos.CENTER);

        Label text = new Label(card.getText());
        text.setPadding(new Insets(20, 20, 20, 20));
        text.setWrapText(true);
        box.setPrefSize(250, 150);
        box.getChildren().add(text);
        box.setAlignment(Pos.CENTER);

        if (type == CardStack.Type.COMMUNITY) {
            //Gemeinschaft
            box.setId("yellowPopup");
        }
        else {
            //Ereignis
            box.setId("orangePopup");
        }

        Popup pop = new Popup(kartPane, Duration.seconds(4));
        displayPopup(pop);
    }

    /*
     * Init Popup fuer den Handel
     */
    public void initTradePopup(Player player, GuiTrade tradeGui) {

        tradeGui.setTradeStarter(player);

        //Liste(n)
        ObservableList<String> choosePlayerOptions = FXCollections.observableList(tradePlayersNames());

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
                    // Nimmt die Auswahl aus der Combobox und übergibt Sie dem Handel
                    String tradePartnersName = (String) choosePlayerBox.getSelectionModel().getSelectedItem();

                    for (int i = 0; i < Global.ref().getGame().getBoard().getActivePlayers().size(); i++) {
                        if (tradePartnersName.equals(Global.ref().getGame().getPlayers()[i].getName())) {
                            tradeGui.setTradePartner(Global.ref().getGame().getPlayers()[i]);
                            break;
                        }
                    }
                    Platform.runLater(() -> {
                        showTradeInfoPopup(tradeGui);
                        destroyPopup(pop);
                    });
                } catch (NullPointerException e) {
                    LOGGER.log(Level.WARNING, "FEHLER in initTradePopup(): {0}", e);
                }
            }
        };

        //Einstellung der benoetigten Objekte
        initTradeGP.setAlignment(Pos.CENTER);
        initTradeGP.add(initTradeVBox, 0, 0);
        initTradeLabel.setFont(Font.font("Tahoma", FontWeight.BOLD, 14));
        acceptPlayerButton.setBackground(new Background(new BackgroundFill(Color.web("#e1f5fe"), CornerRadii.EMPTY, Insets.EMPTY)));

        //Einstellen HBox
        initTradeHBox.getChildren().addAll(acceptPlayerButton);
        initTradeHBox.setSpacing(10);
        initTradeHBox.setAlignment(Pos.CENTER);

        //Einstellen VBox
        initTradeVBox.setId("greenPopup");
        initTradeVBox.setSpacing(10);
        initTradeVBox.setPrefSize(800, 200);
        initTradeVBox.setCenterShape(true);
        initTradeVBox.getChildren().addAll(initTradeLabel, choosePlayerBox, initTradeHBox);
        initTradeVBox.setAlignment(Pos.CENTER);

        Global.ref().getGameSceneManager().safelyQueuePopup(pop);

        //Verkünpfung mit Eventhandler(n)
        acceptPlayerButton.setOnAction(selectPlayer);

    }

    /**
     * Erzeugt eine Liste von Namen aller Spieler ausser des aktiven Spielers
     *
     * @return
     */
    private static List<String> tradePlayersNames() {
        return Arrays.asList(
                (Global.ref().getGame().getBoard().getActivePlayers()).stream()
                        .filter(p -> p.getId() != Global.ref().getClient().getPlayerOnClient().getId())
                        .map(p -> p.getName()).toArray(String[]::new)
        );
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
        tradeInfoGridPane.setAlignment(Pos.CENTER);
        tradeInfoGridPane.add(tradeInfoVBox, 0, 0);

        tradeInfoVBox.setId("greenPopup");
        tradeInfoVBox.setSpacing(10);
        tradeInfoVBox.setPrefSize(800, 200);
        tradeInfoVBox.setAlignment(Pos.CENTER);
        tradeInfoVBox.getChildren().addAll(tradeInfoLabel);

        Popup pop = new Popup(tradeInfoGridPane, Duration.millis(2500));
        Global.ref().getGameSceneManager().safelyQueuePopup(pop);

        selectTradeOfferPopup(tradeGui);

    }

    /**
     * Erzeugt das Gui-Fenster in dem man ein TradeOffer erstellt
     */
    private void selectTradeOfferPopup(GuiTrade tradeGui) {

        //Liste(n)
        List<CheckMenuItem> yourProps = tradePlayersProps(Global.ref().getClient().getPlayerOnClient());
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

        Label yourCardLabel = new Label("Gefängnisfreikarten: " + Global.ref().getClient().getPlayerOnClient().getCardStack().countCardsOfAction(Card.Action.JAIL));
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

        //Anordnen des GUI
        tradeOfferGridPane.add(tradeOfferVBox, 0, 0);
        //Main VBox
        tradeOfferVBox.setId("greenPopup");
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
                LOGGER.log(Level.WARNING, "FEHLER in selectTradeOfferPopup(): {0}", i);
            }
        });
        Global.ref().getGameSceneManager().safelyQueuePopup(pop);
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
        List<PropertyField> playersProps = Global.ref().getGame().getBoard().getFieldManager()
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

        Field[] fields = Global.ref().getGame().getBoard().getFieldManager().getFields();
        int[] propertyIds = new int[itemList.size()];

        for (int i = 0; i < itemList.size(); i++) {
            for (int j = 0; j < fields.length; j++) {

                if (itemList.get(i).getText().equals(fields[j].getName())) {
                    propertyIds[i] = Global.ref().getGame().getBoard().getFieldManager().getFieldId(fields[j]);
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
        int playerCards = player.getCardStack().countCardsOfAction(Card.Action.JAIL);
        return (moneyAmount > -1 && moneyAmount < player.getMoney()) ? (cardAmount > -1 && cardAmount <= playerCards) : false;
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
        tradeWarningGridPane.setAlignment(Pos.CENTER);
        tradeWarningGridPane.add(tradeWarningVBox, 0, 0);

        tradeWarningVBox.setId("greenPopup");
        tradeWarningVBox.setSpacing(10);
        tradeWarningVBox.setPrefSize(800, 200);
        tradeWarningVBox.setAlignment(Pos.CENTER);
        tradeWarningVBox.getChildren().addAll(tradeWarningLabel);

        Popup pop = new Popup(tradeWarningGridPane, Duration.millis(3500));
        safelyQueuePopup(pop);

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

        if (player.equals(Global.ref().getClient().getPlayerOnClient())) {
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

        if (player.equals(Global.ref().getClient().getPlayerOnClient())) {
            return tradeGui.getYourCardIds();
        }
        else {
            return tradeGui.getPartnersCardIds();
        }
    }

    public int getMoneyForTrade(Player player, GuiTrade tradeGui) {

        if (player.equals(Global.ref().getClient().getPlayerOnClient())) {
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
        Separator offersSeparator = new Separator(Orientation.VERTICAL);
        Separator underYourPropsSeparator = new Separator(Orientation.HORIZONTAL);
        Separator underYourCardsSeparator = new Separator(Orientation.HORIZONTAL);
        Separator underPartnersPropsSeparator = new Separator(Orientation.HORIZONTAL);
        Separator underPartnersCardsSeparator = new Separator(Orientation.HORIZONTAL);

        //Einstellen des GUI Fensters
        showOfferGridPane.add(mainOfferVBox, 0, 0);
        showOfferGridPane.setAlignment(Pos.CENTER);

        mainOfferVBox.setId("greenPopup");
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

        safelyQueuePopup(pop);

    }

    private String generatePropertyString(int[] propertyIds) {

        Field[] fields = Global.ref().getGame().getBoard().getFields();
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
        tradeResponseGridPane.setAlignment(Pos.CENTER);
        tradeResponseGridPane.add(tradeResponseVBox, 0, 0);

        tradeResponseVBox.setId("greenPopup");
        tradeResponseVBox.setSpacing(10);
        tradeResponseVBox.setPrefSize(800, 200);
        tradeResponseVBox.setAlignment(Pos.CENTER);
        tradeResponseVBox.getChildren().addAll(tradeResponseLabel);

        Popup pop = new Popup(tradeResponseGridPane);
        Global.ref().getGuiTrade().setWaitForResponsePopup(pop);
        safelyQueuePopup(pop);

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
        tradeAnswerGridPane.setAlignment(Pos.CENTER);
        tradeAnswerGridPane.add(tradeAnswerVBox, 0, 0);

        tradeAnswerVBox.setId("greenPopup");
        tradeAnswerVBox.setSpacing(10);
        tradeAnswerVBox.setPrefSize(800, 200);
        tradeAnswerVBox.setAlignment(Pos.CENTER);
        tradeAnswerVBox.getChildren().addAll(tradeAnswerLabel);

        Popup pop = new Popup(tradeAnswerGridPane, Duration.millis(2000));
        Global.ref().getGameSceneManager().safelyQueuePopup(pop);
    }

    class Popup {

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
        public void onDiceThrow(int[] result, int doubletCount) {
            if (doubletCount == 3) {
                board3d.setNextMoveBackwards(true);
            }
        }

        @Override
        public void onPlayerBankrupt(Player player) {
            safelyQueueTask((() -> {
                Fx3dPlayer fxPlayer = board3d.findFxEquivalent(player);
                playerBox.getChildren().remove(fxPlayer.infoPane());
                board3d.removePlayer(player);
            }));
        }

        @Override
        public void onGameEnd(Player winner) {
            safelyQueuePopup(winnerPopup(winner));
        }

        @Override
        public void onTurnStart(Player player) {
            safelyQueueTask(() -> watchNode(board3d.findFxEquivalent(player)));
        }

        @Override
        public void onPlayerOnCardField(Player player, CardField cardField, Card card) {
            queueTask(() -> showCard(card, cardField.getStackType()), 1500);

            Card.Action action = card.getAction();
            if (action == Card.Action.GO_JAIL || action == Card.Action.MOVE) {
                board3d.setNextMoveBackwards(true);
            }
        }

        @Override
        public void onTurnEnd(Player oldPlayer, Player newPlayer) {
            safelyQueueTask(() -> changeFirstPlayer(), 0);
        }
    }
}
