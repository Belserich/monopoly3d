package de.btu.monopoly.data;

import java.util.logging.Logger;

public class Bank {

    private int balance;

    public Bank(int startMoney) {
        this.balance = startMoney;
    }

    public boolean checkLiquidity(int loss) {
        return (balance - loss >= 0);
    }

    public int balance() {
        return balance;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public void withdraw(int amount) {
        balance -= amount;
    }

    public boolean isLiquid() {
        return balance >= 0;
    }

    @Override
    public String toString() {
        return String.format("[Bank-Account] Balance: %d", balance);
    }
}
