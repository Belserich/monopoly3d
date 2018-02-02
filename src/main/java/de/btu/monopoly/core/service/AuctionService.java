package de.btu.monopoly.core.service;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.btu.monopoly.Global;
import de.btu.monopoly.core.mechanics.Auction;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.chat.GUIChat;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.data.BidRequest;
import de.btu.monopoly.net.data.BroadcastAuctionResponse;
import de.btu.monopoly.net.data.ExitAuctionRequest;
import de.btu.monopoly.net.data.JoinAuctionRequest;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author patrick
 */
public class AuctionService extends Listener {

    private static final Logger LOGGER = Logger.getLogger(AuctionService.class.getCanonicalName());

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

        boolean auctionRun = true;

        auc.setProperty(prop);
        JoinAuctionRequest jaReq = new JoinAuctionRequest();

        auc.getClient().sendTCP(jaReq);
        if (Global.RUN_AS_TEST) { // nicht fuer Test
            return;
        }
        Global.ref().getGameSceneManager().auctionPopup();
        Global.ref().getGameSceneManager().bidTextFieldFocus();
        while (auctionRun) {
            IOService.sleepDeep(500);
            if (Global.RUN_IN_CONSOLE) { // nur fuer @Console
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
            // falls weniger als 2 Bieter beteiligt sind -> Auktion soll terminieren
            if (getActiveBidderCount() < 2) {
                // Timer
                timer();
                terminateAuction();
                auctionRun = false;
            }
            // wenn die Auktion noch lauft
            else {
                if (!Global.RUN_IN_CONSOLE) {
                    Global.ref().getGameSceneManager().updateAuctionPopup(auctionStillActive(), false);
                }
            }

        }
        if (Global.RUN_IN_CONSOLE) {
            sellProperty();
        }

    }

    private static void timer() {
        String message = "Die Auktion endet. ";
        if (AuctionService.getHighestBid() == 0) {
            message += "Wir haben keinen Bieter. ";
        }
        else {
            message += "Wir haben " + auc.getHighestBid() + "€ von "
                    + AuctionService.getPlayer(AuctionService.getHighestBidder()).getName() + ". ";
        }
        if (!Global.RUN_AS_TEST) {
            GUIChat.getInstance().msgLocal("Auktionsleiter", message);
        }
        message = "";
        for (int i = 1; i < 5; i++) {
            IOService.sleepDeep(1600);

            if (AuctionService.getHighestBid() == 0) {
                message += "Zum ";
            }
            else {
                message += auc.getHighestBid() + "€ zum ";
            }
            switch (i) {
                case 1:
                    message += "ersten.";
                    break;
                case 2:
                    message += "zweiten.";
                    break;
                case 3:
                    message += "dritten.";
                    break;
                case 4:
                    message = "Vorbei!";
            }
            if (!Global.RUN_AS_TEST) {
                GUIChat.getInstance().msgLocal("Auktionsleiter", message);
            }
            message = "";
        }
    }

    private static void terminateAuction() {
        if (AuctionService.getHighestBid() > 0) {   // falls ein Bieter gefunden
            // verkaufe
            if (!Global.RUN_AS_TEST) {
                GUIChat.getInstance().msgLocal("Auktionsleiter", "Das Grundstück geht für " + auc.getHighestBid()
                        + "€ an " + AuctionService.getPlayer(AuctionService.getHighestBidder()).getName());
            }
            if (!Global.RUN_IN_CONSOLE) {
                Global.ref().getGameSceneManager().updateAuctionPopup(auctionStillActive(), false);
            }
            sellProperty();
        }
        else {                                    // falls kein Bieter gefunden
            // verkaufe nicht
            if (!Global.RUN_AS_TEST) {
                GUIChat.getInstance().msgLocal("Auktionsleiter", "Das Grundstück wird nicht verkauft");
            }
            if (!Global.RUN_IN_CONSOLE) {
                Global.ref().getGameSceneManager().updateAuctionPopup(auctionStillActive(), true);
            }
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
        return (getActiveBidderCount() > 1);
    }

    public static int getActiveBidderCount() {
        int activeCount = 0;
        for (int[] aucPlayer : auc.getAucPlayers()) {
            activeCount += aucPlayer[2];
        }
        return activeCount;
    }

    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof BroadcastAuctionResponse) {

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

    /**
     * Gibt den Namen der Property zurueck
     *
     * @return
     */
    public static String getPropertyString() {

        return auc.getProperty().getName();
    }

    public static boolean isStillActive(Player player) {
        for (int[] aucPlayer : auc.getAucPlayers()) {
            if (aucPlayer[0] == player.getId()) {
                return (aucPlayer[2] == 1);
            }
        }
        return false;
    }
}
