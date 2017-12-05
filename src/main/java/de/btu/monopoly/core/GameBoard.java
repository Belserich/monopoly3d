package de.btu.monopoly.core;

import com.sun.istack.internal.NotNull;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.card.CardManager;
import de.btu.monopoly.data.field.Field;

import java.util.LinkedList;
import java.util.List;

import static de.btu.monopoly.core.GameBoard.FieldType.*;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class GameBoard
{
    /**
     * Aufzählung der verschiedenen Feldtypen
     */
    public enum FieldType {
        GO, CORNER, STREET, CARD, TAX, STATION, SUPPLY, GO_JAIL
    }
    
    /**
     * Die Feldstruktur ordnet den ids 0-39 die entsprechenden Feldtypen zu. Fängt beim LOS-Feld an und geht im Uhrzeigersinn
     * weiter. Für mehr Infos siehe *\src\resources\data\game_board_de.png
     */
    public static final FieldType[] FIELD_STRUCTURE = {
            GO, STREET, CARD, STREET, TAX, STATION, STREET, CARD, STREET, STREET,
            CORNER, STREET, SUPPLY, STREET, STREET, STATION, STREET, CARD, STREET, STREET,
            CORNER, STREET, CARD, STREET, STREET, STATION, STREET, STREET, SUPPLY, STREET,
            GO_JAIL, STREET, STREET, CARD, STREET, STATION, CARD, STREET, TAX, STREET
    };
    
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
     *
     * @param fields Felder des Spielbretts
     */
    public GameBoard(Field[] fields) {
        this.fieldManager = new FieldManager(fields);
        this.cardManager = new CardManager(this);
        activePlayers = new LinkedList<>();
    }
    
    /**
     * Fügt dem Spielbrett einen aktiven Spieler zu. Setzt {@code isBankrupt} auf {@code false} für diesen Spieler.
     *
     * @param player Spieler
     */
    public void addPlayer(@NotNull Player player) {
        activePlayers.add(player);
    }
    
    /**
     * @return die aktiven Spielerinstanzen
     */
    @NotNull
    public List<Player> getActivePlayers() {
        sortOutInactivePlayers();
        return activePlayers;
    }

    /**
     * Sortiert alle Spieler die bankrott gegangen sind aus.
     */
    private void sortOutInactivePlayers() {
        activePlayers.removeIf(Player::isBankrupt);
    }
    
    /**
     * @return Felder des Spielbretts
     */
    @NotNull
    public Field[] getFields() {
        return fieldManager.getFields();
    }
    
    @NotNull
    public CardManager getCardManager() {
        return cardManager;
    }
    
    @NotNull
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
