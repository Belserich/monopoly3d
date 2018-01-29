package de.btu.monopoly.data.player;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Bank {

    private IntegerProperty balance;
    
    public Bank(int startMoney) {
        this.balance = new SimpleIntegerProperty(startMoney);
    }

    public boolean checkLiquidity(int loss) {
        return (balance.get() - loss >= 0);
    }

    public int balance() {
        return balance.get();
    }

    public void deposit(int amount) {
        balance.set(balance.get() + amount);
    }

    public void withdraw(int amount) {
        balance.set(balance.get() - amount);
    }

    public boolean isLiquid() {
        return balance.get() >= 0;
    }

    public IntegerProperty balanceProperty() { return balance; }
    
    @Override
    public String toString() {
        return String.format("[Bank-Account] Balance: %d", balance.get());
    }
}
