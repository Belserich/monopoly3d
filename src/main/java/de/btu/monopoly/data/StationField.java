package de.btu.monopoly.data;

/**
 * Created by Maximilian Bels on 13/11/2017.
 */
public class StationField extends Property {

    /**
     * Die Miete des Bahnhofs in Abhaengigkeit der Anzahl Bahnhöfe im Besitz
     */
    public static final int[] RENTS = null; // TODO

    /**
     * Der Kaufpreis des Bahnhofs ???
     */
    private static final int PRICE = 0; // TODO

    /**
     *
     * @param name Bahnhofsname
     */
    public StationField(String name) {
        super(name, PRICE);
    }

    @Override
    int getRent() {
        return RENTS[0]; // TODO
    }
}
