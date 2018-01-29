package de.btu.monopoly.util;

import de.btu.monopoly.core.FieldTypes;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.parser.CardDataParser;
import de.btu.monopoly.data.parser.FieldDataParser;
import de.btu.monopoly.ui.fx3d.Fx3dCorner;
import de.btu.monopoly.ui.fx3d.Fx3dGameBoard;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import static de.btu.monopoly.ui.fx3d.Fx3dField.*;

public class Assets
{
    private static final Logger LOGGER = Logger.getLogger(Assets.class.getCanonicalName());
    
    private static final String UNDEFINED = "UNDEFINED";
    private static final String R = "/images/new/";
    
    private static final Color COLOR_TO_REPLACE = Color.TRANSPARENT;
    private static final Color REPLACEMENT_COLOR = new Color(205d / 255, 230d / 255, 208d / 255, 1);
    
    private static final double FIELD_TEXTURE_WIDTH = FIELD_WIDTH * 2 + FIELD_HEIGHT * 2;
    private static final double FIELD_TEXTURE_HEIGHT = FIELD_DEPTH + FIELD_HEIGHT * 2;
    
    private static final double CORNER_TEXTURE_WIDTH = Fx3dCorner.FIELD_WIDTH * 2 + FIELD_HEIGHT * 2;
    private static final double CORNER_TEXTURE_HEIGHT = Fx3dCorner.FIELD_DEPTH + Fx3dCorner.FIELD_HEIGHT * 2;
    
    private static final double GAMEBOARD_TEXTURE_WIDTH = Fx3dGameBoard.BOARD_MODEL_LENGTH * 2 + FIELD_HEIGHT * 2;
    private static final double GAMEBOARD_TEXTURE_HEIGHT = Fx3dGameBoard.BOARD_MODEL_LENGTH + FIELD_HEIGHT * 2;
    
    private static final String IMAGE_3D_PATH = "3d_rotation.png";
    private static final String IMAGE_DICE_1_PATH = "dice_1.png";
    
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
    
    private static final String SUPPLY_0_PATH = "supply_0.png";
    private static final String SUPPLY_1_PATH = "supply_1.png";
    private static final String STATION_PATH = "station.png";
    private static final String CARD_0_PATH = "card_0.png";
    private static final String CARD_1_PATH = "card_1.png";
    private static final String TAX_0_PATH = "tax_0.png";
    private static final String TAX_1_PATH = "tax_1.png";
    
    private static final String DATA_FIELD_PATH = "/data/field_data.xml";
    
    private static final String COMM_CARDS_PATH = "/data/card_data.xml";
    private static final String EVENT_CARDS_PATH = "/data/card_data.xml";
    
    private static final HashMap<String, Image> registeredImages = new HashMap<>();
    private static final HashMap<String, ImageView> registeredIcons = new HashMap<>();
    private static final HashMap<String, String> registeredStrings = new HashMap<>();
    
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
    
    public static void loadFields() {
        try {
            fields = FieldDataParser.parse(DATA_FIELD_PATH);
        }
        catch (IOException | SAXException | ParserConfigurationException ex) {
            LOGGER.warning(String.format("Exception while loading field data.", ex));
        }
    }
    
    public static void loadFxIcons() {
        
        registeredIcons.put("3d_icon", loadImageView(IMAGE_3D_PATH));
        registeredIcons.put("dice_1", loadImageView(IMAGE_DICE_1_PATH));
    }
    
    public static void loadFxImages() {
    
        registeredImages.put("game_board", loadImage(BOARD_PATH,
                GAMEBOARD_TEXTURE_WIDTH, GAMEBOARD_TEXTURE_HEIGHT));
        
        registeredImages.put("corner_0", loadCornerImage(CORNER_0_PATH));
        registeredImages.put("corner_1", loadCornerImage(CORNER_1_PATH));
        registeredImages.put("corner_2", loadCornerImage(CORNER_2_PATH));
        registeredImages.put("corner_3", loadCornerImage(CORNER_3_PATH));
    
        registeredImages.put("street_0", loadFieldImage(STREET_0_PATH));
        registeredImages.put("street_1", loadFieldImage(STREET_1_PATH));
        registeredImages.put("street_2", loadFieldImage(STREET_2_PATH));
        registeredImages.put("street_3", loadFieldImage(STREET_3_PATH));
        registeredImages.put("street_4", loadFieldImage(STREET_4_PATH));
        registeredImages.put("street_5", loadFieldImage(STREET_5_PATH));
        registeredImages.put("street_6", loadFieldImage(STREET_6_PATH));
        registeredImages.put("street_7", loadFieldImage(STREET_7_PATH));
        
        registeredImages.put("supply_0", loadFieldImage(SUPPLY_0_PATH));
        registeredImages.put("supply_1", loadFieldImage(SUPPLY_1_PATH));
        registeredImages.put("station", loadFieldImage(STATION_PATH));
        registeredImages.put("card_0", loadFieldImage(CARD_0_PATH));
        registeredImages.put("card_1", loadFieldImage(CARD_1_PATH));
        registeredImages.put("tax_0", loadFieldImage(TAX_0_PATH));
        registeredImages.put("tax_1", loadFieldImage(TAX_1_PATH));
    
        registeredStrings.put("field_1_name", "WARSCHAUER STRASSE");
        registeredStrings.put("field_3_name", "GEMEINSCHAFTSKARTE");
        
        registeredStrings.put("corner_0_name", "LOS");
        registeredStrings.put("corner_1_name", "Im Gefängnis");
        registeredStrings.put("corner_1_text", "Nur zu Besuch");
        registeredStrings.put("corner_2_name", "Frei Parken");
        registeredStrings.put("corner_3_name", "Gehen Sie ins Gefängnis");
    }
    
    public static void loadData()
    {
        loadCards();
        loadFields();
    }
    
    public static void loadFx() {
        loadFxIcons();
        loadFxImages();
    }
    
    private static Image loadImage(String path, double requestedWidth, double requestedHeight) {
        return new Image(R + path, requestedWidth, requestedHeight, true, true);
    }
    
    private static Image loadImage(String path) {
        return new Image(R + path);
    }
    
    private static Image loadCornerImage(String path) {
        return loadImage(path, CORNER_TEXTURE_WIDTH, CORNER_TEXTURE_HEIGHT);
    }
    
    private static Image loadFieldImage(String path) {
        return loadImage(path, FIELD_TEXTURE_WIDTH, FIELD_TEXTURE_HEIGHT);
    }
    
    private static ImageView loadImageView(String path) {
        return new ImageView(loadImage(path));
    }
    
    public void registerString(String key, String val) {
        registeredStrings.put(key, val.toLowerCase());
    }
    
    public static Image getImage(String name) {
        return registeredImages.get(name);
    }
    
    public static Image getImage(FieldTypes type) {
        return registeredImages.get(type.toString().toLowerCase());
    }
    
    public static ImageView getIcon(String name) {
        return registeredIcons.get(name);
    }
    
    public static String getString(String key) {
        return registeredStrings.getOrDefault(key.toLowerCase(), UNDEFINED);
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
