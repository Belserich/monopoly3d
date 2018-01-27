package de.btu.monopoly.data.field;

import de.btu.monopoly.data.card.CardStack;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class CardField extends Field {

    private CardStack.Type stackType;

    /**
     * @param name Name des Kartenfeldes
     */
    public CardField(String name, CardStack.Type stackType) {
        super(name);
        this.stackType = stackType;
    }
    
    public CardStack.Type getStackType() {
        return stackType;
    }
    
    public void setStackType(CardStack.Type stackType) {
        this.stackType = stackType;
    }
    
    @Override
    public String toString() {
        return String.format("[Kartenfeld] Name: %s, Stapel: %s", getName(), stackType);
    }
}
