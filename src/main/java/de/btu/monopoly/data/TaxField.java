package de.btu.monopoly.data;

/**
 *
 * @author Christian Prinz
 */
public class TaxField extends Field {

    private final int tax;

    public TaxField(String name, int tax) {
        super(name);
        this.tax = tax;
    }

    public int getTax() {
        return this.tax;
    }

}
