package de.btu.monopoly.data.field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Field {

    /**
     * Name des Feldes
     */
    private final String name;

    /**
     * ID des Felds
     */
    private final int id;

    /**
     * Die Klasse {@code Field} ist eine Oberklasse für sämtliche Felder auf
     * denen sich der Spieler während des Spiels bewegt. Alle Eckfelder, außer
     * dem LOS-Feld, sind direkte Instanzen dieser Klasse.
     *
     * @param id Identifikationsnummer
     * @param name des Feldes
     */
    public Field(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @return ID des Feldes
     */
    public int getId() {
        return id;
    }

    /**
     * @return Name des Feldes
     */
    public String getName() {
        return name;
    }
}
