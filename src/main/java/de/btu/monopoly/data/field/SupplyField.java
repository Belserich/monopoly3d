package de.btu.monopoly.data.field;

import de.btu.monopoly.data.player.Player;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class SupplyField extends PropertyField {

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
        Player owner = getOwner();
        if (!isMortgageTaken() && owner != null) {
            int validNeighbourCount = (int) fieldManager.getOwnedNeighbours(this)
                    .filter(p -> !p.isMortgageTaken())
                    .count();
            if (validNeighbourCount == 0) {
                return mult1;
            }
            else return mult2;
        }
        else return 0;
    }

    @Override
    public String toString() {
        return String.format("[Versorgungswerk] %nName: %s, %nPreis: %s, "
                + "%nMultiplikator1: %s, %nMultiplikator2: %s, %nHypothekswert: %s, %nHypotheksrückwert: %s",
                getName(), getPrice(), mult1, mult2, getMortgageValue(), getMortgageBack());
    }
}
