import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.parser.GameBoardParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class GameBoardParserTest {
    
    private static final String BOARD_DATA_PATH = "data/field_data.config";
    
    public static void main(String[] args) throws IOException , SAXException, ParserConfigurationException {
        GameBoard board = GameBoardParser.parse(BOARD_DATA_PATH);
        for (Field f : board.getFields())
        {
            if (f instanceof PropertyField) {
                ((PropertyField) f).getNeighbours().forEach(p -> System.out.println(p.getClass()));
            }
        }
    }
}
