package de.btu.monopoly.core.mechanics;

import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.FieldManager;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class TradeOffer {
    
    private int[] propertyIds;
    private int[] cardIds;
    
    private int playerId;
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
    
    public String toString(GameBoard board) {
        
        FieldManager fm = board.getFieldManager();
        Player supplier = board.getPlayer(playerId);
        CardStack suppStack = supplier.getCardStack();
        
        StringBuilder builder = new StringBuilder();
    
        builder.append(String.format("%s's Gebot%n--------------%n", supplier.getName()));
    
        for (int fieldId : propertyIds) {
            PropertyField field = (PropertyField) fm.getField(fieldId);
            builder.append(String.format("-%s%n", field.toString()));
        }
        
        if (cardIds.length != 0) {
            builder.append("\n");
        }
        
        for (int cardId : cardIds) {
            Card card = suppStack.cardAt(cardId);
            builder.append(String.format("-%s%n", card.toString()));
        }
    
        if (money != 0) {
            builder.append(String.format("%nGeld: %d", money));
        }
    
        builder.append("\n\n");
        return builder.toString();
    }
}
