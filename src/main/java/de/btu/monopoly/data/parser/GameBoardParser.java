package de.btu.monopoly.data.parser;

import de.btu.monopoly.data.*;
import de.btu.monopoly.data.field.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.function.Function;

import static de.btu.monopoly.data.parser.GameBoardParser.Type.*;

public class GameBoardParser {
    
    /**
     * Eine Aufzählung der verschiedenen Feldtypen
     */
    enum Type {
        GO, CORNER, STREET, CARD, TAX, STATION, SUPPLY;
    }
    
    /**
     * Ordnet den ids 0-39 die entsprechenden Feldtypen zu, diese sollen sich schließlich nie verändern
     */
    private static final Type[] FIELD_STRUCTURE = {
            GO, STREET, CARD, STREET, TAX, STATION, STREET, CARD, STREET, STREET,
            CORNER, STREET, SUPPLY, STREET, STREET, STATION, STREET, CARD, STREET, STREET,
            CORNER, STREET, CARD, STREET, STREET, STATION, STREET, STREET, SUPPLY, STREET,
            CORNER, STREET, STREET, CARD, STREET, STATION, CARD, STREET, TAX, STREET
    };
    
    /**
     * Sammlung aller Nachbar-Ids in aufsteigender Reihenfolge
     */
    private static final int[][] NEIGHBOUR_IDS = {
            {3}, {1}, {15, 25, 35}, {8, 9}, {6, 9}, {6, 8},
            {13, 14}, {21}, {11, 14}, {11, 13}, {5, 25, 35}, {18, 19}, {16, 19}, {16, 18},
            {23, 24}, {21, 24}, {23, 21}, {5, 15, 35}, {27, 29}, {26, 29}, {8}, {26, 27},
            {32, 34}, {31, 34}, {32, 31}, {5, 15, 25}, {39}, {37}
    };
    
    /**
     * Kommentarsymbol
     */
    private static final String C = "#";
    
    /**
     * Argumentseparator (darf kein Steuerzeichen sein)
     */
    private static final String S = ",";
    
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
    private  static final String B = "^";
    
    /**
     * End-Pattern ($ steht für Ende eines Regex)
     */
    private  static final String E = "$";
    
    /**
     * Ruft man R.apply(int i) mit einem Wert i auf, dann wird ein Wiederholungspattern als String zurückgegeben.
     */
    private static final Function<Integer, String> R = i -> "{" + i + "}";
    
    /**
     * Automatisiert das konvertieren von Text zu Integer-Werten
     */
    private static final Function<String, Integer> INT_PARSER = s -> Integer.parseInt(s);
    
    /**
     * Jedes der folgenden Datenpattern besteht aus einem B (begin) und E (end) - Tag das sicherstellt, der Ausdruck ist das einzige was in der momentanen Zeile steht. Dazwischen befinden sich
     * weitere, zusammengesetzte Regex Ausdrücke deren Bedeutung man aus der Bedeutung der einzelnen Teilausdrücke schließen kann
     */
    
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
     * ID, Name, 0 -> Gemeinschaftskarten, 1 -> Ereigniskarten
     */
    private static final String CARD_PATTERN = B + F + S + "(0|1)" + E;
    
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
    private String IO_ERROR_MESSAGE = "Error while reading game board data. Corrupted resource file!";
    
    /**
     * Teilt eine Zeile in einzelne Parameter basierend auf einem Separiersymbol
     */
    private StringTokenizer tokenizer;
    
    public final GameBoard readBoard(String path) throws IOException {
        String[] lines = readSignificantLines(path);
        
        if (lines.length != FIELD_STRUCTURE.length) {
            throw new IOException(IO_ERROR_MESSAGE);
        }
        
        int propertyCounter = 0;
        Field[] fields = new Field[FIELD_STRUCTURE.length];
        
        for (int i = 0; i < FIELD_STRUCTURE.length; i++) {
            Type type = FIELD_STRUCTURE[i];
            switch (type) {
                case GO: fields[i] = tryParseGo(lines[i]); break;
                case CORNER: fields[i] = tryParseCorner(lines[i]); break;
                case STREET: fields[i] = tryParseStreetField(lines[i], propertyCounter++); break;
                case CARD: fields[i] = tryParseCardField(lines[i]); break;
                case TAX: fields[i] = tryParseTaxField(lines[i]); break;
                case STATION: fields[i] = tryParseStationField(lines[i], propertyCounter++); break;
                case SUPPLY: fields[i] = tryParseSupplyField(lines[i], propertyCounter++); break;
            }
        }
        
        return new GameBoard(fields);
    }
    
    private String[] readSignificantLines(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(GameBoardParser.class.getClassLoader().getResourceAsStream(path)));
        String[] retObj = reader.lines()
                .map(s -> { int i = s.indexOf(C); return i == -1 ? s : s.substring(0, i); }) // trims comments
                .map(s -> s.replaceAll("[^\\w|" + S + "]", "")) // trims all control characters
                .filter(s -> s.length() > 0) // trims empty lines
                .toArray(String[]::new); // creates a new array and puts all remaining lines in it
        reader.close();
        return retObj;
    }
    
    /**
     * Versucht aus den Daten in line die nötigen Informationen für ein GoField zu nehmen
     *
     * @param line Die aktuelle Zeile (enthält Informationen)
     * @return Das erstellte GoField
     * @ throws Bei Fehlschlag (korrupte Datei)
     */
    private GoField tryParseGo(String line) throws IOException {
        if (line.matches(GO_PATTERN)) {
            tokenizer = new StringTokenizer(line);
            return new GoField(
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    tokenizer.nextToken(),
                    INT_PARSER.apply(tokenizer.nextToken(S)));
        }
        else throw new IOException(IO_ERROR_MESSAGE);
    }
    
    private SupplyField tryParseSupplyField(String line, int propertyCounter) throws IOException {
        if (line.matches(SUPPLY_PATTERN)) {
            tokenizer = new StringTokenizer(line);
            return new SupplyField(
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    tokenizer.nextToken(S),
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    NEIGHBOUR_IDS[propertyCounter],
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    INT_PARSER.apply(tokenizer.nextToken(S)));
        }
        else throw new IOException(IO_ERROR_MESSAGE);
    }
    
    private StationField tryParseStationField(String line, int propertyCounter) throws IOException {
        if (line.matches(STATION_PATTERN)) {
            tokenizer = new StringTokenizer(line);
            return new StationField(
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    tokenizer.nextToken(S),
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    NEIGHBOUR_IDS[propertyCounter]);
        }
        else throw new IOException(IO_ERROR_MESSAGE);
    }
    
    private TaxField tryParseTaxField(String line) throws IOException {
        if (line.matches(TAX_PATTERN)) {
            tokenizer = new StringTokenizer(line);
            return new TaxField(
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    tokenizer.nextToken(S),
                    INT_PARSER.apply(tokenizer.nextToken(S)));
        }
        else throw new IOException(IO_ERROR_MESSAGE);
    }
    
    private CardField tryParseCardField(String line) throws IOException {
        if (line.matches(CARD_PATTERN)) {
            tokenizer = new StringTokenizer(line);
            return new CardField(
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    tokenizer.nextToken(S),
                    null, null);
        }
        else throw new IOException(IO_ERROR_MESSAGE);
    }
    
    private StreetField tryParseStreetField(String line, int propertyCounter) throws IOException {
        if (line.matches(STREET_PATTERN)) {
            tokenizer = new StringTokenizer(line);
            return new StreetField(
                    INT_PARSER.apply(tokenizer.nextToken(S)),    // ID
                    tokenizer.nextToken(S),                      // Name
                    INT_PARSER.apply(tokenizer.nextToken(S)),    // Preis
                    INT_PARSER.apply(tokenizer.nextToken(S)),    // Miete0
                    INT_PARSER.apply(tokenizer.nextToken(S)),    // Miete1
                    INT_PARSER.apply(tokenizer.nextToken(S)),    // Miete2
                    INT_PARSER.apply(tokenizer.nextToken(S)),    // Miete3
                    INT_PARSER.apply(tokenizer.nextToken(S)),    // Miete4
                    INT_PARSER.apply(tokenizer.nextToken(S)),    // Miete5
                    INT_PARSER.apply(tokenizer.nextToken(S)),    // Hauspreis
                    INT_PARSER.apply(tokenizer.nextToken(S)),    // Hypothekswert
                    INT_PARSER.apply(tokenizer.nextToken(S)),    // Hypotheksrückwert
                    NEIGHBOUR_IDS[propertyCounter]);            // Nachbar-IDs
        }
        else throw new IOException(IO_ERROR_MESSAGE);
    }
    
    private Field tryParseCorner(String line) throws IOException {
        if (line.matches(CORNER_PATTERN)) {
            tokenizer = new StringTokenizer(line);
            return new Field(
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    tokenizer.nextToken(S));
        }
        else throw new IOException(IO_ERROR_MESSAGE);
    }
}
