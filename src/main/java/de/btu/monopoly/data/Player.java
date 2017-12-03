package de.btu.monopoly.data;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Player {

    private String name;
    private int id;

    private int position;

    private Bank bank;
    private boolean isBankrupt;

    private boolean isInJail;
    private int daysInJail;
    private int jailCardAmount;

    /**
     * Erstellt eine neue Spielerinstanz.
     *
     * @param name Spielername
     * @param id Spieler-ID
     * @param startMoney Kapital des Spielers
     */
    public Player(String name, int id, int startMoney) {
        this.name = name;
        this.id = id;
        this.bank = new Bank(startMoney);

        isInJail = false;
        jailCardAmount = 0;
        daysInJail = 0;
        position = 0;
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
     * @return Anzahl Gefaengnis-Frei-Karten
     */
    public int getJailCardAmount() {
        return jailCardAmount;
    }

    /**
     * @return Tage im Gefaengnis
     */
    public int getDaysInJail() {
        return daysInJail;
    }

    /**
     * Erhöht die Anzahl der Gefaengnis-frei-Karten um 1.
     */
    public void addJailCard() {
        this.jailCardAmount++;
    }

    /**
     * Senkt die Anzahl der Gefaengnis-frei-Karten um 1.
     */
    public void removeJailCard() {
        this.jailCardAmount--;
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

}
