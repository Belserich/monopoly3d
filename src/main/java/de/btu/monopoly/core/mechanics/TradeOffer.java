package de.btu.monopoly.core.mechanics;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class TradeOffer {
    
    private int playerId;
    private int[] propertyIds;
    private int[] cardIds;
    private int money;
    
    public int getPlayerId() {
        return playerId;
    }
    
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    
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
