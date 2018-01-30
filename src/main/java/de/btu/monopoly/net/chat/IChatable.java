/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.chat;

import de.btu.monopoly.data.player.Player;

/**
 *
 * @author Christian Prinz
 */
public interface IChatable {

    /**
     * Bekommt eine Nachricht von einem Spieler uebergeben und verschickt diese ueber das Netzwerk an die anderen Chat-Instanzen
     *
     * @param player Autor der Nachricht
     * @param message Nachricht
     */
    public void msg(Player player, String message);

    /**
     * Bekommt eine Nachricht von einem Computerspieler, oder Bot (z.B. Auktionsleiter) uebergeben und schickt diese lokal an den
     * Chat
     *
     * @param bot Autor der Nachricht
     * @param message Nachricht
     */
    public void msgLocal(Player bot, String message);

    /**
     * Bekommt eine Event-Mitteilung übergeben und "loggt" diese im Chatfenster, ähnlich dem ehemaligen Protokoll
     *
     * @param eventMessage
     */
    public void event(String eventMessage);

    /**
     * loescht den Chatverlauf
     */
    public void clearChat();
}
