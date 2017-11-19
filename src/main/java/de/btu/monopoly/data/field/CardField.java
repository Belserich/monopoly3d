package de.btu.monopoly.data.field;

import de.btu.monopoly.data.Card;
import de.btu.monopoly.data.field.Field;

import java.util.Random;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class CardField extends Field {

    /**
     * Kartenstapel
     */
    private final Card[] cards;
    
    /**
     * Zufallsgenerator, wird fürs mischen benötigt.
     */
    private Random rand;
    
    /**
     * Eine Instanz dieser Klasse repräsentiert Ereignis- und Gemeinschaftsfelder.
     *
     * @param rand Der Zufallsgenerator
     * @param cards
     */
    public CardField(int id, String name, Random rand, Card[] cards) {
        super(id, name);
        this.rand = rand;
        this.cards = cards;
        shuffle();
    }
    
    /**
     * Die Methode ist für das mischen der Karten zuständig.
     */
    public void shuffle() {
        // TODO
    }
    
    /**
     * @return oberste Karte des Stapels
     */
    public Card nextCard() {
        // TODO
        return null;
    }
}
