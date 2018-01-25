package de.btu.monopoly.data.field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class StreetField extends PropertyField {

    /**
     * Miete der Straße, abhänging vom Bebauungsstatus
     */
    private final int[] rents;

    /**
     * Preis für ein Haus
     */
    private final int housePrice;

    /**
     * Anzahl der Haeuser auf der Strasse
     */
    private int houseCount;

    /**
     * Instanzen dieser Klasse repräsentieren sämtliche Straßen des Spiels.
     *
     * @param name Strassenname
     * @param price Kaufpreis der Strasse
     * @param rent0 Miete unbebaut
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
            int housePrice,
            int mortgage,
            int mortgageBack) {
        super(name, price, mortgage, mortgageBack);

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
     * @return Miete der Strasse
     */
    public int getRent() {
        //System.out.println(houseCount);
        if (!isMortgageTaken() && getOwner() != null) {
            if (fieldManager.isComplete(this) && houseCount == 0) {
                return rents[0] * 2;
            }
            else {
                return rents[houseCount];
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

    public void setHouseCount(int houseCount) {
        this.houseCount = houseCount;
    }

    @Override
    public String toString() {
        return String.format("[Straßenfeld] %nName: %s, %nPreis: %s, %nMiete0: %s, %nMiete1: %s, %nMiete2: %s, %nMiete3: %s, %nMiete4: %s"
                + ", %nMiete5: %s, %nHauspreis: %s, %nHypothekswert: %s, %nHypotheksrückwert: %s",
                getName(), getPrice(), rents[0], rents[1], rents[2], rents[3], rents[4], rents[5], housePrice, getMortgageValue(), getMortgageBack());
    }
}
