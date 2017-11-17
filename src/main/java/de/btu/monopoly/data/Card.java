package de.btu.monopoly.data;

/**
 * Created by Maximilian Bels on 13/11/2017.
 */
public class Card {
    
    // TODO init static final preset cards
    
    private final int type;
    private final String text;
    
    private Card(int type, String text) {
        this.type = type;
        this.text = text;
    }
    
    public int getType() {
        return type;
    }
    
    public String getText() {
        return text;
    }
}
