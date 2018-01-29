package de.btu.monopoly.util;

import de.btu.monopoly.core.FieldTypes;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.field.TaxField;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import static de.btu.monopoly.ui.fx3d.Fx3dField.*;

public class TextUtils
{
    private static final String FIELD_NAME_KEY = "field_%d_name";
    private static final String FIELD_PRICE_KEY = "field_%d_price";
    
    private static final double LINE_WRAPPING_WIDTH = FIELD_WIDTH - 10;
    
    private static final double FIELD_TEX_CENTER_X = FIELD_HEIGHT + FIELD_WIDTH / 2;
    private static final double FIELD_TEX_CENTER_Y = FIELD_HEIGHT + FIELD_DEPTH / 2;
    
    private static final double FIELD_MAX_TEXT_WIDTH = FIELD_WIDTH - 10;
    
    private static final double STREET_NAME_OFF = -25;
    private static final double STREET_PRICE_OFF = 2.7 * -STREET_NAME_OFF;
    
    public static void drawText(GraphicsContext context, Field field, FieldTypes type)
    {
        context.setFont(Font.font("Kabel", 14));
        context.setTextAlign(TextAlignment.CENTER);
        context.setTextBaseline(VPos.CENTER);
        context.setLineWidth(FIELD_MAX_TEXT_WIDTH);
        context.translate(FIELD_TEX_CENTER_X, FIELD_TEX_CENTER_Y);
        
        if (type.isStreet() || type.isStation() || type.isSupply()) {
            PropertyField prop = (PropertyField) field;
            context.fillText(String.valueOf(prop.getPrice()), 0, STREET_PRICE_OFF);
            
            if (type.isStreet()) {
                context.fillText(prop.getName(), 0, STREET_NAME_OFF);
            }
            else if (type.isStation() || type.isSupply()) {
                context.setFont(Font.font("Kabel", 16));
                context.fillText(prop.getName(), 0, STREET_NAME_OFF - 35);
            }
        }
        else if (type.isCard() || type.isTax()) {
            
            if (type.isTax()) {
                TaxField tax = (TaxField) field;
                context.fillText(String.valueOf(tax.getTax()), 0, STREET_PRICE_OFF);
            }
            
            context.setFont(Font.font("Kabel", 16));
            context.fillText(field.getName(), 0, STREET_NAME_OFF - 35);
        }
    }
}
