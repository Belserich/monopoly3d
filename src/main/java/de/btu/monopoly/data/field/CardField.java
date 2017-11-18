package de.btu.monopoly.data.field;

import de.btu.monopoly.data.Card;
import de.btu.monopoly.data.field.Field;

import java.util.Random;

public class CardField extends Field {

    /**
     * der Kartenstapel
     */
    private final Card[] cards;
    private Random rand;

    private CardField(int id, String name, Random rand, Card... cards) {
        super(id, name);
        this.rand = rand;
        this.cards = cards;
        shuffle();
    }

    // TODO
    public void shuffle() {
    }

    // TODO
    public Card nextCard() {
        return null;
    }
}
