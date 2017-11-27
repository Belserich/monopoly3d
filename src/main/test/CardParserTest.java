import de.btu.monopoly.data.parser.CardStackParser;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class CardParserTest
{
    public static void main(String[] args) throws Exception{
        CardStackParser.parse("data/card_data.xml");
    }
}
