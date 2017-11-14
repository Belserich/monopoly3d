package de.btu.monopoly.data;

/**
 * Created by Belserich Gremory on 13/11/2017.
 */
public class StationField extends Property {
    
    public static final int[] RENTS = null; // TODO
    
    private static final int PRICE = 0; // TODO
    
    public StationField(String name) {
        super(name, PRICE);
    }
    
    @Override
    int getRent() {
        return RENTS[0]; // TODO
    }
}
