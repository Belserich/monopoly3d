package de.btu.monopoly.data.field;

import de.btu.monopoly.data.field.Field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class TaxField extends Field {
    
    /**
     * Geldwert der zu leistenden Steuer
     */
    private int tax;
    
    /**
     * Feldklasse für Felder, wie das Einkommens- und das Zusatzsteuerfeld.
     *
     * @param tax Betrag, der zu versteuernden Währung
     */
    public TaxField(String name, int tax) {
        super(name);
        this.tax = tax;
    }
    
    /**
     * @return Betrag, der zu versteuernden Währung
     */
    public int getTax() {
        return tax;
    }
    
    @Override
    public String toString() {
        return String.format("[Steuerfeld] Name: %s,  Steuerwert: %s", getName(), tax);
    }
}
