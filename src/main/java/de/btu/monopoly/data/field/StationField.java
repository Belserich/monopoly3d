package de.btu.monopoly.data.field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class StationField extends Property {
    
    /**
     * Eine Sammlung aller Mietspreise
     */
    private final int[] rents;
    
    public StationField(
            int id,
            String name,
            int price,
            int rent0,
            int rent1,
            int rent2,
            int rent3,
            int mortgage,
            int mortgageBack,
            int[] neighbourIds) {
        super(id, name, price, mortgage, mortgageBack, neighbourIds);
        
        rents = new int[4];
        rents[0] = rent0;
        rents[1] = rent1;
        rents[2] = rent2;
        rents[3] = rent3;
    }
    
    public int getRent(int number) {
        return rents[number];
    }
}
