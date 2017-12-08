/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.networkClasses;

/**
 *
 * @author Christian Prinz
 */
public class BroadcastUsersRequest {

    private String[] users;

    /**
     * @return the users
     */
    public String[] getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(String[] users) {
        this.users = users;
    }

}
