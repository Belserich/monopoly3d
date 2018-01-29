package de.btu.monopoly.ui.fx3d;

import javafx.collections.ObservableFloatArray;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

public class Cuboid extends MeshView
{
    private final TriangleMesh mesh;
    
    private final double width;
    private final double height;
    private final double depth;
    
    public Cuboid(double width, double height, double depth)
    {
        super();
        
        mesh = new TriangleMesh(VertexFormat.POINT_TEXCOORD);
        
        this.width = width;
        this.height = height;
        this.depth = depth;
        
        init();
    }
    
    private void init()
    {
        float hw = (float) (width / 2);
        float hh = (float) (height / 2);
        float hd = (float) (depth / 2);
        
        ObservableFloatArray points = mesh.getPoints();
        points.addAll(
                -hw, -hh, hd,
                -hw, -hh, -hd,
                hw, -hh, -hd,
                hw, -hh, -hd,
                hw, -hh, hd,
                -hw, -hh, hd,
        
                -hw, hh, hd,
                hw, hh, -hd,
                -hw, hh, -hd,
                hw, hh, -hd,
                -hw, hh, hd,
                hw, hh, hd,
                
                -hw, -hh, hd,
                -hw, hh, -hd,
                -hw, -hh, -hd,
                -hw, -hh, hd,
                -hw, hh, hd,
                -hw, hh, -hd,
        
                hw, -hh, hd,
                hw, -hh, -hd,
                hw, hh, -hd,
                hw, -hh, hd,
                hw, hh, -hd,
                hw, hh, hd,
                
                -hw, -hh, -hd,
                -hw, hh, -hd,
                hw, hh, -hd,
                hw, hh, -hd,
                hw, -hh, -hd,
                -hw, -hh, -hd,
        
                -hw, -hh, hd,
                hw, hh, hd,
                -hw, hh, hd,
                hw, hh, hd,
                -hw, -hh, hd,
                hw, -hh, hd
        );
        
        float texWidth = (float) (height * 2 + width * 2);
        float texHeight = (float) (depth + 2 * height);
        
        float wxWeight = (float) (width / texWidth);
        float hxWeight = (float) (height / texWidth);
        
        float hyWeight = (float) (height / texHeight);
        float dyWeight = (float) (depth / texHeight);
        
        ObservableFloatArray texCoords = mesh.getTexCoords();
        texCoords.addAll(
                0, hyWeight,
                0, hyWeight + dyWeight,
                
                hxWeight, 0,
                hxWeight, hyWeight,
                hxWeight, hyWeight + dyWeight,
                hxWeight, hyWeight * 2 + dyWeight,
        
                hxWeight + wxWeight, 0,
                hxWeight + wxWeight, hyWeight,
                hxWeight + wxWeight, hyWeight + dyWeight,
                hxWeight + wxWeight, hyWeight * 2 + dyWeight,
                
                hxWeight * 2 + wxWeight, hyWeight,
                hxWeight * 2 + wxWeight, hyWeight + dyWeight,
        
                hxWeight * 2 + wxWeight * 2, hyWeight,
                hxWeight * 2 + wxWeight * 2, hyWeight + dyWeight
        );
    
        ObservableFaceArray faeces = mesh.getFaces();
        faeces.addAll(
                0, 3, 1, 4, 2, 8,
                3, 8, 4, 7, 5, 3,
                
                6, 12, 7, 11, 8, 13,
                9, 11, 10, 12, 11, 10,
                
                12, 3, 13, 1, 14, 4,
                15, 3, 16, 0, 17, 1,
                
                18, 7, 19, 8, 20, 11,
                21, 7, 22, 11, 23, 10,
                
                24, 4, 25, 5, 26, 9,
                27, 9, 28, 8, 29, 4,
                
                30, 3, 31, 6, 32, 2,
                33, 6, 34, 3, 35, 7
        );
        
        this.setMesh(mesh);
    }
    
    public double getWidth()
    {
        return width;
    }
    
    public double getHeight()
    {
        return height;
    }
    
    public double getDepth()
    {
        return depth;
    }
}
