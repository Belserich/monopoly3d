package de.btu.monopoly.data;

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
    public int getRent() {
        return RENTS[0]; // TODO
    }
}
