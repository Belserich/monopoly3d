/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ki;

import de.btu.monopoly.core.mechanics.Trade;
import de.btu.monopoly.core.mechanics.TradeOffer;
import de.btu.monopoly.data.player.Player;

/**
 *
 * @author Christian Prinz
 */
public class TradeAi {

    private static int[] para;

    private static final double POOR_MONEY_MULTIPLICATOR = 1.3;
    private static final double RICH_MONEY_MULTIPLICATOR = 0.85;
    private static final int CARD_SELLING_AMOUNT = 200;
    private static final int MINIMUM_ACCEPT_AMOUNT = -50;

    protected static boolean calculateChoice(Trade trade, Player ki) {
        // Was die Ki bekommt:
        TradeOffer supply = trade.getSupply();
        int sMoney = supply.getMoney();
        int[] sProps = supply.getPropertyIds();
        int[] sCards = supply.getCardIds();

        // Was sie geben soll
        TradeOffer demand = trade.getDemand();
        int dMoney = demand.getMoney();
        int[] dProps = demand.getPropertyIds();
        int[] dCards = demand.getCardIds();

        // Anderes:
        int supId = supply.getPlayerId();   // von wem kommt das Angebot
        int balance = 0;                    // balance die entscheidet, ob das Angebot angenommen wird
        para = HardKi.getParameters();

        // prüfen ob die Ki danach geuegend Geld hat
        int rawBalance = sMoney - dMoney;
        if (rawBalance < 0 && (ki.getMoney() + rawBalance) < para[6]) {
            HardKi.chat("Also das kann ich mir nicht leisten", true);
            return false;
        }

        // Geld wird auf die balance gerechnet
        balance += calcMoney(ki, sMoney, dMoney);

        // Karten werden auf die balance gerechnet
        balance += calcCards(ki, sCards, dCards);

        // Einkommende Strassen werden auf balance gerechnet
        for (int propId : sProps) {
            balance += calcProperty(ki, propId, false);
        }
        // Ausgehende Strassen werden auf balance gerechnet
        for (int propId : dProps) {
            balance += calcProperty(ki, propId, true);
        }

        ChatAi.tradeResultMessage(balance, MINIMUM_ACCEPT_AMOUNT);
        return (balance > 0);
    }

    private static int calcMoney(Player ki, int sMoney, int dMoney) {
        int balance = 0;

        if (ki.getMoney() < para[6]) {                      //arm
            balance += sMoney * POOR_MONEY_MULTIPLICATOR;
            balance -= dMoney * POOR_MONEY_MULTIPLICATOR;
        }
        else if (ki.getMoney() < para[4]) {                //normal
            balance += sMoney;
            balance -= dMoney;
        }
        else {                                            //reich
            balance += sMoney * RICH_MONEY_MULTIPLICATOR;
            balance -= dMoney * RICH_MONEY_MULTIPLICATOR;
        }

        return balance;
    }

    private static int calcCards(Player ki, int[] sCards, int[] dCards) {
        int balance = 0;

        // KI nimmt keine GFKarten an
        if (sCards.length > 0) {
            HardKi.chat("Also für Gefängnis-Frei-Karten bezahle ich nichts", true);
        }

        // KI verkauft zum Spielbeginn keine GFKarten, sonst fur festgelegten Wert
        if (HardKi.getSoldProperties() > para[1]) {
            balance += (CARD_SELLING_AMOUNT * dCards.length);
        }
        return balance;
    }

    private static int calcProperty(Player ki, int propId, boolean demanding) {
        // Strasse rausbekommen

        // lukrative Lage beruecksichtigen
        // sind bereits Nachbarn im Besitz
        // komplettiert sie einen Strassenzug (demanding)
        // Wieviel Geld besitzt die KI selbst (reich++)
        return 0; //TODO
    }
}
