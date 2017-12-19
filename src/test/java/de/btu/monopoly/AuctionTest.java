package de.btu.monopoly;

//Imports
import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.service.AuctionService;
import de.btu.monopoly.data.card.CardManager;
import de.btu.monopoly.data.field.FieldManager;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.input.IOService;
import de.btu.monopoly.menu.Lobby;
import de.btu.monopoly.menu.LobbyService;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.server.GameServer;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author patrick
 */
public class AuctionTest {

    private Lobby lobby;
    private GameServer server;
    private GameClient client;
    private Game game;
    private GameBoard board;
    private Player[] players;
    private FieldManager fm;
    private CardManager cm;

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

        Game controller = new Game(LobbyService.generatePlayerArray(), lobby.getPlayerClient(), lobby.getRandomSeed());
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
        server.stopServer();
        lobby = null;
        server = null;
        client = null;
        game = null;
        board = null;
        players = null;
        fm = null;
        cm = null;
        System.out.println("\nCLEAR GAME ---- ALLES ZURUECKGESETZT!!!\n");
    }

    @Test
    public void testAuctionEnter() {
        // initialisierung
        initNetwork();
        initLobby();
        initGame();

        AuctionService.startAuction((PropertyField) fm.getFields()[1]);
        //Nullpointer?!
        Assert.assertTrue("Spieler nicht in Auktion", AuctionService.getAuc().getAucPlayers() != null);
    }

}
