package de.btu.monopoly.data.field;

public abstract class Field {
    
    /**
     * Name des Feldes
     */
    private final String name;
    
    /**
     * Die Id des Felds
     */
    private final int id;
    
    /**
     * @param id Identifikationsnummer
     * @param name des Feldes
     */
    public Field(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    /**
     * @return Identifikationsnummer des Feldes
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
