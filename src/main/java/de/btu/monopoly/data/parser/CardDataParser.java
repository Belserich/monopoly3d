package de.btu.monopoly.data.parser;

import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.card.CardAction;
import de.btu.monopoly.data.card.JailCard;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
    public static Card[] parse(String path) throws ParserConfigurationException, IOException, SAXException {
        List<Card> cards;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        File file = new File(CardDataParser.class.getResource(path).getFile());
        Document doc = builder.parse(file);

        NodeList nList = doc.getElementsByTagName("card");
        cards = new LinkedList<>();
        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                cards.addAll(parseElement((Element) node));
            }
        }
        return cards.toArray(new Card[cards.size()]);
    }

    /**
     * Nimmt alle wichtigen Infos aus den card-Tags, die als Parameter übergeben werden und erstellt aus diesen eine Instanz von
     * Card.
     *
     * @param elem Die Element Instanz eines card-Tags
     * @return Die Karte, die mit den entsprechenden Daten erstellt wurde.
     */
    private static List<Card> parseElement(Element elem) {
        String name = null, text = null;
        CardAction[] types = null;
        int[] args = null;
        int amount;

        try {
            name = elem.getAttribute("name");
            text = elem.getAttribute("text");

            types = convertNodesToStream(elem.getElementsByTagName("action"))
                    .map(Node::getTextContent)
                    .map(s -> CardAction.valueOf(s.toUpperCase()))
                    .toArray(CardAction[]::new);
        } catch (NullPointerException ex) {
            logException(ex);
        }

        try {
            args = convertNodesToStream(elem.getElementsByTagName("arg"))
                    .mapToInt(n -> Integer.parseInt(n.getTextContent()))
                    .toArray();
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
            if (Arrays.binarySearch(types, CardAction.JAIL) >= 0) {
                retObj.add(new JailCard(name, text));
            }
            else retObj.add(new Card(name, text, types, args));
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
