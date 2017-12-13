/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import de.btu.monopoly.core.Game;
import de.btu.monopoly.core.service.NetworkService;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.menu.LobbyClientListener;
import de.btu.monopoly.net.networkClasses.BroadcastPlayerChoiceRequest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class GameClient {

    private static final Logger LOGGER = Logger.getLogger(GameClient.class.getCanonicalName());

    private UiInteractionThread uiThread;
    private int tcpPort;
    private int timeout;
    private Client client;
    private Kryo kryo;
    private Player playerOnClient;

    private ClientListener listener;

    public GameClient(int tcp, int timeout) {
        this.tcpPort = tcp;
        this.timeout = timeout;

        uiThread = new UiInteractionThread(this);
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
            listener = new ClientListener(uiThread);
            client.addListener(listener);
            client.addListener(new LobbyClientListener());
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Client konnte nicht gestartet werden {0}", ex);
        }

    }

    public void disconnect() {
        LOGGER.finer("Client trennt Verbindung");
        Game.IS_RUNNING.set(false);
        client.stop();
    }

    public void sendTCP(Object object) {
        client.sendTCP(object);
    }

    public BroadcastPlayerChoiceRequest[] getPlayerChoiceObjects() {
        return uiThread.receivedPlayerChoiceObjects.stream().toArray(BroadcastPlayerChoiceRequest[]::new);
    }
    
    public void clearPlayerChoiceObjects() {
        uiThread.receivedPlayerChoiceObjects.clear();
    }
    
    public UiInteractionThread getUiThread() {
        return uiThread;
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

}
