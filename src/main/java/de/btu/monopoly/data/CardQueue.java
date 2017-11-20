/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.data;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Christian Prinz
 */
class CardQueue {

    /**
     * Name des Kartenstapels
     */
    String name;
    /**
     * Kartenstapel (Queue)
     */
    Queue<Card> cards = new LinkedList<>();

    /**
     * mischt den Kartenstapel
     */
    public void shuffleCards() {
        //this.cards.
    }

    /**
     * fügt eine neue Karte hinzu
     *
     * @param card Karte die hinzugefügt wird
     */
    public void addCard(Card card) {
        this.cards.add(card);
    }

    /**
     * nimmt die naechste Karte von Stapel
     */
    public Card nextCard() {
        return this.cards.remove();

    }

    /**
     * fügt eine Karte hinzu
     *
     * @param card Karte die hinzugefügt wird
     */
    public void returnCard(Card card) {
        addCard(card);
    }

}
