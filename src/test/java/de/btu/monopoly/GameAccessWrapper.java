package de.btu.monopoly;

import de.btu.monopoly.core.Game;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.net.client.GameClient;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
class GameAccessWrapper extends Game {
    
    /**
     * Ein einfacher Wrapper um bestimmte Methoden zu testen.
     *
     * @param client  GameClient
     * @param players Spieler
     * @param seed    Seed
     */
    GameAccessWrapper(GameClient client, Player[] players, long seed) {
        super(client, players, seed);
    }
    
    void fieldPhase(Player player, int[] rollResult) {
        this.player = player;
        this.rollResult = rollResult;
        super.fieldPhase();
    }
    
    void jailPhase(Player player) {
        this.player = player;
        super.jailPhase();
    }
    
    void onJailPayOption(Player player) {
        this.player = player;
        super.onJailPayOption();
    }
    
    void onJailRollOption(Player player) {
        this.player = player;
        super.onJailPayOption();
    }
    
    void onJailCardOption(Player player) {
        this.player = player;
        super.onJailCardOption();
    }
}
