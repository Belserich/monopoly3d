package de.btu.monopoly.data.field;

import de.btu.monopoly.data.player.Player;

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

    public int getRent() {
        Player owner = getOwner();
        if (!isMortgageTaken() && owner != null) {
            int validNeighbourCount = (int) fieldManager.getOwnedNeighbours(this)
                    .filter(p -> !p.isMortgageTaken())
                    .count();
            return rents[validNeighbourCount];
        }
        else return 0;
    }

    @Override
    public String toString() {
        return String.format("[Bahnhof] Name: %s, Preis: %s, Miete0: %s, Miete1: %s, Miete2: %s, Miete3: %s, Hypothekswert: %s, Hypotheksrückwert: %s",
                getName(), getPrice(), rents[0], rents[1], rents[2], rents[3], getMortgageValue(), getMortgageBack());
    }
    
    public String stationInformation() {
        return String.format("[Bahnhof] %nName: %s, %nPreis: %s, %nMiete bei 1 Bahnhof: %s, %nMiete bei 2 Bahnhöfe: %s,"
                + " %nMiete bei 3 Bahnhöfe: %s, %nMiete bei 4 Bahnhöfe: %s, %nHypothekswert: %s, %nHypotheksrückwert: %s",
                getName(), getPrice(), rents[0], rents[1], rents[2], rents[3], getMortgageValue(), getMortgageBack());
    }
}
