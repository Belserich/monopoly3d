package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.ui.util.Assets;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Fx3dPropertyField extends Fx3dField {
    
    private static final int TURN_DURATION_MILLIS = 200;
    private static final double TURN_ANGLE = 180;
    
    private final DoubleProperty zRotateAngle;
    
    public Fx3dPropertyField(PropertyField field, Fx3dFieldType type) {
        super(field, Assets.getImage(type));
        
        Rotate zRotate = new Rotate(0, Rotate.Z_AXIS);
        zRotateAngle = zRotate.angleProperty();
        getTransforms().add(zRotate);
        
        field.mortgageTakenProperty().addListener(prop -> turnAround());
    }
    
    public void turnAround() {
        Timeline anim = new Timeline(
                new KeyFrame(Duration.millis(TURN_DURATION_MILLIS),
                        new KeyValue(zRotateAngle, zRotateAngle.get() + TURN_ANGLE))
        );
        anim.play();
    }
}
