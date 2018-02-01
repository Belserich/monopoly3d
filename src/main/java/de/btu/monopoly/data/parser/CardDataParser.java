package de.btu.monopoly.data.parser;

import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.card.JailCard;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class CardDataParser {

    /**
     * Liesst Kartendaten aus einer XML-Datei und wandelt diese in virtuelle Karten um, mit denen sie dann eine Instanz von
     * CardStack erstellt.
     *
     * @param path Pfad zur XML-Datei
     * @return Kartenstapel
     * @throws ParserConfigurationException wenn beim Lesen ein bestimmtes, wichtiges Element nicht gefunden wird
     * @throws IOException Die Datei konnte nicht gefunden werden.
     * @throws SAXException wenn das Dokument nicht gelesen werden konnte, also eine beschädigte Grobstruktur vorliegt
     */
    public static CardStack parse(String path) throws ParserConfigurationException, IOException, SAXException {
        CardStack stack = new CardStack();
        List<Card> cards;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document doc = builder.parse(CardDataParser.class.getResourceAsStream(path.replaceAll("%20", " ")));
        
        NodeList nList = doc.getElementsByTagName("card");
        cards = new LinkedList<>();
        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                cards.addAll(parseElement(stack, (Element) node));
            }
        }
        
        stack = new CardStack(cards);
        return stack;
    }

    /**
     * Nimmt alle wichtigen Infos aus den card-Tags, die als Parameter übergeben werden und erstellt aus diesen eine Instanz von
     * Card.
     *
     * @param elem Die Element Instanz eines card-Tags
     * @return Die Karte, die mit den entsprechenden Daten erstellt wurde.
     */
    private static List<Card> parseElement(CardStack stack, Element elem) {
        String name = null, text = null;
        Card.Action type = null;
        int arg = 0;
        int amount;

        try {
            name = elem.getAttribute("name");
            text = elem.getAttribute("text");
            type = Card.Action.valueOf(elem.getAttribute("action").toUpperCase());
        }
        catch (NullPointerException ex) {
            logException(ex);
        }

        try {
            NodeList nList = elem.getElementsByTagName("arg");
            int length = nList.getLength();
            if (length != 0) {
                arg = Integer.parseInt(nList.item(length - 1).getTextContent());
            }
        } catch (NumberFormatException ex) {
            logException(ex);
        }

        try {
            amount = Integer.parseInt(elem.getAttribute("amount"));
        }
        catch (NumberFormatException ex) {
            amount = 1;
        }
        
        List<Card> retObj = new LinkedList<>();
        for (int i = 0; i < amount; i++) {
            if (type == Card.Action.JAIL) {
                retObj.add(new JailCard(stack));
            }
            else retObj.add(new Card(name, text, type, arg));
        }
        return retObj;
    }

    /**
     * Hilfsmethode
     *
     * @param list Auflistung bestimmter Node Instanzen
     * @return Stream aller Node-Instanzen der Liste
     */
    private static Stream<Node> convertNodesToStream(NodeList list) {
        Stream.Builder<Node> builder = Stream.builder();
        for (int i = 0; i < list.getLength(); i++) {
            builder.accept(list.item(i));
        }
        return builder.build();
    }

    /**
     * Logging-Methode
     *
     * @param ex geworfene Exception
     */
    private static void logException(Exception ex) {
        Logger.getLogger(CardDataParser.class.getPackage().getName())
                .warning("Exception while reading card data. Corrupted file!" + ex);
    }
}
