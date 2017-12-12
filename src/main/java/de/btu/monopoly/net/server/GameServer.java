/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import de.btu.monopoly.core.Game;
import de.btu.monopoly.net.networkClasses.*;
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
    private LobbyTable lobbyTable;

    public GameServer(int tcp) {
        this.tcpPort = tcp;

        server = new Server();
        kryo = server.getKryo();
        registerKryoClasses();
    }

    public void startServer() {
        LOGGER.finer("Server startet");
        server.start();
        try {
            server.bind(tcpPort);
            serverL = new ServerListener(server);
            lobbyTable = new LobbyTable(server);
            server.addListener(serverL);
            server.addListener(lobbyTable);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Server konnte nicht gebunden werden {0}", ex);
        }

    }

    public void stopServer() {
        LOGGER.finer("Server fährt runter");
        server.stop();
    }

    private void registerKryoClasses() {
        kryo.register(BroadcastPlayerChoiceRequest.class);
        kryo.register(JoinRequest.class);
        kryo.register(JoinResponse.class);
        kryo.register(GamestartRequest.class);
        kryo.register(GamestartResponse.class);
        kryo.register(ChangeUsernameRequest.class);
        kryo.register(RefreshLobbyResponse.class);
        kryo.register(JoinImpossibleResponse.class);
        kryo.register(String[].class);
        kryo.register(String[][].class);
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
