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
        GameController.logger.setLevel(Level.OFF);  // Logger des GameControllers ausschalten
        gc.init();

        GameBoard board = gc.board;
        Player[] players = gc.players;

        // Testmethoden:
        testGameBoard(board, players);
        testRollMethod(gc, players[0]);
        testGoField(gc, players);
        // Eckfeld betreten (Außer GoToJail)
        // pleite gehen
        // Steuer auf TaxFeld bezahlen
        testBuyStreet(gc, players[0]);
        // Straße bebauen (Haus kaufen)
        // Haus verkaufen
        testPayRent(gc, players);
        // Hypothek aufnehmen
        // Hypothek bezahlen
        // betreten des GoToJailFeldes
        testDoubletToJail(gc, players);
        // aus dem Gefaengnis mit Freikarte
        // aus dem Gefaengnis freiwuerfeln
        testPayPrisonDeposit(gc, players);

    }

    /**
     * @author Patrick Kalweit
     * @param gc
     * @param player
     */
    private static void testGoField(GameController gc, Player[] players) {
        //Spieler bestimmen
        Player player1 = players[0];
        Player player2 = players[1];

        //Spielern Geld geben
        player1.setMoney(800);
        player2.setMoney(800);

        //Spieler auf Start setzen
        player1.setPosition(0);
        player2.setPosition(0);

        //Spieler bewegen (1 direkt auf Los, 2 ueber Los hinweg)
        gc.movePlayer(player1, 40);
        gc.movePlayer(player2, 50);

        //Test auf korrekte Ueberweisung des Los - Geldes
        assert player1.getMoney() == 1000 : "failed : Los Geld falsch oder garnicht ueberwiesen!";
        assert player2.getMoney() == 1000 : "failed : Los Geld falsch oder garnicht ueberwiesen!";
    }

    /**
     * @author Patrick Kalweit
     * @param gc
     * @param players
     */
    private static void testDoubletToJail(GameController gc, Player[] players) {
        //Spieler und Variablen initialisieren
        Player prisoner = players[0];
        gc.doubletCounter = 2;

        //auf den naechsten Pasch warten
        do {
            gc.rollPhase(prisoner);
        } while (gc.doubletCounter != 3);

        assert prisoner.isInJail() == true : "failed : Spieler ist nicht im Gefaengnis!";

        prisoner.setInJail(false);
        gc.doubletCounter = 0;

    }

    /**
     * @author Patrick Kalweit
     * @param gc
     * @param players
     */
    private static void testPayPrisonDeposit(GameController gc, Player[] players) {
        //Spieler und Variablen initialisieren
        Player prisoner = players[0];
        prisoner.setMoney(1500);
        prisoner.setInJail(true);

        //Frei kaufen
        System.out.println("Gebe eine 2 ein!"); //TODO Userinput automatisieren
        gc.jailPhase(prisoner);

        assert prisoner.isInJail() == false : "failed : Spieler ist noch im Gefaengniss!";

    }

    private static void testBuyStreet(GameController gc, Player player) {
        Property street = (Property) gc.board.getFields()[1];
        player.setMoney(1000);
        gc.buyStreet(player, street, street.getPrice());

        assert player.getMoney() == 1000 - street.getPrice() : "failed : Geld bei Straßenkauf nicht Abgebucht";
        assert street.getOwner() == player : "failed : Straßenbesitz wurde nicht geaendert";

        System.out.println("passed : Straße kaufen");
    }

    /**
     * @author Christian Prinz
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
        gc.fieldPhase(player, rollResult);                          // -100 = 900
        Property bahnhof2 = (Property) gc.board.getFields()[15];    // owner einen Bahnhof wegnehmen
        bahnhof2.setOwner(null);                                    // jetzt mit 3facher Miete
        gc.fieldPhase(player, rollResult);                          // -75 = 825
        assert player.getMoney() == 825 : "failed : falsche Bahnhofsmiete abgezogen!";
        assert owner.getMoney() == 1175 : "failed : falsche Bahnhofsmiete überwiesen!";

        // Test fuer Miete zahlen auf Werk (10 x Augenzahl)
        player.setPosition(12);
        gc.fieldPhase(player, rollResult);                          // -10*11 = 715
        Property werk2 = (Property) gc.board.getFields()[28];       // owner ein Werk wegnehmen
        werk2.setOwner(null);                                       // jetzt mit 4*Augenzahl
        gc.fieldPhase(player, rollResult);                          // -4*11 = 671
        assert player.getMoney() == 671 : "failed : falsche Werksmiete abgezogen!";
        assert owner.getMoney() == 1329 : "failed : falsche Werksmiete überwiesen!";

        // Test fuer Miete zahlen auf Strasse (alle Strassen im Besitz)
        player.setPosition(1);
        gc.fieldPhase(player, rollResult);                          // -4 = 667

        assert player.getMoney() == 667 : "failed : falsche Strassenmiete abgezogen!";
        assert owner.getMoney() == 1333 : "failed : falsche Strassenmiete überwiesen!";

        StreetField strasse1 = (StreetField) gc.board.getFields()[1];
        strasse1.setHouseCount(5);
        gc.fieldPhase(player, rollResult);                          // - 250 = 412

        assert player.getMoney() == 417 : "failed : falsche Strassenmiete abgezogen!";
        assert owner.getMoney() == 1583 : "failed : falsche Strassenmiete überwiesen!";

        // Owner  alle Strassen wegnehmen (5, 6, 8, 9, 12)
        for (Field field : gc.board.getFields()) {
            if (field instanceof Property) {
                Property actual = (Property) field;
                actual.setOwner(null);
            }
        }

        // Spielergeld zurücksetzen
        owner.setMoney(1500);
        player.setMoney(1500);

        System.out.println("passed : Miete zahlen auf allen Grundstuecken!");
    }

    private static void testRollMethod(GameController gc, Player arg) {
        for (int j = 0; j < 1000; j++) {
            int[] result = gc.roll(arg);
            for (int i : result) {
                assert i > 0 && i < 7 : "failed : Wuerfelergebnis ungueltig!";
            }
        }
        System.out.println("passed : Wuerfeln");
    }

    private static void testGameBoard(GameBoard board, Player[] players) {
        //existiert das Board
        assert board != null : "failed : Gamboard nicht initialisiert!";

        //existieren die Felder
        for (Field field : board.getFields()) {
            assert field != null : "failed : Felder nicht initialisiert!";
        }

        //existieren die Spieler
        for (Player player : players) {
            assert player != null : "failed : Spieler nicht initialisiert!";
        }
        System.out.println("passed : Initialisierung");
    }

}
