/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import de.btu.monopoly.core.service.NetworkService;
import de.btu.monopoly.net.networkClasses.BroadcastPlayerChoiceRequest;
import de.btu.monopoly.net.networkClasses.PlayerTradeRequest;

/**
 *
 * @author Christian Prinz
 */
public class ClientListener extends Listener {

    private UiInteractionThread thread;

    public ClientListener(UiInteractionThread thread) {
        this.thread = thread;
    }

    public synchronized void received(Connection connection, Object object) {
        super.received(connection, object);

        if (!(object instanceof FrameworkMessage)) {
            NetworkService.logReceiveMessage(object);

            if (object instanceof BroadcastPlayerChoiceRequest) {
                thread.receivedPlayerChoiceObjects.add((BroadcastPlayerChoiceRequest) object);
            }
            else if (object instanceof PlayerTradeRequest) {
                thread.tradeRequestObjects.add((PlayerTradeRequest) object);
            }
        }
    }
}
