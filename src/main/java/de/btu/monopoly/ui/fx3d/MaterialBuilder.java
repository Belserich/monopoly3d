package de.btu.monopoly.ui.fx3d;

import de.btu.monopoly.core.FieldTypes;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.util.Assets;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import static de.btu.monopoly.ui.fx3d.Fx3dField.*;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class MaterialBuilder {
    
    public static final Color DEFAULT_BACKGROUND_FILL = new Color(205d / 255, 230d / 255, 208d / 255, 1);
    
    private static final Street streetBuilder = new Street();
    private static final Property propertyBuilder = new Property();
    private static final Card cardBuilder = new Card();
    private static final Tax taxBuilder = new Tax();
    private static final Corner cornerBuilder = new Corner();
    
    private static SnapshotParameters params = new SnapshotParameters();
    
    public static Material buildFor(Field field, FieldTypes type, Color color) {
        
        if (params == null) {
            params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
        }
        
        if (type.isStreet()) {
            return streetBuilder.build((StreetField) field, type, color);
        }
        else if (type.isStation() || type.isSupply()) {
            return propertyBuilder.build((PropertyField) field, type, color);
        }
        else if (type.isCard()) {
            return cardBuilder.build((CardField) field, type, color);
        }
        else if (type.isTax()) {
            return taxBuilder.build((TaxField) field, type, color);
        }
        else if (type.isCorner()) {
            return cornerBuilder.build(field, type, color);
        }
        return FxHelper.getMaterialFor(Color.WHITE);
    }
    
    public static Material buildFor(Field field, FieldTypes types) {
        return buildFor(field, types, DEFAULT_BACKGROUND_FILL);
    }
    
    private static abstract class Base<T extends Field> {
        
        private static final double DEFAULT_TEXTURE_WIDTH = FIELD_HEIGHT * 2 + FIELD_WIDTH * 2;
        private static final double DEFAULT_TEXTURE_HEIGHT = FIELD_HEIGHT * 2 + FIELD_DEPTH;
        
        private static final double DEFAULT_FRONT_CENTER_X = FIELD_HEIGHT + FIELD_WIDTH / 2;
        private static final double DEFAULT_FRONT_CENTER_Y = FIELD_HEIGHT + FIELD_DEPTH / 2;
    
        private static final double DEFAULT_BACK_CENTER_X = DEFAULT_TEXTURE_WIDTH - DEFAULT_FRONT_CENTER_X;
        private static final double DEFAULT_BACK_CENTER_Y = DEFAULT_TEXTURE_HEIGHT - DEFAULT_FRONT_CENTER_Y;
        
        protected final double texWidth;
        protected final double texHeight;
        
        protected final double frontCenterX;
        protected final double frontCenterY;
    
        protected final double backCenterX;
        protected final double backCenterY;
        
        private Base(double texWidth, double texHeight, double frontCenterX, double frontCenterY,
                     double backCenterX, double backCenterY) {
            this.texWidth = texWidth;
            this.texHeight = texHeight;
            this.frontCenterX = frontCenterX;
            this.frontCenterY = frontCenterY;
            this.backCenterX = backCenterX;
            this.backCenterY = backCenterY;
        }
        
        private Base() {
            this(DEFAULT_TEXTURE_WIDTH, DEFAULT_TEXTURE_HEIGHT, DEFAULT_FRONT_CENTER_X, DEFAULT_FRONT_CENTER_Y,
                    DEFAULT_BACK_CENTER_X, DEFAULT_BACK_CENTER_Y);
        }
        
        Material build(T field, FieldTypes type, Color color) {
    
            Canvas canv = createFilledCanvas(color);
            GraphicsContext gc = canv.getGraphicsContext2D();
            
            Image img = Assets.getImage(type.name().toLowerCase());
            gc.drawImage(img, 0, 0);
            drawImageAdditions(img, field, type, gc);
    
            prepareGraphicsForText(field, type, gc);
            drawTextAdditions(field, type, gc);
            
            return createMaterial(canv);
        }
        
        void drawImageAdditions(Image img, T field, FieldTypes type, GraphicsContext gc) {
            // zum überschreiben
        }
    
        void prepareGraphicsForText(T field, FieldTypes type, GraphicsContext gc) {
            gc.setLineWidth(Fx3dField.FIELD_WIDTH);
            gc.setFont(Font.font(Assets.FONT_KABEL_FAMILY, 12));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.translate(frontCenterX, frontCenterY);
        }
        
        void drawTextAdditions(T field, FieldTypes type, GraphicsContext gc) {
            // zum überschreiben
        }
        
        String format(String name, boolean singleNs) {
            return name.replace("-", singleNs ? "\n" : "\n\n").toUpperCase();
        }
        
        String format(String name) {
            return format(name, false);
        }
        
        private Canvas createFilledCanvas(Color color) {
            Canvas canv = new Canvas(texWidth, texHeight);
            GraphicsContext gc = canv.getGraphicsContext2D();
            gc.setFill(color);
            gc.fillRect(0, 0, canv.getWidth(), canv.getHeight());
            gc.setFill(Color.BLACK);
            return canv;
        }
        
        private Material createMaterial(Canvas canv) {
            WritableImage writable = new WritableImage((int) canv.getWidth(), (int)canv.getHeight());
            writable = canv.snapshot(params, writable);
            PhongMaterial retObj = new PhongMaterial();
            retObj.setDiffuseMap(writable);
            return retObj;
        }
    }
    
    private static class Property extends Base<PropertyField> {
        
        @Override
        void drawTextAdditions(PropertyField field, FieldTypes type, GraphicsContext gc) {
            gc.fillText(format(field.getName()), 0, -frontCenterY + 30);
            gc.fillText("€" + field.getPrice(), 0, 70);
        }
    }
    
    private static class Street extends Base<StreetField> {
    
        @Override
        void drawTextAdditions(StreetField field, FieldTypes type, GraphicsContext gc) {
            gc.fillText(format(field.getName()), 0, -25);
            gc.fillText("€" + field.getPrice(), 0, 70);
        }
    }
    
    private static class Card extends Base<CardField> {
    
        @Override
        void drawTextAdditions(CardField field, FieldTypes type, GraphicsContext gc) {
            gc.fillText(format(field.getName()), 0, -frontCenterY + 30);
        }
    }
    
    private static class Tax extends Base<TaxField> {
    
        @Override
        void drawTextAdditions(TaxField field, FieldTypes type, GraphicsContext gc) {
            gc.fillText(format(field.getName()), 0, -frontCenterY + 30);
            gc.fillText("€" + field.getTax(), 0, 70);
        }
    }
    
    private static class Corner extends Base<Field> {
    
        private static final double DEFAULT_TEXTURE_WIDTH = Fx3dCorner.FIELD_HEIGHT * 2 + Fx3dCorner.FIELD_WIDTH * 2;
        private static final double DEFAULT_TEXTURE_HEIGHT = Fx3dCorner.FIELD_HEIGHT * 2 + Fx3dCorner.FIELD_DEPTH;
    
        private static final double DEFAULT_FRONT_CENTER_X = Fx3dCorner.FIELD_HEIGHT + Fx3dCorner.FIELD_WIDTH / 2;
        private static final double DEFAULT_FRONT_CENTER_Y = Fx3dCorner.FIELD_HEIGHT + Fx3dCorner.FIELD_DEPTH / 2;
    
        private static final double DEFAULT_BACK_CENTER_X = DEFAULT_TEXTURE_WIDTH - DEFAULT_FRONT_CENTER_X;
        private static final double DEFAULT_BACK_CENTER_Y = DEFAULT_TEXTURE_HEIGHT - DEFAULT_FRONT_CENTER_Y;
        
        Corner() {
            super(DEFAULT_TEXTURE_WIDTH, DEFAULT_TEXTURE_HEIGHT, DEFAULT_FRONT_CENTER_X, DEFAULT_FRONT_CENTER_Y,
                    DEFAULT_BACK_CENTER_X, DEFAULT_BACK_CENTER_Y);
        }
        
        @Override
        void drawTextAdditions(Field field, FieldTypes type, GraphicsContext gc) {
            
            gc.rotate(-45);
            switch (type) {
                case CORNER_0:
                    gc.fillText(format("Ziehen Sie--im vorübergehen--€200 Gehalt ein.", true), 0, -frontCenterY + 30);
                    gc.setFont(Font.font(Assets.FONT_KABEL_FAMILY, 45));
                    gc.fillText(format("Los", true), 0, 20);
                    break;
                case CORNER_1:
                    String[] subs = splitToSize("Im-Gefängnis-Nur zu-Besuch", 4);
                    gc.setFont(Font.font(Assets.FONT_KABEL_FAMILY, 10));
                    gc.fillText(format(subs[0], true), 0, -frontCenterY + 15);
                    gc.fillText(format(subs[1], true), 0, 17);
                    gc.rotate(45);
                    gc.setFont(Font.font(Assets.FONT_KABEL_FAMILY, 12));
                    gc.fillText(format(subs[2], true), -10, 55);
                    gc.rotate(-90);
                    gc.fillText(format(subs[3], true), 10, 55);
                    gc.rotate(90);
                    break;
                case CORNER_2:
                    subs = splitToSize("Frei-Parken", 2);
                    gc.setFont(Font.font(Assets.FONT_KABEL_FAMILY, 18));
                    gc.fillText(format(subs[0], true), 0, -frontCenterY + 30);
                    gc.fillText(format(subs[1], true), 0, 52);
                    break;
                case CORNER_3:
                    subs = splitToSize("Gehen Sie-in das-Gefängnis", 3);
                    gc.setFont(Font.font(Assets.FONT_KABEL_FAMILY, 13));
                    gc.rotate(-90);
                    gc.fillText(format(subs[0], true), 0, -60);
                    gc.rotate(90);
                    gc.fillText(format(subs[1], true), 0, -60);
                    gc.rotate(90);
                    gc.fillText(format(subs[2], true), 0, -60);
            }
            gc.rotate(45);
        }
        
        private String[] splitToSize(String str, int size) {
            String[] subs = str.split("-");
            if (subs.length != size)
                throw new RuntimeException("Name of a corner field doesn't match the general pattern!");
            return subs;
        }
    }
}
