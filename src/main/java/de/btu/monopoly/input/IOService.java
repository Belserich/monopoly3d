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
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.networkClasses.BroadcastPlayerChoiceRequest;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class IOService {

    private static final Logger LOGGER = Logger.getLogger(IOService.class.getCanonicalName());
    private static GameClient client;

    public static int jailChoice(Player player) {
        int choice = -1;
        switch (player.getKiLevel()) {
            case 0:
                //TODO GUI
                // while not -1 ->Gui
                getClientChoice(player, 3);
                break;
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
                choice = getClientChoice(player, 2);
                break;
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

    // wird noch zu void, wenn GUI fertig
    public static int actionSequence(Player player, GameBoard board) {
        int choice = 1; //kommt weg
        switch (player.getKiLevel()) {
            case 0:
                //TODO GUI
                choice = getClientChoice(player, 5);
                break;
            case 1:
                choice = EasyKi.processActionSequence(player, board);
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
        return choice;
    }

    /**
     * @param aClient the client to set
     */
    public static void setClient(GameClient aClient) {
        client = aClient;
    }

    private static int getClientChoice(Player player, int max) {
        Player thisPlayer = client.getPlayerOnClient();

        if (thisPlayer == player) {
            int choice = InputHandler.getUserInput(max);
            BroadcastPlayerChoiceRequest packet = new BroadcastPlayerChoiceRequest();
            packet.setChoice(choice);
            client.sendTCP(packet);
            return choice;
        } else {
            do {
                BroadcastPlayerChoiceRequest[] packets = client.getPlayerChoiceObjects();
                if (packets.length > 1) {
                    LOGGER.warning("Fehler: Mehr als ein choice-Packet registriert!");
                    return -1;
                } else if (packets.length == 1) {
                    return packets[0].getChoice();
                }
            } while (true);
        }
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            LOGGER.warning("FEHLER: " + ex);
            Thread.currentThread().interrupt();
        }
    }

}
