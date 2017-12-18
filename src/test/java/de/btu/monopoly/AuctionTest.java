//package de.btu.monopoly;
//
////Imports
//import de.btu.monopoly.core.Game;
//import de.btu.monopoly.core.GameBoard;
//import de.btu.monopoly.core.mechanics.Auction;
//import de.btu.monopoly.core.service.AuctionService;
//import de.btu.monopoly.data.card.CardManager;
//import de.btu.monopoly.data.field.*;
//import de.btu.monopoly.data.player.Player;
//import de.btu.monopoly.net.client.GameClient;
//import de.btu.monopoly.net.server.GameServer;
//import org.junit.Assert;
//import org.junit.Test;
//
///**
// *
// * @author patrick
// */
//public class AuctionTest {
//
//    private static Game game;
//    private static GameBoard board;
//    private static Player[] players;
//    private static FieldManager fm;
//    private static GameServer server;
//    private static GameClient client;
//    private static CardManager cm;
//
//    public AuctionTest() {
//
//        players = new Player[4];
//        for (int i = 0; i < 2; i++) {
//            Player player = new Player("root " + (i + 1), i, 1500);
//            players[i] = player;
//        }
//        server = new GameServer(59687);
//        server.startServer();
//        String localHost = System.getProperty("myapplication.ip");
//        client = new GameClient(59687, 5000);
//        client.connect(localHost);
//        client.setPlayerOnClient(players[0]);
//        game = new Game(players, client, 42);
//        game.init();
//    }
//
//    @Test
//    public void testAuctionEnter() {
//        // initialisierung
//
//        Auction auc = new Auction(players, client);
//        AuctionService.startAuction(null);
//        //Nullpointer?!
//        Assert.assertTrue("Spieler nicht in Auktion", AuctionService.getAucPlayers() != null);
//    }
//
//}
