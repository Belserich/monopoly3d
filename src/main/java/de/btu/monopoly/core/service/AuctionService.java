package de.btu.monopoly.core.service;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.btu.monopoly.GlobalSettings;
import de.btu.monopoly.core.mechanics.Auction;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.data.BidRequest;
import de.btu.monopoly.net.data.BroadcastAuctionResponse;
import de.btu.monopoly.net.data.ExitAuctionRequest;
import de.btu.monopoly.net.data.JoinAuctionRequest;
import de.btu.monopoly.ui.SceneManager;
import de.btu.monopoly.ui.TextAreaHandler;
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
        TextAreaHandler handler = new TextAreaHandler();
        LOGGER.addHandler(handler);
    }

    /**
     * Startet die Auktion, ermittelt den Höchstbietenden und übergibt den Gewinner, sowie den Preis an deren Klassenvariablen
     *
     * @param prop
     */
    public static void startAuction(PropertyField prop) {

        int oneMore = 0;

        boolean auctionRun = true;
        boolean noBidder = false;

        auc.setProperty(prop);
        JoinAuctionRequest jaReq = new JoinAuctionRequest();

        auc.getClient().sendTCP(jaReq);
        if (!GlobalSettings.RUN_AS_TEST) { // nicht fuer Test
            SceneManager.auctionPopup();
            SceneManager.bidTextFieldFocus();
            while (auctionRun) {
                IOService.sleepDeep(500);
                if (GlobalSettings.RUN_IN_CONSOLE) { // nur fuer @Console
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
                else { //Nur fuer @GUI
                    /*
                     * Falls nur noch ein Bieter uebrig ist, hat dieser dank dem Boolean auctionRun noch die Moeglichkeit
                     * weiterhin zu bieten, so lange der (weiter unten implementierte) Countdown noch laeuft.
                     */
                    if (!auctionStillActive()) {
                        /*
                         * Falls das Gebot 0 betraegt und nur noch 1 Bieter uebrig ist, bekommt dieser die Chance innerhalb des
                         * (weiter unten implementierten) Countdowns zu bieten.
                         */
                        if (AuctionService.getHighestBid() == 0) {
                            noBidder = true;
                            for (int i = 5; i != 0; i--) {
                                LOGGER.fine("Es wurde noch nichts geboten, es bleiben noch " + i + " Sekunden!");
                                IOService.sleep(1000);
                                if (AuctionService.getHighestBid() != 0) {
                                    noBidder = false;
                                    LOGGER.fine("Es wurde " + AuctionService.getHighestBid() + "€ von "
                                            + AuctionService.getPlayer(AuctionService.getHighestBidder()).getName() + " geboten!");
                                    IOService.sleep(1000);
                                    break;
                                }
                            }
                            //Falls sich kein Bieter gefunden hat (Objekt wird NICHT verkauft)
                            if (noBidder) {
                                LOGGER.fine("Das Grundstück " + AuctionService.getPropertyString() + " wurde nicht verkauft!");
                                auctionRun = false;
                                SceneManager.updateAuctionPopup(auctionStillActive(), noBidder);
                            }
                            //Falls sich ein Bieter gefunden hat
                            else {
                                auctionRun = false;
                                SceneManager.updateAuctionPopup(auctionStillActive(), noBidder);
                                sellProperty();
                            }
                        }
                        else {
                            //Letzter Bieter bekommt nochmal die Moeglichkeit nachzubieten
                            for (int i = 5; i != 0; i--) {
                                LOGGER.fine("Auktion endet in " + i + " Sekunden. Höchstegebot: "
                                        + auc.getHighestBid() + "€ von " + AuctionService.getPlayer(AuctionService.getHighestBidder()).getName());
                                IOService.sleep(1000);
                            }
                            auctionRun = false;
                            SceneManager.updateAuctionPopup(auctionStillActive(), noBidder);
                            sellProperty();
                        }
                    }
                    else {
                        SceneManager.updateAuctionPopup(auctionStillActive(), noBidder);
                    }

                }
            }
            if (GlobalSettings.RUN_IN_CONSOLE) {
                sellProperty();
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
            IOService.sleepDeep(1000);

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
