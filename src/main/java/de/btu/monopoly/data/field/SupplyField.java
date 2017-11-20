package de.btu.monopoly.data.field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class SupplyField extends Property {

    private final int[] rents;

    /**
     * @param name Werksname
     * @param price Kaufpreis des Werks
     */
    private SupplyField(String name, int price, int multiplikator1, int multiplikator2, int mortgage, int mortgageBack, int id) {
        super(id, name, price, mortgage, mortgageBack);

        this.rents = new int[2];
        this.rents[0] = multiplikator1;
        this.rents[1] = multiplikator2;
    }

    @Override
    public int getRent() {
        if (complete()) {
            return rents[1];
        } else {
            return rents[0];
        }
    }

    /**
     *
     * @return sind beide Werke im selben Besitz
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
