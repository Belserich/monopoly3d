package de.btu.monopoly.data;

import java.util.Arrays;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Card {
    
    public enum Action {
        JAIL, GO_JAIL, GIVE_MONEY, NEXT_STATION, RENT_AMP, MOVE_PLAYER, PAY_MONEY, SET_POSITION,
        GIVE_MONEY_IF_GO_PASSED, RENOVATE, NULL_INCOME, NEXT_SUPPLY, BIRTHDAY;
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
     * Kartentypen
     */
    private final Action[] actions;

    /**
     * Kartenargumente
     */
    private final int[] args;

    /**
     * Repräsentiert eine Ereignis- oder Gemeinschaftskarte
     *
     * @param actions Kartentaktionen
     * @param name Kartentitel
     * @param text Kartentext
     * @param args Zusatzargumente
     */
    public Card(String name, String text, Action[] actions, int[] args) {
        this.actions = actions;
        this.name = name;
        this.text = text;
        this.args = args;
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
     * @return Typen der Karte
     */
    public Action[] getActions() {
        return actions;
    }

    /**
     * @return Kartenargumente
     */
    public int[] getArgs() {
        return args;
    }
    
    @Override
    public String toString() {
        return String.format("[Karte] Name: \"%s\", Text: \"%s\", Aktionen: %s, Argumente: %s", name, text, Arrays.toString(actions), Arrays.toString(args));
    }
}
