/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.controller.phases;

import static de.btu.monopoly.controller.GameController.LOGGER;
import de.btu.monopoly.data.Player;
import java.util.logging.Level;

/**
 *
 * @author Christian Prinz
 */
public class RollPhase {

    Player player;
    PlayerManager pm;
    //int doubletCounter;
    //int[] rollResult;

    public RollPhase(Player player, PlayerManager pm) {
        this.player = player;
        this.pm = pm;
    }

    /**
     * die Wurfphase (wuerfeln und ziehen)
     *
     * @param player Spieler in der Wurfphase
     */
    public int[] compute(int doubletCount) {

        int[] rollResult = null;
        LOGGER.log(Level.INFO, player.getName() + " ist dran mit würfeln.");
        if (!(player.isInJail())) { //Gefaengnis hat eigenes Wuerfeln
            rollResult = pm.roll(player);
            doubletCount += (rollResult[0] == rollResult[1]) ? 1 : 0;
            if (doubletCount >= 3) {
                LOGGER.log(Level.INFO, player.getName() + " hat seinen 3. Pasch und geht nicht über LOS, direkt ins Gefängnis!");
                pm.moveToJail(player);
            }
        }
        if (!(player.isInJail())) { //kann sich nach wuerfeln aendern
            pm.move(player, rollResult[0] + rollResult[1]);
        }
        return rollResult;
    }

}
