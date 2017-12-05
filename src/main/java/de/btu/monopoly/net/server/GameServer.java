/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import de.btu.monopoly.net.networkClasses.*;
import java.io.IOException;

/**
 *
 * @author Christian Prinz
 */
public class GameServer {

    private int tcpPort;
    private Server server;
    private Kryo kryo;

    public GameServer(int tcp) {
        this.tcpPort = tcp;

        server = new Server();
        kryo = server.getKryo();
        registerKryoClasses();
    }

    public void startServer() {
        Log.info("Server startet");
        server.start();
        try {
            server.bind(tcpPort);
            server.addListener(new ServerListener());
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.warn("Server konnte nicht gebunden werden{0}", ex);
        }

    }

    public void stopServer() {
        Log.info("Server fährt runter");
        server.stop();
    }

    private void registerKryoClasses() {
        kryo.register(JoinRequest.class);
        kryo.register(JoinResponse.class);
        kryo.register(GamestartRequest.class);
        kryo.register(GamestartResponse.class);
        kryo.register(de.btu.monopoly.menu.LobbyService.class);

    }

}
