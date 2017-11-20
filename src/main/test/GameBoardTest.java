import de.btu.monopoly.controller.GameController;
import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.parser.GameBoardParser;

import java.io.IOException;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class GameBoardTest {
    
    private static final String BOARD_DATA_PATH = "field_data.config";
    
    public static void main(String[] args) throws IOException {
        GameBoardParser parser = new GameBoardParser();
        GameBoard board = parser.readBoard(BOARD_DATA_PATH);
        for (Field f : board.getFields())
        {
            System.out.println(f.getClass());
        }
    }
    
}
