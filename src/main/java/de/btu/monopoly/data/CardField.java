package de.btu.monopoly.data;

import java.util.Random;

/**
 * Created by Belserich Gremory on 13/11/2017.
 */
public class CardField extends Field {

    /**
     * Ereignis- (CHANCE) oder Gemeinschaftsfeld (COMMUNITY)
     */
    public enum Type {
        COMMUNITY, CHANCE;
    }

    private static final String NAME_1 = "Gemeinschaftsfeld";
    private static final String NAME_2 = "Ereignisfeld";

    // TODO
    private static final Card[] CARD_LOADOUT_1 = null;
    private static final Card[] CARD_LOADOUT_2 = null;

    /**
     * der Kartenstapel
     */
    private final Card[] cards;
    private Random rand; //handelt es sich hierbei um den SEED?

    private CardField(String name, Random rand, Card... cards) {
        super(name);
        this.rand = rand;
        this.cards = cards;
        shuffle();
    }

    public static final CardField getInstance(Type type, Random rand) {
        switch (type) {
            case COMMUNITY:
                return new CardField(NAME_1, rand, CARD_LOADOUT_1);
            case CHANCE:
                return new CardField(NAME_2, rand, CARD_LOADOUT_2);
            default:
                return null; // should never happen
        }
    }

    // TODO
    public void shuffle() {
    }

    // TODO
    public Card nextCard() {
        return null;
    }
}
