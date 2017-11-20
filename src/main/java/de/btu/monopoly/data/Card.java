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
     * Kartenargumente
     */
    private final String[] args;

    /**
     * Repräsentiert eine Ereignis- oder Gemeinschaftskarte
     *
     * @param type
     * @param text
     */
    private Card(int type, String text, String[] args) {
        this.type = type;
        this.text = text;
        this.args = args;
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

    /**
     * @return Kartenargumente
     */
    public String[] getArgs() {
        return args;
    }
}
