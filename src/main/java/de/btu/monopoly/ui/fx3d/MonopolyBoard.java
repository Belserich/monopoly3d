package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.ui.util.Assets;
import de.btu.monopoly.ui.util.Cuboid;
import de.btu.monopoly.ui.util.FxHelper;
import de.btu.monopoly.ui.util.TextUtils;
import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.awt.geom.Point2D;
import java.util.List;

import static de.btu.monopoly.ui.fx3d.FieldType.CORNER_0;
import static de.btu.monopoly.ui.fx3d.Fx3dField.FIELD_DEPTH;
import static de.btu.monopoly.ui.fx3d.Fx3dField.FIELD_WIDTH;

public class MonopolyBoard extends Group
{
    public static final int FIELD_COUNT = 40;
    
    private static final double BOARD_LENGTH = 2 * FIELD_DEPTH + 9 * FIELD_WIDTH;
    private static final Cuboid BOARD_MODEL = new Cuboid(BOARD_LENGTH, 10, BOARD_LENGTH);
    
    private static final Rotate CORNER_ROTATE = new Rotate(90, Rotate.Y_AXIS);
    private static final Translate CORNER_TRANSLATE = new Translate(-Fx3dField.FIELD_DEPTH / 2f, 0, 0);
    private static final Translate FIELD_TRANSLATE = new Translate(-Fx3dField.FIELD_WIDTH / 2f, 0, 0);
    
    private int CHUNKY_ROTATION_THRESHOLD = 200;
    
    private final GameBoard board;
    
    private final Cuboid boardModel;
    private final Shape3D[] fieldModels;
    
    private final Group fieldGroup;
    private final Group houseGroup;
    private final Group playerGroup;
    
    private Point2D.Double dragPoint;
    private boolean fluentRotation;
    private boolean transitioning;
    
    public MonopolyBoard(GameBoard board)
    {
        super();
        this.board = board;
        
        boardModel = new Cuboid(BOARD_MODEL.getWidth(), BOARD_MODEL.getHeight(), BOARD_MODEL.getDepth());
        fieldModels = new Shape3D[FIELD_COUNT];
        
        fieldGroup = new Group();
        houseGroup = new Group();
        playerGroup = new Group();
        
        getChildren().addAll(boardModel, fieldGroup, houseGroup, playerGroup);
        
        init();
    }
    
    private void init()
    {
        initBoard();
        initFields();
        initPlayers();
        
        AmbientLight light = new AmbientLight();
        getChildren().add(light);
    }
    
    private void initBoard()
    {
        boardModel.setMaterial(FxHelper.getMaterialFor(Assets.getImage("board")));
        boardModel.setOnMousePressed(event -> setDragPoint(event));
        boardModel.setOnMouseDragged(event ->
        {
            double diffX = event.getScreenX() - dragPoint.getX();
            if (fluentRotation)
            {
                boardModel.setRotationAxis(Rotate.Y_AXIS);
                boardModel.setRotate(diffX / -2);
                setDragPoint(event);
            }
            else if (Math.abs(diffX) >= CHUNKY_ROTATION_THRESHOLD && !transitioning)
            {
                rotateChunky(diffX > 0);
                setDragPoint(event);
            }
        });
    }
    
    public void rotateChunky(boolean clockwise)
    {
        transitioning = true;
        RotateTransition rt = new RotateTransition(Duration.millis(200), this);
        rt.setOnFinished(ev -> transitioning = false);
    
        rt.setAxis(Rotate.Y_AXIS);
        double checkVal = getRotate() % 90;
        if (checkVal == 0)
            rt.setByAngle(clockwise ? -90 : 90);
        else if (checkVal >= 90 / 2)
            rt.setByAngle((90 - checkVal) % 90);
        else
            rt.setByAngle(-checkVal);
        
        rt.playFromStart();
    }
    
    private void initPlayers() {
        
        List<Player> activePlayers = board.getActivePlayers();
        ObservableList<Node> children = playerGroup.getChildren();
        
        for (int i = 0; i < activePlayers.size(); i++) {
            children.add(createFxPlayer(activePlayers.get(i)));
        }
    }
    
    private Fx3dPlayer createFxPlayer(Player player) {
    
        Fx3dPlayer fxPlayer = new Fx3dPlayer(player, new Color(Math.random(), Math.random(), Math.random(), 1));
        
        Shape3D positionField = fieldModels[player.getPosition()];
        fxPlayer.getTransforms().add(positionField.getLocalToSceneTransform());
        
        fxPlayer.getPosition().addListener((val, oldV, newV) -> {
    
            SequentialTransition st = new SequentialTransition(fxPlayer);
            ObservableList<Animation> anims = st.getChildren();
            
            int nextV;
            Transform currTransform, nextTransform;
            while (!oldV.equals(newV)) {
                
                nextV = (oldV.intValue() + 1) % FIELD_COUNT;
                currTransform = fieldModels[oldV.intValue()].getLocalToSceneTransform();
                nextTransform = fieldModels[nextV].getLocalToSceneTransform();
                
                TranslateTransition tt = new TranslateTransition(Duration.millis(200), fxPlayer);
                tt.setByX(nextTransform.getTx() - currTransform.getTx());
                tt.setByY(nextTransform.getTy() - currTransform.getTy());
                tt.setByZ(nextTransform.getTz() - currTransform.getTz());
                
                anims.add(tt);
                oldV = nextV;
            }
            
            st.play();
        });
        
        return fxPlayer;
    }
    
    private void initFields()
    {
        ObservableList<Node> children = fieldGroup.getChildren();
        FieldType[] boardStruct = FieldType.GAMEBOARD_FIELD_STRUCTURE;
    
        FieldType type;
        Shape3D fieldShape;
        Group group;
    
        Transform finalTransform;
        Transform transform = new Translate(0, 0, 0);
        for (int id = 0; id < boardStruct.length; id++)
        {
            group = new Group();
            type = boardStruct[id];
            if (type.isCorner())
            {
                if (type != CORNER_0)
                {
                    transform = transform.createConcatenation(CORNER_TRANSLATE);
                    transform = transform.createConcatenation(CORNER_ROTATE);
                }
                
                fieldShape = new Fx3dCorner(board.getFieldManager().getField(id),
                        Assets.getImage(type.toString().toLowerCase()));
                finalTransform = transform.clone();
                transform = transform.createConcatenation(CORNER_TRANSLATE);
            }
            else
            {
//                if (type.isStreet())
//                {
//                    Fx3dHouse stack = new Fx3dHouse();
//                    stack.setHouseCount((int) (Math.random() * 6));
//                    group.getChildren().add(stack);
//                }
                
                transform = transform.createConcatenation(FIELD_TRANSLATE);
                fieldShape = new Fx3dField(board.getFieldManager().getField(id),
                        Assets.getImage(type.toString().toLowerCase()));
                finalTransform = transform.clone();
                transform = transform.createConcatenation(FIELD_TRANSLATE);
            }
            
            fieldModels[id] = fieldShape;
            
            group.getChildren().addAll(fieldShape, TextUtils.createFieldTexts(id));
            group.getTransforms().add(finalTransform);
            children.add(group);
        }
        
        this.fieldGroup.getTransforms().add(
                new Translate(BOARD_LENGTH / 2 - FIELD_DEPTH / 2, -4, -(BOARD_LENGTH / 2 - FIELD_DEPTH / 2)));
    }
    
    private void setDragPoint(MouseEvent event)
    {
        dragPoint = new Point2D.Double(event.getScreenX(), event.getScreenY());
    }
    
    public void setFluentRotation(boolean val)
    {
        this.fluentRotation = val;
    }
}
