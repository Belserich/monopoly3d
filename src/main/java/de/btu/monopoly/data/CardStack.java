/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.data;

import java.util.*;

/**
 *
 * @author Christian Prinz
 */
public class CardStack {
    
    /**
     * Die Kartenliste
     */
    LinkedList<Card> stack;
    
    /**
     * Ein Kartenstapel.
     */
    public CardStack() {
        stack = new LinkedList<Card>();
    }
    
    /**
     * Ein Kartenstapel mit vorinitialisierten Karten.
     */
    public CardStack(Card[] cards) {
        this();
        
        for (Card card : cards) {
            stack.add(card);
        }
    }

    /**
     * Mischt den Kartenstapel.
     */
    public void shuffle() {
        Collections.shuffle(stack);
    }

    /**
     * Gibt die nächste Karte vom Stapel zurück und legt sie wieder ans "Ende".
     */
    public Card nextCard() {
        Card retObj = stack.remove();
        stack.add(retObj);
        return retObj;
    }
    
    /**
     * Fügt die angegebene Karte ans Ende des Stapels.
     *
     * @param card
     */
    public void addCard(Card card) {
        stack.add(card);
    }
    
    public boolean removeCard(Card card) {
        return stack.remove(card);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[Kartenstapel]\n");
        stack.forEach(c -> builder.append("\t" + c + "\n"));
        return builder.toString();
    }
}
