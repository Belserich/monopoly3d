package de.btu.monopoly.util;

import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.parser.CardDataParser;
import de.btu.monopoly.data.parser.FieldDataParser;
import de.btu.monopoly.ui.fx3d.Fx3dGameBoard;
import de.btu.monopoly.ui.fx3d.FxHelper;
import de.btu.monopoly.ui.fx3d.MaterialBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;

import static de.btu.monopoly.ui.fx3d.Fx3dField.*;

public class Assets
{
    public static final int START_MONEY = 2000;
    
    public static final String FONT_KABEL_FAMILY = "Kabel-Heavy";
    
    private static final String ROOT_DIR = "/images/new/";
    
    private static final String IMG_DIR = ROOT_DIR + "images/";
    private static final String FONT_DIR = ROOT_DIR + "fonts/";
    private static final String DATA_DIR = "/data/";
    private static final String ICON_DIR = ROOT_DIR + "icons/";
    
    private static final double GAMEBOARD_TEXTURE_WIDTH = Fx3dGameBoard.BOARD_MODEL_LENGTH * 2 + FIELD_HEIGHT * 2;
    private static final double GAMEBOARD_TEXTURE_HEIGHT = Fx3dGameBoard.BOARD_MODEL_LENGTH + FIELD_HEIGHT * 2;
    
    private static final String FONT_KABEL_PATH = "kabel.ttf";
    
    private static final String ICON_3D_PATH = "3d_rotation.png";
    private static final String ICON_DICE_1_PATH = "dice_1.png";
    
    private static final String BOARD_PATH = "game_board.png";
    
    private static final String INFO_STREET_0_PATH = "street_0_info.png";
    private static final String INFO_STREET_1_PATH = "street_1_info.png";
    private static final String INFO_STREET_2_PATH = "street_2_info.png";
    private static final String INFO_STREET_3_PATH = "street_3_info.png";
    private static final String INFO_STREET_4_PATH = "street_4_info.png";
    private static final String INFO_STREET_5_PATH = "street_5_info.png";
    private static final String INFO_STREET_6_PATH = "street_6_info.png";
    private static final String INFO_STREET_7_PATH = "street_7_info.png";
    
    private static final String INFO_STATION_PATH = "station_info.png";
    private static final String INFO_SUPPLY_0_PATH = "supply_0_info.png";
    private static final String INFO_SUPPLY_1_PATH = "supply_1_info.png";
    
    private static final String STREET_0_PATH = "street_0.png";
    private static final String STREET_1_PATH = "street_1.png";
    private static final String STREET_2_PATH = "street_2.png";
    private static final String STREET_3_PATH = "street_3.png";
    private static final String STREET_4_PATH = "street_4.png";
    private static final String STREET_5_PATH = "street_5.png";
    private static final String STREET_6_PATH = "street_6.png";
    private static final String STREET_7_PATH = "street_7.png";
    
    private static final String CORNER_0_PATH = "corner_0.png";
    private static final String CORNER_1_PATH = "corner_1.png";
    private static final String CORNER_2_PATH = "corner_2.png";
    private static final String CORNER_3_PATH = "corner_3.png";
    
    private static final String SUPPLY_0_PATH = "supply_0.png";
    private static final String SUPPLY_1_PATH = "supply_1.png";
    private static final String STATION_PATH = "station.png";
    private static final String CARD_0_PATH = "card_0.png";
    private static final String CARD_1_PATH = "card_1.png";
    private static final String TAX_0_PATH = "tax_0.png";
    private static final String TAX_1_PATH = "tax_1.png";
    
    private static final String DATA_FIELD_PATH = "field_data.xml";
    
    private static final String COMM_CARDS_PATH = "community_card_data.xml";
    private static final String EVENT_CARDS_PATH = "event_card_data.xml";
    
    private static final HashMap<String, Image> registeredImages = new HashMap<>();
    private static final HashMap<String, ImageView> registeredIcons = new HashMap<>();
    
    private static Field[] fields;
    
    private static CardStack commCards;
    private static CardStack eventCards;
    
    public static void loadFonts() {
        loadFont(FONT_KABEL_PATH);
    }
    
    public static void loadCards() {
    
        try {
            commCards = CardDataParser.parse(DATA_DIR + COMM_CARDS_PATH);
            eventCards = CardDataParser.parse(DATA_DIR + EVENT_CARDS_PATH);
        }
        catch (IOException | SAXException | ParserConfigurationException ex) {
            throw new RuntimeException("Failed to load card data.", ex);
        }
    }
    
    public static void loadFields() {
        try {
            fields = FieldDataParser.parse(DATA_DIR + DATA_FIELD_PATH);
        }
        catch (IOException | SAXException | ParserConfigurationException ex) {
            throw new RuntimeException("Failed to load field data.", ex);
        }
    }
    
    public static void loadFxIcons() {
        
        registeredIcons.put("3d_icon", loadIcon(ICON_3D_PATH));
        registeredIcons.put("dice_1", loadIcon(ICON_DICE_1_PATH));
    }
    
    public static void loadFxImages() {
    
        Image boardImage = loadImage(BOARD_PATH, GAMEBOARD_TEXTURE_WIDTH, GAMEBOARD_TEXTURE_HEIGHT);
        boardImage = FxHelper.replaceColorInImage(boardImage, Color.TRANSPARENT, MaterialBuilder.DEFAULT_BACKGROUND_FILL);
        registeredImages.put("game_board", boardImage);
        
        registeredImages.put("street_0_info", loadImage(INFO_STREET_0_PATH, FIELD_WIDTH * 2, FIELD_DEPTH * 2));
        registeredImages.put("street_1_info", loadImage(INFO_STREET_1_PATH, FIELD_WIDTH * 2, FIELD_DEPTH * 2));
        registeredImages.put("street_2_info", loadImage(INFO_STREET_2_PATH, FIELD_WIDTH * 2, FIELD_DEPTH * 2));
        registeredImages.put("street_3_info", loadImage(INFO_STREET_3_PATH, FIELD_WIDTH * 2, FIELD_DEPTH * 2));
        registeredImages.put("street_4_info", loadImage(INFO_STREET_4_PATH, FIELD_WIDTH * 2, FIELD_DEPTH * 2));
        registeredImages.put("street_5_info", loadImage(INFO_STREET_5_PATH, FIELD_WIDTH * 2, FIELD_DEPTH * 2));
        registeredImages.put("street_6_info", loadImage(INFO_STREET_6_PATH, FIELD_WIDTH * 2, FIELD_DEPTH * 2));
        registeredImages.put("street_7_info", loadImage(INFO_STREET_7_PATH, FIELD_WIDTH * 2, FIELD_DEPTH * 2));
        
        registeredImages.put("station_info", loadImage(INFO_STATION_PATH, FIELD_WIDTH * 2, FIELD_DEPTH * 2));
        registeredImages.put("supply_0_info", loadImage(INFO_SUPPLY_0_PATH, FIELD_WIDTH * 2, FIELD_DEPTH * 2));
        registeredImages.put("supply_1_info", loadImage(INFO_SUPPLY_1_PATH, FIELD_WIDTH * 2, FIELD_DEPTH * 2));
    
        registeredImages.put("street_0", loadImage(STREET_0_PATH));
        registeredImages.put("street_1", loadImage(STREET_1_PATH));
        registeredImages.put("street_2", loadImage(STREET_2_PATH));
        registeredImages.put("street_3", loadImage(STREET_3_PATH));
        registeredImages.put("street_4", loadImage(STREET_4_PATH));
        registeredImages.put("street_5", loadImage(STREET_5_PATH));
        registeredImages.put("street_6", loadImage(STREET_6_PATH));
        registeredImages.put("street_7", loadImage(STREET_7_PATH));
        
        registeredImages.put("corner_0", loadImage(CORNER_0_PATH));
        registeredImages.put("corner_1", loadImage(CORNER_1_PATH));
        registeredImages.put("corner_2", loadImage(CORNER_2_PATH));
        registeredImages.put("corner_3", loadImage(CORNER_3_PATH));
        
        registeredImages.put("supply_0", loadImage(SUPPLY_0_PATH));
        registeredImages.put("supply_1", loadImage(SUPPLY_1_PATH));
        registeredImages.put("station", loadImage(STATION_PATH));
        registeredImages.put("card_0", loadImage(CARD_0_PATH));
        registeredImages.put("card_1", loadImage(CARD_1_PATH));
        registeredImages.put("tax_0", loadImage(TAX_0_PATH));
        registeredImages.put("tax_1", loadImage(TAX_1_PATH));
    }
    
    public static void loadData() {
        loadFonts();
        loadCards();
        loadFields();
    }
    
    public static void loadFx() {
        loadFxIcons();
        loadFxImages();
    }
    
    public static Font loadFont(String path) {
        return Font.loadFont(Assets.class.getResource(FONT_DIR + path).toExternalForm(), 12);
    }
    
    private static Image loadImage(String path, double requestedWidth, double requestedHeight) {
        return new Image(IMG_DIR + path, requestedWidth, requestedHeight, true, true);
    }
    
    private static Image loadImage(String path) {
        return new Image(IMG_DIR + path);
    }
    
    private static ImageView loadIcon(String path) {
        return new ImageView(ICON_DIR + path);
    }
    
    public static Image getImage(String name) {
        return registeredImages.get(name);
    }
    
    public static ImageView getIcon(String name) {
        return registeredIcons.get(name);
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
