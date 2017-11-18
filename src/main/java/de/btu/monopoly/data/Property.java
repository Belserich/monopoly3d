package de.btu.monopoly.data;

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
     *
     * @param name Grundstücksname
     * @param price Kaufpreis des Grundstuecks
     */
    public Property(String name, int price) {
        super(name);
        this.price = price;
        this.mortgage = (int) (price * 0.5d);
        this.mortgageBack = (int) (mortgage + (mortgage * 0.1d));
    }

    /**
     *
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

    /**
     * gibt die Miete des Grundstuecks zurueck. Im Falle eines werkes entspricht
     * die Miete dem Multiplikator fuer die Augenzahl
     */
    public int getRent() {
        return -1;
    }
;
}
