package de.btu.monopoly.core;

import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class GameStateAdapter implements GameStateListener {
    
    @Override
    public void onGameInit() {
        // nothing
    }
    
    @Override
    public void onGameStart(Player[] players) {
        // nothing
    }
    
    @Override
    public void onTurnStart(Player player) {
        // nothing
    }
    
    @Override
    public void onPlayerStartsTurnInJail(Player player) {
        // nothing
    }
    
    @Override
    public void onDiceThrow(int[] result, int doubletCount) {
        // nothing
    }
    
    @Override
    public void onTurnEnd(Player oldPlayer, Player newPlayer) {
        // nothing
    }
    
    @Override
    public void onGameEnd(Player winner) {
        // nothing
    }
    
    @Override
    public void onRollPhaseStart(Player player) {
        // nothing
    }
    
    @Override
    public void onFieldPhaseStart(Player player) {
        // nothing
    }
    
    @Override
    public void onJailPhaseStart(Player player) {
        // nothing
    }
    
    @Override
    public void onForceJailPayOption(Player player) {
        // nothing
    }
    
    @Override
    public void onJailRollSuccess(Player player) {
        // nothing
    }
    
    @Override
    public void onJailRollFailure(Player player) {
        // nothing
    }
    
    @Override
    public void onJailPayFailure(Player player) {
        // nothing
    }
    
    @Override
    public void onJailPaySuccess(Player player) {
        // nothing
    }
    
    @Override
    public void onJailCardSuccess(Player player) {
        // nothing
    }
    
    @Override
    public void onJailCardFailure(Player player) {
        // nothing
    }
    
    @Override
    public void onPlayerOnNewField(Player player, FieldTypes type) {
        // nothing
    }
    
    @Override
    public void onActionPhaseStart(Player player) {
        // nothing
    }
    
    @Override
    public void onPlayerJailOption(Player player, int choice) {
        // nothing
    }
    
    @Override
    public void onPlayerActionOption(Player player, int choice) {
        // nothing
    }
    
    @Override
    public void onAuctionStart(PropertyField prop) {
    
    }
    
    @Override
    public void onAuctionEnd(Player winner, PropertyField prop) {
    
    }
    
    @Override
    public void onTradeStart(Player player) {
    
    }
    
    @Override
    public void onTradeEnd(Player player) {
    
    }
}
