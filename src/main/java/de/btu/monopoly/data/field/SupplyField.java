package de.btu.monopoly.data.field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class SupplyField extends Property {
    
    /**
     * 1. Multiplikator (Spieler besitzt nur dieses Werk)
     */
    private int mult1;
    
    /**
     * 2. Multiplikator (Spieler besitzt zwei Werke)
     */
    private int mult2;
    
    /**
     * Die Feldklasse für alle Versorgungswerke.
     *
     * @param mult1 1. Multiplikator
     * @param mult2 2. Multiplikator
     */
    public SupplyField(int id, String name, int price, int mortgage, int mortgageBack, int[] neighbourIds, int mult1, int mult2) {
        super(id, name, price, mortgage, mortgageBack, neighbourIds);
        
        this.mult1 = mult1;
        this.mult2 = mult2;
    }
    
    /**
     * 1. Multiplikator (Spieler besitzt nur dieses Werk)
     */
    public int getMult1() {
        return mult1;
    }
    
    /**
     * 2. Multiplikator (Spieler besitzt zwei Werke)
     */
    public int getMult2() {
        return mult2;
    }
}
