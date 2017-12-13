/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.input;

import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.ki.EasyKi;
import de.btu.monopoly.ki.HardKi;
import de.btu.monopoly.ki.MediumKi;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class IOService {

    private static final Logger LOGGER = Logger.getLogger(IOService.class.getCanonicalName());

    public static int jailChoice(Player player) {
        int choice = -1;
        switch (player.getKiLevel()) {
            case 0:
            //TODO GUI
            // while not -1 ->Gui

            case 1:
                choice = EasyKi.jailOption(player);
                break;
            case 2:
                choice = MediumKi.jailOption(player);
                break;
            case 3:
                choice = HardKi.jailOption(player);
                break;
            default:
                LOGGER.warning("Illegale KI-Stufe in JailChoice registriert");
        }
        return choice;
    }

    public static int buyPropertyChoice(Player player, PropertyField prop) {
        int choice = -1;
        switch (player.getKiLevel()) {
            case 0:
            //TODO GUI
            // while not -1 ->Gui

            case 1:
                choice = EasyKi.buyPropOption(player, prop);
                break;
            case 2:
                choice = MediumKi.buyPropOption(player, prop);
                break;
            case 3:
                choice = HardKi.buyPropOption(player, prop);
                break;
            default:
                LOGGER.warning("Illegale KI-Stufe in JailChoice registriert");
        }
        return choice;
    }

    public static void actionSequence(Player player, GameBoard board) {
        switch (player.getKiLevel()) {
            case 0:
            //TODO GUI

            case 1:
                EasyKi.processActionSequence(player, board);
                break;
            case 2:
                MediumKi.processActionSequence(player, board);
                break;
            case 3:
                HardKi.processActionSequence(player, board);
                break;
            default:
                LOGGER.warning("Illegale KI-Stufe in JailChoice registriert");
        }
    }

}
