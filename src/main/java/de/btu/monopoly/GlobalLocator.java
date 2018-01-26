package de.btu.monopoly;

import de.btu.monopoly.ui.GameSceneManager;
import de.btu.monopoly.ui.MenuSceneManager;

/**
 * Das ServiceLocator-Pattern als technische Komponente.
 *
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class GlobalLocator {
    
    /**
     * Singleton-Instance
     */
    private static GlobalLocator instance;
    
    private MenuSceneManager menuSceneMan;
    private GameSceneManager gameSceneMan;
    
    /**
     * Lazy-Konstruktion des Instanz-Objekts.
     *
     * @return globale Instanz
     */
    public static GlobalLocator instance() {
        if (instance == null) instance = new GlobalLocator();
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
}
