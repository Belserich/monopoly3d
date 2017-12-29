package de.btu.monopoly.core.service;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.btu.monopoly.GlobalSettings;
import de.btu.monopoly.core.mechanics.Auction;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.input.IOService;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.networkClasses.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author patrick
 */
public class AuctionService extends Listener {

    private static final Logger LOGGER = Logger.getLogger(AuctionService.class.getCanonicalName());
    private static final boolean isRunAsTest = GlobalSettings.isRunAsTest();
    private static final boolean isRunInConsole = GlobalSettings.isRunInConsole();
    private static Auction auc;

    /**
     * Initialisierung des "Auktionshauses"
     *
     * @param players
     * @param client
     */
    public static void initAuction(Player[] players, GameClient client) {
        auc = new Auction(players, client);
        LOGGER.setLevel(Level.FINEST);
    }

    /**
     * Startet die Auktion, ermittelt den Höchstbietenden und übergibt den Gewinner, sowie den Preis an deren Klassenvariablen
     *
     * @param prop
     */
    public static void startAuction(PropertyField prop) {

        auc.setProperty(prop);
        JoinAuctionRequest jaReq = new JoinAuctionRequest();
        NetworkService.logClientSendMessage(jaReq, auc.getPlayerName());
        auc.getClient().sendTCP(jaReq);

        IOService.sleepDeep(1500);
        if (!isRunAsTest) { // nicht fuer Test
            while (auctionStillActive()) {
                IOService.sleepDeep(500);
                if (isRunInConsole) { // nur fuer @Console
                    LOGGER.finest("Wähle [1] für bieten [2] für aussteigen");
                    Scanner scanner = new Scanner(System.in);
                    switch (scanner.nextInt()) {
                        case 1:
                            LOGGER.finest("Wähle dein Gebot");
                            setBid(getAuc().getClient().getPlayerOnClient().getId(), scanner.nextInt());
                            break;
                        case 2:
                            playerExit(getAuc().getClient().getPlayerOnClient().getId());
                            break;
                        default:
                            break;
                    }
                }
            }
            sellProperty();
        }
    }

    private static void sellProperty() {
        for (Player player : auc.getPlayers()) {
            auc.setWinner((player.getId() == auc.getHighestBidder()) ? player : auc.getWinner());
        }
        if (auc.getWinner() != null) {
            FieldService.buyPropertyField(getAuc().getWinner(), getAuc().getProperty(), getAuc().getHighestBid());
        }
        auc.setHighestBidder(-1);
        auc.setHighestBid(0);
    }

    /**
     * Setzt das Gebot eines Spielers, falls dieses hoeher ist als das aktuell hoechste Gebot
     *
     * @param playerID
     * @param bid
     * @return true wenn Gebot hoeher gesetzt
     */
    public static boolean setBid(int playerID, int bid) {

        boolean isBidOk = false;
        if (PlayerService.checkLiquidity(getPlayer(playerID), bid) && bid > getHighestBid()) {
            isBidOk = true;
            BidRequest bidReq = new BidRequest();
            bidReq.setBid(bid);
            bidReq.setPlayerID(playerID);
            NetworkService.logClientSendMessage(bidReq, auc.getPlayerName());
            auc.getClient().sendTCP(bidReq);
        }

        return isBidOk;
    }

    /**
     * Diese Methode ermoeglicht es einem Spieler, die Auktion zu verlassen.
     *
     * @param playerID
     */
    public static void playerExit(int playerID) {

        ExitAuctionRequest exReq = new ExitAuctionRequest();
        exReq.setPlayerID(playerID);
        NetworkService.logClientSendMessage(exReq, auc.getPlayerName());
        auc.getClient().sendTCP(exReq);

    }

    /**
     * Das hoechste Gebot aller Bieter wird ermittelt. Es wird die ID des Spielers mit dem hoechsten Gebot zurueck gegeben
     *
     * @return playerID
     */
    public static int getHighestBid() {
        return auc.getHighestBid();
    }

    /**
     * Das hoechste Gebot aller Bieter wird ermittelt. Es wird die ID des Spielers mit dem hoechsten Gebot zurueck gegeben
     *
     * @return playerID
     */
    public static int getHighestBidder() {
        return auc.getHighestBidder();
    }

    /**
     * Ueberprueft ob die Auktion noch genug Bieter hat
     *
     * @return stillActive
     */
    public static boolean auctionStillActive() {

        int activCount = 0;
        boolean stillActive = false;
        for (int[] aucPlayer : auc.getAucPlayers()) {
            activCount += aucPlayer[2];
        }

        if (activCount > 1) {
            stillActive = true;
        }

        return stillActive;
    }

    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof BroadcastAuctionResponse) {
            NetworkService.logClientReceiveMessage(object, auc.getPlayerName());
            auc.setAucPlayers(((BroadcastAuctionResponse) object).getAucPlayers());
            auc.setHighestBid(((BroadcastAuctionResponse) object).getHighestBid());
            auc.setHighestBidder(((BroadcastAuctionResponse) object).getHighestBidder());
            IOService.sleepDeep(100);

            //@GUI kommt weg:
            LOGGER.log(Level.FINER, "<AUKTION>: \n  Stra\u00dfe: {0}\n  Auktion\u00e4re:", auc.getProperty());
            for (int[] aucPlayer : auc.getAucPlayers()) {
                LOGGER.log(Level.FINER, "     ID[{0}] {1}\u20ac - aktiv:{2}", new Object[]{aucPlayer[0], aucPlayer[1], aucPlayer[2]});
            }
            LOGGER.log(Level.FINER, "  H\u00f6chstes Gebot: {0}\u20ac von aucID {1}", new Object[]{auc.getHighestBid(), getHighestBidder()});

            //Das nicht
            IOService.betSequence(auc);
        }
    }

    /**
     * @return the auc
     */
    public static Auction getAuc() {
        return auc;
    }

    /**
     * Holt aus der ID den Player
     *
     * @param playerID
     * @return
     */
    public static Player getPlayer(int playerID) {

        Player[] players = auc.getPlayers();
        Player retPlayer = null;

        for (Player player : players) {
            if (player.getId() == playerID) {
                retPlayer = player;
            }
        }
        return retPlayer;
    }
}