package de.btu.monopoly.core;

import de.btu.monopoly.data.Bank;
import de.btu.monopoly.data.Player;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class PlayerService {

    private static final Logger LOGGER = Logger.getLogger(PlayerService.class.getCanonicalName());

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
     * @return Die neue Position des Spielers auf dem Feld.
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
        }
        else {
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
}
