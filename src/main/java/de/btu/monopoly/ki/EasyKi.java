/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ki;

import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.service.AuctionService;
import de.btu.monopoly.core.service.PlayerService;
import de.btu.monopoly.data.card.CardAction;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.input.IOService;
import java.util.Random;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class EasyKi {

    private static final Logger LOGGER = Logger.getLogger(EasyKi.class.getCanonicalName());
    // Wahrscheinlichkeit (in %) eine Strasse zu kaufen
    private static final int BUY_STREET_CAP = 50;
    // Maximalgebot (in %) fuer die Auktion
    private static final int MAXIMUM_BID = 80;

    public static int jailOption(Player player) {
        int choice;
        CardStack stack = player.getCardStack();
        if (stack.countCardsOfAction(CardAction.JAIL) > 0) {
            choice = 3;
        }
        else if (player.getMoney() > 100) {
            choice = 2;
        }
        else {
            choice = 1;
        }
        IOService.sleep(3000);
        return choice;
    }

    public static int buyPropOption(Player player, PropertyField prop, Random random) {
        int percentage = random.nextInt(100);
        IOService.sleep(3000);
        return (percentage <= BUY_STREET_CAP) ? 1 : 2;
    }

    public static int processActionSequence(Player player, GameBoard board) { //wird zu void
        LOGGER.finer("Der Computergegner hat keine Lust zu bauen, oder sich um Hyoptheken zu kümmern. "
                + "Zum Handeln ist er nicht schlau genug");
        IOService.sleep(3000);
        return 1;
    }

    public static void processBetSequence(Player ki, int maximalGebot) {
        IOService.sleep(2000);
        int originPrice = AuctionService.getAuc().getProperty().getPrice();
        int actualPrice = AuctionService.getHighestBid();
        double percentage = (double) actualPrice / ((double) originPrice / 100);
        int newPrice = actualPrice;
        int aucID = -1;
        for (int i = 0; i < AuctionService.getAuc().getAucPlayers().length; i++) {
            if (AuctionService.getAuc().getAucPlayers()[i][0] == ki.getId()) {
                aucID = i;
            }
        }
        // wenn die KI noch an der Auktion teilnimmt und nicht Höchstbietender ist
        if (AuctionService.getAuc().getAucPlayers()[aucID][2] != 0 && AuctionService.getHighestBidder() != ki.getId()) {
            // wenn sie genuegend Geld hat und noch nicht maximalGebot% des Strassenpreises erreicht sind
            if (PlayerService.checkLiquidity(ki, actualPrice) && percentage < maximalGebot) {
                newPrice += (int) (originPrice * 0.1);
                AuctionService.setBid(ki.getId(), newPrice);
            }
            else {
                AuctionService.playerExit(ki.getId());
            }
        }
    }

    /**
     * @return the MAXIMUM_BID
     */
    public static int getMAXIMUM_BID() {
        return MAXIMUM_BID;
    }
}
