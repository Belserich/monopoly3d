package de.btu.monopoly.data.card;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public interface Takeable {
    
    void drawTo(CardStack stack);
    
    void putBack();
}
