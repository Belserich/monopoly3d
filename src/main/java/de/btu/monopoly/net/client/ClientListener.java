/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.client;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.btu.monopoly.net.networkClasses.BroadcastPlayerChoiceRequest;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Christian Prinz
 */
public class ClientListener extends Listener {
    
    private final List<BroadcastPlayerChoiceRequest> receivedPlayerChoiceObjects;

    public ClientListener() {
        receivedPlayerChoiceObjects = new LinkedList<>();
    }
    
    public synchronized void received(Connection connection, Object object) {
        super.received(connection, object);
        
        if (object instanceof BroadcastPlayerChoiceRequest) {
            receivedPlayerChoiceObjects.add((BroadcastPlayerChoiceRequest) object);
        }
    }
    
    public synchronized BroadcastPlayerChoiceRequest[] getPlayerChoiceObjects() {
        int size = receivedPlayerChoiceObjects.size();
        BroadcastPlayerChoiceRequest[] retObj = receivedPlayerChoiceObjects.toArray(new BroadcastPlayerChoiceRequest[size]);
        receivedPlayerChoiceObjects.clear();
        return retObj;
    }
}
