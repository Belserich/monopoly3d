package de.btu.monopoly.data.player;

import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.data.card.CardStack;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Player {
    
    /**
     * zugehöriges Spielbrett
     */
    private final GameBoard board;

    /**
     * Spieler-ID
     */
    private final int id;

    /**
     * Bank-Instanz
     */
    private final Bank bank;

    /**
     * Karten in Spielerhaenden
     */
    private final CardStack stack;

    /**
     * Spielername
     */
    private String name;

    /**
     * Position als ganzzahlige Feld-ID
     */
    private int position;

    /**
     * ob der Spieler im Gefaengnis ist
     */
    private boolean isInJail;

    /**
     * Anzahl Tage im Gefaengnis
     */
    private int daysInJail;

    /**
     * ob der Spieler Pleite ist
     */
    private boolean isBankrupt;

    /**
     * der Schwierigkeitsgrad der KI (0 = keine KI ; 1 = leicht ; 2 = mittel ; 3 = schwer)
     */
    private int aiLevel;

    /**
     * Erstellt eine neue Spielerinstanz.
     *
     * @param board zugehöriges Spielbrett
     * @param name Spielername
     * @param id Spieler-ID
     * @param startMoney Kapital des Spielers
     */
    public Player(GameBoard board, String name, int id, int startMoney) {
        this.board = board;
        this.id = id;
        this.bank = new Bank(startMoney);
        this.aiLevel = 0;
        this.name = name;
        position = 0;

        stack = new CardStack();

        isInJail = false;
        daysInJail = 0;
    }
    
    /**
     * @return Spielbrett
     */
    public GameBoard getBoard() {
        return board;
    }
    
    /**
     * @return Spielername
     */
    public String getName() {
        return name;
    }

    /**
     * @return Spieler-ID
     */
    public int getId() {
        return id;
    }

    /**
     * @return Kapital des Spielers (kurz für {@code player.getBank().balance()})
     */
    public int getMoney() {
        return bank.balance();
    }

    /**
     * @return Bank-Instanz des Spielers
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * @return ob der Spieler im Gefängnis ist
     */
    public boolean isInJail() {
        return isInJail;
    }

    /**
     * @return Tage im Gefaengnis
     */
    public int getDaysInJail() {
        return daysInJail;
    }

    /**
     * @param days Tage im Gefaengnis
     */
    public void setDaysInJail(int days) {
        this.daysInJail = days;
    }

    /**
     * Erhöht die Anzahl der Tage im Gefaengnis um eins.
     */
    public void addDayInJail() {
        this.daysInJail++;
    }

    /**
     * @param pos neue Position (ganzzahlige Feld-ID)
     */
    public void setPosition(int pos) {
        this.position = pos;
    }

    /**
     * @return Position des Spielers als ganzzahlige Feld-ID
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * @return ob Spieler Pleite ist
     */
    public boolean isBankrupt() {
        return isBankrupt;
    }

    /**
     * @param isBankrupt Bankstatus
     */
    public void setBankrupt(boolean isBankrupt) {
        this.isBankrupt = isBankrupt;
    }

    /**
     * @param inJail ob Spieler im Gefaengnis ist
     */
    public void setInJail(boolean inJail) {
        this.isInJail = inJail;
    }

    /**
     * @return Karten in Spielerhand
     */
    public CardStack getCardStack() {
        return stack;
    }
    
    @Override
    public String toString() {
        return String.format("[Spieler] Name: %s, ID: %d, %s %s %n\t%s %n\t%s",
                name, id, isInJail ? "(" + daysInJail + " Tage im Gefängnis)" : "", isBankrupt ? "(Pleite)" : "", bank, stack);
    }

    /**
     * ob der Spieler eine KI ist
     *
     * @return the aiLevel
     */
    public int getAiLevel() {
        return aiLevel;
    }

    /**
     * ob der Spieler eine KI ist
     *
     * @param aiLevel the aiLevel to set
     */
    public void setAiLevel(int aiLevel) {
        this.aiLevel = aiLevel;
    }
}
