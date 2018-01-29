package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.util.TextUtils;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
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
    protected final Fx3dFieldType type;
    protected Image texture;
    
    private final TranslateTransition hoverTransition;
    
    public Fx3dField(Field field, Fx3dFieldType type, Image texture) {
        this(field, type, texture, FIELD_WIDTH, FIELD_HEIGHT, FIELD_DEPTH);
    }
    
    Fx3dField(Field field, Fx3dFieldType type, Image texture, double width, double height, double depth) {
        super(width, height, depth);
        this.field = field;
        this.type = type;
        this.texture = texture;
    
        hoverTransition = new TranslateTransition(Duration.millis(ANIM_DURATION_MILLIS), this);
        hoverTransition.setFromY(-RAISE_HEIGHT);
        hoverTransition.setToY(0);
        
        setOnMouseEntered(event -> raise());
        init();
    }
    
    protected void init() {
        MultiCanvas multi = new MultiCanvas(2, texture.getWidth(), texture.getHeight());
        multi.setImage(texture, 0);
        TextUtils.drawText(multi.getGraphicsContext(1), field, type);
        
        this.texture = multi.blend();
        
        setMaterial(texture != null ? FxHelper.getMaterialFor(texture) : FxHelper.getMaterialFor(Color.WHITE));
    }
    
    private void raise() {
        hoverTransition.play();
    }
}
