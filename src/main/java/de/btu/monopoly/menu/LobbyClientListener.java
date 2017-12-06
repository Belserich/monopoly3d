/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import de.btu.monopoly.net.networkClasses.GamestartResponse;
import de.btu.monopoly.net.networkClasses.JoinResponse;

/**
 *
 * @author Christian Prinz
 */
public class LobbyClientListener extends Listener {

    public void received(Connection connection, Object object) {

        if (object instanceof JoinResponse) {
            Log.info("JoinResponse erhalten");
            JoinResponse joinRes = (JoinResponse) object;
            LobbyService.joinResponse(joinRes.getName(), joinRes.getId());
        }

        if (object instanceof GamestartResponse) {
            LobbyService.gamestartResponse();
        }
    }
}
