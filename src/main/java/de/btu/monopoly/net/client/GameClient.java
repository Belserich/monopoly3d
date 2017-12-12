/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.menu.LobbyService;
import de.btu.monopoly.net.networkClasses.*;
import de.btu.monopoly.ui.controller.MessageControl;
import java.io.IOException;
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

    private ClientListener listener;

    public GameClient(int tcp, int timeout) {
        this.tcpPort = tcp;
        this.timeout = timeout;

        client = new Client();
        kryo = client.getKryo();
        registerKryoClasses();
    }

    public void connect(String serverIP) {
        LOGGER.finer("Client verbindet");
        try {
            client.start();
            client.connect(timeout, serverIP, tcpPort);
            listener = new ClientListener();
            client.addListener(listener);
            client.addListener(new LobbyService());
            // Lobby wird in GUI geöffnet
            MessageControl.setConnectionError(false);
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "Client konnte nicht gestartet werden {0}", ex);
            // GUI meldet Fehler
            MessageControl.setConnectionError(true);
        }

    }

    public void disconnect() {
        LOGGER.finer("Client trennt Verbindung");
        client.stop();
    }

    private void registerKryoClasses() {
        kryo.register(BroadcastPlayerChoiceRequest.class);
        kryo.register(JoinRequest.class);
        kryo.register(JoinResponse.class);
        kryo.register(GamestartRequest.class);
        kryo.register(GamestartResponse.class);
        kryo.register(ChangeUsernameRequest.class);
        kryo.register(RefreshLobbyResponse.class);
        kryo.register(String[].class);
        kryo.register(String[][].class);
    }

    public void sendTCP(Object object) {
        client.sendTCP(object);
    }

    public BroadcastPlayerChoiceRequest[] getPlayerChoiceObjects() {
        return listener.getPlayerChoiceObjects();
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
