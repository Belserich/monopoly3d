/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.service.AuctionService;
import de.btu.monopoly.core.service.NetworkService;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.menu.LobbyService;
import de.btu.monopoly.ui.controller.GuiMessages;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
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
        LOGGER.finer("Client verbindet");
        try {
            client.start();
            client.connect(timeout, serverIP, tcpPort);
            listener = new ClientListener();
            client.addListener(listener);
            client.addListener(new LobbyService());
            client.addListener(new AuctionService());
            // Lobby wird in GUI geöffnet
            GuiMessages.setConnectionError(false);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Client konnte nicht gestartet werden {0}", ex);
            // GUI meldet Fehler
            GuiMessages.setConnectionError(true);
        }

    }

    public void disconnect() {
        LOGGER.finer("Client trennt Verbindung");
        Game.getIS_RUNNING().set(false);
        client.stop();
    }

    public void sendTCP(Object object) {
        client.sendTCP(object);
    }
    
    public Object waitForObjectOfClass(Class<?> clazz) {
        Optional<Object> optional = listener.waitForObjectOfClass(clazz);
        if (optional.isPresent()) {
            return optional.get();
        }
        else throw new RuntimeException(String.format("Thread %s interrupted while waiting for network data.",
                Thread.currentThread().getName()));
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

}
