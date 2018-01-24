package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.ui.util.FxHelper;
import javafx.scene.paint.Color;
import javafx.scene.shape.Cylinder;

public class PlayerBase extends Cylinder
{
    private static final double PLAYER_RADIUS = 20;
    private static final double PLAYER_HEIGHT = 50;
    
    private int position;
    
    public PlayerBase(Color color)
    {
        super(PLAYER_RADIUS, PLAYER_HEIGHT);
        setMaterial(FxHelper.getMaterialFor(color));
        
        position = 0;
    }
    
    public int getPosition()
    {
        return position;
    }
    
    public void setPosition(int position)
    {
        this.position = position;
    }
}
