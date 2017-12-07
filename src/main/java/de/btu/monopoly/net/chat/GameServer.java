/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.chat;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class GameServer {

    private int tcpPort;
    private int udpPort;
    private Server server;
    private Kryo kryo;

    public GameServer(int tcp, int udp) {
        this.tcpPort = tcp;
        this.udpPort = udp;

        server = new Server();
        kryo = server.getKryo();
        registerClasses();
    }

    public void startServer() {
        server.start();

        try {
            server.bind(tcpPort, udpPort);
            server.addListener(new ServerListener());
        } catch (IOException ex) {
            Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void stopServer() {
        server.stop();
    }

    private void registerClasses() {
        //hier kommt zum Beispiel: kryo.register(InputRequest.class);
    }

}
