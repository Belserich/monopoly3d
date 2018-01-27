package de.btu.monopoly.ui.util;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.transform.Affine;
import javafx.util.Duration;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class TransitionAffine extends Affine {
    
    public static final double TARGET_FRAMERATE = 60;
    
    private Timeline timeline;
    private DoubleProperty prop;
    
    private TransitionAffine(Duration dur, double endVal) {
        prop = new SimpleDoubleProperty();
        timeline = new Timeline(TARGET_FRAMERATE, new KeyFrame(dur, new KeyValue(prop, endVal)));
    }
    
    public static TransitionAffine rotate(Duration dur, double angle, double pivX, double pivY, double pivZ, Point3D axis) {
        TransitionAffine trans = new TransitionAffine(dur, angle);
        trans.prop.addListener((p, oldV, newV) -> trans.appendRotation(newV.intValue() - oldV.intValue(), pivX, pivY, pivZ, axis));
        return trans;
    }
    
    public static TransitionAffine rotate(Duration dur, double angle, Point3D axis) {
        return rotate(dur, angle, 0, 0, 0, axis);
    }
    
    public Timeline timeline() {
        return timeline;
    }
}
