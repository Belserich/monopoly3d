package de.btu.monopoly.data.parser;

import de.btu.monopoly.data.*;
import de.btu.monopoly.data.field.*;
import static de.btu.monopoly.data.parser.GameBoardParser.FieldType.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.function.Function;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class GameBoardParser {

    /**
     * Aufzählung der verschiedenen Feldtypen
     */
    enum FieldType {
        GO, CORNER, STREET, CARD, TAX, STATION, SUPPLY
    }

    /**
     * Die Feldstruktur ordnet den ids 0-39 die entsprechenden Feldtypen zu. Fängt beim LOS-Feld an und geht im Uhrzeigersinn
     * weiter. Für mehr Infos siehe *\src\resources\data\classic_game_board_de.png
     */
    private static final FieldType[] FIELD_STRUCTURE = {
        GO, STREET, CARD, STREET, TAX, STATION, STREET, CARD, STREET, STREET,
        CORNER, STREET, SUPPLY, STREET, STREET, STATION, STREET, CARD, STREET, STREET,
        CORNER, STREET, CARD, STREET, STREET, STATION, STREET, STREET, SUPPLY, STREET,
        CORNER, STREET, STREET, CARD, STREET, STATION, CARD, STREET, TAX, STREET
    };

    /**
     * Sammlung aller Nachbar-Ids in aufsteigender Reihenfolge. Der Erste beider Indizes steht immer für eine eigene Property
     * (Bahnhof, Werk oder Straße). Die Zuordnung ist <bold>nicht</bold> 1:1 zu {@code FIELD_STRUCTURE}, d.h. die IDs an der
     * Stelle {@code i} stehen hier nicht für die Nachbarn des Feldes mit dem Index {@code i}, sondern für das {@code i}-te
     * Property Feld. Die Aufzählung beginnt bei der Ersten Straße und schreitet dann im Uhrzeigersinn fort.
     */
    private static final int[][] NEIGHBOUR_IDS = {
        {3}, {1}, {15, 25, 35}, {8, 9}, {6, 9}, {6, 8}, // Erste Reihe
        {13, 14}, {28}, {11, 14}, {11, 13}, {5, 25, 35}, {18, 19}, {16, 19}, {16, 18}, // Zweite Reihe
        {23, 24}, {21, 24}, {23, 21}, {5, 15, 35}, {27, 29}, {26, 29}, {12}, {26, 27}, // Dritte Reihe
        {32, 34}, {31, 34}, {32, 31}, {5, 15, 25}, {39}, {37} // Vierte Reihe
    };

    /*
     * Die folgenden Variablen sind alle Pattern-Strings eines regulären Ausdrucks, Sie werde hier zur Vereinfachugn definiert.
     * Danach werden aus ihnen die eigentlich benötigten Ausdrücke (Regex) zusammengesetzt.
     */
    
    /**
     * Standard Separator Pattern
     */
    private static final String S = ParserUtils.S;
    
    /**
     * Standard Nummer-Pattern (\\d+ steht für beliebig viele Zahlsymbole eines Regex)
     */
    private static final String D = "\\d+";

    /**
     * Standard Wort-Pattern (\\w+ steht für beliebig viele Wortsymbole eines Regex)
     */
    private static final String W = "\\w+";

    /**
     * Standard Feld-Pattern
     */
    private static final String F = D + S + W;

    /**
     * Begin-Pattern (^ steht für Anfang eines Regex)
     */
    private static final String B = "^";

    /**
     * End-Pattern ($ steht für Ende eines Regex)
     */
    private static final String E = "$";

    /**
     * Ruft man R.apply(int i) mit einem Wert i auf, dann wird ein Wiederholungspattern als String zurückgegeben.
     */
    private static final Function<Integer, String> R = i -> String.format("{%d}", i);

    /**
     * Automatisiert das konvertieren von Text zu Integer-Werten
     */
    private static final Function<String, Integer> INT_PARSER = Integer::parseInt;

    /*
     * Jedes der folgenden Datenpattern besteht aus einem B (begin) und E (end) - Tag das sicherstellt, der Ausdruck ist das
     * einzige was in der momentanen Zeile steht. Dazwischen befinden sich weitere, zusammengesetzte Regex Ausdrücke deren
     * Bedeutung man aus der Bedeutung der einzelnen Teilausdrücke schließen kann.
     */
    /**
     * ID, Name, alles andere
     */
    private static final String GENERAL_LINE_PATTERN = B + F + ".*" + E;

    /**
     * ID, Name, Startgeld
     */
    private static final String GO_PATTERN = B + F + S + D + E;

    /**
     * ID, Name
     */
    private static final String CORNER_PATTERN = B + F + E;

    /**
     * ID, Name, Preis, Miete (0-5), Hauspreis, Hypothekswert, Hypotheksrückwert
     */
    private static final String STREET_PATTERN = B + F + "(" + S + D + ")" + R.apply(10) + E;

    /**
     * ID, Name, (0 -> Gemeinschaftskarten, 1 -> Ereigniskarten)
     */
    private static final String CARD_PATTERN = B + F + S + "[01]" + E;

    /**
     * ID, Name, Steuer
     */
    private static final String TAX_PATTERN = GO_PATTERN;

    /**
     * ID, Name, Preis, Miete (0-3), Hypothekswert, Hypotheksrückwert
     */
    private static final String STATION_PATTERN = B + F + "(" + S + D + ")" + R.apply(7) + E;

    /**
     * ID, Name, Preis, Hypothekswert, Hypotheksrückwert, Multiplikator 1, Multiplikator 2
     */
    private static final String SUPPLY_PATTERN = B + F + "(" + S + D + ")" + R.apply(5) + E;

    /**
     * Die allgemeine Exception-Nachricht für diese Klasse
     */
    private static final String IO_EXCEPTION_MESSAGE = "Exception while reading game board data. Corrupted resource file!";

    // Zählt die Anzahl der Unterinstanzen von Property um die korrekten Nachbar-IDs zu übergeben.
    private int propertyCounter;

    // teilt eine Zeile in ihre Parameter auf
    private StringTokenizer tokenizer;

    // Die Felder, mit sämtlichen benutzerdefinierten Infos
    private Field[] fields;

    /**
     * Liest erst alle Zeilen Daten aus der Textdatei, entfernt sämtliche Steuerzeichen (ASCII 0 - 32) und wertet jede Zeile
     * einzeln aus.
     *
     * @param path Pfad zur Textdatei
     * @return Gameboard-Instanz
     * @throws IOException Wenn die Datei nicht geöffnet, oder einzelne Zeilen nicht gelesen werden konnten. Allgemein, wenn sie
     * beschädigt oder falsch editiert wurde.
     */
    public GameBoard parseGameBoard(String path) throws IOException {
        fields = new Field[FIELD_STRUCTURE.length];

        String[] lines = ParserUtils.trimInsignificant(path);

        // Es gibt immer genau so viele Zeilen wie Felder, da sie in einer 1:1 Beziehung zueinander stehen.
        if (lines.length != FIELD_STRUCTURE.length) {
            throw new IOException(IO_EXCEPTION_MESSAGE);
        }

        for (int i = 0; i < FIELD_STRUCTURE.length; i++) {
            String line = lines[i];
            if (line.matches(GENERAL_LINE_PATTERN)) {
                tokenizer = new StringTokenizer(line);

                int id = INT_PARSER.apply(tokenizer.nextToken(S));
                String name = tokenizer.nextToken(S);

                if (id < 0 || id >= FIELD_STRUCTURE.length) {
                    throw new IOException(IO_EXCEPTION_MESSAGE);
                }

                fields[i] = tryParse(FIELD_STRUCTURE[id], line, name, tokenizer);
            } else {
                throw new IOException(IO_EXCEPTION_MESSAGE
                        + "\nLine didn't match pattern! (line: " + line + " pattern: " + GENERAL_LINE_PATTERN);
            }
        }
    
        // fügt allen Property-Feldern sämtliche Nachbarn hinzu
        propertyCounter = 0;
        for (Field field : fields) {
            if (field instanceof Property) {
                for (int id : NEIGHBOUR_IDS[propertyCounter++]) {
                    ((Property) field).addNeighbour(((Property)fields[id]));
                }
            }
        }
        
        return new GameBoard(fields);
    }

    /**
     * Prüft auf das jeweilige Pattern.
     */
    private Field tryParse(FieldType type, String line, String name, StringTokenizer tokenizer) throws IOException {
        switch (type) {
            case GO: {
                if (!line.matches(GO_PATTERN)) {
                    throw new IOException(IO_EXCEPTION_MESSAGE);
                }
                return parseGoField(name, tokenizer);
            }
            case CORNER: {
                if (!line.matches(CORNER_PATTERN)) {
                    throw new IOException(IO_EXCEPTION_MESSAGE);
                }
                return parseCornerField(name);
            }
            case STREET: {
                if (!line.matches(STREET_PATTERN)) {
                    throw new IOException(IO_EXCEPTION_MESSAGE);
                }
                return parseStreetField(name, tokenizer);
            }
            case CARD: {
                if (!line.matches(CARD_PATTERN)) {
                    throw new IOException(IO_EXCEPTION_MESSAGE);
                }
                return parseCardField(name);
            }
            case TAX: {
                if (!line.matches(TAX_PATTERN)) {
                    throw new IOException(IO_EXCEPTION_MESSAGE);
                }
                return parseTaxField(name, tokenizer);
            }
            case STATION: {
                if (!line.matches(STATION_PATTERN)) {
                    throw new IOException(IO_EXCEPTION_MESSAGE);
                }
                return parseStationField(name, tokenizer);
            }
            case SUPPLY: {
                if (!line.matches(SUPPLY_PATTERN)) {
                    throw new IOException(IO_EXCEPTION_MESSAGE);
                }
                return parseSupplyField(name, tokenizer);
            }
            default:
                throw new IOException(IO_EXCEPTION_MESSAGE); // sollte niemals passieren
        }
    }

    /**
     * Entnimmt dem tokenizer die entsprechende Information für die jeweilige Feldinstanz.
     *
     * @param name Name
     * @return die entsprechende Feld-Instanz
     */
    private GoField parseGoField(String name, StringTokenizer tokenizer) {
        return new GoField(
                name,
                INT_PARSER.apply(tokenizer.nextToken(S)));  // Betrag
    }

    private SupplyField parseSupplyField(String name, StringTokenizer tokenizer) {
        return new SupplyField(
                name,
                INT_PARSER.apply(tokenizer.nextToken(S)),   // Preis
                INT_PARSER.apply(tokenizer.nextToken(S)),   // Hypothekswert
                INT_PARSER.apply(tokenizer.nextToken(S)),   // Hypotheksrückwert
                INT_PARSER.apply(tokenizer.nextToken(S)),   // 1. Multiplikator
                INT_PARSER.apply(tokenizer.nextToken(S)));  // 2. Multiplikator
    }

    private StationField parseStationField(String name, StringTokenizer tokenizer) {
        return new StationField(
                name,
                INT_PARSER.apply(tokenizer.nextToken(S)),   // Preis
                INT_PARSER.apply(tokenizer.nextToken(S)),   // Miete 0
                INT_PARSER.apply(tokenizer.nextToken(S)),   // Miete 1
                INT_PARSER.apply(tokenizer.nextToken(S)),   // Miete 2
                INT_PARSER.apply(tokenizer.nextToken(S)),   // Miete 3
                INT_PARSER.apply(tokenizer.nextToken(S)),   // Hypothekswert
                INT_PARSER.apply(tokenizer.nextToken(S))    // Hypotheksrückwert
        );
    }
    private TaxField parseTaxField(String name, StringTokenizer tokenizer) {
        return new TaxField(
                name,
                INT_PARSER.apply(tokenizer.nextToken(S)));  // Betrag
    }

    private CardField parseCardField(String name) {
        return new CardField(
                name, null);
    }

    private StreetField parseStreetField(String name, StringTokenizer tokenizer) {
        return new StreetField(
                name,
                INT_PARSER.apply(tokenizer.nextToken(S)),    // Preis
                INT_PARSER.apply(tokenizer.nextToken(S)),    // Miete0
                INT_PARSER.apply(tokenizer.nextToken(S)),    // Miete1
                INT_PARSER.apply(tokenizer.nextToken(S)),    // Miete2
                INT_PARSER.apply(tokenizer.nextToken(S)),    // Miete3
                INT_PARSER.apply(tokenizer.nextToken(S)),    // Miete4
                INT_PARSER.apply(tokenizer.nextToken(S)),    // Miete5
                INT_PARSER.apply(tokenizer.nextToken(S)),    // Hauspreis
                INT_PARSER.apply(tokenizer.nextToken(S)),    // Hypothekswert
                INT_PARSER.apply(tokenizer.nextToken(S))     // Hypotheksrückwert
        );
}

private Field parseCornerField(String name) {
        return new Field(name);
    }
}
