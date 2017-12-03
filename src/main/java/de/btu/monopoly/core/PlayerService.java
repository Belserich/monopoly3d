package de.btu.monopoly.core;

import de.btu.monopoly.data.Bank;
import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.field.Property;
import de.btu.monopoly.data.field.StreetField;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class PlayerService {

    private static final Logger LOGGER = Logger.getLogger(de.btu.monopoly.core.Game.class.getCanonicalName());

    /**
     * Setzt alle nötigen Attribute, wenn der Spieler ins Gefängnis kommt. Bitte {@code FieldManager.toJail()} benutzen.
     *
     * @param player Spieler
     */
    static void toJail(Player player) {
        player.setInJail(true);
        player.setDaysInJail(0);
        LOGGER.info(String.format("%s ist jetzt im Gefaengnis.", player.getName()));
    }

    public static void freeFromJail(Player player) {
        player.setInJail(false);
        player.setDaysInJail(-1);
        LOGGER.info(String.format("%s wurde aus dem Gefängnis befreit.", player.getName()));
    }

    /**
     * Setzt das Positionsindex für den Spieler. Bitte {@code FieldManager.movePlayer()} benutzen.
     *
     * @param player Spieler
     * @param amount Anzahl der zu laufenden Felder
     * @return Die neue Position des Spielers auf dem Feld( und darueber hinaus).
     */
    static int movePlayer(Player player, int amount) {
        int pos = player.getPosition();
        pos += amount;
        player.setPosition(pos);
        if (amount >= 0) {
            LOGGER.info(String.format("%s wurde %d Felder weiter bewegt.", player.getName(), amount));
        }
        return pos;
    }

    /**
     * Prüft die Zahlungsfähigkeit eines Spielers.
     *
     * @param player Spieler
     * @param amount Anzahl Geldeinheiten
     * @return true, wenn zahlungsfähig, sonst false
     */
    public static boolean checkLiquidity(Player player, int amount) {
        Bank bank = player.getBank();
        if (bank.checkLiquidity(amount)) {
            LOGGER.fine("Spielerkonto auf Liquidität geprüft. Er besitzt genug Geld.");
            return true;
        } else {
            LOGGER.warning("Spielerkonto auf Liquidität geprüft. Er besitzt nicht genug Geld!");
            return false;
        }
    }

    public static boolean takeMoney(Player player, int amount) {
        if (checkLiquidity(player, amount)) {
            takeMoneyUnchecked(player, amount);
            return false;
        }
        return true;
    }

    /**
     * Einem Spieler wird der uebergebene Betrag von seinem Konto abgezogen.
     *
     * @param player Spieler dem der Betrag abgezogen wird
     * @param amount Betrag der dem Spieler abgezogen wird
     */
    public static void takeMoneyUnchecked(Player player, int amount) {
        Bank bank = player.getBank();
        bank.withdraw(amount);
        LOGGER.info(String.format("%s (+%d)", bank.toString(), amount));
        if (!bank.isLiquid()) {
            LOGGER.info(String.format("%s hat sich verschuldet!", player.getName()));
        }
    }

    /**
     * Einem Spieler wird der uebergebene Betrag auf dem Konto gutgeschrieben.
     *
     * @param player Spieler dem der Betrag gutgeschrieben wird
     * @param amount Betrag der dem Spieler gutgeschrieben wird
     */
    public static void giveMoney(Player player, int amount) {
        Bank bank = player.getBank();
        bank.deposit(amount);
        LOGGER.info(String.format("%s (-%d)", bank.toString(), amount));
    }

    /**
     * @return Anzahl der Spieler die nicht Pleite sind
     */
    public static int countActive(Player[] players) {
        return (int) Arrays.stream(players)
                .filter(p -> !(p.isBankrupt()))
                .count();
    }

    /**
     *
     * @return int[] mit den beiden Wuerfelergebnissen
     */
    public static int[] roll(Player player) {
        int[] result = new int[2];

        result[0] = ((int) (Math.random() * 6)) + 1;
        result[1] = ((int) (Math.random() * 6)) + 1;

        LOGGER.info(String.format("Würfelergebnis: %d %d", result[0], result[1]));
        return result;
    }

    /**
     *
     * @param player zu pruefender Spieler
     * @param board das Spielbrett (zum Besitz wegnehmen)
     * @param players alle Mitspieler (zum Game-Over-Check)
     * @return boolean, ob das Spiel gameOver ist
     */
    public static boolean bankrupt(Player player, GameBoard board, Player[] players) {
        boolean gameOver = false;
        LOGGER.info(String.format("%s ist Bankrott und ab jetzt nur noch Zuschauer. All sein Besitz geht zurück an die Bank.",
                player.getName()));
        player.setBankrupt(true);
        Field[] fields = board.getFields(); // TODO
        if (PlayerService.countActive(players) <= 1) {
            gameOver = true;
        }

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
     * @param player Spieler dessen Hauser/Hotels gezaehlt werden
     * @param housePrice Preis fuer ein Haus
     * @param hotelPrice Preis fuer ein Hotel
     * @param board GameBoard
     * @return Summe der Renovierungskosten
     */
    public static int sumRenovation(Player player, int housePrice, int hotelPrice, GameBoard board) {
        //TODO spaeter, wenn Kartenstapel gedruckt wurde

        int renovationHotel = 0;
        int renovationHouse = 0;
        for (Field field : board.getFields()) {
            if (field instanceof StreetField) {
                if (((StreetField) field).getOwner() == player) {
                    int houses = ((StreetField) field).getHouseCount();
                    if (houses < 5) {
                        renovationHouse += (housePrice * houses);

                    } else {
                        renovationHotel += hotelPrice;

                    }
                }
            }
        }
        return renovationHouse + renovationHotel;
    }
}
