package de.btu.monopoly;

import de.btu.monopoly.core.Game;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.ui.GameSceneManager;
import de.btu.monopoly.ui.MenuSceneManager;

/**
 * Das ServiceLocator-Pattern als technische Komponente.
 *
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Global {
    
    /**
     * Singleton-Instance
     */
    private static Global instance;
    
    private MenuSceneManager menuSceneMan;
    private GameSceneManager gameSceneMan;
    
    private GameClient client;
    private Game game;
    
    /**
     * Lazy-Konstruktion des Instanz-Objekts.
     *
     * @return globale Instanz
     */
    public static Global ref() {
        if (instance == null) instance = new Global();
        return instance;
    }
    
    public void setMenuSceneManager(MenuSceneManager sceneManager) {
        this.menuSceneMan = sceneManager;
    }
    
    public MenuSceneManager getMenuSceneManager() {
        return menuSceneMan;
    }
    
    public void setGameSceneManager(GameSceneManager sceneManager) {
        this.gameSceneMan = gameSceneMan;
    }
    
    public GameSceneManager getGameSceneManager() {
        return gameSceneMan;
    }
    
    public GameClient getClient() {
        return client;
    }
    
    public void setClient(GameClient client) {
        this.client = client;
    }
    
    public Game getGame() {
        return game;
    }
    
    public void setGame(Game game) {
        this.game = game;
    }
    
    // convenience-Methoden
    
    public Player playerOnClient() {
        return client.getPlayerOnClient();
    }
    
    public Player currentPlayer() {
        return game.getCurrentPlayer();
    }
}
