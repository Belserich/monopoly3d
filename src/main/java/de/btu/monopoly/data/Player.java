package de.btu.monopoly.data;

/**
 * Created by Belserich Gremory on 13/11/2017.
 */
public class Player {
    
    private static final String DEFAULT_NAME = "Hauptspieler";
    private static final int DEFAULT_ID = 0;
    
    private final String name;
    private final int id;
    
    private int money;
    private boolean inJail;
    private int jailCardAmount;
    private int daysInJail;
    
    public Player(String name, int id, int money) {
        this.name = name;
        this.id = id;
        this.money = money;
        this.inJail = false;
        this.jailCardAmount = 0;
        this.daysInJail = 0;
    }
    
    public Player(String name, int money) {
        this(name, DEFAULT_ID, money);
    }
    
    public Player(int money) {
        this(DEFAULT_NAME, money);
    }
    
    public String getName() {
        return name;
    }
    
    public int getId() {
        return id;
    }
    
    public int getMoney() {
        return money;
    }
    
    public boolean isInJail() {
        return inJail;
    }
    
    public int getJailCardAmount() {
        return jailCardAmount;
    }
    
    public int getDaysInJail() {
        return daysInJail;
    }
    
    public void addJailCardAmount() {
        this.jailCardAmount++;
    }
    
    public void removeJailCard() {
        this.jailCardAmount--;
    }
    
    public void setDaysInJail(int days) {
        this.daysInJail = days;
    }
    
    public void addDayInJail() {
        this.daysInJail++;
    }
    
    public void setMoney(int money) {
        this.money = money;
    }
}
