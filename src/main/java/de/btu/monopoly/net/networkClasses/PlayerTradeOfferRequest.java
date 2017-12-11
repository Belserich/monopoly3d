package de.btu.monopoly.net.networkClasses;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class PlayerTradeOfferRequest {
    
    private int[] propertyIds;
    private int[] cardIds;
    private int money;
    
    public int[] getPropertyIds() {
        return propertyIds;
    }
    
    public void setPropertyIds(int[] propertyIds) {
        this.propertyIds = propertyIds;
    }
    
    public int[] getCardIds() {
        return cardIds;
    }
    
    public void setCardIds(int[] cardIds) {
        this.cardIds = cardIds;
    }
    
    public int getMoney() {
        return money;
    }
    
    public void setMoney(int money) {
        this.money = money;
    }
}