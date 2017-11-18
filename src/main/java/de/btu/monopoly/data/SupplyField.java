package de.btu.monopoly.data;

public class SupplyField extends Property {

    /**
     * Art des Werkes (WATER - Wasserwerk ; ELECTRICITY - Elektrizitaetswerk
     */
    public enum Type {
        WATER, ELECTRICITY;
    }

    /**
     * Multiplikator bei einem Werk im Besitz
     */
    public static final int MULT_1 = 4;
    /**
     * Multiplikator bei beiden Werken im Besitz
     */
    public static final int MULT_2 = 10;
    /**
     * Name fuers Wasserwerk
     */
    private static final String NAME_WATER = "Wasserwerk";
    /**
     * Name fuers Elektrizitätswerk
     */
    private static final String NAME_ELECTRICITY = "Elektrizitätswerk";
    /**
     * Kaufpreis fuer das Werk ???
     */
    private static final int PRICE = 0; // TODO
    /**
     * errechnete Miete fuer das Werk
     */
    private static final int RENT = 0; // TODO

    /**
     * @param name Werksname
     * @param price Kaufpreis des Werks
     */
    private SupplyField(String name, int price) {
        super(name, price);
    }

    /**
     *
     * @param type - Typ des Werkes
     * @return eine Instanz des Werkes
     */
    public SupplyField getInstance(Type type) {
        switch (type) {
            case WATER:
                return new SupplyField(NAME_WATER, PRICE);
            case ELECTRICITY:
                return new SupplyField(NAME_ELECTRICITY, PRICE);
            default:
                return null; // can't happen
        }
    }

    @Override
    public int getRent() {
        return RENT;
    }
}
