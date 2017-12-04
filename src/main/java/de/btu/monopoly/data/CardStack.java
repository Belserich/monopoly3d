/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.data;

import com.sun.istack.internal.NotNull;

import java.util.*;

/**
 *
 * @author Christian Prinz
 */
public class CardStack {
    
    /**
     * Die Kartenliste
     */
    private final LinkedList<Card> stack;
    
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
     * Gibt die naechste Karte des angegebenen Typs zuruec, entfernt sie und fügt sie
     * wieder ans Ende des Stapels.
     *
     * @param action Aktionstyp
     * @return nächste Karte vom angegebenen Typ
     */
    @NotNull
    public Card nextCard(Card.Action action) {
        for (Card c : stack) {
            if (c.getActions().contains(action)) {
                return c;
            }
        }
        return null;
    }
    
    /**
     * Fügt die angegebene Karte ans Ende des Stapels.
     *
     * @param card Karte
     */
    public void addCard(Card card) {
        card.setCardStack(this);
        stack.add(card);
    }
    
    /**
     * Entfernt eine Karte aus dem Stapel.
     *
     * @param card Karte
     * @return ob die Karte im Stapel enthalten war
     */
    public boolean removeCard(Card card) {
        // Geht den Stapel von hinten durch da die gesuchte Karte meist die Letzte ist.
        for (int i = stack.size() - 1; i >= 0; i--) {
            if (stack.get(i) == card) {
                stack.remove(i);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Entfernt die nächste Karte einer bestimmten Action aus dem Stapel.
     *
     * @param action Aktionstyp
     * @return nächste Karte eines bestimmten Aktionstyps
     */
    @NotNull
    public Card removeCardOfAction(Card.Action action) {
        Card retObj = nextCard(action);
        stack.remove(retObj);
        return retObj;
    }
    
    /**
     * Zählt die Anzahl der Karten einer bestimmten Aktionsart im Stapel.
     *
     * @param action Action
     * @return Anzahl Karten des festgelegten Typs
     */
    public int countCardsOfAction(Card.Action action) {
        int counter = 0;
        for (Card c : stack) {
            counter += c.getActions().contains(action) ? 1 : 0;
        }
        return counter;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[Kartenstapel]\n");
        stack.forEach(c -> { builder.append("\t"); builder.append(c); builder.append("\n"); } );
        return builder.toString();
    }
}
