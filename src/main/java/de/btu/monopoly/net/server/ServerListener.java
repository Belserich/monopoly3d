/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import de.btu.monopoly.net.networkClasses.*;

import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class ServerListener extends Listener {

    private static final Logger LOGGER = Logger.getLogger(ServerListener.class.getCanonicalName());

    private Server server;
    private Connection host;

    public void received(Connection connection, Object object) {
        super.received(connection, object);

        if (object instanceof FrameworkMessage) {
            // TODO LOG
        } else if (object instanceof IamHostRequest) {
            this.host = connection;
            LOGGER.finer("Host vermerkt");

        } else if (object instanceof JoinRequest) {
            Log.info("Server: JoinRequest erhalten");
            JoinRequest req = (JoinRequest) object;
            JoinResponse res = new JoinResponse();
            res.setName(req.getName());
            this.getHost().sendTCP(res);
        } else if (object instanceof BroadcastUsersRequest) {
            LOGGER.finer("BroadcastUsersRequest erhalten");
            BroadcastUsersRequest req = (BroadcastUsersRequest) object;
            BroadcastUsersResponse res = new BroadcastUsersResponse();
            res.setUsers(req.getUsers());
            server.sendToAllTCP(res);
        } else if (object instanceof GamestartRequest) {
            LOGGER.finer("GamestatRequest erhalten");
            server.sendToAllTCP(new GamestartResponse());
        } else if (object instanceof BroadcastPlayerChoiceRequest) {
            LOGGER.finer("BroadcastPlayerChoiceRequest erhalten");
            server.sendToAllExceptTCP(connection.getID(), object);
        } else if (object instanceof PlayerTradeRequest) {
            LOGGER.finer("PlayerTradeRequest erhalten");
            server.sendToAllExceptTCP(connection.getID(), object);
        } else if (object instanceof  PlayerTradeResponse) {
            LOGGER.finer("PlayerTradeResposne erhalten");
            server.sendToAllExceptTCP(connection.getID(), object);
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
