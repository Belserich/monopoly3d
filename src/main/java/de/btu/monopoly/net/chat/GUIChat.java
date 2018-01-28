/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.net.chat;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.btu.monopoly.Global;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.net.data.ChatMessage;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Christian Prinz
 */
public class GUIChat extends Chat {

    /**
     * der Chatverlauf
     */
    private final List<ChatMessage> messageList = new ArrayList<>();

    // Vorder- und Hintergrundfarben der Eventnachrichten
    private final String EVENT_MESSAGE_F_COLOR;     //see @ constructor
    private final String EVENT_MESSAGE_B_COLOR;     //see @ constructor
    private final GameClient client;                //see @ constructor

    /**
     * der Singleton-Konstruktor. Wird ausschließlich intern und nur ein mal aufgerufen
     */
    private GUIChat() {
        // <editor-fold defaultstate="collapsed" desc="Singleton">
        this.EVENT_MESSAGE_B_COLOR = "#000000";
        this.EVENT_MESSAGE_F_COLOR = "#FFFFFF";
        Global global = Global.ref();
        client = global.getClient();
        client.addExternalListener(new ChatListener());
        // </editor-fold>
    }

    /**
     * Damit wird die Instanz des Chats geholt. Existiert er noch nicht, wird er neu angelegt
     *
     * @return GUIChat Instance
     */
    public static GUIChat getInstance() {
        return ChatHolder.INSTANCE;
    }

    @Override
    public void msg(Player player, String message) {
        // <editor-fold defaultstate="collapsed" desc="sends Message to Server">
        ChatMessage mess = createMessage(player, message);
        client.sendTCP(mess);
        //</editor-fold>
    }

    @Override
    public void msgLocal(Player bot, String message) {
        // <editor-fold defaultstate="collapsed" desc="sends local Message to Chat">
        ChatMessage mess = createMessage(bot, message);
        addMessage(mess);
        //</editor-fold>
    }

    @Override
    public void event(String eventMessage) {
        // <editor-fold defaultstate="collapsed" desc="creates and sends eventMessage to Server">
        ChatMessage mess = new ChatMessage();
        mess.setMessage(eventMessage);
        mess.setbColor(EVENT_MESSAGE_B_COLOR);
        mess.setfColor(EVENT_MESSAGE_F_COLOR);
        mess.setAutor("");

        client.sendTCP(mess);
        //</editor-fold>
    }

    @Override
    public void clearChat() {
        messageList.clear();
    }

    /**
     * fuegt dem Chatverlauf eine neue ChatMessage hinzu
     *
     * @param mess hinzuzufuegende ChatMessage
     */
    private void addMessage(ChatMessage mess) {
        messageList.add(mess);
    }

    /**
     * erstellt eine neue ChatMessage
     *
     * @param player Autor der Nachricht
     * @param message Nachricht
     * @return gibt die erstellte Nachricht zurueck
     */
    private ChatMessage createMessage(Player player, String message) {
        // <editor-fold defaultstate="collapsed" desc="creates ChatMessageObject">
        ChatMessage mess = new ChatMessage();
        mess.setAutor(player.getName());
        mess.setMessage(message);
        mess.setbColor("#FFFFFF");
        mess.setfColor(player.getColor());

        return mess;
        //</editor-fold>
    }

    /**
     * statische Klasse, welche die Instanz des Chats beinhaltet und bereitstellt
     */
    private static class ChatHolder {
        // <editor-fold defaultstate="collapsed" desc="INSTANCE of this Chat">

        private static final GUIChat INSTANCE = new GUIChat();
        // </editor-fold>
    }

    /**
     * der ClientListener fuer den Chat, welcher ChatMessages vom Server erhaelt und daraufhin addMessage() aufruft
     */
    private class ChatListener extends Listener {
        // <editor-fold defaultstate="collapsed" desc="receives ChatMessages from server">

        @Override
        public void received(Connection connection, Object object) {
            if (object instanceof ChatMessage) {
                addMessage((ChatMessage) object);
            }
        }
        //</editor-fold>
    }
}
