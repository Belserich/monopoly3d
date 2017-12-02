/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.controller.phases;

import static de.btu.monopoly.controller.GameController.CURRENCY_TYPE;
import static de.btu.monopoly.controller.GameController.LOGGER;
import de.btu.monopoly.data.Player;
import java.util.logging.Level;

/**
 *
 * @author Christian Prinz
 */
public class JailPhase {

    Player player;
    PlayerManager pm;
    InputManager im;

    public JailPhase(Player player, PlayerManager pm, InputManager im) {
        this.player = player;
        this.pm = pm;
        this.im = im;
    }

    /**
     * Gefängnisphase
     *
     * @param player Spieler in der Gefaengnisphase
     */
    public void compute() {
        boolean repeat;
        do {
            repeat = false;
            LOGGER.log(Level.INFO, player.getName() + "ist im Gefängnis und kann: \n1. 3 mal Würfeln, um mit einem Pasch freizukommen "
                    + "\n2. Bezahlen (50€) \n3. Gefängnis-Frei-Karte benutzen");

            switch (im.getUserInput(3)) { // @GUI
                // OPTION 1: wuerfeln (bis zu drei mal)
                case 1:
                    int[] result = pm.roll(player);                 // wuerfeln
                    if (!(result[0] == result[1])) {       // kein Pasch -> im Gefaengnis bleiben
                        LOGGER.log(Level.INFO, player.getName() + " hat keinen Pasch und bleibt im Gefängnis.");
                        player.addDayInJail();
                    } else {                        // sonst frei
                        LOGGER.log(Level.INFO, player.getName() + " hat einen Pasch und ist frei.");
                        player.setInJail(false);
                        player.setDaysInJail(0);
                    }
                    // Wenn drei mal kein Pasch dann bezahlen
                    if (player.getDaysInJail() == 3) {
                        if (pm.checkLiquidity(player, 50)) {
                            LOGGER.log(Level.INFO, player.getName() + "hat schon 3 mal keinen Pasch und muss nun 50"
                                    + CURRENCY_TYPE + " zahlen!");
                            pm.takeMoneyUnchecked(player, 50);
                            player.setInJail(false);
                            player.setDaysInJail(0);
                        } else {        //wenn pleite game over
                            LOGGER.log(Level.INFO, player.getName() + " hat schon 3 mal gewürfelt und kann nicht zahlen.");
                            pm.bankrupt(player);
                        }
                    }
                    break;

                //OPTION 2: Bezahlen
                case 2:
                    if (pm.checkLiquidity(player, 50)) {
                        LOGGER.log(Level.INFO, player.getName() + " hat 50" + CURRENCY_TYPE + " gezahlt und ist frei!");
                        pm.takeMoneyUnchecked(player, 50);
                        player.setInJail(false);
                        player.setDaysInJail(0);
                    } else { // muss in der GUI deaktiviert sein!!!
                        LOGGER.log(Level.INFO, player.getName() + " hat kein Geld um sich freizukaufen.");
                        repeat = true;
                    }
                    break;

                //OPTION 3: Freikarte ausspielen
                case 3:
                    if (player.getJailCardAmount() > 0) {
                        LOGGER.log(Level.INFO, player.getName() + " hat eine Gefängnis-Frei-Karte benutzt.");
                        player.removeJailCard(); // TODO jail-card
                        player.setInJail(false);
                        player.setDaysInJail(0);
                    } else { // muss in der GUI deaktiviert sein!!!
                        LOGGER.log(Level.INFO, player.getName() + " hat keine Gefängnis-Frei-Karten mehr.");
                        repeat = true;
                    }
                    break;

                default:
                    LOGGER.log(Level.WARNING, "FEHLER: Gefängnis-Switch überschritten");
                    repeat = true;
                    break;

            }
        } while (repeat);

    }
}
