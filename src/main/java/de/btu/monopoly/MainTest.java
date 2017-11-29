package de.btu.monopoly;

import de.btu.monopoly.controller.GameController;
import de.btu.monopoly.data.*;
import de.btu.monopoly.data.field.*;
import java.util.logging.Level;

/**
 * In dieser Klasse werden saemtliche User-Storys einzeln getestet. Ist eine User-Story erfolgreich gestestet worden, wird ein
 * <code> System.out.println("passed : User-Story")</code> ausgegeben. Für jeden Testparameter wird im Falle eines
 * fehlgeschlagenen Tests ein <code> assert [TEST] : "failed : Fehlerbeschreibung";</code> ausgegeben.
 *
 * @author Christian Prinz
 */
public class MainTest {

    public static void main(String[] args) throws Exception {

        // Variablen:
        GameController gc = new GameController(2);
        gc.init();

        GameBoard board = gc.board;
        Player[] players = gc.players;

        // Logger des GamControllers ausschalten:
        GameController.logger.setLevel(Level.OFF);

        //Testmethoden:
        testGameBoard(board, players);
        testRollMethod(gc, players[0]);
        // LOS Feld (movePlayer())
        // Eckfeld betreten (Außer GoToJail)
        // pleite gehen
        // Steuer auf TaxFeld bezahlen
        // Grundstueck kaufen
        testPayRent(gc, players);
        // Hypothek aufnehmen
        // Hypothek bezahlen
        // Straße bebauen
        // Haus kaufen
        // Haus verkaufen
        // betreten des GoToJailFeldes
        // 3 Paesche ins Gefaengnis
        // aus dem Gefaengnis mit Freikarte
        // aus dem Gefaengnis freiwuerfeln
        // aus dem Gefaengnis frei kaufen

    }

    /**
     *
     * @param gc
     * @param players
     */
    private static void testPayRent(GameController gc, Player[] players) {
        // Spieler bestimmen
        Player player = players[0];
        Player owner = players[1];

        // Spielern Geld geben
        owner.setMoney(1000);
        player.setMoney(1000);

        // Owner  alle Strassen geben (5, 6, 12)
        for (Field field : gc.board.getFields()) {
            if (field instanceof Property) {
                Property actual = (Property) field;
                actual.setOwner(owner);
            }
        }

        // Wuerfelergebnis setzen (6 + 5 = 11)
        int[] rollResult = {6, 5};

        // Test fuer Miete zahlen auf Bahnhof (4fache Bahnhofsmiete abziehen)
        player.setPosition(5);
        gc.fieldPhase(player, rollResult);
        assert player.getMoney() == 900 : "failed : falsche Bahnhofsmiete abgezogen!";
        assert owner.getMoney() == 1100 : "failed : falsche Bahnhofsmiete überwiesen!";

        // Test fuer Miete zahlen auf Werk (10 x Augenzahl)
        player.setPosition(12);
        gc.fieldPhase(player, rollResult);
        assert player.getMoney() != 790 : "failed : falsche Werksmiete abgezogen!"; //TODO
        assert owner.getMoney() != 1290 : "failed : falsche Werksmiete überwiesen!"; //TODO

        // Owner  alle Strassen wegnehmen (5, 6, 8, 9, 12)
        for (Field field : gc.board.getFields()) {
            if (field instanceof Property) {
                Property actual = (Property) field;
                actual.setOwner(null);
            }
        }
    }

    private static void testRollMethod(GameController gc, Player arg) {
        for (int j = 0; j < 1000; j++) {
            int[] result = gc.roll(arg);
            for (int i : result) {
                assert i > 0 && i < 7 : "Würfelergebnis ungültig!";
            }
        }
        System.out.println("passed : Würfeln");
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
            assert player != null : "failed : Spieler nicht initialisiert!";
        }
        System.out.println("passed : Initialisierung");
    }

}
