package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.ui.util.FxHelper;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Fx3dCorner extends Fx3dField
{
    public static final double FIELD_WIDTH = Fx3dField.FIELD_DEPTH;
    public static final double FIELD_HEIGHT = Fx3dField.FIELD_HEIGHT;
    public static final double FIELD_DEPTH = Fx3dField.FIELD_DEPTH;
    
    private final Field field;
    private final Image texture;
    
    public Fx3dCorner(Field field, Image texture) {
        super(field, texture, FIELD_WIDTH, FIELD_HEIGHT, FIELD_DEPTH);
        this.field = field;
        this.texture = texture;
        
        setMaterial(texture != null ? FxHelper.getMaterialFor(texture) : FxHelper.getMaterialFor(Color.WHITE));
    }
}