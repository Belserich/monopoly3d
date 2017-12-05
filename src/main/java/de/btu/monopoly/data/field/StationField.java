package de.btu.monopoly.data.field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class StationField extends PropertyField {

    /**
     * Die Miete des Bahnhofs in Abhaengigkeit der Anzahl Bahnhöfe im Besitz
     */
    private final int[] rents;

    /**
     * Die Klasse steht für sämtliche Bahnhofsfelder des Spielbretts
     *
     * @param name Bahnhofsname
     */
    public StationField(String name, int price, int rent0, int rent1, int rent2, int rent3, int mortgage, int mortgageBack) {
        super(name, price, mortgage, mortgageBack);

        this.rents = new int[4];
        this.rents[0] = rent0;
        this.rents[1] = rent1;
        this.rents[2] = rent2;
        this.rents[3] = rent3;
    }

    @Override
    public int getRent() {
        if (isMortgageTaken()) {
            return 0;
        }
        return rents[ownedStations()];
    }

    /**
     * @return Anzahl der Bahnhofe im selben Besitz
     */
    public int ownedStations() {
        int numberOfStationsOwned = 0;
        for (PropertyField nei : super.getNeighbours()) {
            if (nei.getOwner() == getOwner()) {
                if (!nei.isMortgageTaken()) {
                    numberOfStationsOwned++;
                }
            }
        }
        return numberOfStationsOwned;
    }

    @Override
    public String toString() {
        return String.format("[Bahnhof] Name: %s, Preis: %s, Miete0: %s, Miete1: %s, Miete2: %s, Miete3: %s, Hypothekswert: %s, Hypotheksrückwert: %s",
                getName(), getPrice(), rents[0], rents[1], rents[2], rents[3], getMortgageValue(), getMortgageBack());
    }
}
