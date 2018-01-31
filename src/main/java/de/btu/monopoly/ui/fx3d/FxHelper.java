package de.btu.monopoly.ui.fx3d;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;

public class FxHelper
{
    public static Material getMaterialFor(Color color)
    {
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseColor(color);
        return mat;
    }
    
    public static Material getMaterialFor(Image img)
    {
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseMap(img);
        return mat;
    }
    
    public static WritableImage replaceColorInImage(Image image, Color oldColor, Color newColor)
    {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        
        PixelReader reader = image.getPixelReader();
        WritableImage newImage = new WritableImage(width, height);
        PixelWriter writer = newImage.getPixelWriter();
        
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                Color color = reader.getColor(x, y);
                if (color.equals(oldColor))
                    writer.setColor(x, y, newColor);
                else writer.setColor(x, y, color);
            }
        }
        
        return newImage;
    }
}
