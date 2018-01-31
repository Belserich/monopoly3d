package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.core.FieldTypes;
import de.btu.monopoly.data.field.Field;
import javafx.scene.image.Image;

public class Fx3dCorner extends Fx3dField
{
    public static final double FIELD_WIDTH = Fx3dField.FIELD_DEPTH;
    public static final double FIELD_HEIGHT = Fx3dField.FIELD_HEIGHT;
    public static final double FIELD_DEPTH = Fx3dField.FIELD_DEPTH;
    
    private final Field field;
    private final Image texture;
    
    public Fx3dCorner(Field field, FieldTypes type, Image texture) {
        super(field, type, texture, FIELD_WIDTH, FIELD_HEIGHT, FIELD_DEPTH);
        this.field = field;
        this.texture = texture;
    }
}