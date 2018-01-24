package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.ui.util.FxHelper;
import javafx.beans.property.IntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Cylinder;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Fx3dPlayer extends Cylinder {
    
    private static final Cylinder PLAYER_MODEL = new Cylinder(20, 25);
    
    private static final double DEFAULT_MOVE_DIST_X = -(Fx3dField.FIELD_WIDTH / 2 + Fx3dCorner.FIELD_WIDTH / 2);
    private static final double CORNER_MOVE_DIST_X = -Fx3dField.FIELD_WIDTH;
    
    private Player player;
    private Color color;
    
    private int lastPos;
    
    public Fx3dPlayer(Player player, Color color) {
        
        super(PLAYER_MODEL.getRadius(), PLAYER_MODEL.getHeight());
        this.player = player;
        this.color = color;
        
        init();
    }
    
    private void init() {
        setMaterial(FxHelper.getMaterialFor(color));
    }
    
    public IntegerProperty getPosition() {
        return player.positionProperty();
    }
}
