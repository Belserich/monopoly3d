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
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class EasyKi {

    private static final Logger LOGGER = Logger.getLogger(EasyKi.class.getCanonicalName());

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

    public static int buyPropOption(Player player, PropertyField prop) {
        int choice;
        int percentage = PlayerService.getRng().nextInt(100);
        if (percentage <= 50) {
            choice = 1;
        }
        else {
            choice = 2;
        }
        IOService.sleep(3000);
        return choice;
    }

    public static int processActionSequence(Player player, GameBoard board) { //wird zu void
        LOGGER.finer("Der Computergegner hat keine Lust zu bauen, oder sich um Hyoptheken zu kümmern. "
                + "Zum Handeln ist er nicht schlau genug");
        IOService.sleep(3000);
        return 1;
    }

    public static void processBetSequence(Player ki) {
        IOService.sleep(1000);
        int originPrice = AuctionService.getAuc().getProperty().getPrice();
        int actualPrice = AuctionService.getHighestBid();
        double percentage = (actualPrice / (originPrice / 100));
        int newPrice = actualPrice;
        int aucID = -1;
        for (int i = 0; i < AuctionService.getAuc().getAucPlayers().length; i++) {
            if (AuctionService.getAuc().getAucPlayers()[i][0] == ki.getId()) {
                aucID = i;
            }
        }
        // wenn die KI noch an der Auktion teilnimmt und nicht Höchstbietender ist
        if (AuctionService.getAuc().getAucPlayers()[aucID][2] != 0 && AuctionService.getHighestBidder() != ki.getId()) {
            // wenn sie genuegend Geld hat und noch nicht 80% des Strassenpreises erreicht sind
            if (PlayerService.checkLiquidity(ki, actualPrice) && percentage < 80) {
                newPrice += (int) (originPrice * 0.1);
                AuctionService.setBid(ki.getId(), newPrice);
            }
            else {
                AuctionService.playerExit(ki.getId());
            }
        }
    }
}
