/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import de.btu.monopoly.net.networkClasses.*;

/**
 *
 * @author Christian Prinz
 */
public class ServerListener extends Listener {

    private Server server;
    private Connection host;

    public void received(Connection connection, Object object) {

        if (object instanceof IamHostRequest) {
            this.host = connection;
            Log.info("Host vermerkt");

        }

        if (object instanceof JoinRequest) {
            Log.info("Server: JoinRequest erhalten");
            JoinRequest req = (JoinRequest) object;
            JoinResponse res = new JoinResponse();
            res.setName(req.getName());
            this.getHost().sendTCP(res);
        }

        if (object instanceof BroadcastUsersRequest) {
            Log.info("BroadcastUsersRequest erhalten");
            BroadcastUsersRequest req = (BroadcastUsersRequest) object;
            BroadcastUsersResponse res = new BroadcastUsersResponse();
            res.setUsers(req.getUsers());
            server.sendToAllTCP(res);
        }

        if (object instanceof GamestartRequest) {
            server.sendToAllTCP(new GamestartResponse());
        }
    }

    public ServerListener(Server server) {
        this.server = server;
    }

    /**
     * @return the host
     */
    public Connection getHost() {
        return host;
    }

}
