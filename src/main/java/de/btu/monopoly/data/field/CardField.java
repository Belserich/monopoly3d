package de.btu.monopoly.data.field;

import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.card.CardStack;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class CardField extends Field {

    private CardStack cards;

    /**
     * @param name Name des Kartenfeldes
     */
    public CardField(String name, CardStack cards) {
        super(name);
        this.cards = cards;
        cards.shuffle();
    }
    
    public Card nextCard() {
        return cards.nextCard();
    }
    
    @Override
    public String toString() {
        return String.format("[Kartenfeld] Name: %s, Stapel: %s", getName(), cards);
    }
}
