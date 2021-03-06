package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.core.FieldTypes;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.GameStateAdapter;
import de.btu.monopoly.core.GameStateListener;
import de.btu.monopoly.data.field.FieldManager;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.field.StreetField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.ui.AnimationQueuer;
import de.btu.monopoly.util.Assets;
import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static de.btu.monopoly.ui.fx3d.Fx3dField.*;

public class Fx3dGameBoard extends Group
{
    public static final int FIELD_COUNT = FieldTypes.GAMEBOARD_FIELD_STRUCT.length;
    public static final double BOARD_MODEL_LENGTH = 9 * FIELD_WIDTH;
    
    private static final double BOARD_LENGTH = 2 * FIELD_DEPTH + BOARD_MODEL_LENGTH;
    private static final Cuboid BOARD_MODEL = new Cuboid(BOARD_MODEL_LENGTH, 10, BOARD_MODEL_LENGTH);
    
    private static final double FIELDS_OFF_X = BOARD_LENGTH / 2 + Fx3dField.FIELD_WIDTH / 2;
    private static final double FIELDS_OFF_Y = -FIELD_HEIGHT;
    private static final double FIELDS_OFF_Z = -BOARD_LENGTH / 2 + Fx3dField.FIELD_DEPTH / 2;
    
    private static final double CORNER_DIST = -(Fx3dCorner.FIELD_WIDTH / 2 + Fx3dField.FIELD_WIDTH / 2);
    private static final double FIELD_DIST = -Fx3dField.FIELD_WIDTH;
    
    private static final int ROUND_START_PAUSE_MILLIS = 1000;
    private static final int CARD_FIELD_PAUSE_MILLIS = 1000;
    
    private final GameBoard board;
    
    private final Cuboid boardModel;
    private final Group[] fieldGroups;
    private final Fx3dField[] fieldModels;
    
    private final Group fieldGroup;
    private final Group fieldInfoGroup;
    
    private final Group houseGroup;
    private final Group playerGroup;
    
    private AnimationQueuer queuer;
    private GameStateListener stateListener;
    
    private boolean nextMoveBackwards;
    
    public Fx3dGameBoard(GameBoard board, AnimationQueuer queuer) {
        super();
        this.board = board;
        this.queuer = queuer;
        
        boardModel = new Cuboid(BOARD_MODEL.getWidth(), BOARD_MODEL.getHeight(), BOARD_MODEL.getDepth());
        fieldGroups = new Group[FIELD_COUNT];
        fieldModels = new Fx3dField[fieldGroups.length];
        
        fieldGroup = new Group();
        fieldInfoGroup = new Group();
        
        houseGroup = new Group();
        playerGroup = new Group();
        
        stateListener = new GameStateListenerImpl();
        
        getChildren().addAll(boardModel, fieldGroup, fieldInfoGroup, houseGroup, playerGroup);
        
        init();
    }
    
    public Fx3dPlayer findFxEquivalent(Player player) {
        ObservableList<Node> players = playerGroup.getChildren();
        for (Node n : players) {
            if (n instanceof Fx3dPlayer && player == ((Fx3dPlayer) n).player()) {
                return (Fx3dPlayer) n;
            }
        }
        throw new RuntimeException(
                String.format("Couldn't find a proper 3d equivalent for player: %s", player.getName()));
    }
    
    public void setNextMoveBackwards(boolean val) {
        this.nextMoveBackwards = val;
    }
    
    /**
     * Ruft s??mtliche Untermethoden zum initialiseren des Boards auf und setzt die Lichtquelle.
     */
    private void init() {
        
        initBoard();
        initFields();
        initPlayers();
        
        AmbientLight light = new AmbientLight();
        getChildren().add(light);
    }
    
    /**
     * Erstellt das Spielbrett und l??dt die Texturen.
     */
    private void initBoard() {
        boardModel.setTranslateY(-BOARD_MODEL.getHeight());
        boardModel.setMaterial(FxHelper.getMaterialFor(Assets.getImage("game_board")));
    }
    
    /**
     * Erstellt 3d-Repr??sentanten f??r s??mtliche Spieler.
     */
    private void initPlayers() {
        
        List<Player> activePlayers = board.getActivePlayers();
        ObservableList<Node> children = playerGroup.getChildren();
        
        for (int i = 0; i < activePlayers.size(); i++) {
            children.add(initPlayer(activePlayers.get(i)));
        }
    }
    
    /**
     * Erstellt 3D-Repr??sentanten f??r den gegebenen Spieler. Gibt ihm eine Position.
     *
     * @param player Spieler
     * @return Spieler 3D-Repr??sentation
     */
    private Fx3dPlayer initPlayer(Player player) {
    
        Fx3dPlayer fxPlayer = new Fx3dPlayer(player);
        
        Group positionField = fieldGroups[player.getPosition()];
        fxPlayer.getTransforms().add(positionField.getLocalToSceneTransform());
        fxPlayer.positionProperty.addListener((prop, oldPos, newPos) -> {
            Fx3dPlayer fxplayer = findFxEquivalent(player);
            queuer.queueAnimation(createMoveTransition(fxplayer, oldPos.intValue(), newPos.intValue()));
        });
        
        return fxPlayer;
    }
    
    public void removePlayer(Player player) {
        
        Fx3dPlayer fxPlayer = findFxEquivalent(player);
        playerGroup.getChildren().remove(fxPlayer);
    }
    
    /**
     * Erstellt eine Laufanimation zwischen den Feldern mit den Indizes oldPos und newPos.
     *
     * @param player Spieler
     * @param oldPos Startindex
     * @param newPos Zielindex
     * @return Animation
     */
    private ParallelTransition createMoveTransition(Fx3dPlayer player, int oldPos, int newPos) {
        ParallelTransition retObj = createMoveTransition(player, oldPos, newPos, nextMoveBackwards);
        nextMoveBackwards = false;
        return retObj;
    }
    
    private ParallelTransition createMoveTransition(Fx3dPlayer player, int oldPos, int newPos, boolean moveBackwards) {
    
        List<Transform> waypoints = new LinkedList<>();
        moveBackwards &= newPos < oldPos;
        int dest = (newPos + (moveBackwards ? -1 : 1)) % FIELD_COUNT;
        
        while (oldPos != dest) {
            waypoints.add(fieldGroups[oldPos].getLocalToParentTransform());
            oldPos = (oldPos + (moveBackwards ? -1 : 1)) % FIELD_COUNT;
        }
    
        return createMoveTransition(player, waypoints.toArray(new Transform[waypoints.size()]));
    }
    
    /**
     * Hilfsmethode f??r {@link #createMoveTransition(Fx3dPlayer, int, int)}.
     *
     * @param player Spieler
     * @param waypoints Wegpunkte
     * @return Animation
     */
    private ParallelTransition createMoveTransition(Fx3dPlayer player, Transform[] waypoints) {
        
        TranslateTransition jt = player.createJumpAnimation();
        jt.setCycleCount(2 * (waypoints.length - 1));
    
        SequentialTransition st = new SequentialTransition(player);
        ObservableList<Animation> anims = st.getChildren();
    
        Transform currTransform;
        Transform nextTransform = waypoints[0];
        for (int i = 1; i < waypoints.length; i++) {
        
            currTransform = nextTransform;
            nextTransform = waypoints[i];
        
            TranslateTransition tt = new TranslateTransition(
                    Duration.millis(Fx3dPlayer.FIELD_MOVE_DURATION), player);
            tt.setInterpolator(Interpolator.LINEAR);
            tt.setByX(nextTransform.getTx() - currTransform.getTx());
            tt.setByY(nextTransform.getTy() - currTransform.getTy());
            tt.setByZ(nextTransform.getTz() - currTransform.getTz());
        
            anims.add(tt);
        }
        
        ParallelTransition retObj = new ParallelTransition(jt, st);
        retObj.setNode(player);
        return retObj;
    }
    
    private void initFields() {
        
        ObservableList<Node> children = fieldGroup.getChildren();
        
        FieldTypes[] struct = FieldTypes.GAMEBOARD_FIELD_STRUCT;
        FieldManager fieldMan = board.getFieldManager();
        
        FieldTypes lastType;
        FieldTypes currType = struct[0];
        
        Affine affine = new Affine(new Translate(FIELDS_OFF_X, FIELDS_OFF_Y, FIELDS_OFF_Z));
        for (int id = 0; id < struct.length; id++) {
            
            Group holder = new Group();
            Fx3dField fieldShape;
            lastType = currType;
            currType = struct[id];
            
            if (currType.isCorner()) {
    
                affine.appendTranslation(CORNER_DIST, 0);
                if (currType != FieldTypes.CORNER_0) affine.appendRotation(90, 0, 0, 0, Rotate.Y_AXIS);
                
                fieldShape = new Fx3dCorner(fieldMan.getField(id), currType,
                        Assets.getImage(currType.name().toLowerCase()));
            }
            else {
                affine.appendTranslation(lastType.isCorner() ? CORNER_DIST : FIELD_DIST, 0);
                
                if (currType.isStreet()) {
                    fieldShape = new Fx3dStreetField((StreetField) fieldMan.getField(id), currType, queuer);
                    holder.getChildren().add(((Fx3dStreetField) fieldShape).house());
                }
                else if (currType.isStation() || currType.isSupply()) {
                    fieldShape = new Fx3dPropertyField((PropertyField) fieldMan.getField(id), currType, queuer);
                }
                else fieldShape = new Fx3dField(fieldMan.getField(id), currType, Assets.getImage(currType.name().toLowerCase()));
            }
            
            holder.getChildren().add(fieldShape);
            holder.getTransforms().add(0, affine.clone());
            
            fieldGroups[id] = holder;
            fieldModels[id] = fieldShape;
            children.add(holder);
        }
    }
    
    public Stream<Fx3dField> getFields() {
        return Arrays.stream(fieldModels);
    }
    
    public Stream<Fx3dPlayer> getPlayers() {
        return playerGroup.getChildren().stream()
                .filter(Fx3dPlayer.class::isInstance)
                .map(Fx3dPlayer.class::cast);
    }
    
    public GameStateListener stateListener() {
        return stateListener;
    }
    
    class GameStateListenerImpl extends GameStateAdapter {
    }
}
