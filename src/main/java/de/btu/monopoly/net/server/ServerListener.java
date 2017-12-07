/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.networkClasses.GamestartRequest;
import de.btu.monopoly.net.networkClasses.GamestartResponse;
import de.btu.monopoly.net.networkClasses.JoinRequest;
import de.btu.monopoly.net.networkClasses.JoinResponse;

import java.util.ArrayList;

/**
 *
 * @author Christian Prinz
 */
public class ServerListener extends Listener {

    private static ArrayList<Connection> connections = new ArrayList();
    private static ArrayList<Player> tempPlayerList = new ArrayList();
    private static int runningID = 0;

    public void received(Connection connection, Object object) {

        if (object instanceof JoinRequest) {
            Log.info("JoinRequest erhalten");
            JoinRequest req = (JoinRequest) object;

            // Client der Connections-Liste hizufügen
            connections.add(connection);

            //ID festlegen und namen holen
            int id = runningID;
            runningID++;
            String name = req.getName();

//            // Spieler für den joinenden Client anlegen und ins TempArray legen
//            Player player = new Player(req.getName(), runningID, 1500);
//            runningID++;
//            tempPlayerList.add(player);
//
//            //Player[] erzeugen und fuellen
//            Player[] players = new Player[tempPlayerList.size()];
//            for (int i = 0; i < players.length; i++) {
//                players[i] = tempPlayerList.get(i);
//            }
//            if (players.length > 0) {
//                Log.info(players[0].getName());
//            }
            //Response erzeugen
            JoinResponse res = new JoinResponse();
            res.setId(id);
            res.setName(name);

            //Responses verteilen
            connection.sendTCP(res);
            for (Connection con : connections) {
                con.sendTCP(res);
            }
        }

        if (object instanceof GamestartRequest) {
            // Response verteilen
            for (Connection con : connections) {
                con.sendTCP(new GamestartResponse());
            }

        }
    }

}
