package de.btu.monopoly.data.field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class StreetField extends Property {

    /**
     * die Miete der Straße, abhänging vom Bebauungsstatus
     */
    private final int[] rents;
    /**
     * der Preis für ein Haus
     */
    private final int housePrice;
    /**
     * Anzahl der Haeuser auf der Strasse
     */
    private int houseCount;

    /**
     *
     * @param name Strassenname
     * @param price Kaufpreis der Strasse
     * @param rent0 doppelte Miete unbebaut!
     * @param rent1 Miete mit 1 Haus
     * @param rent2 Miete mit 2 Haeusern
     * @param rent3 Miete mit 3 Haeusern
     * @param rent4 Miete mit 4 Haeusern
     * @param rent5 Miete mit Hotel
     * @param housePrice Preis fuer ein Haus
     */
    public StreetField(String name,
            int price,
            int rent0,
            int rent1,
            int rent2,
            int rent3,
            int rent4,
            int rent5,
            int housePrice, int mortgage, int mortgageBack, int id) {
        super(id, name, price, mortgage, mortgageBack);

        this.rents = new int[6];
        this.rents[0] = rent0;
        this.rents[1] = rent1;
        this.rents[2] = rent2;
        this.rents[3] = rent3;
        this.rents[4] = rent4;
        this.rents[5] = rent5;
        this.housePrice = housePrice;

        this.houseCount = 0;

    }

    /**
     * @return aktuelle Miete der Strasse
     */
    public int getRent() {
        if (!isMortgageTaken()) {
            if (complete()) {
                return rents[houseCount];
            } else {
                return rents[0] / 2;
            }
        }
        return 0;
    }

    /**
     * @return Preis für ein Haus
     */
    public int getHousePrice() {
        return housePrice;
    }

    /**
     * @return Anzahl der Haeuser
     */
    public int getHouseCount() {
        return houseCount;
    }

    /**
     *
     * @return sind alle Strassen des Strassenzuges in selbem besitz
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
