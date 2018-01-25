/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.data.card;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

/**
 *
 * @author Christian Prinz
 */
public class CardStack {

    /**
     * Die Kartenliste
     */
    protected final LinkedList<Card> cards;

    /**
     * Ein Kartenstapel.
     */
    public CardStack() {
        cards = new LinkedList<>();
    }

    /**
     * Ein Kartenstapel mit vorinitialisierten Karten.
     */
    public CardStack(Card[] cards) {
        this();
        Arrays.asList(cards).forEach(this::addCard);
    }

    /**
     * Mischt den Kartenstapel.
     */
    public void shuffle(Random random) {

        Card temp;
        int index;
        for (int i = cards.size() - 1; i > 0; i--) {

            index = random.nextInt(i + 1);
            temp = cards.get(index);
            cards.set(index, cards.get(i));
            cards.set(i, temp);
        }
    }

    /**
     * Gibt die Länge des Stapels zurück
     *
     * @return
     */
    public int size() {
        return cards.size();
    }

    /**
     * Gibt die nächste Karte vom Stapel zurück und legt sie wieder ans "Ende".
     */
    public Card nextCard() {
        Card retObj = cards.remove();
        cards.add(retObj);
        return retObj;
    }

    /**
     * Gibt die naechste Karte des angegebenen Typs zuruec, entfernt sie und
     * fügt sie wieder ans Ende des Stapels.
     *
     * @param action Aktionstyp
     * @return nächste Karte vom angegebenen Typ
     */
    private Card nextCardOfAction(CardAction action) {
        for (Card c : cards) {
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
        cards.add(card);
    }

    /**
     * Entfernt eine Karte aus dem Stapel.
     *
     * @param card Karte
     */
    public void removeCard(Card card) {
        // Geht den Stapel von hinten durch da die gesuchte Karte meist die Letzte ist.
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (cards.get(i) == card) {
                cards.remove(i);
            }
        }
    }

    /**
     * Entfernt die nächste Karte einer bestimmten Action aus dem Stapel.
     *
     * @param action Aktionstyp
     * @return nächste Karte eines bestimmten Aktionstyps
     */
    public Card removeCardOfAction(CardAction action) {
        Card retObj = nextCardOfAction(action);
        cards.remove(retObj);
        return retObj;
    }

    /**
     * Zählt die Anzahl der Karten einer bestimmten Aktionsart im Stapel.
     *
     * @param action Action
     * @return Anzahl Karten des festgelegten Typs
     */
    public int countCardsOfAction(CardAction action) {
        int counter = 0;
        for (Card c : cards) {
            counter += c.getActions().contains(action) ? 1 : 0;
        }
        return counter;
    }

    public Card cardAt(int index) {
        return cards.get(index);
    }

    /**
     * Entfernt alle Karten aus dem Stapel.
     */
    void removeAll() {
        cards.clear();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[Kartenstapel]");

        if (cards.isEmpty()) {
            builder.append(" -");
        }
        else {
            builder.append("\n");
        }

        cards.forEach(c -> {
            builder.append("\t");
            builder.append(c);
            builder.append("\n");
        });
        return builder.toString();
    }
}
