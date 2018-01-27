package de.btu.monopoly.ui;

import javafx.animation.RotateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import java.awt.geom.Point2D;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class CameraManager {
    
    public enum WatchMode {
        ORTHOGONAL, PERSPECTIVE;
    }
    
    private static final int DEFAULT_NEAR_CLIP = 1;
    private static final int DEFAULT_FAR_CLIP = 5000;
    
    private static final double ORTHO_CAM_Z = -2250;
    private static final Point3D ORTHO_CAM_AXIS = Rotate.X_AXIS;
    private static final double ORTHO_CAM_ANGLE = -90;
    
    private static final double ORTHO_CAM_ROTATE_THRESHOLD = 200;
    private static final double ORTHO_CAM_ROTATE_ANGLE = 90;
    private static final int ORTHO_CAM_ROTATE_MILLIS = 200;
    
    private static final double PREF_NODE_CAM_Z = -2000;
    private static final double MIN_NODE_CAM_Z = PREF_NODE_CAM_Z * 0.75;
    private static final double MAX_NODE_CAM_Z = PREF_NODE_CAM_Z * 1.5;
    
    private static final Point3D NODE_CAM_AXIS = Rotate.X_AXIS;
    private static final double NODE_CAM_ANGLE = -45;
    
    private final SubScene scene;
    
    private final Camera orthoCam;
    private final Camera nodeCam;
    
    private final ObjectProperty<Camera> currCam;
    private final ObjectProperty<Node> watchedNode;
    
    private final DoubleProperty nodeCamDist;
    
    private Point2D.Double dragPoint;
    private boolean transitioning;
    
    public CameraManager(SubScene scene) {
        this.scene = scene;
        
        orthoCam = initCam(
                new Rotate(ORTHO_CAM_ANGLE, ORTHO_CAM_AXIS),
                new Translate(0, 0, ORTHO_CAM_Z)
        );
        
        Translate nodeCamTransl = new Translate(0, 0, PREF_NODE_CAM_Z);
        nodeCamDist = nodeCamTransl.zProperty();
        
        nodeCam = initCam(
                new Rotate(NODE_CAM_ANGLE, NODE_CAM_AXIS),
                nodeCamTransl
        );
        
        currCam = new SimpleObjectProperty<>(orthoCam);
        scene.cameraProperty().bindBidirectional(currCam);
        
        ChangeListener<Transform> translateListener = (prop, oldT, newT) -> {
            Camera cam = currCam.get();
            cam.setTranslateX(newT.getTx());
            cam.setTranslateY(newT.getTy());
            cam.setTranslateZ(newT.getTz());
        };
        
        watchedNode = new SimpleObjectProperty<>(scene);
        watchedNode.addListener((prop, oldN, newN) -> {
            oldN.localToSceneTransformProperty().removeListener(translateListener);
            newN.localToSceneTransformProperty().addListener(translateListener);
        });
        
        initMouseListeners();
    }
    
    private void initMouseListeners() {
        
        scene.setOnMousePressed(this::setDragPoint);
        scene.setOnMouseDragged(event -> {
            Camera cam = currCam.get();
            double delta = event.getScreenX() - dragPoint.getX();
            if (cam == nodeCam) {
                nodeCam.setRotationAxis(Rotate.Y_AXIS);
                nodeCam.setRotate(nodeCam.getRotate() + delta / 2);
                setDragPoint(event);
            }
            else if (Math.abs(delta) >= ORTHO_CAM_ROTATE_THRESHOLD && !transitioning) {
                transitioning = true;
                setDragPoint(event);
                RotateTransition trans = new RotateTransition(Duration.millis(ORTHO_CAM_ROTATE_MILLIS), cam);
                trans.setAxis(Rotate.Y_AXIS);
                trans.setByAngle(Math.signum(delta) * ORTHO_CAM_ROTATE_ANGLE);
                trans.setOnFinished(e -> transitioning = false);
                trans.play();
            }
        });
        scene.setOnScroll(event -> {
            Camera cam = currCam.get();
            if (cam == nodeCam) {
                double delta = event.getDeltaY();
                double newDist = nodeCamDist.get() + delta;
                newDist = Math.max(newDist, MAX_NODE_CAM_Z);
                newDist = Math.min(newDist, MIN_NODE_CAM_Z);
                nodeCamDist.set(newDist);
            }
        });
    }
    
    private Camera initCam(Transform... transforms) {
        Camera cam = new PerspectiveCamera(true);
        cam.setNearClip(DEFAULT_NEAR_CLIP);
        cam.setFarClip(DEFAULT_FAR_CLIP);
        cam.getTransforms().addAll(transforms);
        return cam;
    }
    
    public void watch(Node node, WatchMode mode) {
        watchedNode.set(node);
        currCam.set(mode == WatchMode.ORTHOGONAL ? orthoCam : nodeCam);
    }
    
    private void setDragPoint(MouseEvent event) {
        dragPoint = new Point2D.Double(event.getScreenX(), event.getScreenY());
    }
    
    public SubScene scene() {
        return scene;
    }
    
    public ObjectProperty<Camera> currCamProperty() {
        return currCam;
    }
    
    public ObjectProperty<Node> watchedNodeProperty() { return watchedNode; }
}
