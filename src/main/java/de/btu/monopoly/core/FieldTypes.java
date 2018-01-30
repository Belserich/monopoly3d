package de.btu.monopoly.core;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public enum FieldTypes {
    
    CORNER,
    CORNER_0,
    CORNER_1,
    CORNER_2,
    CORNER_3,
    STREET_0,
    STREET_1,
    STREET_2,
    STREET_3,
    STREET_4,
    STREET_5,
    STREET_6,
    STREET_7,
    CARD_0,
    CARD_1,
    TAX_0,
    TAX_1,
    STATION,
    SUPPLY,
    SUPPLY_0,
    SUPPLY_1;
    
    public static final FieldTypes[] GAMEBOARD_FIELD_STRUCT = {
            CORNER_0, STREET_0, CARD_0, STREET_0, TAX_0, STATION, STREET_1, CARD_1, STREET_1, STREET_1,
            CORNER_1, STREET_2, SUPPLY_0, STREET_2, STREET_2, STATION, STREET_3, CARD_0, STREET_3, STREET_3,
            CORNER_2, STREET_4, CARD_1, STREET_4, STREET_4, STATION, STREET_5, STREET_5, SUPPLY_1, STREET_5,
            CORNER_3, STREET_6, STREET_6, CARD_0, STREET_6, STATION, CARD_1, STREET_7, TAX_1, STREET_7
    };
    
    public boolean isStreet()
    {
        return this.toString().startsWith("STREET");
    }
    
    public boolean isCorner()
    {
        return this.toString().startsWith("CORNER");
    }
    
    public boolean isCard()
    {
        return this.toString().startsWith("CARD");
    }
    
    public boolean isTax()
    {
        return this.toString().startsWith("TAX");
    }
    
    public boolean isStation()
    {
        return this.toString().startsWith("STATION");
    }
    
    public boolean isSupply() {
        return this.toString().startsWith("SUPPLY");
    }
    
    public boolean isProperty() {
        return isStreet() || isStation() || isSupply();
    }
    
    public boolean is(FieldTypes type) {
        return this.name().matches(".*" + type.name() + ".*");
    }
}
