package de.btu.monopoly.core.service;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.btu.monopoly.core.mechanics.Auction;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.input.IOService;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.networkClasses.*;
import java.util.Scanner;

/**
 *
 * @author patrick
 */
public class AuctionService extends Listener {

    private static Auction auc;
    private static int[][] aucPlayers;

    /**
     * Initialisierung des "Auktionshauses"
     *
     * @param players
     */
    public static void initAuction(Player[] players, GameClient client) {
        auc = new Auction(players, client);
    }

    /**
     * Startet die Auktion, ermittelt den Höchstbietenden und übergibt den Gewinner, sowie den Preis an deren Klassenvariablen
     *
     * @param prop
     */
    public static void startAuction(PropertyField prop) {

        auc.setProperty(prop);
        JoinAuctionRequest jaReq = new JoinAuctionRequest();
        auc.getClient().sendTCP(jaReq);
        NetworkService.logClientSendMessage(jaReq, auc.getPlayerName());

        IOService.sleep(500);
        while (auctionStillActive()) {
            // IOService.sleep(500); TODO es bleibt nur das.
            System.out.println("Wähle [1] für bieten [2] für aussteigen");
            Scanner scanner = new Scanner(System.in);
            switch (scanner.nextInt()) {
                case 1:
                    System.out.println("Wähle dein Gebot");
                    setBid(getAuc().getClient().getPlayerOnClient().getId(), scanner.nextInt());
                    break;
                case 2:
                    playerExit(getAuc().getClient().getPlayerOnClient().getId());
                    break;
            }
        }

        if (auc.getWinner() != null) {
            FieldService.buyPropertyField(getAuc().getWinner(), getAuc().getProperty(), getAuc().getPropPrice());
        }

    }

    /**
     * Setzt das Gebot eines Spielers, falls dieses hoeher ist als das aktuell hoechste Gebot
     *
     * @param playerID
     * @param bid
     */
    public static boolean setBid(int playerID, int bid) {

        boolean isBidOk = false;

        if (PlayerService.checkLiquidity(getPlayer(playerID), bid)) {
            if (bid > getHighestBid()) {
                isBidOk = true;
                BidRequest bidReq = new BidRequest();
                bidReq.setBid(bid);
                bidReq.setPlayerID(playerID);
                auc.getClient().sendTCP(bidReq);
                NetworkService.logClientSendMessage(bidReq, auc.getPlayerName());
            }
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
        NetworkService.logClientSendMessage(exReq, auc.getPlayerName());

    }

    /**
     * Das hoechste Gebot aller Bieter wird ermittelt. Es wird die ID des Spielers mit dem hoechsten Gebot zurueck gegeben
     *
     * @return playerID
     */
    public static int getHighestBid() {

        int highestBid = -1;

        for (int i = 0; i < getAucPlayers().length; i++) {
            if (highestBid < getAucPlayers()[i][1] && getAucPlayers()[i][1] != 0) {
                highestBid = getAucPlayers()[i][1];
            }
        }

        return highestBid;
    }

    /**
     * Das hoechste Gebot aller Bieter wird ermittelt. Es wird die ID des Spielers mit dem hoechsten Gebot zurueck gegeben
     *
     * @return playerID
     */
    public static int getHighestBidder() {

        int playerID = -1;
        int highestBid = -1;

        for (int i = 0; i < getAucPlayers().length; i++) {
            if (highestBid < getAucPlayers()[i][1] && getAucPlayers()[i][1] != 0) {
                playerID = getAucPlayers()[i][0];
            }
        }

        return playerID;
    }

    /**
     * Ueberprueft ob die Auktion noch genug Bieter hat
     *
     * @return stillActive
     */
    public static boolean auctionStillActive() {

        int activCount = 0;
        boolean stillActive = false;
        for (int i = 0; i < getAucPlayers().length; i++) {
            activCount += getAucPlayers()[i][2];
        }

        if (activCount > 1) {
            stillActive = true;
        }

        return stillActive;
    }

    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof BroadcastAuctionResponse) {
            aucPlayers = ((BroadcastAuctionResponse) object).getAucPlayers();
            NetworkService.logClientReceiveMessage(object, auc.getPlayerName());
            for (int[] aucPlayer : aucPlayers) {
                if (aucPlayer[1] > auc.getPropPrice()) {
                    auc.setPropPrice(aucPlayer[1]);
                    for (Player player : auc.getPlayers()) {
                        if (player.getId() == aucPlayer[0]) {
                            auc.setWinner(player);
                        }
                    }
                }
            }

            //kommt weg:
            System.out.println("Lobby: \nStraße: " + auc.getProperty() + "\nPreis:  " + auc.getPropPrice() + "\nAuktionäre:");
            for (int i = 0; i < aucPlayers.length; i++) {

                System.out.println("ID[" + aucPlayers[i][0] + "] " + aucPlayers[i][1] + "€ - aktiv:" + aucPlayers[i][2]);
            }
            System.out.println(auc.getWinner() + " - " + auc.getPropPrice() + "€");
        }
    }

    /**
     * @return the auc
     */
    public static Auction getAuc() {
        return auc;
    }

    /**
     * @return the aucPlayers
     */
    public static int[][] getAucPlayers() {
        return aucPlayers;
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
