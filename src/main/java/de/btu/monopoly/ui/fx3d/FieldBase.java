package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.ui.util.Cuboid;
import de.btu.monopoly.ui.util.FXHelper;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class FieldBase extends Cuboid
{
    public static final double FIELD_WIDTH = 98;
    public static final double FIELD_HEIGHT = 4;
    public static final double FIELD_DEPTH = 158;
    
    private final Image texture;
    
    public FieldBase(Image texture)
    {
        super(FIELD_WIDTH, FIELD_HEIGHT, FIELD_DEPTH);
        this.texture = texture;
        
        setMaterial(texture != null ? FXHelper.getMaterialFor(texture) : FXHelper.getMaterialFor(Color.WHITE));
    }
    
    public Image getTexture()
    {
        return texture;
    }
}
