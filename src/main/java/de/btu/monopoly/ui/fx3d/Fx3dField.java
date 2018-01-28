package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.data.field.Field;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Fx3dField extends Cuboid
{
    public static final double FIELD_WIDTH = 94;
    public static final double FIELD_DEPTH = 158;
    public static final double FIELD_HEIGHT = 4;
    
    private static final int ANIM_DURATION_MILLIS = 200;
    private static final double RAISE_HEIGHT = 30;
    
    private final Field field;
    private final Image texture;
    
    private final TranslateTransition hoverTransition;
    
    public Fx3dField(Field field, Image texture) {
        this(field, texture, FIELD_WIDTH, FIELD_HEIGHT, FIELD_DEPTH);
    }
    
    Fx3dField(Field field, Image texture, double width, double height, double depth) {
        super(width, height, depth);
        this.field = field;
        this.texture = texture;
    
        hoverTransition = new TranslateTransition(Duration.millis(ANIM_DURATION_MILLIS), this);
        hoverTransition.setFromY(-RAISE_HEIGHT);
        hoverTransition.setToY(0);
        
        setOnMouseEntered(event -> raise());
        setMaterial(texture != null ? FxHelper.getMaterialFor(texture) : FxHelper.getMaterialFor(Color.WHITE));
    }
    
    private void raise() {
        hoverTransition.play();
    }
}
