package de.btu.monopoly.core;

import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public interface GameStateListener {
    
    void onGameInit();
    
    void onGameStart(Player[] players);
    
    void onTurnStart(Player player);
    
    void onPlayerStartsTurnInJail(Player player);
    
    void onDiceThrow(int[] result, int doubletCount);
    
    void onTurnEnd(Player oldPlayer, Player newPlayer);
    
    void onGameEnd(Player winner);
    
    void onRollPhaseStart(Player player);
    
    void onFieldPhaseStart(Player player);
    
    void onJailPhaseStart(Player player);
    
    void onForceJailPayOption(Player player);
    
    void onJailRollSuccess(Player player);
    
    void onJailRollFailure(Player player);
    
    void onJailPayFailure(Player player);
    
    void onJailPaySuccess(Player player);
    
    void onJailCardSuccess(Player player);
    
    void onJailCardFailure(Player player);
    
    void onPlayerOnNewField(Player player, FieldTypes type);
    
    void onActionPhaseStart(Player player);
    
    void onPlayerJailOption(Player player, int choice);
    
    void onPlayerActionOption(Player player, int choice);
    
    void onAuctionStart(PropertyField prop);
    
    void onAuctionEnd(Player winner, PropertyField prop);
    
    void onTradeStart(Player player);
    
    void onTradeEnd(Player player);
}
