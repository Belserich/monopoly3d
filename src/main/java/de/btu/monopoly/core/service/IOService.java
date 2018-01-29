/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.core.service;

import de.btu.monopoly.Global;
import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.mechanics.Auction;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.ki.EasyKi;
import de.btu.monopoly.ki.HardKi;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.data.BroadcastPlayerChoiceRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class IOService {

    private static final Logger LOGGER = Logger.getLogger(IOService.class.getCanonicalName());
    private static GameClient client;

    private static int JAIL = 0;
    private static int ACTION = 1;
    private static int BUY = 2;
    private static int FIELD = 3;

    public static int jailChoice(Player player) {
        int choice = -1;
        switch (player.getAiLevel()) {
            case 0:
                if (Global.RUN_IN_CONSOLE) {
                    choice = getClientChoice(player, 3);
                }
                else {
                    choice = getClientChoiceFromGUI(player, JAIL, null);
                }
                break;
            case 1:
                choice = EasyKi.jailOption(player);
                break;
            case 2:
                choice = HardKi.jailOption(player);
                break;
            default:
                LOGGER.warning("Illegale KI-Stufe in JailChoice registriert");
        }
        return choice;
    }

    public static int buyPropertyChoice(Player player, PropertyField prop) {
        int choice = -1;
        switch (player.getAiLevel()) {
            case 0:

                if (Global.RUN_IN_CONSOLE) {
                    choice = getClientChoice(player, 2);
                }
                else {
                    choice = getClientChoiceFromGUI(player, BUY, null);
                }

                break;
            case 1:
                choice = EasyKi.buyPropOption(player, prop);
                break;
            case 2:
                choice = HardKi.buyPropOption(player, prop);
                break;
            default:
                LOGGER.warning("Illegale KI-Stufe in JailChoice registriert");
        }
        return choice;
    }

    public static int actionSequence(Player player, GameBoard board) {
        int choice = 1;
        switch (player.getAiLevel()) {
            case 0:
                if (Global.RUN_IN_CONSOLE) {
                    choice = getClientChoice(player, 6);
                }
                else {
                    choice = getClientChoiceFromGUI(player, ACTION, null);
                }
                break;
            case 1:
                choice = EasyKi.processActionSequence();
                break;
            case 2:
                choice = HardKi.processActionSequence(player, board);
                break;
            default:
                LOGGER.warning("Illegale KI-Stufe in JailChoice registriert");
        }
        return choice;
    }

    public static void betSequence(Auction auc) {
        // Liste aus KI-Spielern erzeugen und shufflen
        List<Player> aiPlayers = new ArrayList<>();
        Arrays.stream(auc.getPlayers()) // alle Mitspieler
                .filter(p -> p.getAiLevel() > 0) // die eine KI sind
                .filter(p -> p.getId() != auc.getHighestBidder()) // und nicht das höchste Gebot haben
                .filter(p -> AuctionService.isStillActive(p)) // und noch an der Auktion teinehmen
                .forEach(p -> aiPlayers.add(p));
        if (aiPlayers.isEmpty()) {                                  // falls keine KIs mitspielen -> return
            return;
        }
        Collections.shuffle(aiPlayers);                             // Liste shuffeln
        Player rndAi = aiPlayers.get(0);                            // erster aus der Liste ist dran

        LOGGER.log(Level.FINE, "{0} (KI) nimmt an Auktion teil.", rndAi.getName());
        switch (rndAi.getAiLevel()) {
            case 0:
                break;
            case 1:
                EasyKi.processBetSequence(rndAi, EasyKi.getMAXIMUM_BID());
                break;
            case 2:
                HardKi.processBetSequence(rndAi);
                break;
            default:
                LOGGER.warning("Illegale KI-Stufe in BetSequence registriert");
        }
    }

    /**
     * @param aClient the client to set
     */
    public static void setClient(GameClient aClient) {
        client = aClient;
    }

    public static int getClientChoice(Player player, int max) {
        boolean isChoiceFromThisClient = player == client.getPlayerOnClient();
        if (isChoiceFromThisClient) {
            int choice = getUserInput(max);
            BroadcastPlayerChoiceRequest packet = new BroadcastPlayerChoiceRequest();
            packet.setChoice(choice);
            client.sendTCP(packet);
            return choice;
        }
        else {
            BroadcastPlayerChoiceRequest request = (BroadcastPlayerChoiceRequest) client.waitForObjectOfClass(BroadcastPlayerChoiceRequest.class);
            return request.getChoice();
        }
    }

    public static int getClientChoiceFromGUI(Player player, int type, String[] fields) {
        boolean isChoiceFromThisClient = player == client.getPlayerOnClient();
        if (isChoiceFromThisClient) {
            int choice = -1;
            if (type == JAIL) {
                choice = Global.ref().getGameSceneManager().jailChoicePopup();
            }
            if (type == ACTION) {
                choice = Global.ref().getGameSceneManager().actionSequencePopup();
            }
            if (type == BUY) {
                choice = Global.ref().getGameSceneManager().buyPropertyPopup();
            }
            if (type == FIELD) {
                choice = Global.ref().getGameSceneManager().askForFieldPopup(player, fields);
            }

            BroadcastPlayerChoiceRequest packet = new BroadcastPlayerChoiceRequest();
            packet.setChoice(choice);
            client.sendTCP(packet);
            return choice;
        }
        else {
            BroadcastPlayerChoiceRequest request = (BroadcastPlayerChoiceRequest) client.waitForObjectOfClass(BroadcastPlayerChoiceRequest.class);
            return request.getChoice();
        }
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            LOGGER.log(Level.WARNING, "FEHLER: {0}", ex);
            Thread.currentThread().interrupt();
        }
    }

    public static void sleepDeep(int millis) {
        try {
            for (int i = 0; i < millis / 10; i++) {
                Thread.sleep(10);
            }
        } catch (InterruptedException ex) {
            LOGGER.log(Level.WARNING, "FEHLER: {0}", ex);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Nimmt Spielereingaben entgegen.
     *
     * @param max maximale Anzahl an Auswahlmoeglichkeiten
     * @return int der Durch den Anwender gewaehlt wurde
     */
    public static int getUserInput(int max) {
        Scanner scanner = new Scanner(System.in);
        int output = -1;
        do {
            LOGGER.log(Level.INFO, "Eingabe: ");
            try {
                output = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.WARNING, "Fehler: falsche Eingabe!");
            }

            if (output < 1 || output > max) {
                LOGGER.log(Level.INFO, "Deine Eingabe liegt nicht im Wertebereich! Bitte erneut versuchen.");
            }
        } while (output < 1 || output > max);
        return output;
    }

    /**
     * Methode zum Auswaehen einer Strasse die Bearbeitet werden soll in der actionPhase()
     *
     * @param player Spieler der eine Eingabe machen soll
     * @param fieldNames Namen der zur Wahl stehenden Felder
     * @return ein int Wert zu auswaehen einer Strasse
     */
    public static int askForField(Player player, String[] fieldNames) {

        String mesg = player.getName() + "! Wähle ein Feld:\n";
        for (int i = 0; i < fieldNames.length; i++) {
            mesg += String.format("[%d] - %s%n", i + 1, fieldNames[i]);
        }
        LOGGER.info(mesg);
        if (Global.RUN_IN_CONSOLE) {
            return IOService.getClientChoice(player, 39);
        }
        else {
            return IOService.getClientChoiceFromGUI(player, FIELD, fieldNames);
        }
    }

    public static String askForString() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public static Game getGame() {
        return client.getGame();
    }
}
