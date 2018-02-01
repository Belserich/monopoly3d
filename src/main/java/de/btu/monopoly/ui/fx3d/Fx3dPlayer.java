package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.data.player.Player;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.text.Font;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Fx3dPlayer extends Group {
    
    public static final double FIELD_MOVE_DURATION = 400;
    
    private static final Cylinder PLAYER_MODEL = new Cylinder(20, 50);
    private static final double INIT_PLAYER_Y = -PLAYER_MODEL.getHeight();
    private static final double JUMP_HEIGHT = -100;
    
    IntegerProperty positionProperty;
    
    private final Cylinder shape;
    
    private Player player;
    private Color color;
    
    private InfoPane infoPane;
    
    public Fx3dPlayer(Player player) {
        super();
        this.player = player;
        this.color = Color.web(player.getColor());
    
        shape = new Cylinder(PLAYER_MODEL.getRadius(), PLAYER_MODEL.getHeight());
        getChildren().add(shape);
        
        TranslateTransition hoverTransition = new TranslateTransition(Duration.millis(400), shape);
        hoverTransition.setByY(-30);
        hoverTransition.setAutoReverse(true);
        hoverTransition.setCycleCount(Animation.INDEFINITE);
        hoverTransition.play();
        
        getTransforms().add(new Translate(0, INIT_PLAYER_Y, 0));
        shape.setMaterial(FxHelper.getMaterialFor(color));
        
        infoPane = new InfoPane(color);
        
        positionProperty = player.positionProperty();
    }
    
    TranslateTransition createJumpAnimation() {
        TranslateTransition la = new TranslateTransition(
                Duration.millis(FIELD_MOVE_DURATION / 2), shape);
        la.setByY(JUMP_HEIGHT);
        la.setAutoReverse(true);
        return la;
    }
    
    public InfoPane infoPane() {
        return infoPane;
    }
    
    Player player() {
        return player;
    }
    
    private class InfoPane extends HBox {
        
        private static final double PANE_WIDTH = 230;
        private static final double PANE_HEIGHT = 60;
        
        private static final double BOX_WIDTH = PANE_HEIGHT;
        private static final double BOX_HEIGHT = PANE_HEIGHT;
        
        private static final double COLORED_SQUARE_LENGTH = PANE_HEIGHT - 10;
        private static final double MAX_TEXT_WIDTH = PANE_WIDTH - 60;
        
        private Canvas canv;
        
        private PhongMaterial material;
        private Color brighterColor;
        
        private InfoPane(Color color) {
            super();
            this.material = (PhongMaterial) Fx3dPlayer.this.shape.getMaterial();
            this.brighterColor = material.getDiffuseColor().brighter();
            
            canv = new Canvas(PANE_WIDTH, PANE_HEIGHT);
            drawCanvas();
            player.balanceProperty().addListener(prop -> drawCanvas());
            
            getChildren().addAll(canv);
            setStyle("-fx-background-color: #ffffffaa; -fx-background-radius: 5");
            
            setOnMouseEntered(event -> material.setDiffuseColor(brighterColor) );
            setOnMouseExited(event -> material.setDiffuseColor(color));
        }
        
        public Color brighterColor() {
            return brighterColor;
        }
        
        private void drawCanvas() {
            GraphicsContext gc = canv.getGraphicsContext2D();
            gc.clearRect(0, 0, canv.getWidth(), canv.getHeight());
            
            gc.setFill(color);
            gc.setFont(Font.font("Kabel", 16));
    
            gc.fillRoundRect(5, 5, COLORED_SQUARE_LENGTH, COLORED_SQUARE_LENGTH, 5, 5);
    
            gc.setTextBaseline(VPos.TOP);
            gc.fillText(player.getName().toUpperCase(), COLORED_SQUARE_LENGTH + 10, 10, MAX_TEXT_WIDTH);
            
            gc.setTextBaseline(VPos.BASELINE);
            gc.fillText(player.getMoney() + "€", COLORED_SQUARE_LENGTH + 10, PANE_HEIGHT - 10);
    
            gc.save();
        }
    }
}
