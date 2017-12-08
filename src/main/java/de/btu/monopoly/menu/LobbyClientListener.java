/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.menu;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import de.btu.monopoly.net.networkClasses.BroadcastUsersResponse;
import de.btu.monopoly.net.networkClasses.GamestartResponse;
import de.btu.monopoly.net.networkClasses.JoinResponse;

/**
 *
 * @author Christian Prinz
 */
public class LobbyClientListener extends Listener {

    public void received(Connection connection, Object object) {

        if (object instanceof FrameworkMessage) {
            // TODO LOG
        }
        else if (object instanceof JoinResponse) {
            Log.info("JoinResponse erhalten");
            JoinResponse res = (JoinResponse) object;
            LobbyService.joinResponse(res.getName());
        }
        else if (object instanceof BroadcastUsersResponse) {
            Log.info("BroadcastUsersResponse erhalten");
            BroadcastUsersResponse res = (BroadcastUsersResponse) object;
            LobbyService.setNewUsers(res.getUsers());
        }
        else if (object instanceof GamestartResponse) {
            Thread t = new Thread( LobbyService::gamestartResponse );
            t.start();
        }
        else {
            System.err.println("Falsches packet angekommen! " + object.getClass());
        }
    }
}
