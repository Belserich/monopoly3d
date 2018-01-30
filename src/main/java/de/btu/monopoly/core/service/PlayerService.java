package de.btu.monopoly.core.service;

import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.data.player.Bank;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.chat.GUIChat;
import de.btu.monopoly.net.client.GameClient;
import java.util.Random;
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
        IOService.sleep(2000);
        GUIChat.getInstance().event(String.format("%s ist jetzt im Gefaengnis.", player.getName()));
    }

    /**
     * Gibt an, ob die
     *
     * @param player gewählter Spieler
     * @param client Spielclient
     * @return true, wenn der gewählte Spieler, der Hauptspieler des Clienten ist.
     */
    public static boolean isMainPlayer(Player player, GameClient client) {
        return client.getPlayerOnClient() == player;
    }

    /**
     * Befreit den Spieler aus dem Gefaengnis.
     *
     * @param player Spieler
     */
    public static void freeFromJail(Player player) {
        player.setInJail(false);
        player.setDaysInJail(-1);
        GUIChat.getInstance().event(String.format("%s wurde aus dem Gefängnis befreit.", player.getName()));
    }

    /**
     * Setzt das Positionsindex für den Spieler. Bitte {@code FieldManager.movePlayer()} benutzen.
     *
     * @param player Spieler
     * @param amount Anzahl der zu laufenden Felder
     * @return Die neue Position des Spielers auf dem Feld( und darueber hinaus).
     */
    public static int movePlayer(Player player, int amount) {

        int pos = player.getPosition();
        pos += amount;
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

    /**
     * Versucht, dem Spielerkonto Geld abzubuchen. Bricht ab, wenn der Spieler nicht genug Geld besitzt.
     *
     * @param player Spieler
     * @param amount Summe
     * @return ob die Buchung erfolgreich war
     */
    public static boolean takeMoney(Player player, int amount) {
        if (checkLiquidity(player, amount)) {
            takeMoneyUnchecked(player, amount);
            return true;
        }
        return false;
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
        LOGGER.info(bank.toString() + " (" + (amount >= 0 ? "-" : "+") + Math.abs(amount) + ")");
        if (!bank.isLiquid()) {
            LOGGER.info(String.format("%s hat sich verschuldet!", player.getName()));
        }
    }

    /**
     * Nimmt einem Spieler Geld und gibt es einem anderen.
     *
     * @param from Spieler (zu zahlen)
     * @param to Spieler (bekommt)
     * @param amount Summe
     */
    public static void takeAndGiveMoneyUnchecked(Player from, Player to, int amount) {
        takeMoneyUnchecked(from, amount);
        giveMoney(to, amount);
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
        LOGGER.info(String.format("%s (%s%d)", bank.toString(), amount >= 0 ? "+" : "-", Math.abs(amount)));
    }

    /**
     * Generiert zwei Zufallszahlen zwischen 1 und 6.
     *
     * @return int[] mit den beiden Wuerfelergebnissen
     */
    public static int[] roll(Random rng) {
        int[] result = new int[2];

        result[0] = rng.nextInt(6) + 1;
        result[1] = rng.nextInt(6) + 1;

        GUIChat.getInstance().event(String.format("und würfelt eine %d und eine %d.", result[0], result[1]));
        return result;
    }

    /**
     * Setzt einen Spieler bankrott und entfernt ihn aus dem Spiel.
     *
     * @param player betroffener Spieler
     * @param board Spielbrett-Instanz
     */
    public static void bankrupt(Player player, GameBoard board) {
        GUIChat.getInstance().event(String.format("%s ist Bankrott und ab jetzt nur noch Zuschauer. All sein Besitz geht zurück an die Bank.",
                player.getName()));

        player.setBankrupt(true);
        board.getFieldManager().bankrupt(player);
        board.getCardManager().bankrupt(player);
    }
}
