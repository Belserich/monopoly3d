package de.btu.monopoly.core;

import de.btu.monopoly.data.player.Player;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public interface GameStateListener {
    
    void onDiceThrow(int[] result);
    
    void onTurnEnd(Player oldPlayer, Player newPlayer);
}
