package de.btu.monopoly.data;

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
     * @param name des Feldes
     */
    public Field(String name, int id) {
        this.name = name;
        this.id = id;
    }
    
    /**
     * @return Name des Feldes
     */
    public String getName() {
        return name;
    }
    
    public int getId() {
        return id;
    }
}
