package de.btu.monopoly.data.field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class StationField extends Property {

    /**
     * Die Miete des Bahnhofs in Abhaengigkeit der Anzahl Bahnhöfe im Besitz
     */
    public final int[] rents;

    /**
     *
     * @param name Bahnhofsname
     */
    public StationField(String name, int price, int rent1, int rent2, int rent3, int rent4, int mortgage, int mortgageBack) {
        super(name, price, mortgage, mortgageBack);

        this.rents = new int[4];
        this.rents[0] = rent1;
        this.rents[1] = rent2;
        this.rents[2] = rent3;
        this.rents[3] = rent4;
    }

    @Override
    public int getRent() {
        switch (ownedStations()) {
            case 1:
                return rents[0];
            case 2:
                return rents[1];
            case 3:
                return rents[2];
            case 4:
                return rents[3];

        }
        return 0;
    }

    /**
     *
     * @return Anzahl der Bahnhofe im selben Besitz
     */
    public int ownedStations() {
        int numberOfStationsOwned = 1;
        for (Property nei : super.getNeighbours()) {
            if (nei.getOwner().equals(super.getOwner())) {
                numberOfStationsOwned++;
            }
        }
        return numberOfStationsOwned;
    }
}
