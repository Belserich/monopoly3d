package de.btu.monopoly.ui.util;

import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.parser.CardDataParser;
import de.btu.monopoly.data.parser.FieldDataParser;
import de.btu.monopoly.ui.fx3d.FieldType;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

public class Assets
{
    public static final Logger LOGGER = Logger.getLogger(Assets.class.getCanonicalName());
    
    public static final String UNDEFINED = "UNDEFINED";
    public static final String R = "/images/new/";
    
    private static final Color COLOR_TO_REPLACE = new Color(1, 0, 1, 1);
    private static final Color REPLACEMENT_COLOR = new Color(205d / 255, 230d / 255, 208d / 255, 1);
    
    private static final String IMAGE_3D_PATH = "3d_rotation.png";
    
    private static final String BOARD_PATH = "game_board.png";
    
    private static final String CORNER_0_PATH = "corner_0.png";
    private static final String CORNER_1_PATH = "corner_1.png";
    private static final String CORNER_2_PATH = "corner_2.png";
    private static final String CORNER_3_PATH = "corner_3.png";
    
    private static final String STREET_0_PATH = "streets/street_0.png";
    private static final String STREET_1_PATH = "streets/street_1.png";
    private static final String STREET_2_PATH = "streets/street_2.png";
    private static final String STREET_3_PATH = "streets/street_3.png";
    private static final String STREET_4_PATH = "streets/street_4.png";
    private static final String STREET_5_PATH = "streets/street_5.png";
    private static final String STREET_6_PATH = "streets/street_6.png";
    private static final String STREET_7_PATH = "streets/street_7.png";
    
    private static final String CARD_0_PATH = "card_0.png";
    private static final String CARD_1_PATH = "card_1.png";
    
    private static final String STATION_PATH = "station.png";
    
    private static final String SUPPLY_0_PATH = "supply_0.png";
    private static final String SUPPLY_1_PATH = "supply_1.png";
    
    private static final String TAX_0_PATH = "tax_0.png";
    private static final String TAX_1_PATH = "tax_1.png";
    
    private static final String FONT_PATH = "kabel.ttf";
    
    private static final HashMap<String, WritableImage> registeredImages = new HashMap<>();
    private static final HashMap<String, String> registeredStrings = new HashMap<>();
    private static final HashMap<String, Font> registeredFonts = new HashMap<>();
    
    private static final String DATA_FIELD_PATH = "/data/field_data.xml";
    
    private static final String COMM_CARDS_PATH = "/data/card_data.xml";
    private static final String EVENT_CARDS_PATH = "/data/card_data.xml";
    
    private static Field[] fields;
    
    private static CardStack commCards;
    private static CardStack eventCards;
    
    public static void loadCards() {
    
        try {
            commCards = CardDataParser.parse(COMM_CARDS_PATH);
            eventCards = CardDataParser.parse(EVENT_CARDS_PATH);
        }
        catch (IOException | SAXException | ParserConfigurationException ex) {
            LOGGER.warning(String.format("Exception while loading card data.", ex));
        }
    }
    
    public static void loadGeneral() {
        
        try {
            fields = FieldDataParser.parse(DATA_FIELD_PATH);
        }
        catch (IOException | SAXException | ParserConfigurationException ex) {
            LOGGER.warning(String.format("Exception while loading field data.", ex));
        }
    }
    
    public static void loadFxContent() {
        
        registeredImages.put("3d_icon", loadImage(IMAGE_3D_PATH));
    
        registeredImages.put("game_board", loadImage(BOARD_PATH));
    
        registeredImages.put("corner_0", loadImage(CORNER_0_PATH));
        registeredImages.put("corner_1", loadImage(CORNER_1_PATH));
        registeredImages.put("corner_2", loadImage(CORNER_2_PATH));
        registeredImages.put("corner_3", loadImage(CORNER_3_PATH));
    
        registeredImages.put("street_0", loadImage(STREET_0_PATH));
        registeredImages.put("street_1", loadImage(STREET_1_PATH));
        registeredImages.put("street_2", loadImage(STREET_2_PATH));
        registeredImages.put("street_3", loadImage(STREET_3_PATH));
        registeredImages.put("street_4", loadImage(STREET_4_PATH));
        registeredImages.put("street_5", loadImage(STREET_5_PATH));
        registeredImages.put("street_6", loadImage(STREET_6_PATH));
        registeredImages.put("street_7", loadImage(STREET_7_PATH));
    
        registeredImages.put("card_0", loadImage(CARD_0_PATH));
        registeredImages.put("card_1", loadImage(CARD_1_PATH));
    
        registeredImages.put("station", loadImage(STATION_PATH));
    
        registeredImages.put("supply_0", loadImage(SUPPLY_0_PATH));
        registeredImages.put("supply_1", loadImage(SUPPLY_1_PATH));
    
        registeredImages.put("tax_0", loadImage(TAX_0_PATH));
        registeredImages.put("tax_1", loadImage(TAX_1_PATH));
    
        registeredStrings.put("field_1_name", "WARSCHAUER STRASSE");
        registeredStrings.put("field_3_name", "GEMEINSCHAFTSKARTE");
    
        registeredFonts.put("font_default", loadFont(FONT_PATH, 12));
        registeredFonts.put("font_big", loadFont(FONT_PATH, 15));
        registeredFonts.put("font_huge", loadFont(FONT_PATH, 18));
        
    }
    
    public static void load()
    {
        loadCards();
        loadGeneral();
        loadFxContent();
    }
    
    public static boolean loadedFxContent() {
        return !registeredImages.isEmpty() || !registeredStrings.isEmpty();
    }
    
    private static WritableImage loadImage(String path)
    {
        Image image = new Image(R + path);
        WritableImage img = new WritableImage(image.getPixelReader(), (int) image.getWidth(), (int) image.getHeight());
        img = FxHelper.replaceColorInImage(img, COLOR_TO_REPLACE, REPLACEMENT_COLOR);
        return img;
    }
    
    private static Font loadFont(String path, double size) {
        return Font.loadFont(Assets.class.getResource(R + path).toExternalForm(), size);
    }
    
    public void registerString(String key, String val)
    {
        registeredStrings.put(key, val.toLowerCase());
    }
    
    public static Image getImage(String name)
    {
        return registeredImages.get(name);
    }
    
    public static WritableImage getImage(FieldType type) { return registeredImages.get(type.toString().toLowerCase()); }
    
    public static String getString(String key)
    {
        return registeredStrings.getOrDefault(key.toLowerCase(), UNDEFINED);
    }
    
    public static Font getFont(String key) {
        return registeredFonts.get(key);
    }
    
    public static Field[] getFields() {
        return fields;
    }
    
    public static CardStack getCommunityCards() {
        return commCards;
    }
    
    public static CardStack getEventCards() {
        return eventCards;
    }
}
