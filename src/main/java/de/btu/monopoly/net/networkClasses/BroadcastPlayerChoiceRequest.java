package de.btu.monopoly.net.networkClasses;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class BroadcastPlayerChoiceRequest {
    
    private int choice;
    
    public void setChoice(int choice) {
        this.choice = choice;
    }
    
    public int getChoice() {
        return choice;
    }
}
