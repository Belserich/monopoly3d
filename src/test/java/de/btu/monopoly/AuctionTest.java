package de.btu.monopoly;

//Imports
import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.mechanics.Auction;
import de.btu.monopoly.data.card.CardManager;
import de.btu.monopoly.data.field.*;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.server.GameServer;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author patrick
 */
public class AuctionTest {

    private static Game game;
    private static GameBoard board;
    private static Player[] players;
    private static FieldManager fm;
    private static GameServer server;
    private static GameClient client;
    private static CardManager cm;

    @Test
    public void testAuctionEnter() {
        // initialisierung
        server = new GameServer(59687);
        server.startServer();
        client = new GameClient(59687, 5000);
        String localHost = System.getProperty("myapplication.ip");
        client.connect(localHost);

        Auction auc = new Auction(players, client);
        //Nullpointer?!
        Assert.assertTrue("Spieler nicht in Auktion", players.equals(auc.getPlayers()));
    }

}
