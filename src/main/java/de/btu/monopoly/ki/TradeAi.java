/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ki;

import de.btu.monopoly.core.mechanics.Trade;
import de.btu.monopoly.core.mechanics.TradeOffer;
import de.btu.monopoly.data.field.FieldManager;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;

/**
 *
 * @author Christian Prinz
 */
public class TradeAi {

    private static int[] para;

    // Value = Wertveraenderung in %
    private static final int VALUE_OF_MONEY_POOR = 130;
    private static final int VALUE_OF_MONEY_RICH = 85;
    private static final int CARD_SELLING_AMOUNT = 50;
    private static final int MINIMUM_ACCEPT_AMOUNT = -50;
    private static final int VALUE_OF_CHEAP_PROP = 70;
    private static final int VALUE_OF_LUCRATIVE_PROP = 150;
    private static final int VALUE_OF_EXPANSIVE_PROP = 110;
    private static final int VALUE_IF_SOME_NEIGHBOURS_OWNED = 110;
    private static final int VALUE_IF_ALL_NEIGHBOURS_OWNED = 130;
    private static final int VALUE_IF_DEMANDING_COMPLETING_PROP = 200; //falls eine komplettierende Straße verkauft wird
    private static final int VALUE_IF_KI_IS_SUPERRICH = 110;

    protected static boolean calculateChoice(Trade trade, Player ki, FieldManager fima) {
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
        int balance = 0;                    // balance die entscheidet, ob das Angebot angenommen wird
        para = HardKi.getParameters();
        String name = ki.getName();

        // prüfen ob die Ki danach geuegend Geld hat
        int rawBalance = sMoney - dMoney;
        if (rawBalance < 0 && (ki.getMoney() + rawBalance) < para[6]) {
            HardKi.chat(name, "Also das kann ich mir nicht leisten", true);
            return false;
        }

        // Geld wird auf die balance gerechnet
        balance += calcMoney(ki, sMoney, dMoney);

        // Karten werden auf die balance gerechnet
        balance += calcCards(ki, sCards, dCards);

        // Einkommende Strassen werden auf balance gerechnet
        for (int propId : sProps) {
            balance += calcProperty(ki, propId, fima, false);
        }
        // Ausgehende Strassen werden auf balance gerechnet
        for (int propId : dProps) {
            balance += calcProperty(ki, propId, fima, true);
        }

        ChatAi.tradeResultMessage(name, balance, MINIMUM_ACCEPT_AMOUNT);
        return (balance > 0);
    }

    private static int calcMoney(Player ki, int sMoney, int dMoney) {
        int balance = 0;

        if (ki.getMoney() < para[6]) {                      //arm
            balance += sMoney * VALUE_OF_MONEY_POOR / 100;
            balance -= dMoney * VALUE_OF_MONEY_POOR / 100;
        }
        else if (ki.getMoney() < para[4]) {                //normal
            balance += sMoney;
            balance -= dMoney;
        }
        else {                                            //reich
            balance += sMoney * VALUE_OF_MONEY_RICH / 100;
            balance -= dMoney * VALUE_OF_MONEY_RICH / 100;
        }

        return balance;
    }

    private static int calcCards(Player ki, int[] sCards, int[] dCards) {
        int balance = 0;

        // KI nimmt keine GFKarten an
        if (sCards.length > 0) {
            HardKi.chat(ki.getName(), "Also für Gefängnis-Frei-Karten bezahle ich nichts", true);
        }

        // KI verkauft zum Spielbeginn keine GFKarten, sonst fur festgelegten Wert
        if (HardKi.getSoldProperties() > para[1]) {
            balance += (CARD_SELLING_AMOUNT * dCards.length);
        }
        return balance;
    }

    private static int calcProperty(Player ki, int propId, FieldManager fima, boolean demanding) {
        int multiplicator = 1;

        // Strasse rausbekommen
        PropertyField prop = (PropertyField) fima.getField(propId);

        // lukrative Lage beruecksichtigen
        if (propId < para[2]) {              // billig
            multiplicator *= (VALUE_OF_CHEAP_PROP / 100);
        }
        else if (propId < para[3]) {       // lukrativ
            multiplicator *= (VALUE_OF_LUCRATIVE_PROP / 100);
        }
        else {                            // teuer
            multiplicator *= (VALUE_OF_EXPANSIVE_PROP / 100);
        }

        // sind bereits Nachbarn im Besitz
        if (HardKi.someNeighboursOwned(prop, ki)) {
            multiplicator *= (VALUE_IF_SOME_NEIGHBOURS_OWNED / 100);
        }

        // komplettiert sie einen Strassenzug (demanding)
        if (HardKi.allNeighboursOwned(prop, ki)) {
            multiplicator *= demanding
                    ? (VALUE_IF_DEMANDING_COMPLETING_PROP / 100)
                    : (VALUE_IF_ALL_NEIGHBOURS_OWNED / 100);
        }

        // Ist die KI superreich
        multiplicator *= (ki.getMoney() > para[4]) ? (VALUE_IF_KI_IS_SUPERRICH / 100) : 1;

        // Multiplikator anwenden
        return prop.getTradingValue() * multiplicator;
    }
}
