/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.btu.monopoly.net.networkClasses.GamestartResponse;
import de.btu.monopoly.net.networkClasses.JoinResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class LobbyClientListener extends Listener {

    public static final Logger LOGGER = Logger.getLogger(LobbyClientListener.class.getCanonicalName());

    public void recieved(Connection connection, Object object) {

        if (object instanceof JoinResponse) {
            LOGGER.setLevel(Level.ALL);
            JoinResponse joinRes = (JoinResponse) object;
            LobbyService.joinResponse(joinRes.getPlayers());
        }

        if (object instanceof GamestartResponse) {
            LobbyService.gamestartResponse();
        }
    }
}
