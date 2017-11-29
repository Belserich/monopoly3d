package de.btu.monopoly.data;

import de.btu.monopoly.data.field.Field;

import java.util.Arrays;
import java.util.stream.Stream;

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
    
    @Override
    public String toString() {
        String retObj = new String();
        for (Field field : fields) {
            retObj += field.toString() + "\n";
        }
        return retObj;
    }
}
