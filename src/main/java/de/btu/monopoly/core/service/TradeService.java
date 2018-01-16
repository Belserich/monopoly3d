package de.btu.monopoly.core.service;

import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.mechanics.Trade;
import de.btu.monopoly.core.mechanics.TradeOffer;
import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.FieldManager;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class TradeService {
    
    /**
     * Verarbeitet einen Tauschhandel.
     *
     * @param trade Tauschobjekt
     * @param board Spielbrett-Instanz
     */
    public static void completeTrade(Trade trade, GameBoard board) {
        
        TradeOffer supply = trade.getSupply();
        Player supplier = board.getPlayer(supply.getPlayerId());
        
        TradeOffer demand = trade.getDemand();
        Player receipt = board.getPlayer(demand.getPlayerId());
    
        FieldManager fm = board.getFieldManager();
        
        completeTradeOffer(supply, supplier, receipt, fm);
        completeTradeOffer(demand, receipt, supplier, fm);
    }
    
    /**
     * Verarbeitet ein Tauschangebot.
     *
     * @param offer Angebot
     * @param supplier bietender Spieler
     * @param receipt empfangender Spieler
     * @param fm Feldmanager
     */
    private static void completeTradeOffer(TradeOffer offer, Player supplier, Player receipt, FieldManager fm) {
        
        CardStack suppStack = supplier.getCardStack();
        CardStack recStack = receipt.getCardStack();
        int suppMoney = offer.getMoney();
    
        for (int fieldId : offer.getPropertyIds()) {
            PropertyField field = (PropertyField) fm.getField(fieldId);
            field.setOwner(receipt);
        }
    
        for (int cardId : offer.getCardIds()) {
            Card card = suppStack.cardAt(cardId);
            suppStack.removeCard(card);
            recStack.addCard(card);
        }
    
        if (suppMoney != 0) {
            PlayerService.takeAndGiveMoneyUnchecked(supplier, receipt, suppMoney);
        }
    }
}
