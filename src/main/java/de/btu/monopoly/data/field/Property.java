package de.btu.monopoly.data.field;

import de.btu.monopoly.data.Player;
import java.util.ArrayList;
import java.util.List;

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
     * Hypothek des Grundstuecks
     */
    private final int mortgageBack;

    /**
     * Besitzer des Grundstuecks
     */
    private Player ownedBy;

    /**
     * Gibt an, ob die Hypothek aufgenommen wurde.
     */
    private boolean mortgageTaken;

    /**
     * Liste der Nachbarn des Feldes (Straßenzug)
     */
    List<Property> neighbours = new ArrayList<>();

    /**
     * Die abstrakte Oberklasse aller Felder, die kaufbar und mit Mietspreisen
     * belegt sind. Man soll diese Felder außerdem mit einer Hypothek belasten
     * können und diese auch für {@code (Hypothekswert + 10% des Kaufpreises)}
     * Einheiten der gewählten Währung zurückzahlen können.
     *
     * @param price Kaufpreis des Grundstuecks
     * @param mortgage Hypothekswert
     * @param mortgageBack Hypotheksrückwert
     */
    Property(String name, int price, int mortgage, int mortgageBack) {
        super(name);
        this.price = price;
        this.mortgage = mortgage;
        this.mortgageBack = mortgageBack;
    }

    /**
     * @return Besitzer des Grundstuecks
     */
    public Player getOwner() {
        return ownedBy;
    }

    /**
     * @return Kaufpreis des Grundstuecks
     */
    public int getPrice() {
        return price;
    }

    /**
     * @return Wert der zu zahlenden Hypothek
     */
    public int getMortgageValue() {
        return mortgage;
    }

    /**
     * @return Gibt an, ob die Hypothek aufgenommen wurde.
     */
    public boolean isMortgageTaken() {
        return mortgageTaken;
    }

    /**
     * @param player Besitzer des Grundstuecks
     */
    public void setOwner(Player player) {
        this.ownedBy = player;
    }

    /**
     * @param mortgageTaken Gibt an, ob die Hypothek aufgenommen wurde.
     */
    public void setMortgageTaken(boolean mortgageTaken) {
        this.mortgageTaken = mortgageTaken;
    }

    /**
     * Gibt die Miete des Grundstuecks zurueck. Im Falle eines Werkes entspricht
     * die Miete dem Multiplikator fuer die Augenzahl.
     */
    public int getRent() {
        return 0;
    }

    /**
     * Fuegt dem Grundstueck ein Nachbargrundstueck hinzu.
     *
     * @param neighbour neuer Nachbar
     */
    public void addNeighbour(Property neighbour) {
        this.getNeighbours().add(neighbour);
    }

    /**
     * @return Liste der Nachbarn
     */
    public List<Property> getNeighbours() {
        return neighbours;
    }

}
