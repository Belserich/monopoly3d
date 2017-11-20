package de.btu.monopoly.data;

import de.btu.monopoly.data.field.Field;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class GameBoard
{
    /**
     * Felder des Spielbretts
     */
    private Field[] fields;
    
    /**
     * Erstellt eine neue Spielbrett-Instanz.
     *
     * @param fields Felder des Spielbretts
     */
    public GameBoard(Field[] fields) {
        this.fields = fields;
    }
    
    /**
     * @return Felder des Spielbretts
     */
    public Field[] getFields() {
        return fields;
    }
}
