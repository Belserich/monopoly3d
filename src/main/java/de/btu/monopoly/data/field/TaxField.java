package de.btu.monopoly.data.field;

import de.btu.monopoly.data.field.Field;

public class TaxField extends Field {
    
    /**
     * Geldwert der zu leistenden Steuer
     */
    private int tax;
    
    public TaxField(int id, String name, int tax) {
        super(id, name);
        this.tax = tax;
    }
    
    /**
     * @return Geldwert der zu leistenden Steuer
     */
    public int getTax() {
        return tax;
    }
}
