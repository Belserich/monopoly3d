package de.btu.monopoly.data.field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Field {
    
    /**
     * zuständiger Feld-Manager
     */
    protected FieldManager fieldManager;
    
    /**
     * Name des Feldes
     */
    private final String name;

    /**
     * Die Klasse {@code Field} ist eine Oberklasse für sämtliche Felder auf
     * denen sich der Spieler während des Spiels bewegt. Alle Eckfelder, außer
     * dem LOS-Feld, sind direkte Instanzen dieser Klasse.
     *
     * @param name Name des Feldes
     */
    public Field(String name) {
        this.name = name;
    }

    /**
     * @return Name des Feldes
     */
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return String.format("[Eckfeld] Name: %s", name);
    }
}
