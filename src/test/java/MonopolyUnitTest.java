//Imports

import de.btu.monopoly.core.*;
import de.btu.monopoly.core.service.FieldService;
import de.btu.monopoly.core.service.PlayerService;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.data.player.Player;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Patrick Kalweit
 */
public class MonopolyUnitTest {

    private static Game game;
    private static GameBoard board;
    private static Player[] players;
    private static FieldManager fm;

    @Test
    public void testGameBoard() {

        //initialisierung
        game = new Game(2);
        game.init();
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        
        //existiert das Gameboard
        Assert.assertTrue("Gameboard nicht initialisiert", board != null);

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
        game = new Game(1);
        game.init();
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
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
        game = new Game(2);
        game.init();
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];
        Player john = players[1];

        //Spielern Geld geben
        PlayerService.giveMoney(patrick, 1000);
        PlayerService.giveMoney(john, 1000);

        //Spieler auf Start setzen
        patrick.setPosition(0);
        john.setPosition(0);
        //Spieler bewegen
        fm.movePlayer(patrick, 40);
        fm.movePlayer(patrick, 50);

        System.out.println(john.getMoney());
        System.out.println(patrick.getMoney());

        Assert.assertTrue("Los Geld falsch oder garnicht berechnet",
                patrick.getMoney() == 2500 + ((GoField) board.getFields()[0]).getAmount());
        Assert.assertTrue("Los Geld falsch oder garnicht berechnet",
                john.getMoney() == 2500 + ((GoField) board.getFields()[0]).getAmount());
    }

    @Test
    public void testCornerField() {

        //initialisierung
        game = new Game(1);
        game.init();
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
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
        game = new Game(1);
        game.init();
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];
        PropertyField street = (PropertyField) fm.getFields()[1];

        //Kauf der Strasse und testen
        FieldService.buyPropertyField(patrick, street, street.getPrice());
        Assert.assertTrue("Strasse nicht gekauft", street.getOwner() == patrick);
        Assert.assertTrue("Geld nicht abgezogen", patrick.getMoney() == 1500 - street.getPrice());

    }

    @Test
    public void testBuyHouse() {

        game = new Game(1);
        game.init();
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player player = players[0];
        StreetField street = (StreetField) board.getFields()[11];
        StreetField street2 = (StreetField) board.getFields()[13];
        StreetField street3 = (StreetField) board.getFields()[14];

        street.setOwner(player);
        street2.setOwner(player);
        street3.setOwner(player);
        street.setHouseCount(0);
        street2.setHouseCount(1);
        street3.setHouseCount(1);

        //act
        fm.buyHouse(street);
        int expResult = 1;
        int expHousPrice = 100;
        int expMoney = 1500 - street.getHousePrice();
        int result = street.getHouseCount();
        System.out.println(result);
        Assert.assertEquals(expResult, result);
        Assert.assertEquals(expHousPrice, street.getHousePrice());
        Assert.assertEquals(expMoney, player.getMoney());

    }

    @Test
    public void testSellHouse() {

        game = new Game(1);
        game.init();
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player player = players[0];
        StreetField street = (StreetField) board.getFields()[11];
        StreetField street2 = (StreetField) board.getFields()[13];
        StreetField street3 = (StreetField) board.getFields()[14];

        street.setOwner(player);
        street2.setOwner(player);
        street3.setOwner(player);
        street.setHouseCount(1);
        street2.setHouseCount(2);
        street3.setHouseCount(1);

        fm.sellHouse(street);
        int expHouses = 1;
        int expMoney = 1500;
        Assert.assertEquals(expMoney, player.getMoney());
        Assert.assertEquals(expHouses, street.getHouseCount());

        fm.sellHouse(street2);
        Assert.assertEquals(1, street2.getHouseCount());
        Assert.assertEquals(1600, player.getMoney());
    }

    @Test
    public void testIsComplete() {

        game = new Game(1);
        game.init();
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player player = players[0];
        StreetField street = (StreetField) board.getFields()[11];
        StreetField street2 = (StreetField) board.getFields()[13];
        StreetField street3 = (StreetField) board.getFields()[14];

        street.setOwner(player);
        street2.setOwner(player);
        street3.setOwner(player);

        Assert.assertEquals(true, fm.isComplete(street));

    }

    @Test
    public void testTakeMortgage() {

        game = new Game(1);
        game.init();
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player player = players[0];
        StationField suedbahnhof = (StationField) fm.getFields()[5];
        StationField nordbahnhof = (StationField) fm.getFields()[25];

        StreetField theaterStrasse = (StreetField) fm.getFields()[21];
        StreetField badStrasse = (StreetField) fm.getFields()[1];
        StreetField turmStrasse = (StreetField) fm.getFields()[3];

        SupplyField elWerk = (SupplyField) fm.getFields()[12];
        SupplyField wasserWerk = (SupplyField) fm.getFields()[28];

        suedbahnhof.setOwner(player);
        nordbahnhof.setOwner(player);
        theaterStrasse.setOwner(player);
        badStrasse.setOwner(player);
        turmStrasse.setOwner(player);
        elWerk.setOwner(player);
        wasserWerk.setOwner(player);
        badStrasse.setHouseCount(1);
        turmStrasse.setHouseCount(1);

        //BAHNHOF UND WERKE======================================================
        int expMoneyBahn = player.getMoney() + suedbahnhof.getMortgageValue();
        fm.takeMortgage(suedbahnhof);

        //getMortgageValue + giveMoney() + getMoney()
        Assert.assertEquals(expMoneyBahn, player.getMoney());

        int expMoneyWerk = player.getMoney() + elWerk.getMortgageValue();
        fm.takeMortgage(elWerk);
        Assert.assertEquals(expMoneyWerk, player.getMoney());

        //setMortgageTaken() + getRent()
        Assert.assertEquals(0, suedbahnhof.getRent());
        Assert.assertEquals(25, nordbahnhof.getRent());
        Assert.assertEquals(0, elWerk.getRent());

        //NICHT KOMPLETTE STRASSE=======================================
        int expMoneyTheater = player.getMoney() + theaterStrasse.getMortgageValue();
        fm.takeMortgage(theaterStrasse);

        //getMortgageValue() + getMoney()+giveMoney()
        Assert.assertEquals(expMoneyTheater, player.getMoney());

        //getRent()+setMortgagaeTaken()
        Assert.assertEquals(0, theaterStrasse.getRent());

        //KOMPLETTE STRASSE=============================================
        int expMoneyTurmStrasse = player.getMoney();
        fm.takeMortgage(turmStrasse);
        //false : Hypothek kann nicht aufgenommen werden, da Haeuser auf dem Feld

        //getMoney() +giveMoney()
        Assert.assertEquals(expMoneyTurmStrasse, player.getMoney());
        Assert.assertEquals(20, turmStrasse.getRent());

    }

    @Test
    public void testPayMortgage() {

        game = new Game(1);
        game.init();
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player player = players[0];
        PropertyField suedbahnhof = (PropertyField) fm.getFields()[5];
        PropertyField nordbahnhof = (PropertyField) fm.getFields()[25];
        StreetField badStrasse = (StreetField) fm.getFields()[1];

        suedbahnhof.setOwner(player);
        nordbahnhof.setOwner(player);
        badStrasse.setOwner(player);

        suedbahnhof.setMortgageTaken(true);
        //getRent()
        PlayerService.takeMoney(player, 1400);

        fm.payMortgage(suedbahnhof);
        Assert.assertEquals(true, suedbahnhof.isMortgageTaken());
        Assert.assertEquals(100, player.getMoney());
        Assert.assertEquals(0, suedbahnhof.getRent());

        badStrasse.setMortgageTaken(true);
        int expMoney = player.getMoney() - badStrasse.getMortgageBack();
        fm.payMortgage(badStrasse);
        Assert.assertEquals(expMoney, player.getMoney());
        Assert.assertEquals(2, badStrasse.getRent()); //TODO
    }

    @Test
    public void testTaxField() {
        System.out.println("TaxField");
        game = new Game(1);
        game.init();
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player player = players[0];
        TaxField einSteuer = (TaxField) fm.getFields()[4];
        TaxField zusatzSteuer = (TaxField) fm.getFields()[38];

        int expMoney1 = player.getMoney() - einSteuer.getTax();
//        game.processPlayerOnTaxField(player, einSteuer);
        player.setPosition(4);
        Assert.assertEquals(expMoney1, player.getMoney());

        int expMoney2 = player.getMoney() - zusatzSteuer.getTax();
//        game.processPlayerOnTaxField(player, zusatzSteuer);
        player.setPosition(38);
        Assert.assertEquals(expMoney2, player.getMoney());
    }

    @Test
    public void testBankrupt() {

        //initialisierung
        game = new Game(1);
        game.init();
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];
        StationField suedbahnhof = (StationField) fm.getFields()[5];
        StationField nordbahnhof = (StationField) fm.getFields()[25];
        StreetField badStrasse = (StreetField) fm.getFields()[1];

        suedbahnhof.setOwner(patrick);
        nordbahnhof.setOwner(patrick);
        badStrasse.setOwner(patrick);
        badStrasse.setHouseCount(3);
        nordbahnhof.setMortgageTaken(true);

        //bankrupt und testen
        PlayerService.bankrupt(patrick, board);

        Assert.assertTrue("PropertyField noch im Besitz", suedbahnhof.getOwner() != patrick);
        Assert.assertTrue("PropertyField noch im Besitz", nordbahnhof.getOwner() != patrick);
        Assert.assertTrue("PropertyField noch im Besitz", badStrasse.getOwner() != patrick);
        Assert.assertTrue("PropertyField hat noch Hypothek", nordbahnhof.isMortgageTaken() == false);
        Assert.assertTrue("PropertyField hat noch Haueser", badStrasse.getHouseCount() == 0);
        Assert.assertTrue("Spieler ist kein Spectator", patrick.isBankrupt() == true);
    }

    @Test
    public void testPayRent() {

        //initialisierung
        game = new Game(2);
        game.init();
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];
        Player chris = players[1];
        StreetField badStrasse = (StreetField) fm.getFields()[1];
        StreetField turmStrasse = (StreetField) fm.getFields()[3];
        SupplyField wasserWerk = (SupplyField) fm.getFields()[28];
        StationField suedbahnhof = (StationField) fm.getFields()[5];
        StationField westbahnhof = (StationField) fm.getFields()[15];
        StationField nordbahnhof = (StationField) fm.getFields()[25];
        StationField hauptbahnhof = (StationField) fm.getFields()[35];

        PlayerService.giveMoney(patrick, 1000);
        PlayerService.giveMoney(chris, 1000);
        badStrasse.setOwner(patrick);
        turmStrasse.setOwner(patrick);
        wasserWerk.setOwner(patrick);
        suedbahnhof.setOwner(patrick);
        westbahnhof.setOwner(patrick);
        nordbahnhof.setOwner(patrick);
        hauptbahnhof.setOwner(patrick);

        //Miete zahlen und testen
        //4 Bahnhoefe
        chris.setPosition(5);
        PlayerService.takeMoney(chris, suedbahnhof.getRent());
        PlayerService.giveMoney(patrick, suedbahnhof.getRent());
        Assert.assertTrue("Geld nicht korrekt abgebucht", chris.getMoney() == 1000 - suedbahnhof.getRent());
        Assert.assertTrue("Geld nicht korrekt gebucht", patrick.getMoney() == 1000 + suedbahnhof.getRent());
        PlayerService.giveMoney(patrick, 1000);
        PlayerService.giveMoney(chris, 1000);
        //3 Bahnhoefe
        hauptbahnhof.setOwner(null);
        PlayerService.takeMoney(chris, suedbahnhof.getRent());
        PlayerService.giveMoney(patrick, suedbahnhof.getRent());
        Assert.assertTrue("Geld nicht korrekt abgebucht", chris.getMoney() == 1000 - suedbahnhof.getRent());
        Assert.assertTrue("Geld nicht korrekt gebucht", patrick.getMoney() == 1000 + suedbahnhof.getRent());
        PlayerService.giveMoney(patrick, 1000);
        PlayerService.giveMoney(chris, 1000);
        //Werk
        chris.setPosition(28);
        PlayerService.takeMoney(chris, wasserWerk.getRent());
        PlayerService.giveMoney(patrick, wasserWerk.getRent());
        Assert.assertTrue("Geld nicht korrekt abgebucht", chris.getMoney() == 1000 - wasserWerk.getRent()); //TODO kann keine rollResutl uebergeben
        Assert.assertTrue("Geld nicht korrekt gebucht", patrick.getMoney() == 1000 + wasserWerk.getRent());
        PlayerService.giveMoney(patrick, 1000);
        PlayerService.giveMoney(chris, 1000);
        //kompletter Strassenzug
        chris.setPosition(1);
        PlayerService.takeMoney(chris, badStrasse.getRent());
        PlayerService.giveMoney(patrick, badStrasse.getRent());
        Assert.assertTrue("Geld nicht korrekt abgebucht", chris.getMoney() == 1000 - badStrasse.getRent());
        Assert.assertTrue("Geld nicht korrekt gebucht", patrick.getMoney() == 1000 + badStrasse.getRent());
        PlayerService.giveMoney(patrick, 1000);
        PlayerService.giveMoney(chris, 1000);
        //einzelne Strasse
        turmStrasse.setOwner(null);
        PlayerService.takeMoney(chris, badStrasse.getRent());
        PlayerService.giveMoney(patrick, badStrasse.getRent());
        Assert.assertTrue("Geld nicht korrekt abgebucht", chris.getMoney() == 1000 - badStrasse.getRent());
        Assert.assertTrue("Geld nicht korrekt gebucht", patrick.getMoney() == 1000 + badStrasse.getRent());
        PlayerService.giveMoney(patrick, 1000);
        PlayerService.giveMoney(chris, 1000);

    }

    //@Test TODO Kann nicht testen ohne extra Eingaben
    public void testGoToJail() {

        //initialisierung
        game = new Game(1);
        game.init();
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];
        patrick.setPosition(39);
        int[] rollResult = {14, 16};

        //GoToJailField und test
        game.fieldPhase(patrick, rollResult);
        // TODO (von Maxi) Patrick, könntest du es irgendwie so umprogrammieren, dass du hier den Aufruf auf die Feldphase nicht machen musst?

        Assert.assertTrue("Spieler nicht im Gefaengnis", patrick.isInJail() == true);
        Assert.assertTrue("Tage im Gefaengnis sind nicht 0", patrick.getDaysInJail() == 0);
        Assert.assertTrue("Position ist nicht im Gefaengnis", patrick.getPosition() == 10);

    }

}
