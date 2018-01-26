package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.ui.util.FxHelper;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Transform;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Fx3dPlayer extends Cylinder {
    
    private static final Cylinder PLAYER_MODEL = new Cylinder(20, 25);
    
    private static final double FIELD_MOVE_DURATION = 400;
    private static final double JUMP_HEIGHT = 100;
    
    private final List<Animation> animationQueue;
    private final BooleanProperty animationsRunning;
    
    private Player player;
    private Color color;
    
    public Fx3dPlayer(Player player, Color color) {
        
        super(PLAYER_MODEL.getRadius(), PLAYER_MODEL.getHeight());
        this.player = player;
        this.color = color;
        
        animationQueue = new LinkedList<>();
        animationsRunning = new SimpleBooleanProperty(false);
        
        setMaterial(FxHelper.getMaterialFor(color));
    }
    
    public void move(Transform... waypoints) {
        
        SequentialTransition st = new SequentialTransition(this);
        ObservableList<Animation> anims = st.getChildren();
        
        Transform currTransform;
        Transform nextTransform = getLocalToParentTransform();
        for (int i = 0; i < waypoints.length; i++) {
            
            currTransform = nextTransform;
            nextTransform = waypoints[i];
            
            TranslateTransition tt = new TranslateTransition(
                    Duration.millis(FIELD_MOVE_DURATION), this);
            tt.setByX(nextTransform.getTx() - currTransform.getTx());
            tt.setByY(nextTransform.getTy() - currTransform.getTy());
            tt.setByZ(nextTransform.getTz() - currTransform.getTz());
    
            anims.add(tt);
        }
        
        TranslateTransition tt = new TranslateTransition(
                Duration.millis(FIELD_MOVE_DURATION / 2), this);
        tt.setByY(-JUMP_HEIGHT);
        tt.setAutoReverse(true);
        tt.setCycleCount(2 * waypoints.length);
        
        ParallelTransition transition = new ParallelTransition(st, tt);
        transition.setOnFinished(event -> updateAnimationsRunning());
        animationQueue.add(transition);
        
        if (!animationsRunning.get()) {
            animationsRunning.set(true);
            transition.play();
        }
    }
    
    private void updateAnimationsRunning() {
        animationQueue.remove(0);
        animationsRunning.set(animationQueue.size() != 0);
        if (animationsRunning.get())
            animationQueue.get(0).play();
    }
    
    public IntegerProperty positionProperty() {
        return player.positionProperty();
    }
    
    public BooleanProperty animationsRunningProperty() { return animationsRunning; }
}
