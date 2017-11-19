package de.btu.monopoly.data.parser;

import de.btu.monopoly.data.*;
import de.btu.monopoly.data.field.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.function.Function;

import static de.btu.monopoly.data.parser.GameBoardParser.FieldType.*;

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
     * Die Feldstruktur ordnet den ids 0-39 die entsprechenden Feldtypen zu. Fängt beim LOS-Feld an und geht im Uhrzeigersinn weiter.
     * Für mehr Infos siehe *\src\resources\data\classic_game_board_de.png
     */
    private static final FieldType[] FIELD_STRUCTURE = {
            GO, STREET, CARD, STREET, TAX, STATION, STREET, CARD, STREET, STREET,
            CORNER, STREET, SUPPLY, STREET, STREET, STATION, STREET, CARD, STREET, STREET,
            CORNER, STREET, CARD, STREET, STREET, STATION, STREET, STREET, SUPPLY, STREET,
            CORNER, STREET, STREET, CARD, STREET, STATION, CARD, STREET, TAX, STREET
    };
    
    /**
     * Sammlung aller Nachbar-Ids in aufsteigender Reihenfolge. Der Erste beider Indizes steht immer für eine eigene Property (Bahnhof, Werk oder Straße).
     * Die Zuordnung ist <bold>nicht</bold> 1:1 zu {@code FIELD_STRUCTURE}, d.h. die IDs an der Stelle {@code i} stehen hier nicht für die
     * Nachbarn des Feldes mit dem Index {@code i}, sondern für das {@code i}-te Property Feld. Die Aufzählung beginnt bei der Ersten Straße
     * und schreitet dann im Uhrzeigersinn fort.
     */
    private static final int[][] NEIGHBOUR_IDS = {
            {3}, {1}, {15, 25, 35}, {8, 9}, {6, 9}, {6, 8}, // Erste Reihe
            {13, 14}, {21}, {11, 14}, {11, 13}, {5, 25, 35}, {18, 19}, {16, 19}, {16, 18}, // Zweite Reihe
            {23, 24}, {21, 24}, {23, 21}, {5, 15, 35}, {27, 29}, {26, 29}, {8}, {26, 27}, // Dritte Reihe
            {32, 34}, {31, 34}, {32, 31}, {5, 15, 25}, {39}, {37} // Vierte Reihe
    };
    
    /*
     Die folgenden Variablen sind alle Pattern-Strings eines regulären Ausdrucks, Sie werde hier zur Vereinfachugn definiert.
     Danach werden aus ihnen die eigentlich benötigten Ausdrücke (Regex) zusammengesetzt.
     */
    
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
    private static final Function<Integer, String> R = i -> String.format("{%d}", i);
    
    /**
     * Automatisiert das konvertieren von Text zu Integer-Werten
     */
    private static final Function<String, Integer> INT_PARSER = Integer::parseInt;
    
    /*
     Jedes der folgenden Datenpattern besteht aus einem B (begin) und E (end) - Tag das sicherstellt, der Ausdruck ist das einzige was in der momentanen Zeile steht. Dazwischen befinden sich
     weitere, zusammengesetzte Regex Ausdrücke deren Bedeutung man aus der Bedeutung der einzelnen Teilausdrücke schließen kann.
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
    private String IO_ERROR_MESSAGE = "Error while reading game board data. Corrupted resource file!";
    
    /**
     * Liest erst alle Zeilen Daten aus der Textdatei, entfernt sämtliche Steuerzeichen (ASCII 0 - 32) und wertet jede Zeile einzeln aus.
     *
     * @param path Pfad zur Textdatei
     * @return Gameboard-Instanz
     * @throws IOException Wenn die Datei nicht geöffnet, oder einzelne Zeilen nicht gelesen werden konnten. Allgemein, wenn sie beschädigt
     * oder falsch editiert wurde.
     */
    public final GameBoard readBoard(String path) throws IOException {
        String[] lines = readSignificantLines(path);
        
        // Es gibt immer genau so viele Zeilen wie Felder, da sie in einer 1:1 Beziehung zueinander stehen.
        if (lines.length != FIELD_STRUCTURE.length) {
            throw new IOException(IO_ERROR_MESSAGE);
        }
        
        // Zählt die Anzahl der Unterinstanzen von Property um die korrekten Nachbar-IDs zu übergeben.
        int propertyCounter = 0;
        
        // Die Felder, mit sämtlichen benutzerdefinierten Infos
        Field[] fields = new Field[FIELD_STRUCTURE.length];
        
        for (int i = 0; i < FIELD_STRUCTURE.length; i++) {
            FieldType type = FIELD_STRUCTURE[i];
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
    
    /**
     * Reduziert den Inhalt einer Textdatei auf seine Informationen.
     *
     * @param path Der Pfad der Textdatei
     * @return Die Zeilen der Textdatei, frei von Steuerzeichen und unter Ausschluss leerer Zeilen.
     * @throws IOException Datei ist nicht lesbar, nicht vorhanden oder beschädigt.
     */
    private String[] readSignificantLines(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(GameBoardParser.class.getClassLoader().getResourceAsStream(path)));
        String[] retObj = reader.lines()
                .map(s -> { int i = s.indexOf(C); return i == -1 ? s : s.substring(0, i); }) // trims comments
                .map(s -> s.replaceAll("[^\\w|" + S + "]", "")) // trimmt alle Kontrollsymbole
                .filter(s -> s.length() > 0) // trimmt leere Zeilen
                .toArray(String[]::new); // erstellt ein neues Array und packt alle verbliebenen Zeilen hinein.
        reader.close();
        return retObj;
    }
    
    /**
     * Versucht aus den Daten in line die nötigen Informationen für das entsprechende Feld zu nehmen.
     *
     * @param line Die aktuelle Zeile (enthält alle wichtigen Parameter)
     * @return die entsprechende Feld-Instanz
     * @throws IOException Fehlschlag (korrupte Datei)
     */
    private GoField tryParseGo(String line) throws IOException {
        if (line.matches(GO_PATTERN)) {
            StringTokenizer tokenizer = new StringTokenizer(line);
            return new GoField(
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    tokenizer.nextToken(),
                    INT_PARSER.apply(tokenizer.nextToken(S)));
        }
        else throw new IOException(IO_ERROR_MESSAGE);
    }
    
    /**
     * Versucht aus den Daten in line die nötigen Informationen für das entsprechende Feld zu nehmen.
     *
     * @param line Die aktuelle Zeile (enthält alle wichtigen Parameter)
     * @param propertyCounter Property-Zähler
     * @return die entsprechende Feld-Instanz
     * @throws IOException Fehlschlag (korrupte Datei)
     */
    private SupplyField tryParseSupplyField(String line, int propertyCounter) throws IOException {
        if (line.matches(SUPPLY_PATTERN)) {
            StringTokenizer tokenizer = new StringTokenizer(line);
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
    
    /**
     * Versucht aus den Daten in line die nötigen Informationen für das entsprechende Feld zu nehmen.
     *
     * @param line Die aktuelle Zeile (enthält alle wichtigen Parameter)
     * @param propertyCounter Property-Zähler
     * @return die entsprechende Feld-Instanz
     * @throws IOException Fehlschlag (korrupte Datei)
     */
    private StationField tryParseStationField(String line, int propertyCounter) throws IOException {
        if (line.matches(STATION_PATTERN)) {
            StringTokenizer tokenizer = new StringTokenizer(line);
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
    
    /**
     * Versucht aus den Daten in line die nötigen Informationen für das entsprechende Feld zu nehmen.
     *
     * @param line Die aktuelle Zeile (enthält alle wichtigen Parameter)
     * @return die entsprechende Feld-Instanz
     * @throws IOException Fehlschlag (korrupte Datei)
     */
    private TaxField tryParseTaxField(String line) throws IOException {
        if (line.matches(TAX_PATTERN)) {
            StringTokenizer tokenizer = new StringTokenizer(line);
            return new TaxField(
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    tokenizer.nextToken(S),
                    INT_PARSER.apply(tokenizer.nextToken(S)));
        }
        else throw new IOException(IO_ERROR_MESSAGE);
    }
    
    /**
     * Versucht aus den Daten in line die nötigen Informationen für das entsprechende Feld zu nehmen.
     *
     * @param line Die aktuelle Zeile (enthält alle wichtigen Parameter)
     * @return die entsprechende Feld-Instanz
     * @throws IOException Fehlschlag (korrupte Datei)
     */
    private CardField tryParseCardField(String line) throws IOException {
        if (line.matches(CARD_PATTERN)) {
            StringTokenizer tokenizer = new StringTokenizer(line);
            return new CardField(
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    tokenizer.nextToken(S),
                    null, new Card[0]);
        }
        else throw new IOException(IO_ERROR_MESSAGE);
    }
    
    /**
     * Versucht aus den Daten in line die nötigen Informationen für das entsprechende Feld zu nehmen.
     *
     * @param line Die aktuelle Zeile (enthält alle wichtigen Parameter)
     * @param propertyCounter Property-Zähler
     * @return die entsprechende Feld-Instanz
     * @throws IOException Fehlschlag (korrupte Datei)
     */
    private StreetField tryParseStreetField(String line, int propertyCounter) throws IOException {
        if (line.matches(STREET_PATTERN)) {
            StringTokenizer tokenizer = new StringTokenizer(line);
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
    
    /**
     * Versucht aus den Daten in line die nötigen Informationen für das entsprechende Feld zu nehmen.
     *
     * @param line Die aktuelle Zeile (enthält alle wichtigen Parameter)
     * @return die entsprechende Feld-Instanz
     * @throws IOException Fehlschlag (korrupte Datei)
     */
    private Field tryParseCorner(String line) throws IOException {
        if (line.matches(CORNER_PATTERN)) {
            StringTokenizer tokenizer = new StringTokenizer(line);
            return new Field(
                    INT_PARSER.apply(tokenizer.nextToken(S)),
                    tokenizer.nextToken(S));
        }
        else throw new IOException(IO_ERROR_MESSAGE);
    }
}
