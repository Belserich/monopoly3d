package de.btu.monopoly.data;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Player {
    
    private final int id;
    private final Bank bank;
    private final CardStack stack;
    
    private String name;
    private int position;

    private boolean isInJail;
    private int daysInJail;
    
    private boolean isBankrupt;

    /**
     * Erstellt eine neue Spielerinstanz.
     *
     * @param name Spielername
     * @param id Spieler-ID
     * @param startMoney Kapital des Spielers
     */
    public Player(String name, int id, int startMoney) {
        this.id = id;
        this.bank = new Bank(startMoney);
        
        this.name = name;
        position = 0;

        stack = new CardStack();
        
        isInJail = false;
        daysInJail = 0;
    }

    /**
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * @return Kapital
     */
    public int getMoney() {
        return bank.balance();
    }

    public Bank getBank() {
        return bank;
    }

    /**
     *
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
     * Erhöht die Anzahl der Tage im Gefaengnis um 1.
     */
    public void addDayInJail() {
        this.daysInJail++;
    }

    /**
     * *
     * @param pos neue Position
     */
    public void setPosition(int pos) {
        this.position = pos;
    }

    /**
     * @return Position
     */
    public int getPosition() {
        return this.position;
    }

    /**
     *
     * @return ob Spieler ausgeschieden (Zuschauer) ist
     */
    public boolean isBankrupt() {
        return isBankrupt;
    }

    /**
     * @param isBankrupt neuer Zuschauerstatus
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
    
    public CardStack getStack() {
        return stack;
    }
}
