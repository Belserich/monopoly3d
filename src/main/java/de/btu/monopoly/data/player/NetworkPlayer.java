package de.btu.monopoly.data.player;

import com.esotericsoftware.kryonet.Connection;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class NetworkPlayer extends Player {
    
    private Connection connection;
    
    /**
     * Erstellt eine neue Spielerinstanz.
     *
     * @param name       Spielername
     * @param id         Spieler-ID
     * @param startMoney Kapital des Spielers
     */
    public NetworkPlayer(String name, int id, int startMoney) {
        super(name, id, startMoney);
    }
    
    public Connection getConnection() {
        return connection;
    }
}
