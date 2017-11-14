package de.btu.monopoly.data;

/**
 * Created by Belserich Gremory on 13/11/2017.
 */
public class GoField extends Field {

    /**
     * Name des LOS Feldes
     */
    private static final String NAME = "LOS";

    /**
     * Standard Geldbetrag des LOS Feldes
     */
    private static int AMOUNT = 200;

    /**
     * Geldbetrag des LOS Feldes
     */
    private final int amount;

    /**
     * normaler Konstruktor (erzeugt LOS Feld mit 200 Geld)
     */
    public GoField() {
        super(NAME);
        this.amount = AMOUNT;
    }

    /**
     * erweiterter Konstruktor
     *
     * @param amount Geldbetrag des LOS Feldes
     */
    public GoField(int amount) {
        super(NAME);
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
