package de.btu.monopoly.data;

/**
 * Created by Belserich Gremory on 13/11/2017.
 */
public class StreetField extends Property {
    
    private final int[] rents;
    private final int housePrice;
    
    private int houseCount;
    
    public StreetField(String name,
                       int price,
                       int rent0,
                       int rent1,
                       int rent2,
                       int rent3,
                       int rent4,
                       int rent5,
                       int housePrice)
    {
        super(name, price);
        
        this.rents = new int[6];
        this.rents[0] = rent0;
        this.rents[1] = rent1;
        this.rents[2] = rent2;
        this.rents[3] = rent3;
        this.rents[4] = rent4;
        this.rents[5] = rent5;
        this.housePrice = housePrice;
    
        this.houseCount = 0;
        
    }
    
    public int getRent() {
        if (!isMortgageTaken()) {
            return rents[houseCount];
        }
        return -1;
    }
    
    public int getHousePrice() {
        return housePrice;
    }
    
    public int getHouseCount() {
        return houseCount;
    }
}
