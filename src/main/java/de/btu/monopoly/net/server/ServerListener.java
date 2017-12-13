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
import de.btu.monopoly.net.networkClasses.*;

import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class ServerListener extends Listener {

    private static final Logger LOGGER = Logger.getLogger(ServerListener.class.getCanonicalName());

    private Server server;

    @Override
    public void received(Connection connection, Object object) {
        super.received(connection, object);

        if (object instanceof FrameworkMessage) {
            // TODO LOG

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

    @Override
    public void disconnected(Connection connection) {
        // TODO
    }

    public ServerListener(Server server) {
        this.server = server;
    }
}
