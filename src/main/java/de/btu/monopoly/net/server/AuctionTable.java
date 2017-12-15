/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.btu.monopoly.core.service.NetworkService;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.networkClasses.BroadcastAuctionResponse;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class AuctionTable extends Listener {

    private static final Logger LOGGER = Logger.getLogger(AuctionTable.class.getCanonicalName());
    private Server server;
    private static Player[] players;
    private static int[][] aucPlayers;

    /**
     * @param aPlayers the players to set
     */
    public static void setPlayers(Player[] aPlayers) {
        players = aPlayers;
    }

    AuctionTable(Server server) {
        this.server = server;
    }

    private void generateAuctionPlayerList() {
        aucPlayers = new int[players.length][3];
        for (int i = 0; i < players.length; i++) {
            aucPlayers[i][0] = players[i].getId();
            aucPlayers[i][1] = 0;
            aucPlayers[i][2] = players[i].isBankrupt() ? 0 : 1;
        }
    }

    private void setBid(int id, int amount) {
        for (int i = 0; i < aucPlayers.length; i++) {
            aucPlayers[i][1] = (aucPlayers[i][0] == id) ? amount : aucPlayers[i][1];
        }
    }

    private void exitPlayer(int id) {
        for (int i = 0; i < aucPlayers.length; i++) {
            aucPlayers[i][2] = (aucPlayers[i][0] == id) ? 1 : aucPlayers[i][2];
        }

    }

    private void broadcastList() {
        BroadcastAuctionResponse res = new BroadcastAuctionResponse();
        res.setAucPlayers(aucPlayers);
        server.sendToAllTCP(res);
        NetworkService.logSendMessage(res);
    }

    //LISTENER:____________________________________________________________________
    @Override
    public void received(Connection connection, Object object) {

    }

}
