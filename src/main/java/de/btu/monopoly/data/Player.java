package de.btu.monopoly.data;

/**
 * Created by Belserich Gremory on 13/11/2017.
 */
public class Player {

    /**
     * Standardname des Spielers
     */
    private static final String DEFAULT_NAME = "Hauptspieler";
    /**
     * Standard ID des Spielers
     */
    private static final int DEFAULT_ID = 0;

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
    private int position;

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
        this.position = 0;
    }

    public Player(String name, int money) { //geht das mit Default ID?
        this(name, DEFAULT_ID, money);
    }

    public Player(int money) { // geht das ohne ID?
        this(DEFAULT_NAME, money);
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
     * @return Tage im Gefaengnis
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
     * @param pos 
     */
    public void setPosition(int pos){
        this.position = pos;
    }
    
    /**
     * Rückgabe der Spielerposition
     * @return 
     */
    public int getPosition(){
        return position;
    }
    
}
