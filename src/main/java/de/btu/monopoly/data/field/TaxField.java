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
     * Feldklasse für das Einkommens- und das Zusatzsteuerfeld.
     *
     * @param tax Betrag, der zu versteuernden Währung
     */
    public TaxField(int id, String name, int tax) {
        super(id, name);
        this.tax = tax;
    }
    
    /**
     * @return Betrag, der zu versteuernden Währung
     */
    public int getTax() {
        return tax;
    }
}
