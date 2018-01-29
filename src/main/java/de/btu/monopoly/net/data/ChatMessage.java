/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.data;

/**
 *
 * @author Christian Prinz
 */
public class ChatMessage {

    private String autor;
    private String message;
    private String fColor;
    private boolean isEvent;

    /**
     * @return the autor
     */
    public String getAutor() {
        return autor;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the fColor
     */
    public String getfColor() {
        return fColor;
    }

    /**
     * @param autor the autor to set
     */
    public void setAutor(String autor) {
        this.autor = autor;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param fColor the fColor to set
     */
    public void setfColor(String fColor) {
        this.fColor = fColor;
    }

    /**
     * @return the isEvent
     */
    public boolean isEvent() {
        return isEvent;
    }

    /**
     * @param isEvent the isEvent to set
     */
    public void setIsEvent(boolean isEvent) {
        this.isEvent = isEvent;
    }
}
