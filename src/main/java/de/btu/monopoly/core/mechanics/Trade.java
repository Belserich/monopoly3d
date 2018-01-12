package de.btu.monopoly.core.mechanics;

import de.btu.monopoly.core.GameBoard;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Trade {
    
    private TradeOffer supply;
    private TradeOffer demand;
    
    public TradeOffer getSupply() {
        return supply;
    }
    
    public void setSupply(TradeOffer supply) {
        this.supply = supply;
    }
    
    public TradeOffer getDemand() {
        return demand;
    }
    
    public void setDemand(TradeOffer demand) {
        this.demand = demand;
    }
    
    public String toString(GameBoard board) {
        
        StringBuilder builder = new StringBuilder("============ HANDEL ============\n");
        
        builder.append(supply.toString(board));
        builder.append(demand.toString(board));
        
        builder.append("================================\n");
        
        return builder.toString();
    }
}
