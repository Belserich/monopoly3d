package de.btu.monopoly;

import de.btu.monopoly.core.Game;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.client.GameClient;
import de.btu.monopoly.ui.GameSceneManager;
import de.btu.monopoly.ui.GuiTrade;
import de.btu.monopoly.ui.MenuSceneManager;
import java.util.Random;

/**
 * Das ServiceLocator-Pattern als technische Komponente.
 *
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class Global {

    public static boolean RUN_IN_CONSOLE = false;
    public static boolean RUN_AS_TEST = false;
    public static boolean FX_3D_TEST = false;           // kurze Version (ohne Menu)
    public static long randomSeed = 42;                // setzt den RandomSeed für kurze und originale Version; 0 = zufall

    /**
     * Singleton-Instance
     */
    private static Global instance;

    private MenuSceneManager menuSceneMan;
    private GameSceneManager gameSceneMan;

    private GameClient client;
    private Game game;

    private GuiTrade guiTrade;

    /**
     * Lazy-Konstruktion des Instanz-Objekts.
     *
     * @return globale Instanz
     */
    public static Global ref() {
        if (instance == null) {
            instance = new Global();
        }
        return instance;
    }

    public void setMenuSceneManager(MenuSceneManager sceneManager) {
        this.menuSceneMan = sceneManager;
    }

    public MenuSceneManager getMenuSceneManager() {
        return menuSceneMan;
    }

    public void setGameSceneManager(GameSceneManager sceneManager) {
        this.gameSceneMan = sceneManager;
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

    public GuiTrade getGuiTrade() {
        return guiTrade;
    }

    public void setGuiTrade(GuiTrade guiTrade) {
        this.guiTrade = guiTrade;
    }

    public long getRandomSeed() {
        return (randomSeed == 0) ? (new Random().nextLong()) : randomSeed;
    }
}
