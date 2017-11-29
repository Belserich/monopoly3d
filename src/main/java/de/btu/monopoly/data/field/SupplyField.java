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
    public SupplyField(String name, int price, int mortgage, int mortgageBack, int mult1, int mult2) {
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
            return mult2;
        } else {
            return mult1;
        }
    }

    /**
     * Prüft, ob alle zugehörigen Werke im Besitz desselben Spielers sind.
     *
     * @return true, wenn alle Werke im Besitz des Spielers sind, sonst false.
     */
    public boolean complete() {
        for (Property nei : super.getNeighbours()) {
            if (!(nei.getOwner().equals(super.getOwner()))) {
                return false;
            }
        }
        return true;
    }
}
