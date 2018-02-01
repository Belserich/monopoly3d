package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.core.FieldTypes;
import de.btu.monopoly.data.field.StreetField;
import de.btu.monopoly.ui.AnimationQueuer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Fx3dStreetField extends Fx3dPropertyField {
    
    private Fx3dHouse house;
    
    public Fx3dStreetField(StreetField field, FieldTypes type, AnimationQueuer queuer) {
        super(field, type, queuer);
        this.house = new Fx3dHouse();
        house.setTranslateZ(FIELD_DEPTH / 2 - house.getDepth() / 2);
        
        field.houseCountProperty().addListener((prop, oldI, newI) ->
                Platform.runLater(() -> queuer.queueAnimation(pauseAndChangeHouseCount(newI.intValue())))
        );
    }
    
    private PauseTransition pauseAndChangeHouseCount(int newI) {
        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(inv -> houseCountChanged(newI));
        return pause;
    }
    
    private void houseCountChanged(int newI) {
        house.setHouseCount(newI);
    }
    
    public Fx3dHouse house() {
        return house;
    }
}
