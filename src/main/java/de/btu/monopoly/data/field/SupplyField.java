package de.btu.monopoly.data.field;

public class SupplyField extends Property {
    
    /**
     * 1. Multiplikator
     */
    private int mult1;
    
    /**
     * 2. Multiplikator
     */
    private int mult2;
    
    public SupplyField(int id, String name, int price, int mortgage, int mortgageBack, int mult1, int mult2) {
        super(id, name, price, mortgage, mortgageBack);
        
        this.mult1 = mult1;
        this.mult2 = mult2;
    }

    public int getMult1() {
        return mult1;
    }
    
    public int getMult2() {
        return mult2;
    }
}
