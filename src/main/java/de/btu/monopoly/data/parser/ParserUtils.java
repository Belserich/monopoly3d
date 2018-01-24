package de.btu.monopoly.data.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class ParserUtils {
    
    /**
     * Kommentarsymbol
     */
    public static final String C = "//";
    
    /**
     * Argumentseparator (darf kein Steuerzeichen sein)
     */
    public static final String S = ",";
    
    /**
     * Reduziert den Inhalt einer Textdatei auf seine Informationen.
     *
     * @param path Der Pfad der Textdatei
     * @return Die Zeilen der Textdatei, frei von Steuerzeichen und unter Ausschluss leerer Zeilen.
     * @throws IOException Datei ist nicht lesbar, nicht vorhanden oder beschädigt.
     */
    public static String[] trimInsignificant(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(FieldDataParser.class.getClassLoader().getResourceAsStream(path)));
        String[] retObj = reader.lines()
                .map(s -> {
                    int i = s.indexOf(C);
                    return i == -1 ? s : s.substring(0, i);
                }) // trims comments
                .map(s -> s.replaceAll("[^\\w|" + S + "]", "")) // trimmt alle Kontrollsymbole
                .filter(s -> s.length() > 0) // trimmt leere Zeilen
                .toArray(String[]::new); // erstellt ein neues Array und packt alle verbliebenen Zeilen hinein.
        reader.close();
        return retObj;
    }
}
