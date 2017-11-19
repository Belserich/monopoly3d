package de.btu.monopoly.data;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Card {
    
    /**
     * Kartentyp
     */
    private final int type;
    
    /**
     * Kartentext
     */
    private final String text;
    
    /**
     * Repräsentiert eine Ereignis- oder Gemeinschaftskarte
     *
     * @param type
     * @param text
     */
    private Card(int type, String text) {
        this.type = type;
        this.text = text;
    }
    
    /**
     * @return Typ der Karte
     */
    public int getType() {
        return type;
    }
    
    /**
     * @return Kartentext
     */
    public String getText() {
        return text;
    }
}
