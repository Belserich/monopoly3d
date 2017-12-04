/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class GameClient {

    private int tcpPort;
    private int udpPort;
    private int timeout;
    private Client client;
    private Kryo kryo;

    public GameClient(int tcp, int udp, int timeout) {
        this.tcpPort = tcp;
        this.udpPort = udp;
        this.timeout = timeout;

        client = new Client();
        kryo = client.getKryo();
        registerClasses();
    }

    public void connect(String serverIP) {
        try {
            client.connect(timeout, serverIP, tcpPort, udpPort);
            client.addListener(new ClientListener());
        } catch (IOException ex) {
            Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disconnect() {
        client.stop();
    }

    private void registerClasses() {
        // die selben Klassen wie beim Server
    }

}
