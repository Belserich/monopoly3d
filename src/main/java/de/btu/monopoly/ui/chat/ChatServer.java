package de.btu.monopoly.ui.chat;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import de.btu.monopoly.data.Player;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class ChatServer extends Listener {
    
    List<Player> connectedPlayers;
    
    private Server server;
    private Kryo kryo;
    
    private PrintStream out;
    
    public ChatServer(PrintStream out) {
        connectedPlayers = new LinkedList<>();
        this.out = out;
    }
    
    public void init() throws IOException {
        server = new Server();
        server.start();
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> server.stop()));
        server.bind(10222);
    
        server.addListener(this);
        kryo = server.getKryo();
        kryo.register(NetworkData.class);
        kryo.register(NetworkData.Type.class);
        kryo.register(Player.class);
    }
    
    @Override
    public void connected(Connection connection) {
        int id = connection.getID();
        
        Player player = new Player("Spieler-" + id, id, 200);
        connectedPlayers.add(player);
    
        NetworkData data = new NetworkData(NetworkData.Type.CONNECT, player);
    
        out.println(String.format("%s verbunden!", player.getName()));
        server.sendToTCP(id, data);
    }
    
    @Override
    public void disconnected(Connection connection) {
        Optional<Player> optional =  connectedPlayers.stream()
                .filter(p -> p.getId() == connection.getID())
                .findFirst();

        if (optional.isPresent()) {
            Player player = optional.get();
            connectedPlayers.remove(player);
            out.println(String.format("% hat die Verbindung getrennt!", player.getName()));
        }
        else {
            out.println(String.format("Fehler: Die Verbindung von Spieler-%s konnte nicht sauber getrennt werden!",
                    connection.getID()));
        }
    }
}
