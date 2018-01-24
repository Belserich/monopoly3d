package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.ui.util.Cuboid;
import de.btu.monopoly.ui.util.FxHelper;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Fx3dField extends Cuboid
{
    public static final double FIELD_WIDTH = 98;
    public static final double FIELD_HEIGHT = 4;
    public static final double FIELD_DEPTH = 158;
    
    private final Field field;
    private final Image texture;
    
    public Fx3dField(Field field, Image texture) {
        super(FIELD_WIDTH, FIELD_HEIGHT, FIELD_DEPTH);
        this.field = field;
        this.texture = texture;
        
        setMaterial(texture != null ? FxHelper.getMaterialFor(texture) : FxHelper.getMaterialFor(Color.WHITE));
    }
}
