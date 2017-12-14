package de.btu.monopoly.core.mechanics;

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
}
