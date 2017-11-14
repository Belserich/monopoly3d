package de.btu.monopoly.data;

/**
 * Created by Belserich Gremory on 13/11/2017.
 */
public class GoField extends Field {
    
    private static final String NAME = "LOS";
    private static final int AMOUNT = 200;
    
    private final int amount;
    
    public GoField() {
        super(NAME);
        this.amount = AMOUNT;
    }
    
    public int getAmount() {
        return amount;
    }
}
