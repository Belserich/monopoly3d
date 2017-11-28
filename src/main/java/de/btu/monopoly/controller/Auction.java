package de.btu.monopoly.controller;

import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.Property;

/**
 * Eine Klasse die zur Auktion von Grundstuecken dient
 *
 * @author Christian Prinz
 */
public class Auction {

    private int price;

    private Player[] players;

    private Player winner;

    private Property property;

    /**
     *
     * @param property Die Strasse die versteigert werden soll
     * @param players Spieler die an der Auktion teilnehmen
     */
    public Auction(Property property, Player[] players) {
        this.property = property;
        this.players = players;
    }

    /**
     * Startet die Auktion, ermittelt den Höchstbietenden und übergibt den Gewinner, sowie den Preis an deren Klassenvariablen
     */
    public void startAuction() {
        /*
         * TODO hier findet die Auktion statt. Resultat der Auktion ist, dass ein Gewinner gefunden und anschliessend der Preis,
         * sowie der Gewinner in der Klasse gespeichert werden. Hier wird kein Besitz übertragen und kein Geld abgezogen!!!
         */
        //PLATZHALTER:
        this.price = 0;
        this.winner = players[0];
    }

    /**
     * @return the price
     */
    public int getPrice() {
        return price;
    }

    /**
     * @return the winner
     */
    public Player getWinner() {
        return winner;
    }

}
