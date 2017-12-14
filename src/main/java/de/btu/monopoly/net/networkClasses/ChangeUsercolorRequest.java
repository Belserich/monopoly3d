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
public class ChangeUsercolorRequest {

    private String userColor;
    private int userId;

    /**
     * @return the userColor
     */
    public String getUserColor() {
        return userColor;
    }

    /**
     * @param userColor the userColor to set
     */
    public void setUserColor(String userColor) {
        this.userColor = userColor;
    }

    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

}
