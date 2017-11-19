package de.btu.monopoly.data;

import de.btu.monopoly.data.field.Field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
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
     * Kapital
     */
    private int money;
    
    /**
     * Gibt an, ob der Spieler im Gefängnis ist.
     */
    private boolean inJail;
    
    /**
     * Anzahl Gefaengnis-Frei-Karten
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
     * Gibt an, ob der Spieler ausgeschieden (Zuschauer) ist.
     */
    private boolean isSpectator;

    /**
     * Erstellt eine neue Spielerinstanz.
     *
     * @param name Spielername
     * @param id Spieler-ID
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
        return money;
    }

    /**
     *
     * @return ob Spieler im Gefängnis
     */
    public boolean isInJail() {
        return inJail;
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
    public void addJailCardAmount() {
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
     * @param money neues Kapital
     */
    public void setMoney(int money) {
        this.money = money;
    }

    /**     *
     * @param pos neue Position
     */
    public void setPosition(Field pos) {
        this.position = pos;
    }

    /**
     * @return Position
     */
    public Field getPosition() {
        return this.position;
    }

    /**
     *
     * @return ob Spieler ausgeschieden (Zuschauer) ist
     */
    public boolean isSpectator() {
        return isSpectator;
    }

    /**
     * @param isSpectator neuer Zuschauerstatus
     */
    public void setSpectator(boolean isSpectator) {
        this.isSpectator = isSpectator;
    }

    /**
     * @param inJail ob Spieler im Gefaengnis ist
     */
    public void setInJail(boolean inJail) {
        this.inJail = inJail;
    }

}
