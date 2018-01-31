package de.btu.monopoly.data.field;

import de.btu.monopoly.data.Tradeable;
import de.btu.monopoly.data.player.Player;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public abstract class PropertyField extends Field implements Tradeable {

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
    private ObjectProperty<Player> owner;

    /**
     * Gibt an, ob die Hypothek aufgenommen wurde.
     */
    private BooleanProperty mortgageTaken;

    /**
     * Die abstrakte Oberklasse aller Felder, die kaufbar und mit Mietspreisen belegt sind. Man soll diese Felder außerdem mit
     * einer Hypothek belasten können und diese auch für {@code (Hypothekswert + 10% des Kaufpreises)} Einheiten der gewählten
     * Währung zurückzahlen können.
     *
     * @param price Kaufpreis des Grundstuecks
     * @param mortgage Hypothekswert
     * @param mortgageBack Hypotheksrückwert
     */
    PropertyField(String name, int price, int mortgage, int mortgageBack) {
        super(name);
        this.price = price;
        this.mortgage = mortgage;
        this.mortgageBack = mortgageBack;
        
        owner = new SimpleObjectProperty<>(null);
        mortgageTaken = new SimpleBooleanProperty(false);
    }

    /**
     * @return Besitzer des Grundstuecks
     */
    public Player getOwner() {
        return owner.get();
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
        return mortgageTaken.get();
    }

    /**
     * @return Hypotheksrückwert
     */
    public int getMortgageBack() {
        return mortgageBack;
    }

    /**
     * @param player Besitzer des Grundstuecks
     */
    public void setOwner(Player player) {
        this.owner.set(player);
    }

    /**
     * @param mortgageTaken Gibt an, ob die Hypothek aufgenommen wurde.
     */
    public void setMortgageTaken(boolean mortgageTaken) {
        this.mortgageTaken.set(mortgageTaken);
    }
    
    public int getRent() { return 0; }
    
    public BooleanProperty mortgageTakenProperty() {
        return mortgageTaken;
    }
    
    public ObjectProperty<Player> ownerProperty() {
        return owner;
    }
    
    @Override
    public int getTradingValue() {
        return getRent();
    }
}
