package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.ui.util.Assets;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class MonopolySceneData
{
    private static final double DEFAULT_SCENE_WIDTH = 1280;
    private static final double DEFAULT_SCENE_HEIGHT = 720;
    
    private final Scene scene;
    
    private final BorderPane uiPane;
    private final Group uiGroup;
    private final Group popupGroup;
    
    private final MonopolyBoard board3d;
    private final SubScene gameSub;
    
    private Camera orthoCam;
    private Camera perspectiveCam;
    private boolean perspectiveView;
    
    public MonopolySceneData(GameBoard board) {
        this.board3d = new MonopolyBoard(board);
    
        gameSub = new SubScene(board3d, 0, 0, true, SceneAntialiasing.BALANCED);
        gameSub.setCache(true);
        gameSub.setCacheHint(CacheHint.SPEED);
        
        popupGroup = new Group();
        uiGroup = new Group(popupGroup);
        
        uiPane = new BorderPane();
        uiPane.setPadding(new Insets(5, 5, 5, 5));
        uiPane.setPickOnBounds(false);
        
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(gameSub, uiPane, uiGroup);
        
        scene = new Scene(stackPane, DEFAULT_SCENE_WIDTH, DEFAULT_SCENE_HEIGHT);
        
        initScene();
    }
    
    public Scene getScene() {
        return scene;
    }
    
    public void addPopupPane(Pane pane) {
        ObservableList<Node> children = popupGroup.getChildren();
        pane.layoutXProperty().bind(scene.widthProperty().divide(2).subtract(pane.widthProperty().divide(2)));
        pane.layoutYProperty().bind(scene.heightProperty().divide(2).subtract(pane.heightProperty().divide(2)));
        children.add(pane);
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
    
    private void initScene()
    {
        gameSub.setFill(Color.LIGHTGRAY);
        
        gameSub.widthProperty().bind(scene.widthProperty());
        gameSub.heightProperty().bind(scene.heightProperty());
        
        initUi();
        initCams();
        
        gameSub.setCamera(orthoCam);
    }
    
    private void initUi()
    {
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
            perspectiveView = !perspectiveView;
            gameSub.setCamera(perspectiveView ? perspectiveCam : orthoCam);
            board3d.setFluentRotation(perspectiveView);
            board3d.setRotationAxis(Rotate.Y_AXIS);
            if (!perspectiveView && board3d.getRotate() % 90 != 0)
                board3d.rotateChunky(false);
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
    
    private void initCams()
    {
        orthoCam = initCam();
        orthoCam.setTranslateY(-2250);
        orthoCam.setRotate(-90);
        orthoCam.setRotationAxis(Rotate.X_AXIS);
        
        perspectiveCam = initCam();
        perspectiveCam.setTranslateY(-1500);
        perspectiveCam.setTranslateZ(-1500);
        perspectiveCam.setRotate(-45);
        perspectiveCam.setRotationAxis(Rotate.X_AXIS);
        
        scene.setOnScroll(event -> {
            perspectiveCam.getTransforms().add(new Translate(0, 0, event.getDeltaY() * 2));
        });
    }
    
    private Camera initCam()
    {
        Camera cam = new PerspectiveCamera(true);
        cam.setNearClip(1);
        cam.setFarClip(5000);
        return cam;
    }
}
