package de.btu.monopoly.data;

/**
 * Created by Belserich Gremory on 13/11/2017.
 */
public class SupplyField extends Property {
    
    public enum Type {
        WATER, ELECTRICITY;
    }
    
    public static final int MULT_1 = 4;
    public static final int MULT_2 = 10;
    
    private static final String NAME_WATER = "Wasserwerk";
    private static final String NAME_ELECTRICITY = "Elektrizitätswerk";
    
    private static final int PRICE = 0; // TODO
    private static final int RENT = 0; // TODO
    
    private SupplyField(String name, int price) {
        super(name, price);
    }
    
    public SupplyField getInstance(Type type) {
        switch (type) {
            case WATER : return new SupplyField(NAME_WATER, PRICE);
            case ELECTRICITY: return new SupplyField(NAME_ELECTRICITY, PRICE);
            default : return null; // can't happen
        }
    }
    
    @Override
    public int getRent() {
        return RENT;
    }
}
