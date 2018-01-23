package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.ui.util.Assets;
import de.btu.monopoly.ui.util.Cuboid;
import de.btu.monopoly.ui.util.FXHelper;
import de.btu.monopoly.ui.util.TextUtils;
import javafx.animation.RotateTransition;
import javafx.collections.ObservableList;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.awt.geom.Point2D;

import static de.btu.monopoly.ui.fx3d.FieldBase.FIELD_DEPTH;
import static de.btu.monopoly.ui.fx3d.FieldBase.FIELD_WIDTH;
import static de.btu.monopoly.ui.fx3d.FieldType.CORNER_0;

public class MonopolyBoard extends Group
{
    public static final int FIELD_COUNT = 40;
    
    private static final double BOARD_LENGTH = 2 * FIELD_DEPTH + 9 * FIELD_WIDTH;
    private static final Cuboid BOARD_MODEL = new Cuboid(BOARD_LENGTH, 10, BOARD_LENGTH);
    
    private static final Rotate CORNER_ROTATE = new Rotate(90, Rotate.Y_AXIS);
    private static final Translate CORNER_TRANSLATE = new Translate(-FieldBase.FIELD_DEPTH / 2f, 0, 0);
    private static final Translate FIELD_TRANSLATE = new Translate(-FieldBase.FIELD_WIDTH / 2f, 0, 0);
    
    private int CHUNKY_ROTATION_THRESHOLD = 200;
    
    private final Shape3D[] fields = new Shape3D[FIELD_COUNT];
    
    private Cuboid boardBase;
    private Group fieldGroup;
    private Group houseGroup;
    private Group playerGroup;
    
    private Point2D.Double dragPoint;
    private boolean fluentRotation;
    private boolean transitioning;
    
    public MonopolyBoard()
    {
        super();
        
        boardBase = new Cuboid(BOARD_MODEL.getWidth(), BOARD_MODEL.getHeight(), BOARD_MODEL.getDepth());

        fieldGroup = new Group();
        houseGroup = new Group();
        playerGroup = new Group();
        
        getChildren().addAll(boardBase, fieldGroup, houseGroup, playerGroup);
        
        init();
    }
    
    private void init()
    {
        initBoard();
        initFields();
        
        AmbientLight light = new AmbientLight();
        getChildren().add(light);
    }
    
    private void initBoard()
    {
        boardBase.setMaterial(FXHelper.getMaterialFor(Assets.getImage("board")));
        boardBase.setOnMousePressed(event -> setDragPoint(event));
        boardBase.setOnMouseDragged(event ->
        {
            double diffX = event.getScreenX() - dragPoint.getX();
            if (fluentRotation)
            {
                setRotationAxis(Rotate.Y_AXIS);
                setRotate(getRotate() - diffX / 2);
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
    
    public int addPlayer(PlayerBase base)
    {
//        ObservableList<Node> children = playerGroup.getChildren();
//        int index = children.size();
//        children.add(index, base);
//        return index;
        return 0;
    }
    
    public void removePlayer(int playerId)
    {
//        playerGroup.getChildren().remove(playerId);
    }
    
    public void movePlayer(int playerId)
    {
//        PlayerBase base = (PlayerBase) playerGroup.getChildren().get(playerId);
//
//        int position = base.getPosition();
//        double oldX = fields[position].getTranslateX();
//        position = (position + 1) % FIELD_COUNT;
//        double diffX = oldX - fields[position].getTranslateX();
//
//        TranslateTransition tt = new TranslateTransition(Duration.millis(2000), base);
//        tt.setByX(diffX);
//        tt.playFromStart();
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
                
                fieldShape = new CornerBase(Assets.getImage(type.toString().toLowerCase()));
                finalTransform = transform.clone();
                transform = transform.createConcatenation(CORNER_TRANSLATE);
            }
            else
            {
//                if (type.isStreet())
//                {
//                    HouseStack stack = new HouseStack();
//                    stack.setHouseCount((int) (Math.random() * 6));
//                    group.getChildren().add(stack);
//                }
                
                transform = transform.createConcatenation(FIELD_TRANSLATE);
                fieldShape = new FieldBase(Assets.getImage(type.toString().toLowerCase()));
                finalTransform = transform.clone();
                transform = transform.createConcatenation(FIELD_TRANSLATE);
            }
            
            fields[id] = fieldShape;
            
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
