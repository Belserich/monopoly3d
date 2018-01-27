/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ki;

import de.btu.monopoly.core.service.AuctionService;
import de.btu.monopoly.core.service.IOService;
import de.btu.monopoly.core.service.PlayerService;
import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;

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

    /**
     *
     * @param player ki
     * @return int fuer die Wahl der Option im Gefaengnis 1 - wuerfeln, 2 - bezahlen, 3 - GFKarte
     */
    public static int jailOption(Player player) {
        int choice;
        CardStack stack = player.getCardStack();
        if (stack.countCardsOfAction(Card.Action.JAIL) > 0) {
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

    /**
     *
     * @param player
     * @param prop
     * @return int für die Kaufentscheidung 1 - kaufen , 2 - nicht kaufen
     */
    public static int buyPropOption(Player player, PropertyField prop) {
        Random random = IOService.getGame().getRandom();
        int percentage = random.nextInt(100);
        IOService.sleep(3000);
        return (percentage <= BUY_STREET_CAP) ? 1 : 2;
    }

    /**
     *
     * @return 1 fur die Wahl in der Aktionsphase nichts zu tun
     */
    public static int processActionSequence() { //wird zu void
        LOGGER.finer("Der Computergegner hat keine Lust zu bauen, oder sich um Hyoptheken zu kümmern. "
                + "Zum Handeln ist er nicht schlau genug");
        IOService.sleep(3000);
        return 1;
    }

    /**
     *
     * @param ki bietende KI
     * @param maximalGebot bis zu welcher Grenze (in %) bietet die KI fuer die Strasse mit
     */
    public static void processBetSequence(Player ki, int maximalGebot) {
        IOService.sleep(2000);
        int originPrice = AuctionService.getAuc().getProperty().getPrice();
        int actualPrice = AuctionService.getHighestBid();
        double percentage = (double) actualPrice / ((double) originPrice / 100);
        int newPrice = actualPrice + (int) (originPrice * 0.1);
        int aucID = -1;
        for (int i = 0; i < AuctionService.getAuc().getAucPlayers().length; i++) {
            if (AuctionService.getAuc().getAucPlayers()[i][0] == ki.getId()) {
                aucID = i;
            }
        }
        // wenn die KI noch an der Auktion teilnimmt und nicht Höchstbietender ist
        if (AuctionService.getAuc().getAucPlayers()[aucID][2] != 0 && AuctionService.getHighestBidder() != ki.getId()) {
            // wenn sie genuegend Geld hat und noch nicht maximalGebot% des Strassenpreises erreicht sind
            if (PlayerService.checkLiquidity(ki, newPrice) && percentage < maximalGebot) {
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
