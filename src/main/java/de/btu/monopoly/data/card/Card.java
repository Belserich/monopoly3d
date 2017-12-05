package de.btu.monopoly.data.card;

import de.btu.monopoly.data.parser.CardAction;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Card {
    
    /**
     * zugehöriger Kartenstapel
     */
    private CardStack stack;
    
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
    private final List<CardAction> actions;

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
    public Card(String name, String text, CardAction[] actions, int[] args) {
        this.name = name;
        this.text = text;
        this.args = args;
        
        this.actions = new LinkedList<>();
        this.actions.addAll(Arrays.asList(actions));
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
    List<CardAction> getActions() {
        return actions;
    }

    /**
     * @return Kartenargumente
     */
    public int[] getArgs() {
        return args;
    }
    
    /**
     * Setzt den derzeitigen zugehörigen Kartenstapel
     *
     * @param stack Kartenstapel
     */
    void setCardStack(CardStack stack) {
        this.stack = stack;
    }
    
    /**
     * @return der zugehörige Kartenstapel
     */
    CardStack getCardStack() {
        return stack;
    }
    
    @Override
    public String toString() {
        return String.format("[Karte] Name: \"%s\", Text: \"%s\", Aktionen: %s, Argumente: %s",
                name, text, Arrays.toString(actions.toArray()), Arrays.toString(args));
    }
}
