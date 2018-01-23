package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.ui.util.Cuboid;
import de.btu.monopoly.ui.util.FXHelper;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class CornerBase extends Cuboid
{
    private final Image texture;
    
    public CornerBase(Image texture)
    {
        super(FieldBase.FIELD_DEPTH, FieldBase.FIELD_HEIGHT, FieldBase.FIELD_DEPTH);
        this.texture = texture;
    
        setMaterial(texture != null ? FXHelper.getMaterialFor(texture) : FXHelper.getMaterialFor(Color.WHITE));
    }
    
    public Image getTexture()
    {
        return texture;
    }
}
