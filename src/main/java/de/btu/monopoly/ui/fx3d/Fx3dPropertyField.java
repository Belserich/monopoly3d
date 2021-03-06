package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.core.FieldTypes;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.ui.AnimationQueuer;
import de.btu.monopoly.util.Assets;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Fx3dPropertyField extends Fx3dField {
    
    private static final int TURN_DURATION_MILLIS = 200;
    private static final double TURN_ANGLE = 180;
    
    private static final Color DEFAULT_COLOR = MaterialBuilder.DEFAULT_BACKGROUND_FILL;
    private static final Color MORTGAGE_COLOR = Color.rgb(237, 26, 36, 1);
    
    private final Pane infoPane;
    
    private final RotateTransition turnTrans;
    
    public Fx3dPropertyField(PropertyField field, FieldTypes type, AnimationQueuer queuer) {
        super(field, type, Assets.getImage(type.name().toLowerCase()));
        
        infoPane = InfoPaneBuilder.buildFor(field, type);
    
        turnTrans = new RotateTransition(Duration.millis(TURN_DURATION_MILLIS), this);
        turnTrans.setByAngle(TURN_ANGLE);
        turnTrans.setAxis(Rotate.Z_AXIS);
    
        field.mortgageTakenProperty().addListener((prop, oldB, newB) ->
                Platform.runLater(() -> queuer.queueAnimation(pauseAndMortageChange(newB)))
        );
    
        field.ownerProperty().addListener((prop, oldP, newP) -> {
            Color newColor = Color.web(newP.getColor()).brighter().brighter();
            Platform.runLater(() -> queuer.queueAnimation(pauseAndChangeColor(newColor)));
        });
    }
    
    private PauseTransition pauseAndChangeColor(Color newColor) {
        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(inv -> changeColor(newColor));
        return pause;
    }
    
    private PauseTransition pauseAndMortageChange(boolean newB) {
        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(inv -> changeMortgageStatus(newB));
        return pause;
    }
    
    private void changeColor(Color newColor) {
        setMaterial(MaterialBuilder.buildFor(field, type, newColor));
    }
    
    private void changeMortgageStatus(boolean newVal) {
        if (!newVal) {
            Player owner = ((PropertyField) field).getOwner();
            changeColor(owner == null ? DEFAULT_COLOR : Color.web(owner.getColor()));
        }
        else changeColor(MORTGAGE_COLOR);
        turnTrans.playFromStart();
    }
    
    public Pane infoPane() {
        return infoPane;
    }
}
