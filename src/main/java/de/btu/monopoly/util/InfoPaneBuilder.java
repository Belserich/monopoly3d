package de.btu.monopoly.util;

import de.btu.monopoly.core.FieldTypes;
import de.btu.monopoly.data.field.*;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class InfoPaneBuilder {
    
    private static final Street streetBuilder = new Street();
    private static final Station stationBuilder = new Station();
    private static final Supply supplyBuilder = new Supply();
    
    public static Pane buildFor(Field field, FieldTypes type) {
        
        if (type.isStreet()) {
            return streetBuilder.build((StreetField) field, type);
        }
        else if (type.isStation()) {
            return stationBuilder.build((StationField) field, type);
        }
        else if (type.isSupply()) {
            return supplyBuilder.build((SupplyField) field, type);
        }
        else {
            throw new RuntimeException(String.format("No info for field type %s available.", type));
        }
    }
    
    private static abstract class Base<T> {
    
        private final HashMap<T, Pane> builtPanes;
        
        Base() {
            builtPanes = new HashMap<>();
        }
        
        Pane build(T field, FieldTypes type) {
            return builtPanes.get(field);
        }
    
        Text createText(String str, Font font) {
            Text text = new Text(str);
            text.setTextAlignment(TextAlignment.CENTER);
            text.setFont(font);
            text.setPickOnBounds(false);
            return text;
        }
    
        Text createText(String str, String fontFamily, double fontSize) {
            return createText(str, Font.font(fontFamily, fontSize));
        }
    
        Text createText(String str, double fontSize) {
            return createText(str, null, fontSize);
        }
    
        Text createText(String str) {
            return createText(str, null, 12);
        }
    
        Text createText(int value) {
            return createText(String.valueOf(value), null, 12);
        }
        
        VBox createBaseBox(Insets insets, String imgLabel) {
            
            VBox box = new VBox();
            box.setAlignment(Pos.TOP_CENTER);
            box.setPadding(insets);
            box.setPickOnBounds(false);
            box.setSpacing(8);
            
            Image img = Assets.getImage(imgLabel);
            BackgroundImage backImg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            box.setBackground(new Background(backImg));
            box.setMinSize(img.getWidth(), img.getHeight());
            box.setMaxSize(img.getWidth(), img.getHeight());
            
            return box;
        }
    }
    
    private static abstract class Property<T extends PropertyField> extends Base<T> {
        
        GridPane createInfoPane() {
            
            GridPane infoPane = new GridPane();
            infoPane.setPadding(new Insets(3, 17, 0, 17));
    
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setPercentWidth(80);
            ColumnConstraints constraints1 = new ColumnConstraints();
            constraints1.setPercentWidth(20);
            constraints1.setHalignment(HPos.RIGHT);
            infoPane.getColumnConstraints().addAll(constraints, constraints1);
            
            return infoPane;
        }
        
        Text createMortgageText(PropertyField field) {
            return createText(String.format("Hypothekswert %d", field.getMortgageValue()),
                    Font.font(null, FontWeight.BOLD, 12));
        }
        
        void addNameText(VBox box, String name) {
            name = name.replace('-', '\n').toUpperCase();
            box.getChildren().add(createText(name, Font.font("Kabel", 17)));
            if (name.contains("\n")) {
                box.setPadding(new Insets(12, 0, 0, 0));
                box.setSpacing(2);
            }
        }
    }
    
    private static class Street extends Property<StreetField> {
        
        @Override
        protected Pane build(StreetField field, FieldTypes type) {
            
            Pane pane = super.build(field, type);
            if (pane != null) {
                return pane;
            }
            
            String imgLabel = String.format("%s_info", type.name().replace(' ', '_').toLowerCase());
            VBox box = createBaseBox(new Insets(18, 0, 0, 0), imgLabel);
            ObservableList<Node> children = box.getChildren();
            
            Text supText = createText("Besitzrechtkarte".toUpperCase());
            
            GridPane infoPane = createInfoPane();
            infoPane.addRow(0, createText("Grundstückspreis"), createText(field.getPrice()));
            infoPane.addRow(1, createText("Miete Grundstück allein"), createText(field.getRent(0)));
            infoPane.addRow(2, createText("\t- mit 1 Haus"), createText(field.getRent(1)));
            infoPane.addRow(3, createText("\t- mit 2 Haus"), createText(field.getRent(2)));
            infoPane.addRow(4, createText("\t- mit 3 Haus"), createText(field.getRent(3)));
            infoPane.addRow(5, createText("\t- mit 4 Haus"), createText(field.getRent(4)));
            infoPane.addRow(6, createText("\t- mit HOTEL"), createText(field.getRent(5)));
            infoPane.addRow(7, createText("Hypothekswert"), createText(field.getMortgageValue()));
            infoPane.addRow(8, createText("Haus-/Hotelpreis"), createText(field.getHousePrice()));
            
            Text doubleRentText = createText("Wenn ein Spieler alle Grundstücke einer Farbengruppe besitzt, " +
                    "so ist die Miete auf den unbebauten Grundstücken dieser Farbe doppelt so hoch.", 10);
            doubleRentText.setWrappingWidth(160);
            doubleRentText.setTextAlignment(TextAlignment.CENTER);
    
            VBox subBox = new VBox(infoPane, new Line(0, 0, 160, 0), doubleRentText);
            subBox.setPadding(new Insets(7, 0, 0, 0));
            subBox.setSpacing(5);
            subBox.setAlignment(Pos.CENTER);
            
            children.add(supText);
            addNameText(box, field.getName());
            children.add(subBox);
            return box;
        }
    }
    
    private static class Station extends Property<StationField> {
        
        @Override
        protected Pane build(StationField field, FieldTypes type) {
            
            Pane pane = super.build(field, type);
            if (pane != null) {
                return pane;
            }
            
            VBox box = createBaseBox(new Insets(145, 0, 0, 0), "station_info");
            ObservableList<Node> children = box.getChildren();
            
            GridPane infoPane = createInfoPane();
            infoPane.addRow(0, createText("Miete".toUpperCase()), createText(field.getRent(0)));
            infoPane.addRow(1, createText("Wenn man \n2 Bahnhöfe besitzt", 10), createText(field.getRent(1)));
            infoPane.addRow(2, createText("Wenn man \n3 Bahnhöfe besitzt", 10), createText(field.getRent(2)));
            infoPane.addRow(3, createText("Wenn man \n4 Bahnhöfe besitzt", 10), createText(field.getRent(3)));
    
            addNameText(box, field.getName());
            children.addAll(infoPane, createMortgageText(field));
            return box;
        }
    }
    
    private static class Supply extends Property<SupplyField> {
        
        @Override
        protected Pane build(SupplyField field, FieldTypes type) {
            
            Pane pane = super.build(field, type);
            if (pane != null) {
                return pane;
            }
            
            String imgLabel = String.format("%s_info", type.name().replace(' ', '_').toLowerCase());
            VBox box = createBaseBox(new Insets(148, 0, 0, 0), imgLabel);
            ObservableList<Node> children = box.getChildren();
            
            Text nameText = createText(field.getName(), Font.font("Kabel", FontWeight.BOLD, 14));
    
            Text desc1 = createText("Wenn man Besitzer eines Elektrizitätswerks ist, " +
                    "so ist die Miete 4-mal so hoch, wie Augen auf den zwei Würfeln sind.", 9);
            desc1.setWrappingWidth(160);
    
            Text desc2 = createText("Wenn man Besitzer beider Versorgungswerke ist, " +
                    "so ist die Miete 10-mal so hoch, wie Augen auf den zwei Würfeln sind.", 9);
            desc2.setWrappingWidth(160);
    
            children.addAll(nameText, desc1, desc2, createMortgageText(field));
            return box;
        }
    }
}
