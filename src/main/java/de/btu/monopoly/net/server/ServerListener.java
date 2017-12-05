/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.net.networkClasses.*;
import java.util.ArrayList;

/**
 *
 * @author Christian Prinz
 */
public class ServerListener extends Listener {

    private static ArrayList<Connection> connections = new ArrayList();
    private static ArrayList<Player> tempPlayerList = new ArrayList();
    private static int runningID = 0;

    public void recieved(Connection connection, Object object) {

        System.out.println("hhhhh" + object);
        if (object instanceof JoinRequest) {
            Log.info("JoinRequest erhalten");
            JoinRequest req = (JoinRequest) object;

            // Client der Connections-Liste hizufügen
            connections.add(connection);

            // Spieler für den joinenden Client anlegen und ins TempArray legen
            Player player = new Player(req.getName(), runningID, 1500);
            runningID++;
            tempPlayerList.add(player);

            //Player[] erzeugen und fuellen
            Player[] players = new Player[tempPlayerList.size()];
            for (int i = 0; i < players.length; i++) {
                players[i] = tempPlayerList.get(i);
            }

            //Response erzeugen
            JoinResponse res = new JoinResponse();
            res.setPlayers(players);

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
