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
     * Ein Kartenstapel.
     */
    LinkedList<Card> cardStack;

    /**
     * Liste alle Gefaengnis-Frei-Karten in Spielerhaenden.
     */
    LinkedList<Card> jailCardsInGame;

    public CardStack(Card[] cards) {
        this.cardStack = new LinkedList<Card>();
        this.jailCardsInGame = new LinkedList<Card>();

        for (Card c : cards) {
            cardStack.add(c);
        }
    }

    /**
     * Mischt den Kartenstapel.
     */
    public void shuffle() {
        Collections.shuffle(cardStack);
    }

    /**
     * Nimmt die nächste Karte vom Stapel. Prüft, ob es eine Gefaengnis-Frei Karte ist.
     */
    public Card nextCard() {
        Card retObj = cardStack.remove();
        for (Card.Action action : retObj.getActions()) {
            if (action == Card.Action.JAIL) {
                jailCardsInGame.add(retObj);
                return retObj;
            }
        }
        cardStack.add(retObj);
        return retObj;
    }

    /**
     * Legt eine Gefaengnis-Frei-Karte zurueck auf de Stapel.
     */
    public void returnJailCard() {
        if (!jailCardsInGame.isEmpty()) {
            cardStack.add(jailCardsInGame.remove());
        }
    }
}
