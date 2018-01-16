package de.btu.monopoly.net.networkClasses;

import de.btu.monopoly.core.mechanics.Trade;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class PlayerTradeRequest {
    
    private Trade trade;
    private boolean denied;
    
    public Trade getTrade() {
        return trade;
    }
    
    public void setTrade(Trade trade) {
        this.trade = trade;
    }
    
    public void setDenied(boolean val) {
        denied = val;
    }
    
    public boolean isDenied() {
        return denied;
    }
}