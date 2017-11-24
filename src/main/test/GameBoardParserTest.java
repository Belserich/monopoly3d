import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.field.Property;
import de.btu.monopoly.data.parser.GameBoardParser;

import java.io.IOException;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class GameBoardParserTest {
    
    private static final String BOARD_DATA_PATH = "data/field_data.config";
    
    public static void main(String[] args) throws IOException {
        GameBoardParser parser = new GameBoardParser();
        GameBoard board = parser.parseGameBoard(BOARD_DATA_PATH);
        for (Field f : board.getFields())
        {
            if (f instanceof Property) {
                ((Property) f).getNeighbours().forEach(p -> System.out.println(p.getClass()));
            }
        }
    }
    
}
