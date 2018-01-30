package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.core.FieldTypes;
import de.btu.monopoly.data.field.Field;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Fx3dField extends Cuboid
{
    public static final double FIELD_WIDTH = 98;
    public static final double FIELD_DEPTH = 158;
    public static final double FIELD_HEIGHT = 10;
    
    private static final int ANIM_DURATION_MILLIS = 200;
    private static final double RAISE_HEIGHT = 30;
    
    protected final Field field;
    protected final FieldTypes type;
    protected Image texture;
    
    private final TranslateTransition reactTrans;
    
    public Fx3dField(Field field, FieldTypes type, Image texture) {
        this(field, type, texture, FIELD_WIDTH, FIELD_HEIGHT, FIELD_DEPTH);
    }
    
    Fx3dField(Field field, FieldTypes type, Image texture, double width, double height, double depth) {
        super(width, height, depth);
        this.field = field;
        this.type = type;
        this.texture = texture;
    
        reactTrans = new TranslateTransition(Duration.millis(ANIM_DURATION_MILLIS), this);
        reactTrans.setFromY(-RAISE_HEIGHT);
        reactTrans.setToY(0);
        
        addEventHandler(MouseEvent.MOUSE_ENTERED, event -> react());
        setMaterial(texture != null ? FxHelper.getMaterialFor(texture) : FxHelper.getMaterialFor(Color.WHITE));
    }
    
    private void react() {
        reactTrans.play();
    }
}
