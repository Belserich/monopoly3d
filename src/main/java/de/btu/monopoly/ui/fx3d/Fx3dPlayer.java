package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.data.player.Player;
import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
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
public class Fx3dPlayer extends Cylinder {
    
    public static final double FIELD_MOVE_DURATION = 400;
    
    private static final Cylinder PLAYER_MODEL = new Cylinder(20, 50);
    private static final double INIT_PLAYER_Y = -PLAYER_MODEL.getHeight();
    private static final double JUMP_HEIGHT = 100;
    
    private Player player;
    private Color color;
    
    private InfoPane infoPane;
    
    public Fx3dPlayer(Player player, Color color) {
        
        super(PLAYER_MODEL.getRadius(), PLAYER_MODEL.getHeight());
        this.player = player;
        this.color = color;
        
        TranslateTransition tt = new TranslateTransition(Duration.millis(400), this);
        tt.setByY(-20);
        tt.setAutoReverse(true);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.play();
        
        getTransforms().add(new Translate(0, INIT_PLAYER_Y, 0));
        setMaterial(FxHelper.getMaterialFor(color));
        
        infoPane = new InfoPane();
    }
    
    TranslateTransition createJumpAnimation() {
        TranslateTransition jt = new TranslateTransition(
                Duration.millis(FIELD_MOVE_DURATION / 2), this);
        jt.setByY(-JUMP_HEIGHT);
        jt.setAutoReverse(true);
        return jt;
    }
    
    public InfoPane infoPane() {
        return infoPane;
    }
    
    Player player() {
        return player;
    }
    
    private class InfoPane extends HBox {
        
        private static final double PANE_WIDTH = 210;
        private static final double PANE_HEIGHT = 60;
        
        private static final double BOX_WIDTH = PANE_HEIGHT;
        private static final double BOX_HEIGHT = PANE_HEIGHT;
        
        private static final double COLORED_SQUARE_LENGTH = PANE_HEIGHT - 10;
        private static final double MAX_TEXT_WIDTH = PANE_WIDTH - 60;
        
        private Canvas canv;
        
        private PhongMaterial material;
        private Color brighterColor;
        
        private ObservableList<Node> iconList;
        
        private InfoPane() {
            super();
            this.material = (PhongMaterial) Fx3dPlayer.this.getMaterial();
            this.brighterColor = material.getDiffuseColor().brighter();
            
            canv = new Canvas(PANE_WIDTH, PANE_HEIGHT);
            drawCanvas();
            player.balanceProperty().addListener(prop -> drawCanvas());
            
            FlowPane iconPane = new FlowPane(Orientation.HORIZONTAL);
            iconList = iconPane.getChildren();
            iconPane.setPrefSize(BOX_WIDTH, BOX_HEIGHT);
            
            getChildren().addAll(canv, iconPane);
            setStyle(
                    "-fx-background-color: #ffffff33;" +
                            "-fx-background-radius: 5;"
            );
            
            setOnMouseEntered(event -> material.setDiffuseColor(brighterColor) );
            setOnMouseExited(event -> material.setDiffuseColor(color));
        }
        
        private void drawCanvas() {
            GraphicsContext g = canv.getGraphicsContext2D();
            g.clearRect(0, 0, canv.getWidth(), canv.getHeight());
            
            g.setFill(color);
            g.setFont(Font.font("Kabel", 16));
    
            g.fillRoundRect(5, 5, COLORED_SQUARE_LENGTH, COLORED_SQUARE_LENGTH, 5, 5);
    
            g.setTextBaseline(VPos.TOP);
            g.fillText(player.getName().toUpperCase(), COLORED_SQUARE_LENGTH + 10, 10, MAX_TEXT_WIDTH);
            
            g.setTextBaseline(VPos.BASELINE);
            g.fillText(player.getMoney() + "€", COLORED_SQUARE_LENGTH + 10, PANE_HEIGHT - 10);
    
            g.save();
        }
        
        public void addIcon(ImageView icon) {
            iconList.add(icon);
        }
        
        public void removeIcon(ImageView icon) {
            iconList.remove(icon);
        }
    }
}
