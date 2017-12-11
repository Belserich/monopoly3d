package de.btu.monopoly.data.card;

import de.btu.monopoly.data.Tradeable;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class JailCard extends Card implements Tradeable {
    
    public JailCard(String name, String text) {
        super(name, text, new CardAction[] { CardAction.JAIL }, new int[0]);
    }
    
    @Override
    public int getTradingValue() {
        return 50; // TODO Die hardgecodete Zahl wegbekommen
    }
}
