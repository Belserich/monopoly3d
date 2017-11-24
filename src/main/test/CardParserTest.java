import de.btu.monopoly.data.CardStack;
import de.btu.monopoly.data.parser.CardParser;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedList;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class CardParserTest
{
    public static void main(String[] args) throws Exception{
        CardParser.parseCardStack("data/chance_card_data.xml");
    }
}
