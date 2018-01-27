package de.btu.monopoly.ui.util;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;

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
    
    public static Image ensureTextureSize(Image texture, int width, int height)
    {
        if (texture.getWidth() != width || texture.getHeight() != width)
        {
            PixelReader reader = texture.getPixelReader();
            WritableImage newTex = new WritableImage(width, height);
            PixelWriter writer = newTex.getPixelWriter();
            
            int maxX = Math.min((int) texture.getWidth(), width);
            int maxY = Math.min((int) texture.getHeight(), height);
            
            for (int x = 0; x < maxX; x++)
            {
                for (int y = 0; y < maxY; y++)
                {
                    writer.setColor(x, y, reader.getColor(x, y));
                }
            }
            return newTex;
        }
        return texture;
    }
    
    public static Image ensureTextureSize(Image texture, Cuboid cub)
    {
        double cubHeight = cub.getHeight();
        double texWidth = cub.getWidth() * 2 + cubHeight * 2;
        double texHeight = cub.getDepth() + 2 * cubHeight;
        
        return ensureTextureSize(texture, (int) texWidth, (int) texHeight);
    }
    
    public static Node initOrigins()
    {
        Sphere origin = new Sphere(10);
        origin.setMaterial(getMaterialFor(Color.BLACK));
        
        Sphere xSph = new Sphere(10);
        xSph.setTranslateX(200);
        xSph.setMaterial(getMaterialFor(Color.BLUE));
        
        Sphere ySph = new Sphere(10);
        ySph.setTranslateY(200);
        ySph.setMaterial(getMaterialFor(Color.GREEN));
    
        Sphere zSph = new Sphere(10);
        zSph.setTranslateZ(200);
        zSph.setMaterial(getMaterialFor(Color.RED));
        
        return new Group(origin, xSph, ySph, zSph);
    }
    
    public static Group initAxes()
    {
        Box xBox = new Box(800, 2, 2);
        xBox.setMaterial(getMaterialFor(Color.BLUE));
        
        Box yBox = new Box(2, 800, 2);
        yBox.setMaterial(getMaterialFor(Color.GREEN));
        
        Box zBox = new Box(2, 2, 800);
        zBox.setMaterial(getMaterialFor(Color.RED));
        
        return new Group(xBox, yBox, zBox);
    }
    
    public static WritableImage replaceColorInImage(WritableImage image, Color oldColor, Color newColor)
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
