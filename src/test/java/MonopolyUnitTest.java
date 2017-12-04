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

    //@Test
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
        Property street = (Property) gc.board.getFields()[1];

        //Kauf der Strasse und testen
        fm.buyProperty(patrick, street, street.getPrice());
        Assert.assertTrue("Strasse nicht gekauft", street.getOwner() == patrick);
        Assert.assertTrue("Geld nicht abgezogen", patrick.getMoney() == 1500 - street.getPrice());

    }

    @Test
    public void testBuyHouse() {
        //initialisierung
        gc = new Game(1);
        gc.init();
        Field[] fields = gc.board.getFields();
        players = gc.players;
        fm = gc.getFieldManager();
        Player player = players[0];
        StreetField street = (StreetField) fields[11];
        StreetField street2 = (StreetField) fields[13];
        StreetField street3 = (StreetField) fields[14];

        //Strassenbesitzer geben
        street.setOwner(player);
        street2.setOwner(player);
        street3.setOwner(player);

        //Haeuseranzahl setzen
        street.setHouseCount(0);
        street2.setHouseCount(1);
        street3.setHouseCount(1);

        //act und assert
        fm.buyHouse(street);
        int expResult = 1;
        int expHousPrice = 100;
        int expMoney = 1500 - street.getHousePrice();

        Assert.assertTrue("Hause wurde nicht bebaut! expected: "
                + expResult + " but was: " + street.getHouseCount(), expResult == street.getHouseCount());
        Assert.assertTrue("Preis vom Haus ist falsch! expected: "
                + expHousPrice + " but was: " + street.getHousePrice(), expHousPrice == street.getHousePrice());
        Assert.assertTrue("`Geld wurde nicht abgebucht! expected: "
                + expMoney + " but was: " + player.getMoney(), expMoney == player.getMoney());

    }

    @Test
    public void testSellHouse() {
        //initialisierung
        gc = new Game(1);
        gc.init();
        Field[] fields = gc.board.getFields();
        players = gc.players;
        fm = gc.getFieldManager();
        Player player = players[0];
        StreetField street = (StreetField) fields[11];
        StreetField street2 = (StreetField) fields[13];
        StreetField street3 = (StreetField) fields[14];

        //Streassenbesitzer geben
        street.setOwner(player);
        street2.setOwner(player);
        street3.setOwner(player);
        street.setHouseCount(1);
        street2.setHouseCount(2);
        street3.setHouseCount(1);

        //act und assert
        fm.sellHouse(street);
        int expHouses = 1;
        int expMoney = 1500;
        Assert.assertTrue("Geld wurde ueberwiesen! expected: " + expMoney
                + " but was: " + player.getMoney(), expMoney == player.getMoney());
        Assert.assertTrue("Haus wurde verkauft! expected: " + expHouses
                + " but was: " + street.getHouseCount(), expHouses == street.getHouseCount());

        fm.sellHouse(street2);
        Assert.assertTrue("Haus wurde nicht verkauft! expected: " + 1
                + " but was: " + street2.getHouseCount(), 1 == street2.getHouseCount());
        Assert.assertTrue("Geld wurde nicht ueberwiesen! expected: " + 1600
                + " but was: " + player.getMoney(), 1600 == player.getMoney());
    }

    @Test
    public void testIsComplete() {
        //initialisierung
        gc = new Game(1);
        gc.init();
        Field[] fields = gc.board.getFields();
        players = gc.players;
        fm = gc.getFieldManager();
        Player player = players[0];
        StreetField street = (StreetField) fields[11];
        StreetField street2 = (StreetField) fields[13];
        StreetField street3 = (StreetField) fields[14];

        //Strassenbesitzer geben
        street.setOwner(player);
        street2.setOwner(player);
        street3.setOwner(player);

        //act und assert
        Assert.assertTrue("Strasse ist nicht komplett!", true == fm.isComplete(street));

    }

    @Test
    public void testTakeMortgage() {

        //initialisierung
        gc = new Game(1);
        gc.init();
        Field[] fields = gc.board.getFields();
        players = gc.players;
        fm = gc.getFieldManager();
        Player player = players[0];
        StationField suedbahnhof = (StationField) fields[5];
        StationField nordbahnhof = (StationField) fields[25];
        StreetField theaterStrasse = (StreetField) fields[21];
        StreetField badStrasse = (StreetField) fields[1];
        StreetField turmStrasse = (StreetField) fields[3];
        SupplyField elWerk = (SupplyField) fields[12];
        SupplyField wasserWerk = (SupplyField) fields[28];

        //Strassenbesitzer geben
        suedbahnhof.setOwner(player);
        nordbahnhof.setOwner(player);
        theaterStrasse.setOwner(player);
        badStrasse.setOwner(player);
        turmStrasse.setOwner(player);
        elWerk.setOwner(player);
        wasserWerk.setOwner(player);

        //Haeuseranzahl geben
        badStrasse.setHouseCount(1);
        turmStrasse.setHouseCount(1);

        //act und assert
        //BAHNHOF UND WERKE======================================================
        int expMoneyBahn = player.getMoney() + suedbahnhof.getMortgageValue();
        fm.takeMortgage(suedbahnhof);

        //getMortgageValue + giveMoney() + getMoney()
        Assert.assertTrue("Geld nicht richtig ueberwiesen! expected: "
                + expMoneyBahn + " but was: " + player.getMoney(), expMoneyBahn == player.getMoney());

        int expMoneyWerk = player.getMoney() + elWerk.getMortgageValue();
        fm.takeMortgage(elWerk);
        Assert.assertTrue("Geld nicht richtig ueberwiesen! expected: "
                + expMoneyWerk + " but was: " + player.getMoney(), expMoneyWerk == player.getMoney());

        //setMortgageTaken() + getRent()
        Assert.assertTrue("Rent falsch! expected: " + 0 + " but was: "
                + suedbahnhof.getRent(), 0 == suedbahnhof.getRent());
        Assert.assertTrue("Rent falsch! expected: " + 25 + " but was: "
                + nordbahnhof.getRent(), 25 == nordbahnhof.getRent());
        Assert.assertTrue("Rent falsch! expected: " + 0 + " but was: "
                + elWerk.getRent(), 0 == elWerk.getRent());

        //NICHT KOMPLETTE STRASSE=======================================
        int expMoneyTheater = player.getMoney() + theaterStrasse.getMortgageValue();
        fm.takeMortgage(theaterStrasse);

        Assert.assertTrue("Hypothek nicht aufgenommen! expected: "
                + expMoneyTheater + " but was: " + player.getMoney(), expMoneyTheater == player.getMoney());

        //getRent()+setMortgagaeTaken()
        Assert.assertTrue("Rent falsch! expected: " + 0 + " but was: "
                + theaterStrasse.getRent(), 0 == theaterStrasse.getRent());

        //KOMPLETTE STRASSE=============================================
        int expMoneyTurmStrasse = player.getMoney();
        fm.takeMortgage(turmStrasse);
        //false : Hypothek kann nicht aufgenommen werden, da Haeuser auf dem Feld

        Assert.assertTrue("Hypothek wurde aufgenommen! ", expMoneyTurmStrasse == player.getMoney());
        Assert.assertTrue("Rent falsch! expected: " + 20 + " but was: "
                + theaterStrasse.getRent(), 20 == turmStrasse.getRent());

    }

    @Test
    public void testPayMortgage() {

        //initialisierung
        gc = new Game(1);
        gc.init();
        Field[] fields = gc.board.getFields();
        players = gc.players;
        fm = gc.getFieldManager();
        Player player = players[0];
        Property suedbahnhof = (Property) fields[5];
        Property nordbahnhof = (Property) fields[25];
        StreetField badStrasse = (StreetField) fields[1];
        
        //Strassenbesitzer geben
        suedbahnhof.setOwner(player);
        nordbahnhof.setOwner(player);
        badStrasse.setOwner(player);
        
        //setze Hypothek ist aufgenommen
        suedbahnhof.setMortgageTaken(true);
        PlayerService pc = new PlayerService();
        
        //Geld von Spieler abziehe, damit er nicht genug hat
        pc.takeMoney(player, 1400);

        //act und assert
        fm.payMortgage(suedbahnhof);
        Assert.assertEquals(true, suedbahnhof.isMortgageTaken());
        Assert.assertEquals(100, player.getMoney());
        Assert.assertEquals(0, suedbahnhof.getRent());

        badStrasse.setMortgageTaken(true);
        int expMoney = player.getMoney() - badStrasse.getMortgageBack();
        fm.payMortgage(badStrasse);
        Assert.assertTrue("Geld nicht richtig abgebucht! expected: " + expMoney + "but: "
                + player.getMoney(), expMoney == player.getMoney());
        Assert.assertTrue("Geld nicht richtig abgebucht! expected: " + 2 + " but: "
                + badStrasse.getRent(), 2 == badStrasse.getRent()); //TODO
    }

    @Test
    public void testTaxField() {
        //initialisierung
        gc = new Game(2);
        gc.init();
        Field[] fields = gc.board.getFields();
        players = gc.players;
        fm = gc.getFieldManager();
        Player player = players[0];
        Player player2 = players[1];
        int[] rollResult = {8, 3};
        TaxField einSteuer = (TaxField) fields[4];
        TaxField zusatzSteuer = (TaxField) fields[38];

        int expMoney1 = player2.getMoney() - einSteuer.getTax();
        int expMoney2 = player.getMoney() - zusatzSteuer.getTax();

        //Position setzen
        player.setPosition(38);
        player2.setPosition(4);
        
        //act und assert
        gc.fieldPhase(player, rollResult);
        gc.fieldPhase(player2, rollResult);

        Assert.assertTrue("Geld wurde nicht abgebucht!", expMoney1 == player2.getMoney());
        Assert.assertTrue("Geld wurde nicht abgebucht!", expMoney2 == player.getMoney());
    }

    @Test
    public void testBankrupt() {

        //initialisierung
        gc = new Game(1);
        gc.init();
        board = gc.board;
        players = gc.players;
        fm = gc.getFieldManager();
        Player patrick = players[0];
        StationField suedbahnhof = (StationField) gc.board.getFields()[5];
        StationField nordbahnhof = (StationField) gc.board.getFields()[25];
        StreetField badStrasse = (StreetField) gc.board.getFields()[1];

        suedbahnhof.setOwner(patrick);
        nordbahnhof.setOwner(patrick);
        badStrasse.setOwner(patrick);
        badStrasse.setHouseCount(3);
        nordbahnhof.setMortgageTaken(true);

        //bankrupt und testen
        PlayerService.bankrupt(patrick, board, players);

        Assert.assertTrue("Property noch im Besitz", suedbahnhof.getOwner() != patrick);
        Assert.assertTrue("Property noch im Besitz", nordbahnhof.getOwner() != patrick);
        Assert.assertTrue("Property noch im Besitz", badStrasse.getOwner() != patrick);
        Assert.assertTrue("Property hat noch Hypothek", nordbahnhof.isMortgageTaken() == false);
        Assert.assertTrue("Property hat noch Haueser", badStrasse.getHouseCount() == 0);
        Assert.assertTrue("Spieler ist kein Spectator", patrick.isBankrupt() == true);
    }

    //@Test
    public void testPayRent() {

        //initialisierung
        gc = new Game(2);
        gc.init();
        board = gc.board;
        players = gc.players;
        fm = gc.getFieldManager();
        Player patrick = players[0];
        Player chris = players[1];
        StreetField badStrasse = (StreetField) gc.board.getFields()[1];
        StreetField turmStrasse = (StreetField) gc.board.getFields()[3];
        SupplyField wasserWerk = (SupplyField) gc.board.getFields()[28];
        StationField suedbahnhof = (StationField) gc.board.getFields()[5];
        StationField westbahnhof = (StationField) gc.board.getFields()[15];
        StationField nordbahnhof = (StationField) gc.board.getFields()[25];
        StationField hauptbahnhof = (StationField) gc.board.getFields()[35];

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
        gc = new Game(1);
        gc.init();
        board = gc.board;
        players = gc.players;
        fm = gc.getFieldManager();
        Player patrick = players[0];
        patrick.setPosition(39);
        int[] rollResult = {14, 16};

        //GoToJailField und test
        gc.fieldPhase(patrick, rollResult);

        Assert.assertTrue("Spieler nicht im Gefaengnis", patrick.isInJail() == true);
        Assert.assertTrue("Tage im Gefaengnis sind nicht 0", patrick.getDaysInJail() == 0);
        Assert.assertTrue("Position ist nicht im Gefaengnis", patrick.getPosition() == 10);

    }

}
