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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

/**
 *
 * @author Christian Prinz
 */
public class ClientListener extends Listener {
    
    private LinkedList<Object> receivedObjects = new LinkedList<>();
    
    public ClientListener() {
        receivedObjects = new LinkedList<>();
    }

    public void received(Connection connection, Object object) {
        super.received(connection, object);

        if (!(object instanceof FrameworkMessage)) {
    
            NetworkService.logServerReceiveMessage(object);
            synchronized (receivedObjects) {
                receivedObjects.add(object);
            }
        }
    }
    
    public Optional<Object> waitForObjectOfClass(Class<?> clazz) {
        
        do {
            synchronized (receivedObjects) {
                Iterator it = receivedObjects.iterator();
                while (it.hasNext()) {
                    Object obj = it.next();
                    if (obj.getClass() == clazz) {
                        it.remove();
                        return Optional.of(obj);
                    }
                }
            }
            
            try {
                Thread.sleep(200);
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        while (!Thread.currentThread().isInterrupted());
        
        return Optional.empty();
    }
}
