package de.btu.monopoly.ui.chat;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.btu.monopoly.data.Player;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class ChatTest extends Application {
    
    public static void main(String[] args) {
        ChatTest.launch(args);
    }
    
    private TextArea chatArea;
    private TextField chatField;
    
    private Client client;
    
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Chat");
        
        ChatServer server = new ChatServer(System.out);
        server.init();
        
        initClient();
        
        ChatPanel chatPanel = new ChatPanel();
        chatPanel.setPrefSize(300, 300);
        
        TextField field = chatPanel.getTextField();
//        field.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.ENTER) {
//                field.
//            }
//        });
        
        Scene scene = new Scene(chatPanel);
        stage.setScene(scene);
        
        stage.show();
    }
    
    public void initClient() {
        Client client = new Client();
        client.start();
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> client.stop()));
        client.addListener(new ClientListener());
    
        Kryo kryo = client.getKryo();
        kryo.register(NetworkData.class);
        kryo.register(NetworkData.Type.class);
        kryo.register(Player.class);
        
        try {
            client.connect(5000, "localhost", 10222);
        }
        catch (IOException ex) {
            System.err.println("Konnte nicht mit dem Server verbinden!");
            ex.printStackTrace();
        }
    }
    
    class ClientListener extends Listener {
        
        @Override
        public void received(Connection connection, Object obj) {
            if (obj  instanceof NetworkData) {
                NetworkData data = (NetworkData) obj;
                Player player;
                switch (data.getType()) {
                    case CONNECT:
                        player = (Player) data.getObject();
                        chatArea.appendText(player.getName() + " ist dem Chat beigetreten.");
                        break;
                    case DISCONNECT:
                        player = (Player) data.getObject();
                        chatArea.appendText(player.getName() + " hat den Chat verlassen.");
                        break;
                    default:
                        chatArea.appendText("[Client] Es ist ein interner Fehler aufgetreten.");
                        System.err.println("Error: Unidentifyable packet type.");
                }
            }
        }
    }
}
