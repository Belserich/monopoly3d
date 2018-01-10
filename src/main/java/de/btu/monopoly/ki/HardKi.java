/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ki;

import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.data.card.CardAction;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.input.IOService;
import de.btu.monopoly.net.client.GameClient;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class HardKi {

    private static final Logger LOGGER = Logger.getLogger(HardKi.class.getCanonicalName());
    private static GameClient client;
    private static final int PROPERTY_CAP_FOR_STAYING_IN_PRISON = 15;

    public static int jailOption(Player player, GameClient Gclient) {
        IOService.sleep(3000);
        client = Gclient;
        int days = player.getDaysInJail();
        CardStack stack = player.getCardStack();
        int soldProps = getSoldProperties();
                
        // Ist die Obergrenze noch nicht erreicht, versucht die KI sofort rauszukommen
        if (soldProps < PROPERTY_CAP_FOR_STAYING_IN_PRISON){
            if (stack.countCardsOfAction(CardAction.JAIL) > 0){ // mit Karte
                return 3;   
            } 
            else if (player.getMoney() > 100) {                 // mit Geld
                return 2;
            }
            else {
                return 1;
            }
        } 
        else {    // sonst bleibt sie so lang wie moeglich drin
            if (days >= 3 && stack.countCardsOfAction(CardAction.JAIL) > 0){
                return 3;
            } else {
                return 1;
            }
        }
        
       
        
    }

    public static int buyPropOption(Player player, PropertyField prop) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void processActionSequence(Player player, GameBoard board) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void processBetSequence() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * 
     * @return Anzahl der Properties die einen Besitzer haben
     */
    private static int getSoldProperties() {
        return (int) Arrays.stream(client.getGame().getBoard().getFields())
                .filter(p -> p instanceof PropertyField).map(p -> (PropertyField)p)
                .filter(p -> p.getOwner() != null).count();
    }

}
