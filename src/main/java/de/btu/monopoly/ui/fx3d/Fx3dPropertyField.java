package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.core.FieldTypes;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.util.Assets;
import de.btu.monopoly.util.InfoPaneBuilder;
import javafx.animation.RotateTransition;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Fx3dPropertyField extends Fx3dField {
    
    private static final int TURN_DURATION_MILLIS = 200;
    private static final double TURN_ANGLE = 180;
    
    private final Pane infoPane;
    
    private final RotateTransition turnTrans;
    
    public Fx3dPropertyField(PropertyField field, FieldTypes type) {
        super(field, type, Assets.getImage(type.name().toLowerCase()));
        infoPane = InfoPaneBuilder.buildFor(field, type);
        
        field.mortgageTakenProperty().addListener(inv -> turnAround());
        turnTrans = new RotateTransition(Duration.millis(TURN_DURATION_MILLIS), this);
        turnTrans.setByAngle(TURN_ANGLE);
        turnTrans.setAxis(Rotate.Z_AXIS);
    }
    
    public void turnAround() {
        turnTrans.playFromStart();
    }
    
    public Pane infoPane() {
        return infoPane;
    }
}
