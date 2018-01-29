package de.btu.monopoly.ui.fx3d;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class MultiCanvas {
    
    private Canvas[] layers;
    private Canvas parent;
    private SnapshotParameters params;
    
    private double width;
    private double height;
    
    public MultiCanvas(int numLayers, double width, double height) {
        if (numLayers <= 0) {
            throw new IllegalArgumentException(String.format("Invalid number of layers %d", numLayers));
        }
        layers = new Canvas[numLayers];
        this.width = width;
        this.height = height;
        
        for (int i = 0; i < numLayers; i++) {
            Canvas canv = new Canvas(width, height);
            canv.getGraphicsContext2D().clearRect(0, 0, width, height);
            layers[i] = canv;
        }
        parent = new Canvas(width, height);
        
        params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
    }
    
    private void rangeCheck(int index) {
        if (index < 0 || index >= layers.length) {
            throw new ArrayIndexOutOfBoundsException(String.format("index: %d length: %d", 0, layers.length));
        }
    }
    
    public GraphicsContext getGraphicsContext(int index) {
        rangeCheck(index);
        return layers[index].getGraphicsContext2D();
    }
    
    public Canvas getCanvas(int index) {
        rangeCheck(index);
        return layers[index];
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setBlendMode(int index, BlendMode mode) {
        rangeCheck(index);
        layers[index].setBlendMode(mode);
    }
    
    public void setImage(Image img, int index) {
        rangeCheck(index);
        layers[index].getGraphicsContext2D().drawImage(img, 0, 0, width, height);
    }
    
    public WritableImage getImage(int index) {
        rangeCheck(index);
        return layers[index].snapshot(params, createWritableImage());
    }
    
    public WritableImage blend(int... indices) {
        GraphicsContext context = parent.getGraphicsContext2D();
        for (int i = 0; i < indices.length; i++)
            drawImage(getImage(indices[i]), context);
        return snapshot();
    }
    
    public WritableImage blend() {
        GraphicsContext context = parent.getGraphicsContext2D();
        for (int i = 0; i < layers.length; i++) {
            drawImage(getImage(i), context);
        }
        return snapshot();
    }
    
    private WritableImage snapshot() {
        WritableImage img = parent.snapshot(params, createWritableImage());
        cleanContext();
        return img;
    }
    
    private void drawImage(WritableImage img, GraphicsContext context) {
        context.drawImage(img, 0, 0, width, height);
    }
    
    private WritableImage createWritableImage() {
        return new WritableImage((int) width, (int) height);
    }
    
    private void cleanContext() {
        GraphicsContext context = parent.getGraphicsContext2D();
        context.clearRect(0, 0, width, height);
    }
}
