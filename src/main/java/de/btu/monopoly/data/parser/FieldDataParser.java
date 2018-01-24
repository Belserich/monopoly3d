package de.btu.monopoly.data.parser;

import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class FieldDataParser {

    /**
     * Die allgemeine Exception-Nachricht für diese Klasse
     */
    private static final String IO_EXCEPTION_MESSAGE = "Exception while reading game board data. Corrupted file data!";

    private static final Logger LOGGER = Logger.getLogger(FieldDataParser.class.getCanonicalName());

    private static CardStack CARD_LOADOUT_0 = null;
    private static CardStack CARD_LOADOUT_1 = null;

    public static Field[] parse(String path) throws ParserConfigurationException, IOException, SAXException {
        
        Field[] fields = new Field[GameBoard.FIELD_STRUCTURE.length];
        DocumentBuilder builder;
        Element elem;
        int id;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();

        Document doc = builder.parse(CardDataParser.class.getResourceAsStream(path.replaceAll("%20", " ")));
        LOGGER.info("Dokument erfolgreich ausgelesen!");

        NodeList fieldList = doc.getElementsByTagName("field");

        if (fieldList.getLength() != GameBoard.FIELD_STRUCTURE.length) {
            LOGGER.warning("Anzahl Felder in " + path + " ungleich dem erwarteten Wert.");
            throw new IOException(IO_EXCEPTION_MESSAGE);
        }

        for (int i = 0; i < fieldList.getLength(); i++) {
            elem = (Element) fieldList.item(i);
            id = parseId(elem);

            Field field;
            switch (GameBoard.FIELD_STRUCTURE[id]) {
                case GO:
                    field = parseGoField(elem);
                    break;
                case GO_JAIL:
                // siehe case CORNER
                case CORNER:
                    field = parseCornerField(elem);
                    break;
                case STREET:
                    field = parseStreetField(elem);
                    break;
                case STATION:
                    field = parseStationField(elem);
                    break;
                case SUPPLY:
                    field = parseSupplyField(elem);
                    break;
                case TAX:
                    field = parseTaxField(elem);
                    break;
                case CARD:
                    field = parseCardField(elem);
                    break;
                default:
                    LOGGER.warning("Der Feldtyp " + elem.getParentNode().getNodeName() + " existiert nicht!");
                    throw new IOException(IO_EXCEPTION_MESSAGE);
            }
            if (field == null) {
                LOGGER.warning("Das Feld " + i + " vom Typ " + elem.getParentNode().getNodeName() + " konnte nicht erstellt werden.");
                throw new IOException(IO_EXCEPTION_MESSAGE);
            }
            fields[id] = field;
        }
        LOGGER.info("Alle Feldinstanzen erfolgreich erstellt.");

        return fields;
    }

    private static int parseId(Element elem) {
        return Integer.parseInt(elem.getAttribute("id"));
    }

    private static GoField parseGoField(Element elem) {
        String name = elem.getAttribute("name");
        int amount = Integer.parseInt(elem.getAttribute("amount"));

        return new GoField(name, amount);
    }

    private static Field parseCornerField(Element elem) {
        return new Field(elem.getAttribute("name"));
    }

    private static StreetField parseStreetField(Element elem) {
        StreetField retObj = null;

        String name = elem.getAttribute("name");
        try {
            int price = Integer.parseInt(elem.getAttribute("price"));
            int rent0 = Integer.parseInt(elem.getAttribute("rent0"));
            int rent1 = Integer.parseInt(elem.getAttribute("rent1"));
            int rent2 = Integer.parseInt(elem.getAttribute("rent2"));
            int rent3 = Integer.parseInt(elem.getAttribute("rent3"));
            int rent4 = Integer.parseInt(elem.getAttribute("rent4"));
            int rent5 = Integer.parseInt(elem.getAttribute("rent5"));
            int housePrice = Integer.parseInt(elem.getAttribute("house_price"));
            int mortgage = Integer.parseInt(elem.getAttribute("mortgage"));
            int mortgageBack = Integer.parseInt(elem.getAttribute("mortgage_back"));

            retObj = new StreetField(
                    name,
                    price,
                    rent0,
                    rent1,
                    rent2,
                    rent3,
                    rent4,
                    rent5,
                    housePrice,
                    mortgage,
                    mortgageBack
            );
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "", ex);
        }
        return retObj;
    }

    private static StationField parseStationField(Element elem) {
        StationField retObj = null;

        String name = elem.getAttribute("name");
        try {
            int price = Integer.parseInt(elem.getAttribute("price"));
            int rent0 = Integer.parseInt(elem.getAttribute("rent0"));
            int rent1 = Integer.parseInt(elem.getAttribute("rent1"));
            int rent2 = Integer.parseInt(elem.getAttribute("rent2"));
            int rent3 = Integer.parseInt(elem.getAttribute("rent3"));
            int mortgage = Integer.parseInt(elem.getAttribute("mortgage"));
            int mortgageBack = Integer.parseInt(elem.getAttribute("mortgage_back"));

            retObj = new StationField(
                    name,
                    price,
                    rent0,
                    rent1,
                    rent2,
                    rent3,
                    mortgage,
                    mortgageBack
            );
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "", ex); // TODO Exception im Logging-Formatter integrieren.
        }
        return retObj;
    }

    private static SupplyField parseSupplyField(Element elem) {
        SupplyField retObj = null;

        String name = elem.getAttribute("name");
        try {
            int price = Integer.parseInt(elem.getAttribute("price"));
            int mult1 = Integer.parseInt(elem.getAttribute("mult1"));
            int mult2 = Integer.parseInt(elem.getAttribute("mult2"));
            int mortgage = Integer.parseInt(elem.getAttribute("mortgage"));
            int mortgageBack = Integer.parseInt(elem.getAttribute("mortgage_back"));

            retObj = new SupplyField(
                    name,
                    price,
                    mult1,
                    mult2,
                    mortgage,
                    mortgageBack);
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "", ex);
        }
        return retObj;
    }

    private static TaxField parseTaxField(Element elem) {
        TaxField retObj = null;

        String name = elem.getAttribute("name");
        try {
            int tax = Integer.parseInt(elem.getAttribute("tax"));
            retObj = new TaxField(name, tax);
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "", ex);
        }
        return retObj;
    }

    private static CardField parseCardField(Element elem) {
        CardField retObj = null;

        String name = elem.getAttribute("name");
        try {
            int type = Integer.parseInt(elem.getAttribute("type"));
            retObj = new CardField(name, type == 0 ? CARD_LOADOUT_0 : CARD_LOADOUT_1);
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "", ex);
        }
        return retObj;
    }

    public static void setCardLoadout0(CardStack stack) {
        CARD_LOADOUT_0 = stack;
    }

    public static void setCardLoadout1(CardStack stack) {
        CARD_LOADOUT_1 = stack;
    }
}
