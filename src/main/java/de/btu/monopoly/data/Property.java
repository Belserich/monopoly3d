package de.btu.monopoly.data;

/**
 * Created by Belserich Gremory on 13/11/2017.
 */
public abstract class Property extends Field {
    
    private final int price;
    private final int mortgage;
    private final int mortgageBack;
    
    private Player ownedBy;
    private boolean mortgageTaken;
    
    public Property(String name, int price) {
        super(name);
        this.price = price;
        this.mortgage = (int) (price * 0.5d);
        this.mortgageBack = (int) (mortgage + (mortgage * 0.1d));
    }
    
    public Player getOwner() {
        return ownedBy;
    }
    
    public int getPrice() {
        return price;
    }
    
    public int getMortgageValue() {
        return mortgage;
    }
    
    public boolean isMortgageTaken() {
        return mortgageTaken;
    }
    
    public void setOwner(Player player) {
        this.ownedBy = player;
    }
    
    public void setMortgageTaken(boolean mortgageTaken) {
        this.mortgageTaken = mortgageTaken;
    }
    
    int getRent() { return -1; };
}
