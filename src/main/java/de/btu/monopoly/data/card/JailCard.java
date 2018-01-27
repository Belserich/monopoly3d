package de.btu.monopoly.data.card;

import de.btu.monopoly.data.Tradeable;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class JailCard extends Card implements Tradeable, Takeable {
    
    /**
     * zugehöriger Kartenstapel
     */
    CardStack originalStack;
    
    /**
     * zeitweiliger Kartenstapel
     */
    CardStack temporaryStack;
    
    public JailCard(CardStack originalStack) {
        super("Gefängnis-Frei", "Du kommst aus dem Gefängnis frei.", Action.JAIL);
        this.originalStack = originalStack;
    }
    
    public CardStack getOriginalStack() {
        return originalStack;
    }
    
    public CardStack getTemporaryStack() {
        return temporaryStack;
    }
    
    @Override
    public void drawTo(CardStack stack) {
        temporaryStack = stack;
        temporaryStack.addCard(this);
        originalStack.removeCard(this);
    }
    
    @Override
    public void putBack() {
        temporaryStack.removeCard(this);
        originalStack.addCard(this);
        temporaryStack = originalStack;
    }
    
    @Override
    public int getTradingValue() {
        return 50; // TODO Die hardgecodete Zahl wegbekommen
    }
}
