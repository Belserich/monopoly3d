/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.btu.monopoly.core.service.NetworkService;
import de.btu.monopoly.net.networkClasses.Lobby.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class LobbyTable extends Listener {

    private static final Logger LOGGER = Logger.getLogger(LobbyTable.class.getCanonicalName());
    private final Server server;
    private String[][] users;
    private boolean gameStarted = false;
    private long randomSeed;
    private int playerID = 0;

    LobbyTable(Server server) {
        this.server = server;
    }

    /**
     * Registriert einen neuen User in der Lobby und verteilt die Liste neu
     *
     * @param name des neuen Users
     * @param connection des Clients, welcher den neuen User besitzt
     * @param kiLevel des Users (0 falls menschlich, 1-3 KI-Stufen)
     */
    public void registerUser(String name, Connection connection, int kiLevel) {
        LOGGER.finer("User wird registriert");
        String[][] tempusers;
        String connectionString = connection.toString();
        int slot;

        // Array fuer neuen User vorbereiten und ID festlegen
        if (users == null) {
            tempusers = new String[1][5];
            slot = 0;
        }
        else {
            tempusers = new String[users.length + 1][5];
            slot = users.length;
            // altes Array in neues uebernehmen
            for (int i = 0; i < users.length; i++) {
                tempusers[i][0] = users[i][0];  //PlayerID (nicht UserID)
                tempusers[i][1] = users[i][1];  //UserName
                tempusers[i][2] = users[i][2];  //ConnectionString
                tempusers[i][3] = users[i][3];  //kiLevel
                tempusers[i][4] = users[i][4];  //UserColor
            }
        }

        //neuen User eintragen
        tempusers[slot][0] = Integer.toString(playerID);
        tempusers[slot][1] = name;
        tempusers[slot][2] = connectionString;
        tempusers[slot][3] = Integer.toString(kiLevel);
        tempusers[slot][4] = "0xffffffff";
        users = tempusers;

        if (kiLevel == 0) {
            joinRespone(playerID, connection);  //sendet (menschl.) Spieler seine erzeugte ID zu
        }
        playerID++;
        refreshLobbyResponse();                 //verteilt die neue Users[][]
    }

    /**
     * aendert den Namen eines Users und verteilt die aktualisierte Liste
     *
     * @param id des Users
     * @param name neuer Name des Users
     */
    public void changeUserName(int id, String name) {
        LOGGER.finer("Username wird geändert");
        String idstr = Integer.toString(id);
        for (String[] user : users) {
            if (user[0].equals(idstr)) {
                user[1] = name;
            }
        }
        refreshLobbyResponse();
    }

    /**
     * aendert die Farbe eines Users und verteilt die aktualisierte Liste
     *
     * @param id des Users
     * @param userColor neue Farbe des Users
     */
    private void changeUserColor(int id, String userColor) {
        LOGGER.finer("Usercolor wird geändert");
        String idstr = Integer.toString(id);
        for (String[] user : users) {
            if (user[0].equals(idstr)) {
                user[4] = userColor;
            }
        }
        refreshLobbyResponse();
    }

    /**
     * entfernt einen User aus der Liste und verteilt die aktualisierte Liste
     *
     * @param con
     * @param userID
     */
    public void deleteUser(Connection con, int userID) {
        int delID = userID;
        LOGGER.finer("User wird entfernt");
        String connectionString = con.toString();

        if (users == null) {
            return;
        }
        // lokalisieren, falls keineID, dann Connection
        if (userID == -1) {
            for (int i = 0; i < users.length; i++) {
                if (users[i][2].equals(connectionString)) {
                    delID = i;
                }
            }
            if (delID == -1) {
                LOGGER.warning("deleteUser fehlgeschlagen: nicht lokalisierbar");
            }
        }

        //loeschen
        String[][] tempusers = new String[users.length - 1][5];
        for (int j = 0; j < delID; j++) {
            tempusers[j][0] = users[j][0];
            tempusers[j][1] = users[j][1];
            tempusers[j][2] = users[j][2];
            tempusers[j][3] = users[j][3];
            tempusers[j][4] = users[j][4];
        }
        for (int k = delID + 1; k < users.length; k++) {
            tempusers[k - 1][0] = users[k][0];
            tempusers[k - 1][1] = users[k][1];
            tempusers[k - 1][2] = users[k][2];
            tempusers[k - 1][3] = users[k][3];
            tempusers[k - 1][4] = users[k][4];
        }

        //ueberschreiben
        users = tempusers;

        refreshLobbyResponse();
    }

    // RESPONSES:____________________________________________
    public void joinRespone(int id, Connection connection) {
        JoinResponse joinres = new JoinResponse();
        joinres.setId(id);
        joinres.setSeed(randomSeed);
        NetworkService.logServerSendMessage(joinres);
        connection.sendTCP(joinres);
    }

    public void refreshLobbyResponse() {
        RefreshLobbyResponse refres = new RefreshLobbyResponse();
        refres.setUsers(users);
        NetworkService.logServerSendMessage(refres);
        server.sendToAllTCP(refres);
    }

    public void gamestartResponse() {
        shuffle();
        refreshLobbyResponse();
        GamestartResponse gares = new GamestartResponse();
        NetworkService.logServerSendMessage(gares);
        server.sendToAllTCP(gares);
    }

    // LISTENER:_____________________________________________
    @Override
    public void received(Connection connection, Object object) {

        if (object instanceof JoinRequest) {
            NetworkService.logServerReceiveMessage(object);
            if (!gameStarted) {
                JoinRequest joinreq = (JoinRequest) object;
                registerUser(joinreq.getName(), connection, 0);
            }
            else {
                NetworkService.logServerSendMessage(new JoinImpossibleResponse());
                connection.sendTCP(new JoinImpossibleResponse());
            }
        }
        else if (object instanceof AddKiRequest) {
            NetworkService.logServerReceiveMessage(object);
            AddKiRequest akr = (AddKiRequest) object;
            registerUser(akr.getName(), connection, akr.getKiLevel());
        }
        else if (object instanceof ChangeUsernameRequest) {
            NetworkService.logServerReceiveMessage(object);
            ChangeUsernameRequest chanreq = (ChangeUsernameRequest) object;
            changeUserName(chanreq.getUserId(), chanreq.getUserName());
        }
        else if (object instanceof ChangeUsercolorRequest) {
            NetworkService.logServerReceiveMessage(object);
            ChangeUsercolorRequest chanreq = (ChangeUsercolorRequest) object;
            changeUserColor(chanreq.getUserId(), chanreq.getUserColor());
        }
        else if (object instanceof GamestartRequest) {
            NetworkService.logServerReceiveMessage(object);
            gameStarted = true;
            shuffle();
            refreshLobbyResponse();
            gamestartResponse();
        }
        else if (object instanceof BroadcastRandomSeedRequest) {
            NetworkService.logServerReceiveMessage(object);
            BroadcastRandomSeedRequest req = (BroadcastRandomSeedRequest) object;
            randomSeed = req.getSeed();
        }

    }

    @Override
    public void disconnected(Connection connection) {
        deleteUser(connection, -1);
    }

    public void shuffle() { //TODO ausprobieren und implementieren
        Collections.shuffle(Arrays.asList(users));
    }

}
