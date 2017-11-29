package de.btu.monopoly;

import de.btu.monopoly.controller.GameController;
import de.btu.monopoly.data.*;
import de.btu.monopoly.data.field.*;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class MainTest {

    public static void main(String[] args) throws Exception {
        
        // Schaltet das Logging für die Tests aus
        GameController.logger.setLevel(Level.OFF);
        
        // Variablen:
        GameController gc = new GameController(2);
        gc.init();

        GameBoard board = gc.board;
        Player[] players = gc.players;

        //Testmethoden:
        testGameBoard(board, players);
        testRollMethod(gc, players[0]);
    }

    private static void testRollMethod(GameController gc, Player arg) throws Exception {
        for (int j = 0; j < 1; j++) {
            int[] result = gc.roll(arg);
            for (int i : result) {
                assert i > 0 && i < 7 : "LOL";
            }
        }
    }

    private static void testGameBoard(GameBoard board, Player[] players) {
        //existiert das Board
        assert board != null : "Gamboard nicht initialisiert!";

        //existieren die Felder
        for (Field field : board.getFields()) {
            assert field != null : "Felder nicht initialisiert!";
        }

        //existieren die Spieler
        for (Player player : players) {
            assert player != null : "Spieler nicht initialisiert!";
        }
    }

}
