/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.btu.monopoly.net.networkClasses.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class LobbyTable extends Listener {

    private static final Logger LOGGER = Logger.getLogger(LobbyTable.class.getCanonicalName());
    private Server server;
    private String[][] users;
    private boolean gameStarted = false;
    private long randomSeed;

    LobbyTable(Server server) {
        this.server = server;
    }

    public void registerUser(String name, Connection connection) {
        LOGGER.finer("User wird registriert");
        String[][] tempusers;
        String connectionString = connection.toString();
        int newUserId = -1;

        // Array fuer neuen User vorbereiten und ID festlegen
        if (users == null) {
            tempusers = new String[1][3];
            newUserId = 0;
        } else {
            tempusers = new String[users.length + 1][3];
            newUserId = users.length;
            // altes Array in neues uebernehmen
            for (int i = 0; i < users.length; i++) {
                tempusers[i][0] = users[i][0];
                tempusers[i][1] = users[i][1];
                tempusers[i][2] = users[i][2];
            }
        }

        //neuen User eintragen
        tempusers[newUserId][0] = "" + newUserId;
        tempusers[newUserId][1] = name;
        tempusers[newUserId][2] = connectionString;
        users = tempusers;

        joinRespone(newUserId, connection);
        refreshLobbyResponse();
    }

    public void changeUserName(int id, String name) {
        LOGGER.finer("Username wird geändert");
        String idstr = "" + id;
        for (int i = 0; i < users.length; i++) {
            if (users[i][0].equals(idstr)) {
                users[i][1] = name;
            }
        }

        refreshLobbyResponse();
    }

    public void deleteUser(Connection con) {
        LOGGER.finer("User wird entfernt");
        String connectionString = con.toString();
        int userToDelete = -1;

        // lokalisieren
        for (int i = 0; i < users.length; i++) {
            if (users[i][2].equals(connectionString)) {
                userToDelete = i;
            }
        }
        if (userToDelete == -1) {
            LOGGER.warning("deleteUser fehlgeschlagen: nicht lokalisierbar");
        }

        //loeschen
        String[][] tempusers = new String[users.length - 1][3];
        for (int j = 0; j < userToDelete; j++) {
            tempusers[j][0] = users[j][0];
            tempusers[j][1] = users[j][1];
            tempusers[j][2] = users[j][2];
        }
        for (int k = userToDelete + 1; k < users.length; k++) {
            tempusers[k - 1][0] = users[k][0];
            tempusers[k - 1][1] = users[k][1];
            tempusers[k - 1][2] = users[k][2];
        }

        //ueberschreiben
        users = tempusers;

        refreshLobbyResponse();
    }

    public void createGameTable() {
        //TODO evtl für Auktion!!!
    }

    // RESPONSES:____________________________________________
    public void joinRespone(int id, Connection connection) {
        LOGGER.finer("Server sendet JoinResponse");
        JoinResponse joinres = new JoinResponse();
        joinres.setId(id);
        joinres.setSeed(randomSeed);
        connection.sendTCP(joinres);
    }

    public void refreshLobbyResponse() {
        LOGGER.finer("Server sendet RefreshLobbyResponse");
        RefreshLobbyResponse refres = new RefreshLobbyResponse();
        refres.setUsers(users);
        server.sendToAllTCP(refres);
    }

    public void gamestartResponse() {
        LOGGER.finer("Server sendet GamestartResponse");
        GamestartResponse gares = new GamestartResponse();
        server.sendToAllTCP(gares);
    }

    // LISTENER:_____________________________________________
    @Override
    public void received(Connection connection, Object object) {
        super.received(connection, object);

        if (object instanceof FrameworkMessage) {
            // TODO LOG

        } else if (object instanceof JoinRequest) {
            LOGGER.finer("JoinRequest erhalten");
            if (!gameStarted) {
                JoinRequest joinreq = (JoinRequest) object;
                registerUser(joinreq.getName(), connection);
            } else {
                connection.sendTCP(new JoinImpossibleResponse());
            }
        } else if (object instanceof ChangeUsernameRequest) {
            LOGGER.finer("ChangeUsernameRequest erhalten");
            ChangeUsernameRequest chanreq = (ChangeUsernameRequest) object;
            changeUserName(chanreq.getUserId(), chanreq.getUserName());
        } else if (object instanceof GamestartRequest) {
            LOGGER.finer("GamestartRequest erhalten");
            gameStarted = true;
            gamestartResponse();
            createGameTable();
        } else if (object instanceof BroadcastRandomSeedRequest) {
            BroadcastRandomSeedRequest req = (BroadcastRandomSeedRequest) object;
            randomSeed = req.getSeed();
        }
    }

    @Override
    public void disconnected(Connection connection) {
        deleteUser(connection);
    }

    public void shuffle() { //TODO ausprobieren und implementieren
        Collections.shuffle(Arrays.asList(users));
    }

}
