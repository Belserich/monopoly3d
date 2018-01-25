package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.ui.util.Assets;
import de.btu.monopoly.ui.util.TransitionAffine;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Fx3dPropertyField extends Fx3dField {
    
    private final TransitionAffine turnTransition =
            TransitionAffine.rotate(Duration.millis(500), 180, Rotate.Z_AXIS);
    
    public Fx3dPropertyField(PropertyField field, FieldType type) {
        super(field, Assets.getImage(type));
        
        getTransforms().add(turnTransition);
        field.mortgageTakenProperty().addListener(prop -> turnAround());
    }
    
    public void turnAround() {
        turnTransition.timeline().playFromStart();
    }
}
