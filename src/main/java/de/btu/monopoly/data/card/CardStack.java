/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.data.card;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Christian Prinz
 */
public class CardStack {

    public enum Type {
        COMMUNITY,
        EVENT;
    }
    
    /**
     * Liste der Karten
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
    
    public CardStack(List<Card> cards) {
        this(cards.toArray(new Card[cards.size()]));
    }

    /**
     * Mischt den Kartenstapel nach deterministischen Werten einer Zufallsinstanz.
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
     * Gibt die oberste Karte im Stapel zurück und legt sie wieder nach unten.
     *
     * @return die oberste Karte im Stapel
     */
    public Card nextCard() {
        Card retObj = cards.remove();
        cards.add(retObj);
        return retObj;
    }

    /**
     * Gibt die naechste Karte des angegebenen Typs zurueck, entfernt sie und
     * legt sie wieder nach unten.
     *
     * @param action Aktionstyp
     * @return nächste Karte vom angegebenen Typ
     */
    public Card nextCardOfAction(Card.Action action) {
        for (Card c : cards) {
            if (c.getAction() == action) {
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
    public Card removeCardOfAction(Card.Action action) {
        Card retObj = nextCardOfAction(action);
        cards.remove(retObj);
        return retObj;
    }

    /**
     * Zählt die Karten eines bestimmten Aktionstyps im Stapel.
     *
     * @param action Aktion
     * @return Anzahl Karten des festgelegten Typs
     */
    public int countCardsOfAction(Card.Action action) {
        int counter = 0;
        for (Card c : cards) {
            counter += c.getAction() == action ? 1 : 0;
        }
        return counter;
    }
    
    /**
     * @param index Index
     * @return Karte an der Stelle {@code index} im Stapel
     */
    public Card cardAt(int index) {
        return cards.get(index);
    }

    /**
     * Entfernt alle Karten aus dem Stapel.
     */
    void removeAll() {
        cards.clear();
    }
    
    /**
     * @return Anzahl der Karten im Stapel
     */
    public int size() {
        return cards.size();
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
