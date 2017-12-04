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
    private LinkedList<Card> stack;
    
    /**
     * Ein Kartenstapel.
     */
    public CardStack() {
        stack = new LinkedList<>();
    }
    
    /**
     * Ein Kartenstapel mit vorinitialisierten Karten.
     */
    public CardStack(Card[] cards) {
        this();
        stack.addAll(Arrays.asList(cards));
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
     * @param card Karte
     */
    public void addCard(Card card) {
        stack.add(card);
    }
    
    /**
     * Entfernt eine Karte aus dem Stapel.
     *
     * @param card Karte
     * @return ob die Karte im Stapel enthalten war
     */
    public boolean removeCard(Card card) {
        return stack.remove(card);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[Kartenstapel]\n");
        stack.forEach(c -> { builder.append("\t"); builder.append(c); builder.append("\n"); } );
        return builder.toString();
    }
}
