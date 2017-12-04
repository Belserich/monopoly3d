package de.btu.monopoly.data;

import de.btu.monopoly.data.field.Field;

import java.util.Arrays;

import static de.btu.monopoly.data.GameBoard.FieldType.*;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class GameBoard
{
    /**
     * Aufzählung der verschiedenen Feldtypen
     */
    public enum FieldType {
        GO, CORNER, STREET, CARD, TAX, STATION, SUPPLY, GO_JAIL;
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
     * Felder des Spielbretts
     */
    private final Field[] fields;
    
    /**
     * Spieler am Spielbrett
     */
    private Player[] activePlayers;
    
    /**
     * Erstellt eine neue Spielbrett-Instanz.
     *
     * @param fields Felder des Spielbretts
     */
    public GameBoard(Field[] fields) {
        this.fields = fields;
    }
    
    /**
     * Legt die, am Spiel beteiligten aktiven Spielerinstanzen fest.
     *
     * @param activePlayers aktive Spielerinstanzen
     */
    public void setActivePlayers(Player[] activePlayers) {
        this.activePlayers = activePlayers;
    }
    
    /**
     * @return die aktiven Spielerinstanzen
     */
    public Player[] getActivePlayer() {
        return activePlayers;
    }
    
    /**
     * Sortiert alle Spieler die bankrott gegangen sind aus.
     */
    public void sortOutInactivePlayers() {
        activePlayers = Arrays.stream(activePlayers)
                .filter(p -> !p.isBankrupt())
                .toArray(Player[]::new);
    }
    
    /**
     * @return Felder des Spielbretts
     */
    public Field[] getFields() {
        return fields;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Field field : fields) {
            builder.append(field.toString());
            builder.append("\n");
        }
        return builder.toString();
    }
}
