package de.btu.monopoly.data;

/**
 * Created by Belserich Gremory on 13/11/2017.
 */
public abstract class Field {
    
    private final String name;
    
    public Field(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
