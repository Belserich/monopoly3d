package de.btu.monopoly.data.parser;

import de.btu.monopoly.core.FieldTypes;
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

    private static final Logger LOGGER = Logger.getLogger(FieldDataParser.class.getCanonicalName());

    public static Field[] parse(String path) throws ParserConfigurationException, IOException, SAXException {
        
        Field[] fields = new Field[FieldTypes.GAMEBOARD_FIELD_STRUCT.length];
        DocumentBuilder builder;
        Element elem;
        int id;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();

        Document doc = builder.parse(CardDataParser.class.getResourceAsStream(path.replaceAll("%20", " ")));
        LOGGER.info("Dokument erfolgreich ausgelesen!");

        NodeList fieldList = doc.getElementsByTagName("field");

        if (fieldList.getLength() != FieldTypes.GAMEBOARD_FIELD_STRUCT.length) {
            LOGGER.warning("Anzahl Felder in " + path + " ungleich dem erwarteten Wert.");
            throw new IOException("Exception while reading game board data. Corrupted file data!");
        }

        for (int i = 0; i < fieldList.getLength(); i++) {
            elem = (Element) fieldList.item(i);
            id = parseId(elem);

            FieldTypes type = FieldTypes.GAMEBOARD_FIELD_STRUCT[id];
            Field field;
            
            if (type.isCorner()) {
                if (type == FieldTypes.CORNER_0)
                    field = parseGoField(elem);
                else field = parseCornerField(elem);
            }
            else if (type.isStreet()) {
                field = parseStreetField(elem);
            }
            else if (type.isStation()) {
                field = parseStationField(elem);
            }
            else if (type.isSupply()) {
                field = parseSupplyField(elem);
            }
            else if (type.isTax()) {
                field = parseTaxField(elem);
            }
            else if (type.isCard()) {
                field = parseCardField(elem);
            }
            else throw new IOException(String.format("Undefined field type: %s", type));
            
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

    private static StationField parseStationField(Element elem) throws IOException {
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
            throw new IOException("Exception while parsing station field.");
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
            retObj = new CardField(name, type == 0 ? CardStack.Type.COMMUNITY : CardStack.Type.EVENT);
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.WARNING, "", ex);
        }
        return retObj;
    }
}
