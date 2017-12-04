//Imports

import de.btu.monopoly.core.*;
import de.btu.monopoly.data.*;
import de.btu.monopoly.data.field.*;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Patrick Kalweit
 */
public class MonopolyUnitTest {

    private static Game gc;
    private static GameBoard board;
    private static Player[] players;
    private static FieldManager fm;

    @Test
    public void testGameBoard() {

        //initialisierung
        gc = new Game(2);
        gc.init();
        board = gc.board;
        players = gc.players;
        fm = gc.getFieldManager();

        //existiert das Gameboard
        Assert.assertTrue("Gameboard nicht initialisiert", gc.board != null);

        //existieren die Felder
        for (Field field : board.getFields()) {
            Assert.assertTrue("Felder nicht initialisiert", field != null);
        }

        //existieren die Spieler
        for (Player player : players) {
            Assert.assertTrue("Spieler nicht initialisiert", player != null);
        }
    }

    @Test
    public void testRoll() {

        //initialisierung
        gc = new Game(1);
        gc.init();
        board = gc.board;
        players = gc.players;
        fm = gc.getFieldManager();
        Player patrick = players[0];

        for (int j = 0; j < 1000; j++) {
            int[] result = PlayerService.roll(patrick);
            for (int i : result) {
                Assert.assertTrue("Wuerfelergebnis ungueltig", i > 0 && i < 7);
            }
        }
    }

    @Test
    public void testGoField() {

        //initialisierung
        gc = new Game(2);
        gc.init();
        board = gc.board;
        players = gc.players;
        fm = gc.getFieldManager();
        Player patrick = players[0];
        Player john = players[1];

        //Spielern Geld geben
        PlayerService.giveMoney(patrick, 1000);
        PlayerService.giveMoney(john, 1000);

        //Spieler auf Start setzen
        patrick.setPosition(0);
        john.setPosition(0);

        //Spieler bewegen
        fm.movePlayer(patrick, 40, ((GoField) board.getFields()[0]).getAmount());
        fm.movePlayer(patrick, 50, ((GoField) board.getFields()[0]).getAmount());

        Assert.assertTrue("Los Geld falsch oder garnicht berechnet", patrick.getMoney() == 1000 + ((GoField) board.getFields()[0]).getAmount());
        Assert.assertTrue("Los Geld falsch oder garnicht berechnet", john.getMoney() == 1000 + ((GoField) board.getFields()[0]).getAmount());
    }

    @Test
    public void testCornerField() {

        //initialisierung
        gc = new Game(1);
        gc.init();
        board = gc.board;
        players = gc.players;
        fm = gc.getFieldManager();
        Player patrick = players[0];
        patrick.setInJail(false);

        //Spieler setzen und testen
        patrick.setPosition(10);
        Assert.assertTrue("Spieler ist im Gefaengnis", patrick.isInJail() == false);

        patrick.setPosition(20);
        Assert.assertTrue("Spieler ist nicht auf Frei Parken", patrick.getPosition() == 20);

        patrick.setPosition(0);
        Assert.assertTrue("Spieler ist nicht auf Los", patrick.getPosition() == 0);

    }

    @Test
    public void testBuyStreet() {

        //initialisierung
        gc = new Game(1);
        gc.init();
        board = gc.board;
        players = gc.players;
        fm = gc.getFieldManager();
        Player patrick = players[0];
        PlayerService.giveMoney(patrick, 1000);
        Property street = (Property) gc.board.getFields()[1];

        //Kauf der Strasse und testen
        fm.buyProperty(patrick, street, street.getPrice());
        Assert.assertTrue("Strasse nicht gekauft", street.getOwner() == patrick);
        Assert.assertTrue("Geld nicht abgezogen", patrick.getMoney() == 1000 - street.getPrice());

    }

}
