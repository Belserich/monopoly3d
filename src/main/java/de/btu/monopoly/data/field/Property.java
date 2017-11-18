package de.btu.monopoly.data.field;

import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.Field;

public abstract class Property extends Field {

    /**
     * der Preis des Grundstuecks
     */
    private final int price;
    /**
     * die Hypothek des Grundstuecks
     */
    private final int mortgage;
    /**
     * der Rueckkaufwert der Hypothek
     */
    private final int mortgageBack;

    /**
     * Besitzer des Grundstuecks
     */
    private Player ownedBy;
    
    /**
     * ist die Hypothek aufgenommen
     */
    private boolean mortgageTaken;
    
    /**
     * Enthält die IDs der "Nachbarn" eines Felds
     */
    private int[] neighbourIds;

    /**
     * @param price Kaufpreis des Grundstuecks
     * @param mortgage Hypothekswert
     * @param mortgageBack Hypotheksrückwert
     */
    Property(int id, String name, int price, int mortgage, int mortgageBack, int[] neighbourIds) {
        super(id, name);
        
        this.price = price;
        this.mortgage = mortgage;
        this.mortgageBack = mortgageBack;
        this.neighbourIds = neighbourIds;
    }

    /**
     * @return Besitzer des Grundstuecks
     */
    public Player getOwner() {
        return ownedBy;
    }

    /**
     *
     * @return Kaufpreis des Grundstuecks
     */
    public int getPrice() {
        return price;
    }

    /**
     *
     * @return Wert der Hypothek (ausgezahlt)
     */
    public int getMortgageValue() {
        return mortgage;
    }

    /**
     *
     * @return ob die Hypothek aufgenommen wurde
     */
    public boolean isMortgageTaken() {
        return mortgageTaken;
    }

    /**
     *
     * @param player Besitzer des Grundstuecks
     */
    public void setOwner(Player player) {
        this.ownedBy = player;
    }

    /**
     *
     * @param mortgageTaken ob die Hypothek aufgenommen wurde
     */
    public void setMortgageTaken(boolean mortgageTaken) {
        this.mortgageTaken = mortgageTaken;
    }
}
