/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;
import de.btu.monopoly.menu.LobbyClientListener;
import de.btu.monopoly.net.networkClasses.*;
import java.io.IOException;

/**
 *
 * @author Christian Prinz
 */
public class GameClient {

    private int tcpPort;
    private int timeout;
    private Client client;
    private Kryo kryo;

    public GameClient(int tcp, int timeout) {
        this.tcpPort = tcp;
        this.timeout = timeout;

        client = new Client();
        kryo = client.getKryo();
        registerKryoClasses();
    }

    public void connect(String serverIP) {
        Log.info("Client verbindet");
        try {
            client.start();
            client.connect(timeout, serverIP, tcpPort);
            client.addListener(new ClientListener());
            client.addListener(new LobbyClientListener());
        } catch (IOException ex) {
            Log.warn("Client konnte nicht gestartet werden{0}", ex);
        }

    }

    public void disconnect() {
        Log.info("Client trennt Verbindung");
        client.stop();
    }

    private void registerKryoClasses() {
        kryo.register(JoinRequest.class);
        kryo.register(JoinResponse.class);
        kryo.register(GamestartRequest.class);
        kryo.register(GamestartResponse.class);
    }

    public void sendTCP(Object object) {
        client.sendTCP(object);
    }
}
