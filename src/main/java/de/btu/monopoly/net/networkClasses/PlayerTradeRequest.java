package de.btu.monopoly.net.networkClasses;

import de.btu.monopoly.core.mechanics.Trade;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class PlayerTradeRequest {
    
    private Trade trade;
    
    public Trade getTrade() {
        return trade;
    }
    
    public void setTrade(Trade trade) {
        this.trade = trade;
    }
}