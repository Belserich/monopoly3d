package de.btu.monopoly.data.field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class SupplyField extends Property {

    private final int mult1;
    private final int mult2;

    /**
     * Instanzen dieser Klasse stellen Versogungswerke, wie das Wasser- und das Elektizitätswerk dar.
     *
     * @param mult1 1. Multiplikator
     * @param mult2 2. Multipliaktor
     */
    public SupplyField(String name, int price, int mult1, int mult2, int mortgage, int mortgageBack) {
        super(name, price, mortgage, mortgageBack);
        this.mult1 = mult1;
        this.mult2 = mult2;
    }

    /**
     * Gibt, statt eines Mietswertes, den entsprechenden Multiplikator zurück.
     *
     * @return Multiplikator
     */
    @Override
    public int getRent() {
        if (complete()) {
            return mult1;
        } else {
            return mult2;
        }
    }

    /**
     * Prüft, ob alle zugehörigen Werke im Besitz desselben Spielers sind.
     *
     * @return true, wenn alle Werke im Besitz des Spielers sind, sonst false.
     */
    public boolean complete() {
        for (Property nei : super.getNeighbours()) {
            if (!(nei.getOwner() == (super.getOwner()))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("[Versorgungswerk] Name: %s, Preis: %s, Multiplikator1: %s, Multiplikator2: %s, Hypothekswert: %s, Hypotheksrückwert: %s",
                getName(), getPrice(), mult1, mult2, getMortgageValue(), getMortgageBack());
    }
}
