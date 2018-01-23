package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.ui.util.FXHelper;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.Box;
import javafx.scene.transform.Translate;

import static de.btu.monopoly.ui.fx3d.FieldBase.FIELD_DEPTH;
import static de.btu.monopoly.ui.fx3d.FieldBase.FIELD_WIDTH;

public class HouseStack extends Box
{
    private static final Box HOUSE_MODEL = new Box(FIELD_WIDTH - 20, -25, FIELD_DEPTH / 4);
    private static final Translate HOUSE_TRANSLATE = new Translate(0, 0, FIELD_DEPTH / 2 - HOUSE_MODEL.getDepth() / 2);
    
    private static final int MAX_HOUSE_COUNT = 5;
    private static final Material HOUSE_MATERIAL = FXHelper.getMaterialFor(Color.GREEN);
    private static final Material HOTEL_MATERIAL = FXHelper.getMaterialFor(Color.RED);
    
    public HouseStack()
    {
        super(HOUSE_MODEL.getWidth(), 0, HOUSE_MODEL.getDepth());
        getTransforms().add(HOUSE_TRANSLATE);
        setMaterial(HOUSE_MATERIAL);
    }
    
    public void setHouseCount(int count)
    {
        setHeight(-count * HOUSE_MODEL.getHeight());
        setTranslateY(-getHeight() / 2);
        
        if (count >= MAX_HOUSE_COUNT)
            setMaterial(HOTEL_MATERIAL);
        else setMaterial(HOUSE_MATERIAL);
    }
}
