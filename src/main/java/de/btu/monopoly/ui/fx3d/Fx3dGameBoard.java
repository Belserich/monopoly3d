package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.core.FieldTypes;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.GameStateAdapter;
import de.btu.monopoly.data.field.FieldManager;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.field.StreetField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.util.Assets;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    
    private final GameBoard board;
    private final GameStateAdapterImpl stateAdapter;
    
    private final Cuboid boardModel;
    private final Group[] fieldGroups;
    private final Fx3dField[] fieldModels;
    
    private final Group fieldGroup;
    private final Group fieldInfoGroup;
    
    private final Group houseGroup;
    private final Group playerGroup;
    
    private List<Animation> animQueue;
    private BooleanProperty isAnimatingProperty;
    
    public Fx3dGameBoard(GameBoard board) {
        super();
        this.board = board;
        
        stateAdapter = new GameStateAdapterImpl();
        
        boardModel = new Cuboid(BOARD_MODEL.getWidth(), BOARD_MODEL.getHeight(), BOARD_MODEL.getDepth());
        fieldGroups = new Group[FIELD_COUNT];
        fieldModels = new Fx3dField[fieldGroups.length];
        
        fieldGroup = new Group();
        fieldInfoGroup = new Group();
        
        houseGroup = new Group();
        playerGroup = new Group();
        
        animQueue = new LinkedList<>();
        isAnimatingProperty = new SimpleBooleanProperty(false);
        isAnimatingProperty.addListener((inv, oldV, newV) -> requestNextMoveAnim());
        
        getChildren().addAll(boardModel, fieldGroup, fieldInfoGroup, houseGroup, playerGroup);
        
        init();
    }
    
    private void requestNextMoveAnim() {
        if (!isAnimatingProperty.get() && !animQueue.isEmpty()) {
            isAnimatingProperty.set(true);
            Animation anim = animQueue.remove(0);
            anim.setOnFinished(inv -> isAnimatingProperty.set(false));
            anim.play();
        }
    }
    
    private Fx3dPlayer findEquivalent(Player player) {
        ObservableList<Node> players = playerGroup.getChildren();
        for (Node n : players) {
            if (n instanceof Fx3dPlayer && player == ((Fx3dPlayer) n).player()) {
                return (Fx3dPlayer) n;
            }
        }
        throw new RuntimeException(
                String.format("Couldn't find a proper 3d equivalent for player: %s", player.getName()));
    }
    
    /**
     * Ruft sämtliche Untermethoden zum initialiseren des Boards auf und setzt die Lichtquelle.
     */
    private void init() {
        
        initBoard();
        initFields();
        initPlayers();
        
        AmbientLight light = new AmbientLight();
        getChildren().add(light);
    }
    
    /**
     * Erstellt das Spielbrett und lädt die Texturen.
     */
    private void initBoard() {
        boardModel.setTranslateY(-BOARD_MODEL.getHeight());
        boardModel.setMaterial(FxHelper.getMaterialFor(Assets.getImage("game_board")));
    }
    
    /**
     * Erstellt 3d-Repräsentanten für sämtliche Spieler.
     */
    private void initPlayers() {
        
        List<Player> activePlayers = board.getActivePlayers();
        ObservableList<Node> children = playerGroup.getChildren();
        
        for (int i = 0; i < activePlayers.size(); i++) {
            children.add(initPlayer(activePlayers.get(i)));
        }
    }
    
    /**
     * Erstellt 3D-Repräsentanten für den gegebenen Spieler. Gibt ihm eine Position.
     *
     * @param player Spieler
     * @return Spieler 3D-Repräsentation
     */
    private Fx3dPlayer initPlayer(Player player) {
    
        Fx3dPlayer fxPlayer = new Fx3dPlayer(player);
        
        Group positionField = fieldGroups[player.getPosition()];
        fxPlayer.getTransforms().add(positionField.getLocalToSceneTransform());
        
        return fxPlayer;
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
        
        int diff = newPos + 1 - oldPos;
        if (diff < 0) diff = FIELD_COUNT + diff;
    
        Transform[] waypoints = new Transform[diff];
        for (int i = 0; i < diff; i++) {
            waypoints[i % FIELD_COUNT] = fieldGroups[(oldPos + i) % FIELD_COUNT].getLocalToParentTransform();
        }
        return createMoveTransition(player, waypoints);
    }
    
    /**
     * Hilfsmethode für {@link #createMoveTransition(Fx3dPlayer, int, int)}.
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
            tt.setByX(nextTransform.getTx() - currTransform.getTx());
            tt.setByY(nextTransform.getTy() - currTransform.getTy());
            tt.setByZ(nextTransform.getTz() - currTransform.getTz());
        
            anims.add(tt);
        }
        
        return new ParallelTransition(st, jt);
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
                    fieldShape = new Fx3dStreetField((StreetField) fieldMan.getField(id), currType);
                    holder.getChildren().add(((Fx3dStreetField) fieldShape).house());
                }
                else if (currType.isStation() || currType.isSupply())
                    fieldShape = new Fx3dPropertyField((PropertyField) fieldMan.getField(id), currType);
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
    
    public GameStateAdapterImpl gameStateAdapter() {
        return stateAdapter;
    }
    
    public BooleanProperty animatingProperty() {
        return isAnimatingProperty;
    }
    
    class GameStateAdapterImpl extends GameStateAdapter {
        
        @Override
        public void onPlayerMove(Player player, int oldPos, int newPos, boolean passedGo) {
            Fx3dPlayer fxplayer = findEquivalent(player);
            animQueue.add(createMoveTransition(fxplayer, oldPos, newPos));
            requestNextMoveAnim();
        }
    }
}
