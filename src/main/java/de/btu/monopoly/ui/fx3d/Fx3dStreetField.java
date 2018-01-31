package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.core.FieldTypes;
import de.btu.monopoly.data.field.StreetField;
import javafx.application.Platform;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Fx3dStreetField extends Fx3dPropertyField {
    
    private Fx3dHouse house;
    
    public Fx3dStreetField(StreetField field, FieldTypes type) {
        super(field, type);
        this.house = new Fx3dHouse();
        house.setTranslateZ(FIELD_DEPTH / 2 - house.getDepth() / 2);
        
        field.houseCountProperty().addListener((prop, oldI, newI) ->
                Platform.runLater(() -> houseCountChanged(oldI.intValue(), newI.intValue())));
    }
    
    private void houseCountChanged(int oldI, int newI) {
        
        house.setHouseCount(newI);
    }
    
    public Fx3dHouse house() {
        return house;
    }
}
