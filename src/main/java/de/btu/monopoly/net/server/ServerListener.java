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
import de.btu.monopoly.net.data.BroadcastPlayerChoiceRequest;
import de.btu.monopoly.net.data.ChatMessage;
import de.btu.monopoly.net.data.PlayerTradeRequest;
import de.btu.monopoly.net.data.PlayerTradeResponse;

/**
 *
 * @author Christian Prinz
 */
public class ServerListener extends Listener {

    private Server server;

    public ServerListener(Server server) {
        this.server = server;
    }

    @Override
    public void received(Connection connection, Object object) {
        super.received(connection, object);

        if (!(object instanceof FrameworkMessage)) {

            if (object instanceof BroadcastPlayerChoiceRequest) {

                server.sendToAllExceptTCP(connection.getID(), object);
            }
            else if (object instanceof PlayerTradeRequest) {

                server.sendToAllExceptTCP(connection.getID(), object);
            }
            else if (object instanceof PlayerTradeResponse) {

                server.sendToAllExceptTCP(connection.getID(), object);
            }
            else if (object instanceof ChatMessage) {

                server.sendToAllTCP(object);
            }
        }
    }
}
