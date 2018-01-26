package de.btu.monopoly.ui.fx3d;

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
    
    private static final double ORTHO_CAM_Y = -2250;
    private static final Point3D ORTHO_CAM_AXIS = Rotate.X_AXIS;
    private static final double ORTHO_CAM_ANGLE = -90;
    
    private static final double NODE_CAM_Y = -1500;
    private static final double NODE_CAM_Z = -1500;
    private static final Point3D NODE_CAM_AXIS = Rotate.X_AXIS;
    private static final double NODE_CAM_ANGLE = -45;
    
    private final SubScene scene;
    
    private final Camera orthoCam;
    private final Camera nodeCam;
    
    private final ObjectProperty<Camera> currCam;
    private final ObjectProperty<Node> watchedNode;
    
    private Point2D.Double dragPoint;
    
    public CameraManager(SubScene scene) {
        this.scene = scene;
        
        orthoCam = initCam(
                new Translate(0, ORTHO_CAM_Y, 0),
                new Rotate(ORTHO_CAM_ANGLE, ORTHO_CAM_AXIS)
        );
    
        nodeCam = initCam(
                new Translate(0, NODE_CAM_Y, NODE_CAM_Z),
                new Rotate(NODE_CAM_ANGLE, NODE_CAM_AXIS)
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
        
        scene.setOnMousePressed(this::setDragPoint);
        scene.setOnMouseDragged(event -> {
            double deltaX = event.getScreenX() - dragPoint.getX();
            Camera cam = currCam.get();
            cam.setRotationAxis(Rotate.Y_AXIS);
            cam.setRotate(cam.getRotate() + deltaX / 2);
            setDragPoint(event);
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
    
    public ObjectProperty<Camera> currCamProperty() {
        return currCam;
    }
    
    public ObjectProperty<Node> watchedNodeProperty() { return watchedNode; }
}
