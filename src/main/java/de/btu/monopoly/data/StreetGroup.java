package de.btu.monopoly.data;

import java.awt.*;

/**
 * Created by Belserich Gremory on 13/11/2017.
 */
public class StreetGroup {
    
    public enum TYPE {
        PURPLE, AZURE, VIOLET, ORANGE, RED, YELLOW, GREEN, BLUE, STATION, SUPPLIER;
    }
    
    private static final Color COLOR_PURPLE = Color.decode("803399");
    private static final Color COLOR_AZURE = Color.decode("80CCFF");
    private static final Color COLOR_VIOLET = Color.decode("CC44CC");
    private static final Color COLOR_ORANGE = Color.decode("FF8000");
    private static final Color COLOR_RED = Color.decode("FF0000");
    private static final Color COLOR_YELLOW = Color.decode("FFFF00");
    private static final Color COLOR_GREEN = Color.decode("338033");
    private static final Color COLOR_BLUE = Color.decode("2020CC");
    private static final Color COLOR_WHITE = Color.decode("FFFFFF");
    
    //TODO START
    private static final StreetField[] PURPLE_FIELDS = null;
    private static final StreetField[] AZURE_FIELDS = null;
    private static final StreetField[] VIOLET_FIELDS = null;
    private static final StreetField[] ORANGE_FIELDS = null;
    private static final StreetField[] RED_FIELDS = null;
    private static final StreetField[] YELLOW_FIELDS = null;
    private static final StreetField[] GREEN_FIELDS = null;
    private static final StreetField[] BLUE_FIELDS = null;
    private static final StationField[] STATION_FIELDS = null;
    private static final SupplyField[] SUPPLY_FIELDS = null;
    // TODO END
    
    private final Property[] properties;
    private final Color color;
    private boolean isComplete;
    
    private StreetGroup(Color color, Property[] property) {
        this.color = color;
        this.properties = property;
    }
    
    public static final StreetGroup getInstance(TYPE type) {
        switch (type) {
            case PURPLE:
                return new StreetGroup(COLOR_PURPLE, PURPLE_FIELDS);
            case AZURE:
                return new StreetGroup(COLOR_AZURE, AZURE_FIELDS);
            case VIOLET:
                return new StreetGroup(COLOR_VIOLET, VIOLET_FIELDS);
            case ORANGE:
                return new StreetGroup(COLOR_ORANGE, ORANGE_FIELDS);
            case RED:
                return new StreetGroup(COLOR_RED, RED_FIELDS);
            case YELLOW:
                return new StreetGroup(COLOR_YELLOW, YELLOW_FIELDS);
            case GREEN:
                return new StreetGroup(COLOR_GREEN, GREEN_FIELDS);
            case BLUE:
                return new StreetGroup(COLOR_BLUE, BLUE_FIELDS);
            case STATION:
                return new StreetGroup(COLOR_WHITE, STATION_FIELDS);
            case SUPPLIER:
                return new StreetGroup(COLOR_WHITE, SUPPLY_FIELDS);
            default: return null; // should never happen
        }
    }
    
    public Property[] getProperties()  {
        return properties;
    }
    
    public Color getColor() {
        return color;
    }
    
    public boolean isComplete() {
        Player owner = properties[0].getOwner();
        for(int i = 1; i < properties.length; i++) {
            if (properties[i].getOwner() != owner) {
                return false;
            }
        }
        return true;
    }
    
    public int computeRent(Property property) {
        for (Property other : properties) {
            if (other == property) {
            
            }
        }
        return -1;
    }
}
