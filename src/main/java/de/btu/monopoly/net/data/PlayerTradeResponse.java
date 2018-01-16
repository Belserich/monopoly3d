package de.btu.monopoly.net.data;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class PlayerTradeResponse {
    
    PlayerTradeRequest request;
    boolean accepted;
    
    public PlayerTradeRequest getRequest() {
        return request;
    }
    
    public void setRequest(PlayerTradeRequest request) {
        this.request = request;
    }
    
    public boolean isAccepted() {
        return accepted;
    }
    
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
