package de.btu.monopoly.data.field;

import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.Field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public abstract class Property extends Field {

    /**
     * Preis des Grundstuecks
     */
    private final int price;
    /**
     * Hypothek des Grundstuecks
     */
    private final int mortgage;
    /**
     * Rueckkaufwert der Hypothek
     */
    private final int mortgageBack;

    /**
     * Besitzer des Grundstuecks
     */
    private Player ownedBy;
    
    /**
     * gibt an, ob Hypothek aufgenommen
     */
    private boolean mortgageTaken;
    
    /**
     * IDs der "Nachbarn" eines Feldes
     */
    private int[] neighbourIds;

    /**
     * Die abstrakte Oberklasse aller Felder, die kaufbar und mit Mietspreisen belegt sind.
     * Man soll diese Felder außerdem mit einer Hypothek belasten können und diese auch für
     * {@code (Hypothekswert + 10% des Kaufpreises)} Einheiten der gewählten Währung zurückzahlen können.
     *
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
     * @return Wert der zu zahlenden Hypothek
     */
    public int getMortgageValue() {
        return mortgage;
    }

    /**
     *
     * @return Gibt an,ob die Hypothek aufgenommen wurde.
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
     * @param mortgageTaken Gibt an, ob die Hypothek aufgenommen wurde.
     */
    public void setMortgageTaken(boolean mortgageTaken) {
        this.mortgageTaken = mortgageTaken;
    }
}
