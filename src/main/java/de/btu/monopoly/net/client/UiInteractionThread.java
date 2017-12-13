package de.btu.monopoly.net.client;

import de.btu.monopoly.core.Game;
import de.btu.monopoly.net.networkClasses.BroadcastPlayerChoiceRequest;
import de.btu.monopoly.net.networkClasses.PlayerTradeRequest;
import de.btu.monopoly.net.networkClasses.PlayerTradeResponse;

import javax.swing.*;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class UiInteractionThread extends Thread {
    
    protected final CopyOnWriteArrayList<BroadcastPlayerChoiceRequest> receivedPlayerChoiceObjects;
    protected final CopyOnWriteArrayList<PlayerTradeRequest> tradeRequestObjects;
    
    private GameClient client;
    
    public UiInteractionThread(GameClient client) {
        super();
        
        this.client = client;
        receivedPlayerChoiceObjects = new CopyOnWriteArrayList<>();
        tradeRequestObjects = new CopyOnWriteArrayList<>();
    }
    
    @Override
    public void run() {
        while (!isInterrupted() && Game.IS_RUNNING.get()) {
            if (!tradeRequestObjects.isEmpty()) {
                
                Iterator<PlayerTradeRequest> it = tradeRequestObjects.iterator();
                while (it.hasNext()) {
                    PlayerTradeResponse response = new PlayerTradeResponse();
                    response.setRequest(it.next());
                    response.setAccepted(getTradeChoice());
                    client.sendTCP(response);
                }
                tradeRequestObjects.clear();
                System.out.println("Trade response gesendet.");
            }
            
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException ex) {
                Logger.getLogger(this.getClass().getCanonicalName()).warning(this.getName() + " has been interrupted!");
                this.interrupt();
            }
        }
    }
    
    private boolean getTradeChoice() {
        int choice = -1;
        do {
            String input = JOptionPane.showInputDialog("Du hast ein Tauschangebot erhalten! 0 - Ablehnen, 1 - Annehmen");
            try {
                choice = Integer.parseInt(input);
                if (choice == 0) {
                    return false;
                }
                else if (choice == 1) {
                    return true;
                }
            }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Ungültige Eingabe! Versuche es nochmal!");
            }
        }
        while (Game.IS_RUNNING.get());
        return false;
    }
}
