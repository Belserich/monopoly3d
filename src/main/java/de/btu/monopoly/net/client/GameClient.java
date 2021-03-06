/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.service.AuctionService;
import de.btu.monopoly.core.service.NetworkService;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.menu.LobbyService;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class GameClient {

    private static final Logger LOGGER = Logger.getLogger(GameClient.class.getCanonicalName());

    private int tcpPort;
    private int timeout;
    private Client client;
    private Kryo kryo;
    private Player playerOnClient;
    private Game game;

    private ClientListener listener;

    public GameClient(int tcp, int timeout) {
        this.tcpPort = tcp;
        this.timeout = timeout;

        client = new Client();
        kryo = client.getKryo();
        NetworkService.registerKryoClasses(kryo);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> disconnect()));
    }

    public void connect(String serverIP) {

        LOGGER.finer("Die Client versucht, eine Verbindung aufzubauen.");
        try {

            client.start();
            client.connect(timeout, serverIP, tcpPort);
            listener = new ClientListener();
            client.addListener(listener);
            client.addListener(new LobbyService());
            client.addListener(new AuctionService());
            client.addListener(new TrafficListener());

        } catch (IOException ex) {

            String errorMsg = String.format("Connection error! ip: %s port: %d", serverIP, tcpPort);
            LOGGER.warning(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    public void addExternalListener(Listener listener) {
        client.addListener(listener);
    }

    public void disconnect() {
        LOGGER.finer("Client trennt Verbindung");
        client.stop();
    }

    public void sendTCP(Object object) {
        NetworkService.logClientSendMessage(object,
                (playerOnClient == null) ? String.valueOf(client.getID()) : playerOnClient.getName());
        client.sendTCP(object);
    }

    public Object waitForObjectOfClass(Class<?> clazz) {
        Optional<Object> optional = listener.waitForObjectOfClass(clazz);
        if (optional.isPresent()) {
            return optional.get();
        }
        else {
            throw new RuntimeException(String.format("Thread %s interrupted while waiting for network data.",
                    Thread.currentThread().getName()));
        }
    }

    /**
     * @return the playerOnClient
     */
    public Player getPlayerOnClient() {
        return playerOnClient;
    }

    /**
     * @param playerOnClient the playerOnClient to set
     */
    public void setPlayerOnClient(Player playerOnClient) {
        this.playerOnClient = playerOnClient;
    }

    /**
     * @return the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * @param game the game to set
     */
    public void setGame(Game game) {
        this.game = game;
    }

    private class TrafficListener extends Listener {

        @Override
        public void received(Connection connection, Object object) {
            if (!(object instanceof FrameworkMessage)) {
                NetworkService.logClientReceiveMessage(object,
                        (playerOnClient == null) ? String.valueOf(client.getID()) : playerOnClient.getName());
            }
        }
    }
}
