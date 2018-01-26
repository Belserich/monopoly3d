package de.btu.monopoly.ui;

import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.ui.fx3d.MonopolyBoard;
import de.btu.monopoly.ui.util.Assets;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;

import static de.btu.monopoly.ui.CameraManager.WatchMode;

public class GameSceneManager
{
    private static final double DEFAULT_SCENE_WIDTH = 1280;
    private static final double DEFAULT_SCENE_HEIGHT = 720;
    
    private final Scene scene;
    private final BorderPane uiPane;
    
    private final Group uiGroup;
    private final Group popupGroup;
    private final List<Pane> popupQueue;
    
    private final MonopolyBoard board3d;
    private final SubScene gameSub;
    
    private CameraManager camMan;
    
    public GameSceneManager(GameBoard board) {
        
        this.board3d = new MonopolyBoard(board);
        
        gameSub = new SubScene(board3d, 0, 0, true, SceneAntialiasing.BALANCED);
        gameSub.setCache(true);
        gameSub.setCacheHint(CacheHint.SPEED);
    
        popupGroup = new Group();
        popupQueue = new LinkedList<>();
        board3d.readyForPopupProperty().addListener((prop, oldB, newB) -> {
            Platform.runLater(() ->{
                popupGroup.getChildren().addAll(popupQueue);
                popupQueue.clear();
            });
        });
        
        uiGroup = new Group(popupGroup);
        
        uiPane = new BorderPane();
        uiPane.setPadding(new Insets(5, 5, 5, 5));
        uiPane.setPickOnBounds(false);
        
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(gameSub, uiPane, uiGroup);
        
        scene = new Scene(stackPane, DEFAULT_SCENE_WIDTH, DEFAULT_SCENE_HEIGHT);
        
        initScene();
    }
    
    private void initScene()
    {
        gameSub.setFill(Color.LIGHTGRAY);
        
        gameSub.widthProperty().bind(scene.widthProperty());
        gameSub.heightProperty().bind(scene.heightProperty());
        
        initUi();
        initCams();
    }
    
    private void initUi() {
        
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
        
        ToggleButton viewButton = new ToggleButton(null, new ImageView(Assets.getImage("3d_icon")));
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
    }
    
    private void initCams() {
        camMan = new CameraManager(gameSub);
        camMan.watch(board3d, WatchMode.ORTHOGONAL);
    }
    
    public Scene getScene() {
        return scene;
    }
    
    public void queuePopupPane(Pane pane) {
        pane.layoutXProperty().bind(scene.widthProperty().divide(2).subtract(pane.widthProperty().divide(2)));
        pane.layoutYProperty().bind(scene.heightProperty().divide(2).subtract(pane.heightProperty().divide(2)));
        if (board3d.readyForPopupProperty().get())
            popupGroup.getChildren().add(pane);
        else popupQueue.add(pane);
    }
    
    public void removePopupPane(Pane pane) {
        popupGroup.getChildren().remove(pane);
    }
    
    public void clearPopups() {
        popupGroup.getChildren().clear();
    }
    
    public MonopolyBoard getBoard3d() {
        return board3d;
    }
}
