package de.btu.monopoly.core;

import de.btu.monopoly.data.card.CardManager;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.field.FieldManager;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.util.Assets;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class GameBoard {

    /**
     * Feldmanager
     */
    private final FieldManager fieldManager;

    /**
     * Kartenmanager
     */
    private final CardManager cardManager;

    /**
     * Spieler am Spielbrett
     */
    private final List<Player> activePlayers;

    /**
     * Erstellt eine neue Spielbrett-Instanz.
     */
    public GameBoard() {
        this.fieldManager = new FieldManager(Assets.getFields());
        this.cardManager = new CardManager(this);
        activePlayers = new LinkedList<>();
    }

    /**
     * Fügt dem Spielbrett einen aktiven Spieler zu. Setzt {@code isBankrupt} auf {@code false} für diesen Spieler.
     *
     * @param player Spieler
     */
    public void addPlayer(Player player) {
        activePlayers.add(player);
    }

    /**
     * @return die aktiven Spielerinstanzen
     */
    public List<Player> getActivePlayers() {
        return activePlayers;
    }
    
    public Player getPlayer(int playerId) {
        for (Player player : activePlayers) {
            if (player.getId() == playerId) {
                return player;
            }
        }
        return null;
    }
    
    /**
     * Sortiert alle Spieler die bankrott gegangen sind aus.
     *
     * @return GameBoard
     */
    public GameBoard updateActivePlayers() {
        activePlayers.removeIf(Player::isBankrupt);
        return this;
    }

    /**
     * @return Felder des Spielbretts
     */
    public Field[] getFields() {
        return fieldManager.getFields();
    }

    public CardManager getCardManager() {
        return cardManager;
    }

    public FieldManager getFieldManager() {
        return fieldManager;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Field field : fieldManager.getFields()) {
            builder.append(field.toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}
