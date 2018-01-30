package de.btu.monopoly.data.card;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Card {
    
    public enum Action {
        JAIL,
        GO_JAIL,
        GET_MONEY,
        MOVE_NEXT_STATION_RENT_AMP,
        MOVE,
        PAY_BANK,
        SET_POSITION,
        RENOVATE, MOVE_NEXT_SUPPLY,
        BIRTHDAY, PAY_ALL;
        
        public static boolean mustRepeatFieldPhase(Action ac) {
            return ac == MOVE_NEXT_STATION_RENT_AMP || ac == MOVE || ac == MOVE_NEXT_SUPPLY || ac == SET_POSITION;
        }
    }
    
    /**
     * Kartentitel
     */
    private final String name;
    
    /**
     * Kartentext
     */
    private final String text;
    
    /**
     * Kartentyp
     */
    private final Action action;

    /**
     * Kartenargument
     */
    private final int arg;

    /**
     * Repräsentiert eine Ereignis- oder Gemeinschaftskarte.
     *
     * @param name Kartentitel
     * @param text Kartentext
     * @param action Kartentaktion
     * @param arg Zusatzargument
     */
    public Card(String name, String text, Action action, int arg) {
        this.name = name;
        this.text = text;
        this.action = action;
        this.arg = arg;
    }
    
    public Card(String name, String text, Action action) {
        this(name, text, action, 0);
    }
    
    /**
     * @return Kartentitel
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return Kartentext
     */
    public String getText() {
        return text;
    }
    
    /**
     * @return Typ der Karte
     */
    public Action getAction() {
        return action;
    }

    /**
     * @return Kartenargument
     */
    public int getArg() {
        return arg;
    }
    
    @Override
    public String toString() {
        return String.format("[Karte] Name: \"%s\", Text: \"%s\", Aktionen: %s, Argumente: %s",
                name, text, action, arg);
    }
}
