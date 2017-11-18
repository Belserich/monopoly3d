package de.btu.monopoly.data.field;

import de.btu.monopoly.data.field.Field;

public class GoField extends Field {

    /**
     * Geldbetrag des LOS Feldes
     */
    private final int amount;

    /**
     * normaler Konstruktor (erzeugt LOS Feld mit 200 Geld)
     */
    public GoField(int id, String name, int amount) {
        super(id, name);
        
        this.amount = amount;
    }

    /**
     *
     * @return Geldbetrag des LOS Feldes
     */
    public int getAmount() {
        return amount;
    }

}
