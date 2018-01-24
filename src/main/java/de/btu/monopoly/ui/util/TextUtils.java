package de.btu.monopoly.ui.util;

import de.btu.monopoly.ui.fx3d.FieldType;
import de.btu.monopoly.ui.fx3d.MonopolyBoard;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import static de.btu.monopoly.ui.fx3d.Fx3dField.FIELD_HEIGHT;
import static de.btu.monopoly.ui.fx3d.Fx3dField.FIELD_WIDTH;

public class TextUtils
{
    private static final Rotate TEXT_ROTATE = new Rotate(-90, Rotate.X_AXIS);
    private static final Transform CORNER_TEXT_ROTATE = TEXT_ROTATE;
    
    private static final String FIELD_NAME_KEY = "field_%d_name";
    private static final String FIELD_PRICE_KEY = "field_%d_price";
    
    private static final double LINE_WRAPPING_WIDTH = FIELD_WIDTH - 10;
    
    private static final Transform FIELD_NAME_TRANSFORM_0 = new Translate(0, -FIELD_HEIGHT, 20).createConcatenation(TEXT_ROTATE);
    private static final Transform FIELD_NAME_TRANSFORM_1 = new Translate(0, -FIELD_HEIGHT, 55).createConcatenation(TEXT_ROTATE);
    private static final Transform FIELD_PRICE_TRANSFORM = new Translate(0, -FIELD_HEIGHT, -70).createConcatenation(TEXT_ROTATE);
    
    private static final Transform BASIC_CORNER_TRANSFORM = TEXT_ROTATE.createConcatenation(CORNER_TEXT_ROTATE);
    
    public static Group createFieldTexts(int fieldId)
    {
        String str = Assets.getString(String.format(FIELD_NAME_KEY, fieldId));
        Group group = new Group();
        
        FieldType type = FieldType.GAMEBOARD_FIELD_STRUCTURE[fieldId];
        if (!type.isCorner() && !type.isCard())
        {
            Text name = createText(str);
            name.getTransforms().add(type.isStreet() ? FIELD_NAME_TRANSFORM_0 : FIELD_NAME_TRANSFORM_1);
    
            Text price = createText(Assets.getString(String.format(FIELD_PRICE_KEY, fieldId)));
            price.getTransforms().add(FIELD_PRICE_TRANSFORM);
    
            if (type.isStation() || type.isSupply())
                name.setFont(Assets.getFont("font_big"));
            else if (type.isTax())
                name.setFont(Assets.getFont("font_huge"));
            
            group.getChildren().addAll(name, price);
        }
        else if (type.isCard())
        {
            Text name = createText(str);
            name.getTransforms().add(FIELD_NAME_TRANSFORM_1);
            group.getChildren().add(name);
        }
        else
        {
            int delim = str.lastIndexOf(' ');
            delim = delim == -1 ? 0 : delim;
            if (type == FieldType.CORNER_0)
            {
                Text text = createText(str.substring(0, delim));
                text.setFont(Assets.getFont("font_default"));
                
                Text name = createText(str.substring(delim));
                name.setFont(Assets.getFont("font_huge"));
                
                group.getChildren().addAll(text, name);
                group.getTransforms().add(FIELD_NAME_TRANSFORM_1);
            }
            else group.getChildren().add(createText(str));
        }
        
        return group;
    }
    
    private static void rangeCheck(int fieldId)
    {
        if (fieldId < 0 || fieldId > MonopolyBoard.FIELD_COUNT)
            throw new IllegalArgumentException(String.format("id (%d) out of range!", fieldId));
    }
    
    private static Text createText(String content)
    {
        Text text = new Text(content);
        text.setWrappingWidth(LINE_WRAPPING_WIDTH);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Assets.getFont("font_default"));
        text.setTranslateX(-text.getLayoutBounds().getWidth() / 2);
        return text;
    }
}
