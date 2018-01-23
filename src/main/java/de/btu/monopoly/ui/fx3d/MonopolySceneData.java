package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.ui.util.Assets;
import javafx.scene.*;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class MonopolySceneData
{
    private static final double DEFAULT_SCENE_WIDTH = 1280;
    private static final double DEFAULT_SCENE_HEIGHT = 720;
    
    private final Scene scene;
    
    private final Group parent;
    private final Group uiGroup;
    
    private final MonopolyBoard board;
    private final SubScene gameSub;
    
    private Camera orthoCam;
    private Camera perspectiveCam;
    private boolean perspectiveView;
    
    public MonopolySceneData() {
        
        parent = new Group();
        uiGroup = new Group();
        
        board = new MonopolyBoard();
        gameSub = new SubScene(board, 0, 0, true, SceneAntialiasing.BALANCED);
        
        parent.getChildren().addAll(gameSub, uiGroup);
        scene = new Scene(parent, DEFAULT_SCENE_WIDTH, DEFAULT_SCENE_HEIGHT);
        
        initScene();
    }
    
    public Scene getScene() {
        return scene;
    }
    
    private void initScene()
    {
        if (!Assets.loaded()) {
            Assets.load();
        }
        gameSub.setFill(Color.LIGHTGRAY);
        
        gameSub.widthProperty().bind(scene.widthProperty());
        gameSub.heightProperty().bind(scene.heightProperty());
        
        initButtons();
        initCams();
        
        gameSub.setCamera(orthoCam);
    }
    
    private void initButtons()
    {
        ToggleButton button = new ToggleButton(null, new ImageView(Assets.getImage("3d_rotation")));
        button.setOnMousePressed(event ->
        {
            perspectiveView = !perspectiveView;
            gameSub.setCamera(perspectiveView ? perspectiveCam : orthoCam);
            board.setFluentRotation(perspectiveView);
            board.setRotationAxis(Rotate.Y_AXIS);
            if (!perspectiveView && board.getRotate() % 90 != 0)
                board.rotateChunky(false);
        });
        button.setPrefSize(50, 50);
        button.setTranslateX(5);
        button.setTranslateY(5);
        
        uiGroup.getChildren().add(new HBox(button));
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
