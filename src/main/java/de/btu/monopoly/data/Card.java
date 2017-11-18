package de.btu.monopoly.data;

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
