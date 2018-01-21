/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly;

import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.service.AuctionService;
import de.btu.monopoly.core.service.IOService;
import de.btu.monopoly.data.card.CardManager;
import de.btu.monopoly.data.field.FieldManager;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.menu.LobbyService;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.server.GameServer;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Christian Prinz
 */
public class NetDependedUnitTests {

    private Lobby lobby;
    private GameServer server;
    private GameClient client;
    private Game game;
    private GameBoard board;
    private Player[] players;
    private FieldManager fm;
    private CardManager cm;

    public NetDependedUnitTests() {
        GlobalSettings.RUN_AS_TEST = true;
    }

    /*
     * DIE TESTS MACHEN NOCH PROBLEME BEIM BUILDEN DER PIPELINE !!! daher deaktiviert, beim Push
     */
    private void initNetwork() {
        server = new GameServer(443);
        server.startServer();
        client = new GameClient(443, 5000);
        client.connect(System.getProperty("myapp.ip"));
    }

    private void initLobby() {
        initNetwork();
        lobby = new Lobby();
        lobby.setHost(true);
        lobby.setPlayerName("Dummy");
        lobby.setPlayerClient(client);
    }

    private void initGame() {
        initLobby();
        LobbyService.setLobby(lobby);
        LobbyService.joinRequest();
        IOService.sleep(100);
        LobbyService.addKI("Gegner", 1);
        IOService.sleep(100);

        Game controller = new Game(lobby.getPlayerClient(), LobbyService.generatePlayerArray(), lobby.getRandomSeed());
        lobby.setController(controller);
        lobby.getPlayerClient().setGame(controller);
        controller.init();

        game = LobbyService.getLobby().getController();
        board = game.getBoard();
        players = game.getPlayers();
        fm = board.getFieldManager();
        cm = board.getCardManager();
        IOService.sleep(100);
    }

    private void clearGame() {
        client.disconnect();
        IOService.sleep(1000);
        server.stopServer();
        lobby = null;
        server = null;
        client = null;
        game = null;
        board = null;
        players = null;
        fm = null;
        cm = null;
        System.out.println("\nCLEAR GAME ---- ALLES ZURUECKGESETZT!!!");
        IOService.sleep(100);
    }

    private void testOutput(String testName) {
        System.out.println("\n__________________________________________________\n"
                + "STARTE TEST: " + testName);
    }

    private void test(String message, boolean condition) {
        Assert.assertTrue(message, condition);
    }

    @Test
    public void testInitNetwork() {
        testOutput("testInitNetwork");
        initNetwork();
        test("Server nicht initialisiert", server != null);
        test("Client nicht initialisiert", client != null);
        clearGame();
    }

    @Test
    public void testInitLobby() {
        testOutput("testInitLobby");
        initLobby();
        LobbyService.setLobby(lobby);
        test("Lobby nicht initialisiert", LobbyService.getLobby() != null);
        test("Host nicht gesetzt", LobbyService.getLobby().isHost() == true);

        LobbyService.joinRequest();
        IOService.sleep(100);
        test("Spieler nicht registriert", LobbyService.getLobby().getUsers() != null);
        test("Spieler nicht menschlich", Integer.parseInt(LobbyService.getLobby().getUsers()[0][3]) == 0);

        LobbyService.changeName("TestSpieler");
        IOService.sleep(100);
        test("Spielernamen nicht geändert", "TestSpieler".equals(LobbyService.getLobby().getUsers()[0][1]));

        LobbyService.addKI("Gegner", 1);
        IOService.sleep(100);
        test("KI nicht hizugefügt", LobbyService.getLobby().getUsers().length == 2);
        test("KI-Stufe nicht gesetzt", Integer.parseInt(LobbyService.getLobby().getUsers()[1][3]) == 1);
        test("KI-Name nicht gesetzt", "Gegner".equals(LobbyService.getLobby().getUsers()[1][1]));

        Game controller = new Game(lobby.getPlayerClient(), LobbyService.generatePlayerArray(), lobby.getRandomSeed());
        lobby.setController(controller);
        lobby.getPlayerClient().setGame(controller);
        test("Game nicht erstellt", LobbyService.getLobby().getController() != null);
        test("PlayerOnClient nicht erstellt", LobbyService.getLobby().getPlayerClient() != null);

        clearGame();
    }

    @Test
    public void testInitGame() {
        testOutput("testInitGame");
        initGame();
        test("Game nicht initialisiert", game != null);
        test("board nicht initialisiert", board != null);
        test("players nicht initialisiert", players != null);
        test("FieldManager nicht initialisiert", fm != null);
        test("CardManager nicht initialisiert", cm != null);
        clearGame();
    }

    @Test
    public void testEasyKiJailOption() {
        testOutput("testEasyKiJailOption");
        initGame();
        // KI ins Gefängnis setzen
        Player ki = players[1];
        ki.setInJail(true);
        ki.setPosition(10);

        //KI sollte sich freikaufen
        game.jailPhase(ki);
        test("KI hat nicht bezahlt", !ki.isInJail());

        //KI sollte sich freiwuerfeln
        ki.getBank().withdraw(ki.getMoney());
        int choice = IOService.jailChoice(ki);
        test("KI will sich nicht freiwürfeln", choice == 1);
        clearGame();
    }

    @Test
    public void testHardKiJailOption() {
        testOutput("testHardKiJailOption");
        initGame();
        Player ki = players[1];
        ki.setKiLevel(2);

        // KI ins Gefängnis setzen
        ki.setInJail(true);
        ki.setPosition(10);

        //KI sollte sich freikaufen
        game.jailPhase(ki);
        test("KI hat nicht bezahlt", !ki.isInJail());

        // Alle Strassen verkaufen
        Arrays.stream(board.getFields())
                .filter(p -> p instanceof PropertyField).map(p -> (PropertyField) p)
                .forEach(p -> p.setOwner(ki));

        test("Strassen wurden nicht verkauft", ((PropertyField) board.getFields()[1]).getOwner() != null);

        //KI sollte sich jetzt freiwuerfeln
        int choice = IOService.jailChoice(ki);
        test("KI will sich nicht freiwürfeln", choice == 1);
        clearGame();
    }

    @Test
    public void testHardKiBuyProperty() {
        testOutput("testHardKiBuyProperty");
        initGame();
        Player ki = players[1];
        ki.setKiLevel(2);
        ki.getBank().withdraw(ki.getMoney());
        ki.getBank().deposit(1000);

        // Strassen
        PropertyField cheap = (PropertyField) IOService.getGame().getBoard().getFields()[1];
        PropertyField lurca = (PropertyField) IOService.getGame().getBoard().getFields()[21];
        PropertyField expan = (PropertyField) IOService.getGame().getBoard().getFields()[39];

        // superreich kauft alle Straßen
        test("supperreich kauft nicht billig", IOService.buyPropertyChoice(ki, cheap) == 1);
        test("supperreich kauft nicht lukrativ", IOService.buyPropertyChoice(ki, lurca) == 1);
        test("supperreich kauft nicht teuer", IOService.buyPropertyChoice(ki, expan) == 1);

        // reich kauft nur teuer und lukrativ
        ki.getBank().withdraw(250); //Stand 750 -> reich
        test("reich kauft billig", IOService.buyPropertyChoice(ki, cheap) == 2);
        test("reich kauft nicht lukrativ", IOService.buyPropertyChoice(ki, lurca) == 1);
        test("reich kauft nicht teuer", IOService.buyPropertyChoice(ki, expan) == 1);

        // fluessig kauft nur lukrativ
        ki.getBank().withdraw(200); //Stand 550 -> fluessig
        test("flüssig kauft billig", IOService.buyPropertyChoice(ki, cheap) == 2);
        test("flüssig kauft nicht lukrativ", IOService.buyPropertyChoice(ki, lurca) == 1);
        test("flüssig kauft teuer", IOService.buyPropertyChoice(ki, expan) == 2);

        // arm kauft nichts
        ki.getBank().withdraw(300); //Stand 250 -> arm
        test("arm kauft billig", IOService.buyPropertyChoice(ki, cheap) == 2);
        test("arm kauft lukrativ", IOService.buyPropertyChoice(ki, lurca) == 2);
        test("arm kauft teuer", IOService.buyPropertyChoice(ki, expan) == 2);

        // es sei denn sie hat bereits eine Nachbarstrasse
        ((PropertyField) IOService.getGame().getBoard().getFields()[3]).setOwner(ki);
        test("arm kauft billigen Nachbar nicht", IOService.buyPropertyChoice(ki, cheap) == 1);

        // aber auch nur wenn er genügend Geld hat
        ((PropertyField) IOService.getGame().getBoard().getFields()[37]).setOwner(ki);
        test("arm kauft teuren Nachbar trotzdem", IOService.buyPropertyChoice(ki, expan) == 2);

        clearGame();
    }

//    @Test
    public void testAuctionEnter() {
        testOutput("testAuctionEnter");
        initGame();
        test("Auktion nicht initialisiert", AuctionService.getAuc() != null);

        AuctionService.startAuction((PropertyField) fm.getFields()[1]);
        test("Spieler nicht in Auktion", AuctionService.getAuc().getAucPlayers() != null);
        clearGame();
    }

}
