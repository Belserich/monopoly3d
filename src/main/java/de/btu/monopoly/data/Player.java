package de.btu.monopoly.data;

/**
 * Created by Belserich Gremory on 13/11/2017.
 */
public class Player {

    /**
     * Name des Spielers
     */
    private final String name;
    /**
     * ID des Spielers
     */
    private final int id;
    /**
     * Farbe des Spielers
     */
    //TODO Farbe

    /**
     * Spielfigur des Spielers
     */
    //TODO Spielfigur
    /**
     * Geldmenge auf dem SpielerKonto (Kapital)
     */
    private int money;
    /**
     * ob er im Gefaengnis ist
     */
    private boolean inJail;
    /**
     * Anzahl Gefaengnis-frei-Karten
     */
    private int jailCardAmount;
    /**
     * Tage im Gefaengnis
     */
    private int daysInJail;

    /**
     * Position des Spielers
     */
    private Field position;

    /**
     * ob der Spieler ein Zuschauer (ausgeschieden) ist
     */
    private boolean isSpectator;

    /**
     *
     * @param name Spielername
     * @param id Spieler ID
     * @param money Kapital des Spielers
     */
    public Player(String name, int id, int money) {
        this.name = name;
        this.id = id;
        this.money = money;
        this.inJail = false;
        this.jailCardAmount = 0;
        this.daysInJail = 0;
        this.position = null;
    }

    /**
     *
     * @return Spielername
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return Spieler ID
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return SpielerKapital
     */
    public int getMoney() {
        return money;
    }

    /**
     *
     * @return ob der Spieler im Gefängnis ist
     */
    public boolean isInJail() {
        return inJail;
    }

    /**
     *
     * @return Anzahl Gefaengnis-frei-Karten
     */
    public int getJailCardAmount() {
        return jailCardAmount;
    }

    /**
     *
     * @return Tage im Gefaengnis
     */
    public int getDaysInJail() {
        return daysInJail;
    }

    /**
     * erhöht die Anzahl der Gefaengnis-frei-Karten um 1
     */
    public void addJailCardAmount() {
        this.jailCardAmount++;
    }

    /**
     * senkt die Anzahl der Gefaengnis-frei-Karten um 1
     */
    public void removeJailCard() {
        this.jailCardAmount--;
    }

    /**
     *
     * @param days Tage im Gefaengnis
     */
    public void setDaysInJail(int days) {
        this.daysInJail = days;
    }

    /**
     * erhöht die Tage im Gefaengnis um 1
     */
    public void addDayInJail() {
        this.daysInJail++;
    }

    /**
     *
     * @param money SpielerKapital
     */
    public void setMoney(int money) {
        this.money = money;
    }

    /**
     * Festlegen der Spielerposition
     *
     * @param pos
     */
    public void setPosition(Field pos) {
        this.position = pos;
    }

    /**
     * Rückgabe der Spielerposition
     *
     * @return
     */
    public Field getPosition() {
        return this.position;
    }

    /**
     *
     * @return ob der Spieler Zuschauer ist (ausgeschieden)
     */
    public boolean isSpectator() {
        return isSpectator;
    }

    /**
     *
     * @param isSpectator ob der Spieler ausgeschieden ist
     */
    public void setSpectator(boolean isSpectator) {
        this.isSpectator = isSpectator;
    }

}
