/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.controller.phases;

import static de.btu.monopoly.controller.GameController.LOGGER;
import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.Field;
import java.util.Scanner;
import java.util.logging.Level;

/**
 *
 * @author Christian Prinz
 */
public class InputHandler {

    GameBoard board;

    public InputHandler(GameBoard board) {
        this.board = board;
    }

    /**
     *
     * @param max maximale Anzahl an Auswahlmoeglichkeiten
     * @return int der Durch den Anwender gewaehlt wurde
     */
    public int getUserInput(int max) {

        Scanner scanner = new Scanner(System.in);
        int output = -1;

        // Solange nicht der richtige Wertebereich eingegeben wird, wird die Eingabe wiederholt.
        do {
            LOGGER.log(Level.INFO, "Eingabe: ");
            try {
                output = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.WARNING, "FEHLER: falsche Eingabe!");
            }

            if (output < 1 || output > max) {
                LOGGER.log(Level.INFO, "Deine Eingabe liegt nicht im Wertebereich! Bitte erneut versuchen:");
            }
        } while (output < 1 || output > max);

        return output;
    }

    /**
     * Methode zum Auswaehen einer Strasse die Bearbeitet werden soll in der actionPhase()
     *
     * @param player Spieler der eine Eingabe machen soll
     * @return ein int Wert zu auswaehen einer Strasse
     */
    public int askForField(Player player) {
        String mesg = player.getName() + "! Wähle ein Feld:\n";
        Field[] fields = board.getFields();
        for (int i = 0; i < fields.length; i++) {
            mesg += String.format("[%d] - %s%n", i + 1, fields[i].getName());
        }
        LOGGER.log(Level.INFO, mesg);
        return getUserInput(39);
    }
}
