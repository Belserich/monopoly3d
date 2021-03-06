/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ui;

import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.ui.GameSceneManager.Popup;

/**
 *
 * @author patrick
 */
public class GuiTrade {

    private Player tradeStarter;
    private Player tradePartner;
    private int[] yourPropIds;
    private int[] partnersPropIds;
    private int[] yourCardIds;
    private int[] partnersCardIds;
    private int yourMoney;
    private int partnersMoney;
    private int yourCardAmount;
    private int partnersCardAmount;
    private Popup waitForResponsePopup;

    public GuiTrade() {

        this.yourMoney = 0;
        this.partnersMoney = 0;
        this.yourCardAmount = 0;
        this.partnersCardAmount = 0;
    }

    public Player getTradeStarter() {
        return tradeStarter;
    }

    public void setTradeStarter(Player tradeStarter) {
        this.tradeStarter = tradeStarter;
    }

    public Player getTradePartner() {
        return tradePartner;
    }

    public void setTradePartner(Player tradePartner) {
        this.tradePartner = tradePartner;
    }

    public int[] getYourPropIds() {
        return yourPropIds;
    }

    public void setYourPropIds(int[] yourPropIds) {
        this.yourPropIds = yourPropIds;
    }

    public int[] getPartnersPropIds() {
        return partnersPropIds;
    }

    public void setPartnersPropIds(int[] partnersPropIds) {
        this.partnersPropIds = partnersPropIds;
    }

    public int[] getYourCardIds() {
        return yourCardIds;
    }

    public void setYourCardIds(int[] yourCardIds) {
        this.yourCardIds = yourCardIds;
    }

    public int[] getPartnersCardIds() {
        return partnersCardIds;
    }

    public void setPartnersCardIds(int[] partnersCardIds) {
        this.partnersCardIds = partnersCardIds;
    }

    public int getYourMoney() {
        return yourMoney;
    }

    public void setYourMoney(int yourMoney) {
        this.yourMoney = yourMoney;
    }

    public int getPartnersMoney() {
        return partnersMoney;
    }

    public void setPartnersMoney(int partnersMoney) {
        this.partnersMoney = partnersMoney;
    }

    public int getYourCardAmount() {
        return yourCardAmount;
    }

    public void setYourCardAmount(int yourCardAmount) {
        this.yourCardAmount = yourCardAmount;
    }

    public int getPartnersCardAmount() {
        return partnersCardAmount;
    }

    public void setPartnersCardAmount(int partnersCardAmount) {
        this.partnersCardAmount = partnersCardAmount;
    }

    /**
     * @return the waitForResponsePopup
     */
    public Popup getWaitForResponsePopup() {
        return waitForResponsePopup;
    }

    /**
     * @param waitForResponsePopup the waitForResponsePopup to set
     */
    public void setWaitForResponsePopup(Popup waitForResponsePopup) {
        this.waitForResponsePopup = waitForResponsePopup;
    }

}
