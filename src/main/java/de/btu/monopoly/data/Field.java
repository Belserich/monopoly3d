package de.btu.monopoly.data;

public abstract class Field {

    /**
     * Name des Feldes
     */
    private final String name;

    /**
     *
     * @param name des Feldes
     */
    public Field(String name) {
        this.name = name;
    }

    /**
     *
     * @return Name des Feldes
     */
    public String getName() {
        return name;
    }
}
