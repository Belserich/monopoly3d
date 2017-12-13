/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.service.NetworkService;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class GameServer {

    private static final Logger LOGGER = Logger.getLogger(Game.class.getCanonicalName());
    private int tcpPort;
    private Server server;
    private Kryo kryo;
    private ServerListener serverL;

    public GameServer(int tcp) {
        this.tcpPort = tcp;

        server = new Server();
        kryo = server.getKryo();
        NetworkService.registerKryoClasses(kryo);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> stopServer()));
    }

    public void startServer() {
        LOGGER.finer("Server startet");
        server.start();
        try {
            server.bind(tcpPort);
            serverL = new ServerListener(server);
            server.addListener(serverL);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Server konnte nicht gebunden werden {0}", ex);
        }

    }

    public void stopServer() {
        LOGGER.finer("Server fährt runter");
        server.stop();
    }

    public String getServerIP() {
        String output = "";
        try {
            output = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            LOGGER.log(Level.WARNING, "ServerIP konnte nicht ausgelesen werden{0}", ex);
        }
        return output;
    }

    /**
     * @return the serverL
     */
    public ServerListener getServerListener() {
        return serverL;
    }

}
