package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.data.field.FieldManager;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.util.Assets;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import java.util.List;
import java.util.stream.Stream;

import static de.btu.monopoly.ui.fx3d.Fx3dField.*;

public class Fx3dGameBoard extends Group
{
    public static final int FIELD_COUNT = Fx3dFieldType.GAMEBOARD_FIELD_STRUCTURE.length;
    public static final double BOARD_MODEL_LENGTH = 9 * FIELD_WIDTH;
    
    private static final double BOARD_LENGTH = 2 * FIELD_DEPTH + BOARD_MODEL_LENGTH;
    private static final Cuboid BOARD_MODEL = new Cuboid(BOARD_MODEL_LENGTH, 10, BOARD_MODEL_LENGTH);
    
    private static final double FIELDS_OFF_X = BOARD_LENGTH / 2 + Fx3dField.FIELD_WIDTH / 2;
    private static final double FIELDS_OFF_Y = -FIELD_HEIGHT;
    private static final double FIELDS_OFF_Z = -BOARD_LENGTH / 2 + Fx3dField.FIELD_DEPTH / 2;
    
    private static final double CORNER_DIST = -(Fx3dCorner.FIELD_WIDTH / 2 + Fx3dField.FIELD_WIDTH / 2);
    private static final double FIELD_DIST = -Fx3dField.FIELD_WIDTH;
    
    private final GameBoard board;
    
    private final Cuboid boardModel;
    private final Fx3dField[] fieldModels;
    
    private final Group fieldGroup;
    private final Group houseGroup;
    private final Group playerGroup;
    
    private final IntegerProperty runningAnimationCount;
    private final BooleanProperty readyForPopup;
    
    public Fx3dGameBoard(GameBoard board) {
        super();
        this.board = board;
        
        boardModel = new Cuboid(BOARD_MODEL.getWidth(), BOARD_MODEL.getHeight(), BOARD_MODEL.getDepth());
        fieldModels = new Fx3dField[FIELD_COUNT];
        
        fieldGroup = new Group();
        houseGroup = new Group();
        playerGroup = new Group();
        
        getChildren().addAll(boardModel, fieldGroup, houseGroup, playerGroup);
        
        runningAnimationCount = new SimpleIntegerProperty(0);
        readyForPopup = new SimpleBooleanProperty(true);
        readyForPopup.bind(runningAnimationCount.isEqualTo(0));
        
        init();
    }
    
    private void init() {
        
        initBoard();
        initFields();
        initPlayers();
        
        AmbientLight light = new AmbientLight();
        getChildren().add(light);
    }
    
    private void initBoard() {
        
        boardModel.setMaterial(FxHelper.getMaterialFor(Assets.getImage("game_board")));
    }
    
    private void initPlayers() {
        
        List<Player> activePlayers = board.getActivePlayers();
        ObservableList<Node> children = playerGroup.getChildren();
        
        for (int i = 0; i < activePlayers.size(); i++) {
            children.add(initFxPlayer(activePlayers.get(i)));
        }
    }
    
    private Fx3dPlayer initFxPlayer(Player player) {
    
        Fx3dPlayer fxPlayer = new Fx3dPlayer(player, new Color(Math.random(), Math.random(), Math.random(), 1));
        
        Shape3D positionField = fieldModels[player.getPosition()];
        fxPlayer.getTransforms().add(positionField.getLocalToSceneTransform());
        
        fxPlayer.positionProperty().addListener((val, oldI, newI) -> {
            
            int oldV = oldI.intValue();
            int diff = newI.intValue() + 1 - oldV;
            if (diff < 0) diff = FIELD_COUNT + diff;
            
            Transform[] waypoints = new Transform[diff];
            for (int i = 0; i < diff; i++) {
                waypoints[i % FIELD_COUNT] = fieldModels[(oldV + i) % FIELD_COUNT].getLocalToParentTransform();
            }
            
            fxPlayer.move(waypoints);
        });
        
        fxPlayer.animationsRunningProperty().addListener((prop, oldV, newV) ->
                runningAnimationCount.set(runningAnimationCount.get() + (newV ? 1 : -1)));
        
        return fxPlayer;
    }
    
    private void initFields() {
        
        ObservableList<Node> children = fieldGroup.getChildren();
        Fx3dFieldType[] struct = Fx3dFieldType.GAMEBOARD_FIELD_STRUCTURE;
        FieldManager fieldMan = board.getFieldManager();
        
        Fx3dFieldType lastType;
        Fx3dFieldType currType = struct[0];
        
        Affine affine = new Affine(new Translate(FIELDS_OFF_X, FIELDS_OFF_Y, FIELDS_OFF_Z));
        for (int id = 0; id < struct.length; id++) {
            
            Fx3dField fieldShape;
            lastType = currType;
            currType = struct[id];
            
            if (currType.isCorner()) {
    
                affine.appendTranslation(CORNER_DIST, 0);
                if (currType != Fx3dFieldType.CORNER_0) affine.appendRotation(90, 0, 0, 0, Rotate.Y_AXIS);
                
                fieldShape = new Fx3dCorner(fieldMan.getField(id), Assets.getImage(currType));
            }
            else {
                affine.appendTranslation(lastType.isCorner() ? CORNER_DIST : FIELD_DIST, 0);
                
                if (currType.isStreet() || currType.isStation() || currType.isSupply())
                    fieldShape = new Fx3dPropertyField((PropertyField) fieldMan.getField(id), currType);
                else fieldShape = new Fx3dField(fieldMan.getField(id), currType, Assets.getImage(currType));
            }
            
            fieldShape.getTransforms().add(0, affine.clone());
            fieldModels[id] = fieldShape;
            children.add(fieldShape);
        }
    }
    
    public BooleanProperty readyForPopupProperty() { return readyForPopup; }
    
    public Stream<Fx3dPlayer> getPlayers() {
        return playerGroup.getChildren().stream()
                .filter(Fx3dPlayer.class::isInstance)
                .map(Fx3dPlayer.class::cast);
    }
}
