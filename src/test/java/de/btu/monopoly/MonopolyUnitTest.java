package de.btu.monopoly;

//Imports

import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.service.FieldService;
import de.btu.monopoly.core.service.PlayerService;
import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.card.CardAction;
import de.btu.monopoly.data.card.CardManager;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.server.GameServer;
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
    private static GameServer server;
    private static GameClient client;
    private static CardManager cm;

    public MonopolyUnitTest() {

        players = new Player[4];
        for (int i = 0; i < 4; i++) {
            Player player = new Player("Mathias " + (i + 1), i, 1500);
            players[i] = player;
        }
        client = new GameClient(59687, 5000);
        game = new Game(players, client, 42);
        game.init();
    }

    @Test
    public void testNetwork() {

        // initialisierung
        server = new GameServer(59687);
        server.startServer();
        client = new GameClient(59687, 5000);
        String localHost = System.getProperty("myapplication.ip");
        client.connect(localHost);
        Assert.assertTrue("Server nicht initialisiert", server != null);
        Assert.assertTrue("Client nicht initialisiert", client != null);
        Assert.assertTrue("2. Client nicht initialisiert", client != null);

    }

    @Test
    public void testGameBoard() {

        //initialisierung
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
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];
        Player john = players[1];

        //Spielern Geld geben
        PlayerService.giveMoney(patrick, 1000);
        PlayerService.giveMoney(john, 1000);

        int moneyPatrick = patrick.getMoney();

        //Spieler auf Start setzen
        patrick.setPosition(0);
        john.setPosition(0);
        //Spieler bewegen
        fm.movePlayer(patrick, 40); // beide Male über LOS gelaufen, warum fragst du auf 2500 und nicht mehr ab?
        fm.movePlayer(patrick, 50);

        Assert.assertTrue("Los Geld falsch oder garnicht berechnet",
                patrick.getMoney() == moneyPatrick + fm.getGoField().getAmount() * 2);
        Assert.assertTrue("Los Geld falsch oder garnicht berechnet",
                john.getMoney() == 2500);
    }

    @Test
    public void testCornerField() {

        //initialisierung
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
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];
        PropertyField street = (PropertyField) fm.getFields()[1];

        //Kauf der Strasse und testen
        FieldService.buyPropertyField(patrick, street);
        Assert.assertTrue("Strasse nicht gekauft", street.getOwner() == patrick);
        Assert.assertTrue("Geld nicht abgezogen", patrick.getMoney() == 1500 - street.getPrice());

    }

    @Test
    public void testBuyHouse() {

        //initialisierung
        board = game.getBoard();
        Field[] fields = board.getFields();
        players = game.getPlayers();

        fm = board.getFieldManager();
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
        int result = street.getHouseCount();

        Assert.assertTrue(
                "Hause wurde nicht bebaut! expected: "
                + expResult + " but was: " + street.getHouseCount(), expResult == street.getHouseCount());
        Assert.assertTrue(
                "Preis vom Haus ist falsch! expected: "
                + expHousPrice + " but was: " + street.getHousePrice(), expHousPrice == street.getHousePrice());
        Assert.assertTrue(
                "`Geld wurde nicht abgebucht! expected: "
                + expMoney + " but was: " + player.getMoney(), expMoney == player.getMoney());

    }

    @Test
    public void testSellHouse() {

        board = game.getBoard();
        Field[] fields = board.getFields();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player player = players[0];
        StreetField street = (StreetField) fields[11];
        StreetField street2 = (StreetField) fields[13];
        StreetField street3 = (StreetField) fields[14];

        int expMoney = player.getMoney();

        street.setOwner(player);
        street2.setOwner(player);
        street3.setOwner(player);

        fm.buyHouse(street);
        fm.buyHouse(street2);
        fm.buyHouse(street2); // geht nicht, da Straßenzug unausgeglichen
        fm.buyHouse(street3);

        fm.sellHouse(street); // wenn du das Haus hier wieder verkaufst kann danach die Anzahl Häuser auf dem Feld nicht 1 sein!
        int expHouses = 1;

        expMoney = expMoney - street.getHousePrice() - street2.getHousePrice() - street3.getHousePrice() + (street.getHousePrice() / 2);
        Assert.assertEquals(expMoney, player.getMoney());
        Assert.assertEquals(0, street.getHouseCount()); // expHouses durch 0 ersetzt

        expMoney += street.getHousePrice() / 2;
        fm.sellHouse(street2);
        Assert.assertEquals(0, street2.getHouseCount());
        Assert.assertEquals(expMoney, player.getMoney()); // 1600 durch expMoney ersetzt
    }

    @Test
    public void testIsComplete() {

        //initialisierung
        board = game.getBoard();
        Field[] fields = board.getFields();
        players = game.getPlayers();
        fm = board.getFieldManager();
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
        board = game.getBoard();
        Field[] fields = board.getFields();
        players = game.getPlayers();

        fm = board.getFieldManager();
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

        //getMortgageValue() + getMoney()+giveMoney()
        Assert.assertTrue("Hypothek nicht aufgenommen! expected: "
                + expMoneyTheater + " but was: " + player.getMoney(), expMoneyTheater == player.getMoney());

        //getRent()+setMortgagaeTaken()
        Assert.assertTrue("Rent falsch! expected: " + 0 + " but was: "
                + theaterStrasse.getRent(), 0 == theaterStrasse.getRent());

        //KOMPLETTE STRASSE=============================================
        int expMoneyTurmStrasse = player.getMoney();
        fm.takeMortgage(turmStrasse);
        //false : Hypothek kann nicht aufgenommen werden, da Haeuser auf dem Feld

        //getMoney() +giveMoney()
        Assert.assertTrue("Hypothek wurde aufgenommen! ", expMoneyTurmStrasse == player.getMoney());
        Assert.assertTrue("Rent falsch! expected: " + 20 + " but was: "
                + theaterStrasse.getRent(), 20 == turmStrasse.getRent());

    }

    @Test
    public void testPayMortgage() {

        //initialisierung
        board = game.getBoard();
        Field[] fields = board.getFields();
        players = game.getPlayers();

        fm = board.getFieldManager();
        Player player = players[0];

        StationField suedbahnhof = (StationField) fields[5];
        StationField nordbahnhof = (StationField) fields[25];
        StreetField badStrasse = (StreetField) fields[1];

        //Strassenbesitzer geben
        suedbahnhof.setOwner(player);
        nordbahnhof.setOwner(player);
        badStrasse.setOwner(player);

        //setze Hypothek ist aufgenommen
        suedbahnhof.setMortgageTaken(true);
        //getRent()
        PlayerService pc = new PlayerService();

        //Geld von Spieler abziehe, damit er nicht genug hat
        pc.takeMoney(player, 1400);

        //act und assert
        fm.payMortgage(suedbahnhof);
        badStrasse.setMortgageTaken(true);
        int expMoney = player.getMoney() - badStrasse.getMortgageBack();
        fm.payMortgage(badStrasse);
        Assert.assertTrue("Geld nicht richtig abgebucht! expected: " + expMoney + "but: "
                + player.getMoney(), expMoney == player.getMoney());
        Assert.assertTrue("Geld nicht richtig abgebucht! expected: " + 2 + " but: "
                + badStrasse.getRent(), 2 == badStrasse.getRent());
    }

    @Test
    public void testTaxField() {
        //initialisierung
        board = game.getBoard();
        Field[] fields = board.getFields();
        players = game.getPlayers();

        fm = board.getFieldManager();
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
        game.fieldPhase(player, rollResult);

        game.fieldPhase(player2, rollResult);

        Assert.assertTrue(
                "Geld wurde nicht abgebucht!", expMoney1 == player2.getMoney());
        Assert.assertTrue(
                "Geld wurde nicht abgebucht!", expMoney2 == player.getMoney());

    }

    @Test
    public void testBankrupt() {

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
        fm.buyHouse(badStrasse);
//        badStrasse.setHouseCount(3);
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
        int chrisMoneyBefore = chris.getMoney();
        int patMoneyBefore = patrick.getMoney();
        PlayerService.takeMoney(chris, suedbahnhof.getRent());
        PlayerService.giveMoney(patrick, suedbahnhof.getRent());
        Assert.assertTrue("Geld nicht korrekt abgebucht", chris.getMoney() == chrisMoneyBefore - suedbahnhof.getRent());
        Assert.assertTrue("Geld nicht korrekt gebucht", patrick.getMoney() == patMoneyBefore + suedbahnhof.getRent());
        PlayerService.giveMoney(patrick, 1000);
        PlayerService.giveMoney(chris, 1000);

        //3 Bahnhoefe
        chrisMoneyBefore = chris.getMoney();
        patMoneyBefore = patrick.getMoney();
        hauptbahnhof.setOwner(null);
        PlayerService.takeMoney(chris, suedbahnhof.getRent());
        PlayerService.giveMoney(patrick, suedbahnhof.getRent());
        Assert.assertTrue("Geld nicht korrekt abgebucht", chris.getMoney() == chrisMoneyBefore - suedbahnhof.getRent());
        Assert.assertTrue("Geld nicht korrekt gebucht", patrick.getMoney() == patMoneyBefore + suedbahnhof.getRent());
        PlayerService.giveMoney(patrick, 1000);
        PlayerService.giveMoney(chris, 1000);

        //Werk
        chrisMoneyBefore = chris.getMoney();
        patMoneyBefore = patrick.getMoney();
        chris.setPosition(28);
        PlayerService.takeMoney(chris, wasserWerk.getRent());
        PlayerService.giveMoney(patrick, wasserWerk.getRent());
        Assert.assertTrue("Geld nicht korrekt abgebucht", chris.getMoney() == chrisMoneyBefore - wasserWerk.getRent()); //TODO kann keine rollResutl uebergeben
        Assert.assertTrue("Geld nicht korrekt gebucht", patrick.getMoney() == patMoneyBefore + wasserWerk.getRent());
        PlayerService.giveMoney(patrick, 1000);
        PlayerService.giveMoney(chris, 1000);

        //kompletter Strassenzug
        chrisMoneyBefore = chris.getMoney();
        patMoneyBefore = patrick.getMoney();
        chris.setPosition(1);
        PlayerService.takeMoney(chris, badStrasse.getRent());
        PlayerService.giveMoney(patrick, badStrasse.getRent());
        Assert.assertTrue("Geld nicht korrekt abgebucht", chris.getMoney() == chrisMoneyBefore - badStrasse.getRent());
        Assert.assertTrue("Geld nicht korrekt gebucht", patrick.getMoney() == patMoneyBefore + badStrasse.getRent());
        PlayerService.giveMoney(patrick, 1000);
        PlayerService.giveMoney(chris, 1000);

        //einzelne Strasse
        chrisMoneyBefore = chris.getMoney();
        patMoneyBefore = patrick.getMoney();
        turmStrasse.setOwner(null);
        PlayerService.takeMoney(chris, badStrasse.getRent());
        PlayerService.giveMoney(patrick, badStrasse.getRent());
        Assert.assertTrue("Geld nicht korrekt abgebucht", chris.getMoney() == chrisMoneyBefore - badStrasse.getRent());
        Assert.assertTrue("Geld nicht korrekt gebucht", patrick.getMoney() == patMoneyBefore + badStrasse.getRent());
        PlayerService.giveMoney(patrick, 1000);
        PlayerService.giveMoney(chris, 1000);

    }

    @Test
    public void testGoToJail() {

        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];
        patrick.setPosition(30);
        int[] rollResult = {14, 16};

        //GoToJailField und test
        game.fieldPhase(patrick, rollResult);
        // TODO evt ohne fielPhase Aufruf

        Assert.assertTrue("Spieler nicht im Gefaengnis", patrick.isInJail() == true);
        Assert.assertTrue("Tage im Gefaengnis sind nicht 0", patrick.getDaysInJail() == 0);
        Assert.assertTrue("Position ist nicht im Gefaengnis", patrick.getPosition() == 10);

    }

    public void testDoubletToJail() {

        //initialisierung
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];

        /*
         * Zur Zeit nicht implementier bar, man muss irgendwie in der turn() (welche private) das Attribut doubletCounter
         * veraendern koennen!! TODO @Maxi (von patrick) ne Idee?
         */
    }

    @Test
    public void testPayPrisonDeposit() {

        //initialisierung
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];

        patrick.setPosition(10);
        patrick.setInJail(true);
        int patrickMoney = patrick.getMoney();

        //Freikaufen + Tests
        game.processJailPayOption(patrick);
        Assert.assertTrue("Gefaengnisfreikauf hat nicht funktioniert", patrick.getMoney() == (patrickMoney - 50));

    }

    @Test
    public void testRollOutOfJail() {

        //initialisierung
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];

        patrick.setPosition(10);
        patrick.setInJail(true);

        //Freiwuerfeln + Tests
        do {
            game.processJailRollOption(patrick);
            patrick.setDaysInJail(0);
        } while (patrick.isInJail());
        Assert.assertTrue("Spieler ist immer noch im Gefaengnis", patrick.isInJail() == false);
    }

    @Test
    public void testLeavJailWithCard() {

        //initialisierung
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];

        patrick.setPosition(10);
        patrick.setInJail(true);

        Card jailCard = new Card("Gefaengnisfrei Karte", "Du kommst aus dem Gefaengnis frei", new CardAction[]{CardAction.JAIL}, null);
        patrick.getCardStack().addCard(jailCard);

        //Freikarte + testen
        game.processJailCardOption(patrick);
        Assert.assertTrue("Spieler immer noch im Gefaengnis", patrick.isInJail() == false);
    }

    @Test
    public void testCardPayMoney() {

        //initialisierung
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];
        cm = board.getCardManager();

        Card payCard = new Card("Strafzettel", "Strafzettel! Zahle #1{$currency}.", new CardAction[]{CardAction.PAY_MONEY}, new int[]{15});

        int currMoney = patrick.getMoney();

        cm.manageCardAction(patrick, payCard);
        Assert.assertTrue("Spieler musste kein Geld zahlen", currMoney == patrick.getMoney() + 15);

    }

    //@Test
    public void testCardMoving() {

        //initialisierung
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];
        cm = board.getCardManager();

        patrick.setPosition(0);
        Card nextTrain = new Card("Bahnhof", "Ruecke vor bis zum naechsten Bahnhof", new CardAction[]{CardAction.NEXT_STATION_RENT_AMP}, new int[]{2});
        Card seestrasse = new Card("Seestrasse", "Ruecke vor bis zur Seestrasse", new CardAction[]{CardAction.SET_POSITION}, new int[]{11});
        Card opernplatz = new Card("Opernplatz", "Ruecke vor bis zum Opernplatz", new CardAction[]{CardAction.SET_POSITION}, new int[]{24});
        Card werk = new Card("Werk", "Ruecke vor bis zum naechsten Werk", new CardAction[]{CardAction.NEXT_SUPPLY}, null);
        Card schlossallee = new Card("Schlossallee", "Ruecke vor bis zur Schlossallee", new CardAction[]{CardAction.SET_POSITION}, new int[]{39});
        Card suedbahnhof = new Card("Suedbahnhof", "Ruecke vor bis zum Suedbahnhof", new CardAction[]{CardAction.SET_POSITION}, new int[]{5});
        Card dreiZurueck = new Card("Drei Felder zurueck", "Drei Felder zurueck", new CardAction[]{CardAction.MOVE_PLAYER}, new int[]{3});
        Card los = new Card("Los", "Ruecke vor bis auf Los", new CardAction[]{CardAction.SET_POSITION}, new int[]{0});

        //Karten + Tests
        cm.manageCardAction(patrick, nextTrain);
        Assert.assertTrue("Spieler ist nicht auf Suedbahnhof(naechster)!", patrick.getPosition() == 5);

        cm.manageCardAction(patrick, seestrasse);
        Assert.assertTrue("Spieler nicht auf Seestrasse", patrick.getPosition() == 11);

        cm.manageCardAction(patrick, opernplatz);
        Assert.assertTrue("Spieler nicht auf Opernplatz", patrick.getPosition() == 24);

        //TODO Fehler ausbessern
//        cm.manageCardAction(patrick, werk);
//        Assert.assertTrue("Spieler nicht auf Wasserwerk", patrick.getPosition() == 28);
        cm.manageCardAction(patrick, schlossallee);
        Assert.assertTrue("Spieler nicht auf Schlossallee", patrick.getPosition() == 39);

        //TODO Spieler muss ueber Los bewegt werden und Geld bekommen!!
        int currMoney = patrick.getMoney();
        cm.manageCardAction(patrick, suedbahnhof);
        Assert.assertTrue("Spieler nicht auf Suedbahnhof", patrick.getPosition() == 5);
        Assert.assertTrue("Spieler hat kein Geld bekommen (Los)", patrick.getMoney() == currMoney + fm.getGoField().getAmount());

        cm.manageCardAction(patrick, dreiZurueck);
        Assert.assertTrue("Spieler nicht drei Felder zurueck gegangen!", patrick.getPosition() == 2);

        //TOD Spieler bekommt auch hier kein Geld!
        currMoney = patrick.getMoney();
        cm.manageCardAction(patrick, los);
        Assert.assertTrue("Spieler nicht auf Los!", patrick.getPosition() == 0);
        Assert.assertTrue("Spieler hat kein Geld bekommen (Los)", patrick.getMoney() == currMoney + fm.getGoField().getAmount());

    }

    @Test
    public void testCardGiveMoney() {

        //initialisierung
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];
        cm = board.getCardManager();
        int currMoney = patrick.getMoney();
        Card contract = new Card("Bausparvertrag", "Dein Bausparvertrag wird faellig. Du erhaelst #1{$currency}.", new CardAction[]{CardAction.GIVE_MONEY}, new int[]{150});
        Card dividend = new Card("Dividende", "Die Bank zahlt dir eine Dividende von #1{$currency}.", new CardAction[]{CardAction.GIVE_MONEY}, new int[]{50});

        //Karten + Tests
        cm.manageCardAction(patrick, contract);
        Assert.assertTrue("Spieler hat kein Geld aus Bausparvertrag erhalten", patrick.getMoney() == currMoney + 150);

        currMoney = patrick.getMoney();
        cm.manageCardAction(patrick, dividend);
        Assert.assertTrue("Spieler hat kein Geld aus der Dividende erhalten", patrick.getMoney() == currMoney + 50);

    }

    //@Test TODO @Maxi haben darueber gesprochen :)
    public void testCardRenovate() {

        //initialisierung
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];
        cm = board.getCardManager();
        Field[] fields = board.getFields();
        int currMoney = patrick.getMoney();

        StreetField seestasse = (StreetField) fields[11];
        StreetField hafenstrasse = (StreetField) fields[13];
        StreetField neueStrasse = (StreetField) fields[14];

        //Strassenbesitzer geben
        seestasse.setOwner(patrick);
        hafenstrasse.setOwner(patrick);
        neueStrasse.setOwner(patrick);

        //Haeueser bauen
        seestasse.setHouseCount(1);
        hafenstrasse.setHouseCount(1);
        neueStrasse.setHouseCount(1);

        //TODO fehlt 2. args
        Card renovate = new Card("Renovieren", "Du laesste deine Haeuser renovieren", new CardAction[]{CardAction.RENOVATE}, new int[]{25});

        //Renovieren + Test
        cm.manageCardAction(patrick, renovate);
        Assert.assertTrue("Spieler wurder kein/falsch Geld abgezogen", patrick.getMoney() == currMoney - 75);

    }

    //@Test
    public void testCardCEO() {

        //initialisierung
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];
        Player schmarotzer = players[1];
        Player parasit = players[2];
        cm = board.getCardManager();

        int currMoney1 = patrick.getMoney();
        int currMoney2 = schmarotzer.getMoney();
        int currMoney3 = parasit.getMoney();

        Card ceo = new Card("Vorstand", "Zahle jedem Spieler #1{$currency}.", new CardAction[]{CardAction.PAY_MONEY_ALL}, new int[]{50});

        //Vorstandswahl + Test
        cm.manageCardAction(patrick, ceo);
        //TODO Spieler wird falsch Geld abgezogen
        Assert.assertTrue("Spieler wurde kein/falsch Geld abgezogen", patrick.getMoney() == currMoney1 - (2 * ceo.getArgs()[0]));
        Assert.assertTrue("Spieler wurde kein/falsch Geld gegeben", schmarotzer.getMoney() == currMoney2 + ceo.getArgs()[0]);
        Assert.assertTrue("Spieler wurde kein/falsch Geld gegeben", parasit.getMoney() == currMoney3 + ceo.getArgs()[0]);

    }

    @Test
    public void testCardGoToJail() {

        //initialisierung
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        Player patrick = players[0];
        cm = board.getCardManager();

        patrick.setInJail(false);
        patrick.setPosition(0);

        Card goToJail = new Card("Ins Gefaengnis", "Gehe in das Gefaegnis!", new CardAction[]{CardAction.GO_JAIL}, new int[]{200});

        //Ins Gefaegnis + Test
        cm.manageCardAction(patrick, goToJail);
        Assert.assertTrue("Spieler nicht auf Gefaengnisfeld", patrick.getPosition() == 10);
        Assert.assertTrue("Spieler nicht IM Gefaegnis", patrick.isInJail() == true);
    }

}
