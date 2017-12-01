/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.controller.phases;

import static de.btu.monopoly.controller.GameController.CURRENCY_TYPE;
import static de.btu.monopoly.controller.GameController.logger;
import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.field.GoField;
import de.btu.monopoly.data.field.Property;
import de.btu.monopoly.data.field.StreetField;
import java.util.Arrays;
import java.util.logging.Level;

/**
 *
 * @author Christian Prinz
 */
public class PlayerManager {

    private GameBoard board;
    private Player[] players;

    public PlayerManager(GameBoard board, Player[] players) {
        this.board = board;
        this.players = players;
    }

    /**
     * das Wuerfeln. Ergebnisse werden lokal in rollResult und doubletCounter gespeichert
     */
    public int[] roll(Player player) {
        int[] rollResult = new int[2];
        // Erzeugen der Zufallszahl
        rollResult[0] = ((int) (Math.random() * 6)) + 1;
        rollResult[1] = ((int) (Math.random() * 6)) + 1;

        logger.log(Level.INFO, player.getName() + " würfelt " + rollResult[0] + " + " + rollResult[1]
                + " = " + (rollResult[0] + rollResult[1]));

        return rollResult;
    }

    /**
     * bewegt den Spieler zu einer neuen Position.
     *
     * @param player Spieler der bewegt wird
     */
    public void movePlayer(Player player, int fields) {
        if (player.getPosition() + fields > 39) {
            logger.log(Level.FINE, player.getName() + " hat das \"LOS\"-Feld passiert und erhaelt "
                    + ((GoField) board.getFields()[0]).getAmount() + " " + CURRENCY_TYPE + ".");
            giveMoney(player, ((GoField) board.getFields()[0]).getAmount());

            player.setPosition(player.getPosition() + fields - 39);
        } else {
            player.setPosition(player.getPosition() + fields);
        }
    }

    /**
     * bewegt den Spieler ins Gefängnis und setzt seine Attribute entsprechend
     *
     * @param player Spieler der ins Gefaengnis kommt
     */
    public void moveToJail(Player player) {
        /*
         * Spieler wird auf Position 10 gesetzt setInJail wird true, damit der Spieler nicht "nur zu Besuch" ist Die Tage im
         * Gefängnis werden auf 0 gesetzt
         */
        player.setPosition(10);
        player.setInJail(true);
        player.setDaysInJail(0);
        logger.log(Level.INFO, player.getName() + " ist jetzt im Gefaengnis.");
    }

    /**
     * ueberpruft ob der uebergebene Spieler mindestens soviel Geld besitzt, wie die Methode uebergeben bekommt.
     *
     * @param player Spieler der auf Liquiditaet geprueft wird
     * @param amount Geld was der Spieler besitzen muss
     */
    public boolean checkLiquidity(Player player, int amount) {

        logger.log(Level.INFO, "Es wird geprüft, ob " + player.getName() + " genug Geld hat für die Transaktion.");
        return (player.getMoney() - amount) > 0;

    }

    /**
     * Einem Spieler wird der uebergebene Betrag von seinem Konto abgezogen.
     *
     * @param player Spieler dem der Betrag abgezogen wird
     * @param amount Betrag der dem Spieler abgezogen wird
     */
    public void takeMoney(Player player, int amount) {

        player.setMoney(player.getMoney() - amount);
        logger.log(Level.INFO, player.getName() + " werden " + amount + CURRENCY_TYPE + " abgezogen.");

        if (amount < 0) {
            bankrupt(player);
        }

        logger.log(Level.INFO, "Der Kontostand von " + player.getName() + " beträgt nun: " + player.getMoney() + CURRENCY_TYPE + ".");

    }

    /**
     * Einem Spieler wird der uebergebene Betrag auf dem Konto gutgeschrieben.
     *
     * @param player Spieler dem der Betrag gutgeschrieben wird
     * @param amount Betrag der dem Spieler gutgeschrieben wird
     */
    public void giveMoney(Player player, int amount) {

        player.setMoney(player.getMoney() + amount);
        logger.log(Level.INFO, player.getName() + " erhält " + amount + CURRENCY_TYPE + ".");
        logger.log(Level.INFO, "Der Kontostand von " + player.getName() + " beträgt nun: " + player.getMoney() + CURRENCY_TYPE + ".");
    }

    /**
     * Macht einen Spieler zum Beobachter und entfernt all seinen Besitz!
     *
     * @param player Spieler der bankrott gegangen ist
     * @return ob das Spiel vorbei ist (gameOver)
     */
    public boolean bankrupt(Player player) {
        boolean gameOver = false; //TODO aufrufe verändern

        logger.log(Level.INFO, player.getName() + " ist Bankrott und ab jetzt nur noch Zuschauer. All sein Besitz geht zurück an die Bank.");

        // Spieler als Zuschauer festlegen
        player.setSpectator(true);

        // Temporär das Feldarray zum Durchgehen zwischenspeichern
        Field[] fields = board.getFields();

        // geht ein Spieler bankrott wird geprueft, ob er der vorletzte war
        if (countActivePlayers() <= 1) {
            gameOver = true;
        }

        // Durchgehen des Array fields, ggf. Eigentum löschen
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            // Löschen der Hypothek und des Eigentums
            if (field instanceof Property) {
                if (((Property) field).getOwner() == player) {
                    ((Property) field).setOwner(null);
                    ((Property) field).setMortgageTaken(false);
                }

                // Löschen der Anzahl an Häusern
                if (field instanceof StreetField) {
                    ((StreetField) fields[i]).setHouseCount(0);

                }
            }
        }
        return gameOver;
        // TODO @cards - Gefängnisfreikarten müssen zurück in den Stapel
    }

    /**
     *
     * @return Anzahl der Spieler die nicht pleite sind
     */
    public int countActivePlayers() {
        return (int) Arrays.stream(players)
                .filter(p -> !(p.isSpectator()))
                .count();
    }

    /**
     * Alle Gebaeude eines Spielers werden gezaehlt
     *
     * Die Preise fuer Renovierung werden von dem entsprechenden Karte bekannt und dies wird mit der Anzahl von Haeuser/Hotels
     * multipliziert und am Ende addiert = Summe
     *
     * @param housePrice Hauspreis
     * @param hotelPrice Hotelpreis
     */
    public void sumRenovation(Player player, int housePrice, int hotelPrice) {
        //TODO spaeter, wenn Kartenstapel gedruckt wurde

        int renovationHotel = 0;
        int renovationHouse = 0;
        for (Field field : board.getFields()) {
            if (field instanceof StreetField) {
                int houses = ((StreetField) field).getHouseCount();
                if (houses < 5) {
                    renovationHouse += (housePrice * houses);

                } else {
                    renovationHotel += hotelPrice;

                }
            }
        }
        int sum = renovationHouse + renovationHotel;
        if (checkLiquidity(player, sum)) {
            takeMoney(player, sum);
        } else {
            bankrupt(player);
        }
    }

}
