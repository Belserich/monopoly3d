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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class GUIChat extends Chat {

    private static final Logger LOGGER = Logger.getLogger(GUIChat.class.getCanonicalName());

    /**
     * der Chatverlauf
     */
    private final List<ChatMessage> messageList = new ArrayList<>();

    // Vorder- und Hintergrundfarben der Eventnachrichten
    private final String EVENT_MESSAGE_F_COLOR;     //see @ constructor
    private final GameClient client;                //see @ constructor
    private final ChatNotifier notifier;

    // Styles
    String eventMsg = "-fx-background-color: lightgray;\n"
            + "-fx-fill: black;\n"
            + "-fx-font-weight: 800;\n"
            + "-fx-font-family: 'Roboto';\n"
            + "-fx-font-size: 15";

    String playerMsg = "-fx-background-color: lightgray;\n"
            + "-fx-font-style: oblique;\n"
            + "-fx-fill: black;\n"
            + "-fx-font-weight: 400;\n"
            + "-fx-font-family: 'Roboto';\n"
            + "-fx-font-size: 14";

    /**
     * der Singleton-Konstruktor. Wird ausschließlich intern und nur ein mal aufgerufen
     */
    private GUIChat() {
        // <editor-fold defaultstate="collapsed" desc="Singleton">
        this.EVENT_MESSAGE_F_COLOR = "#FFFFFF";
        Global global = Global.ref();
        client = global.getClient();
        client.addExternalListener(new ChatListener());
        notifier = new ChatNotifier();
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

    public void msgLocal(String botName, String message) {
        // <editor-fold defaultstate="collapsed" desc="sends local Message form Bot to Chat">
        ChatMessage mess = new ChatMessage();
        mess.setAutor(botName);
        mess.setMessage(message);
        mess.setfColor("#000000");
        mess.setIsEvent(false);
        addMessage(mess);
        //</editor-fold>
    }

    @Override
    public void event(String eventMessage) {
        // <editor-fold defaultstate="collapsed" desc="creates and sends eventMessage to Server">
        if (!Global.RUN_AS_TEST) {
            LOGGER.info("C: " + eventMessage);
            ChatMessage mess = new ChatMessage();
            mess.setMessage(eventMessage);
            mess.setfColor(EVENT_MESSAGE_F_COLOR);
            mess.setAutor(">>> ");
            mess.setIsEvent(true);

            addMessage(mess);
        }
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
        notifyChat(mess);
    }

    private void notifyChat(ChatMessage mess) {
        // <editor-fold defaultstate="collapsed" desc="notifies Observers with new Text[]">
        Text[] message = new Text[2];
        message[1] = new Text(mess.getMessage() + "\n");
        if (mess.isEvent()) {
            message[0] = new Text(mess.getAutor());
            message[1].setStyle(eventMsg);
            message[0].setFont(Font.font(eventMsg));

        }
        else {
            message[0] = new Text(mess.getAutor() + ": ");
            message[0].setFont(Font.font("Roboto", FontWeight.BOLD, 16));
            message[0].setFill(Color.web(mess.getfColor()));
            message[1].setStyle(playerMsg);
        }
        notifier.notify(message);
        //</editor-fold>
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
        mess.setfColor(player.getColor());
        mess.setIsEvent(false);

        return mess;
        //</editor-fold>
    }

    public void addObserver(Observer obs) {
        notifier.addObserver(obs);
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

    public class ChatNotifier extends Observable {
        // <editor-fold defaultstate="collapsed" desc="Notifier --ChatMessage--> Observers">

        private void notify(Text[] message) {
            setChanged();
            notifyObservers(message);
        }
        //</editor-fold>
    }
}
