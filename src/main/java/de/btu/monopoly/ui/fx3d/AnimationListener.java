package de.btu.monopoly.ui.fx3d;

import javafx.scene.Node;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public interface AnimationListener {
    
    void onStartAnimation(Node node);
    
    void onEndAnimation(Node node);
}
