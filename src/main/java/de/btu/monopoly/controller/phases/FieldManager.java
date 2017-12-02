package de.btu.monopoly.controller.phases;

import static de.btu.monopoly.controller.GameController.LOGGER;
import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.*;

import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class FieldManager {

    /**
     * Sammlung aller Nachbar-Ids in aufsteigender Reihenfolge. Der Erste beider Indizes steht immer für eine eigene Property
     * (Bahnhof, Werk oder Straße). Die Zuordnung ist <bold>nicht</bold> 1:1 zu {@code GameBoard.FIELD_STRUCTURE}, d.h. die
     * IDs an der Stelle {@code i} stehen hier nicht für die Nachbarn des Feldes mit dem Index {@code i}, sondern für
     * das {@code i}-te Property Feld. Die Aufzählung beginnt bei der Ersten Straße und schreitet dann im Uhrzeigersinn fort.
     */
    private static final int[][] NEIGHBOUR_IDS = {
            {3}, {1}, {15, 25, 35}, {8, 9}, {6, 9}, {6, 8}, // Erste Reihe
            {13, 14}, {28}, {11, 14}, {11, 13}, {5, 25, 35}, {18, 19}, {16, 19}, {16, 18}, // Zweite Reihe
            {23, 24}, {21, 24}, {23, 21}, {5, 15, 35}, {27, 29}, {26, 29}, {12}, {26, 27}, // Dritte Reihe
            {32, 34}, {31, 34}, {32, 31}, {5, 15, 25}, {39}, {37} // Vierte Reihe
    };

    /**
     * Die Felder des Spielbretts
     */
    private final Field[] fields;

    public FieldManager(Field[] fields) {
        this.fields = fields;
    }

    /**
     * Setzt die Spielerposition auf die des Gefängnisfelds und führt {@code PlayerService.toJail()} aus.
     *
     * @param player
     */
    public void toJail(Player player) {
        player.setPosition(10);
        PlayerManager.toJail(player);
    }

    /**
     * Bewegt einen Spieler um die festgelegte Anzahl Feldern.
     *
     * @param player Spieler
     * @param amount Anzahl Felder
     * @return ob der Spieler über LOS gekommen ist
     */
    public boolean movePlayer(Player player, int amount) {
        int newPos = PlayerManager.movePlayer(player, amount);
        if (newPos >= fields.length) {
            player.setPosition(newPos % fields.length);
            return true;
        }
        return false;
    }

    /**
     * Kauft ein Grundstück, sofern der Spieler zahlungsfähig ist.
     *
     * @param player Käufer
     * @param fieldId Die ID des betroffenen Feldes
     * @return true, wenn das Feld gekauft wurde, false sonst
     */
    public boolean buyProperty(Player player, int fieldId) {
        Property prop = getPropertyField(fieldId);
        int price = prop.getPrice();

        if (PlayerManager.checkLiquidity(player, price)) {
            LOGGER.info(String.format("%s kauft das Grundstück %s", player.getName(), prop.getName()));
            prop.setOwner(player);
            PlayerManager.takeMoneyUnchecked(player, price);
            return true;
        }
        else {
            LOGGER.warning(String.format("%s hat nicht genug Geld, um %s zu kaufen!", player.getName(), prop.getName()));
            return false;
        }
    }

    /**
     * Kauft ein Haus auf dem gewählten Feld, sofern es ein Straßenfeld ist und der Spieler genug Geld hat.
     *
     * @param player Käufer
     * @param fieldId Die ID des betroffenen Feldes
     * @return true, wenn der Kauf erfolgreich war, false sonst
     */
    public boolean buyHouse(Player player, int fieldId) {
        StreetField street = getStreetField(fieldId);

        LOGGER.info(String.format("%s versucht, ein Haus auf %s zu kaufen.", player.getName(), street.getName()));
        if (balanceCheck(street, 1, 0)
                && PlayerManager.checkLiquidity(player, street.getHousePrice())) {
            if (street.getHouseCount() < 5) {
                buyHouseUnchecked(player, street);
                return true;
            }
            else {
                LOGGER.warning(String.format("Auf %s steht bereits die maximale Anzahl an Haeusern.", street.getName()));
            }
        }
        return false;
    }

    public void buyHouseUnchecked(Player player, int fieldId) {
        StreetField street = getStreetField(fieldId);
        buyHouseUnchecked(player, street);
    }

    /**
     * Kauft ein Haus auf dem gewählten Feld ohne auf die gegebenen Umstände zu prüfen.
     *
     * @param player Käufer
     * @param street Straßenfeld
     */
    private void buyHouseUnchecked(Player player, StreetField street) {
        PlayerManager.takeMoneyUnchecked(player, street.getHousePrice());
        street.setHouseCount(street.getHouseCount() + 1);
    }
    /**
     * Verkauft ein Haus auf dem gewählten Feld, sofern es ein Straßenfeld ist und bereits bebaut wurde.
     *
     * @param player Käufer
     * @param fieldId Die ID des betroffenen Feldes
     * @return true, wenn der Verkauf erfolgreich war, false sonst
     */

    public boolean sellHouse(Player player, int fieldId) {
        StreetField street = getStreetField(fieldId);

        LOGGER.info(String.format("%s versucht, ein Haus auf %s zu verkaufen.", player.getName(), street.getName()));
        if (balanceCheck(street, 0, 1)) {
            if (street.getHouseCount() > 0) {
                sellHouseUnchecked(player, street);
                return true;
            }
            else {
                LOGGER.warning(String.format("Auf %s stehen keine Haeuser.", street.getName()));
            }
        }
        return false;
    }

    public void sellHouseUnchecked(Player player, int fieldId) {
        StreetField street = getStreetField(fieldId);
        sellHouseUnchecked(player, street);
    }

    /**
     * Verkauft ein Haus auf dem gewählten Feld ohne auf die gegebenen Umstände zu prüfen.
     *
     * @param player Käufer
     * @param street Die betroffene Straße
     */
    private void sellHouseUnchecked(Player player, StreetField street) {
        PlayerManager.giveMoney(player, street.getHousePrice());
        street.setHouseCount(street.getHouseCount() - 1);
    }

    /**
     * Prüft auf gleichmäßige Bebauung eines Straßenzugs innerhalb bestimmter Toleranzgrenzen.
     *
     * @param street betroffene Straße
     * @param posTolerance obere Toleranzgrenze
     * @param negTolerance untere Toleranzgrenze
     * @return ob die Straße innerhalb der Toleranzgrenzen gleichmäßig bebaut wurde
     */
    private boolean balanceCheck(StreetField street, int posTolerance, int negTolerance) {
        int hc = street.getHouseCount();
        int[] neighbours = NEIGHBOUR_IDS[getPropertyId(street)];
        for (Integer neigh : neighbours) {
            int otherHc = ((StreetField) fields[neigh]).getHouseCount();
            if (hc + posTolerance < otherHc || hc - negTolerance > otherHc) {
                LOGGER.warning(String.format("Straßenzug %s unausgeglichen!", street.getName()));
                return false;
            }
        }
        LOGGER.fine(String.format("Balancetest für Straßenzug %s erfolgreich!", street.getName()));
        return true;
    }

    /**
     * siehe {@code NEIGHBOUR_IDS}
     *
     * @param prop betroffenes Feld
     * @return Anzahl der Property Felder vor diesem +1
     */
    private int getPropertyId(Property prop) {
        int propertyId = -1;
        for (Field field : fields) {
            if (field instanceof Property) {
                propertyId++;
                if (field == prop) {
                    return propertyId;
                }
            }
        }
        if (propertyId > 0 && propertyId < NEIGHBOUR_IDS.length) {
            throw new IllegalArgumentException("Given property is not a field of the current board.");
        }
        return propertyId;
    }

    private Property getPropertyField(int fieldId) {
        Field field = fields[fieldId];
        if (field instanceof Property) {
            return (StreetField) field;
        }
        else {
            throw new IllegalArgumentException("Given fieldId does not refer to a valid property field!");
        }
    }

    private StreetField getStreetField(int fieldId) {
        Field field = fields[fieldId];
        if (field instanceof StreetField) {
            return (StreetField) field;
        }
        else {
            throw new IllegalArgumentException("Given fieldId does not refer to a valid street field!");
        }
    }

    /**
     * Nimmt eine Hypothek auf das gewählte Propery-Feld auf, soweit möglich.
     *
     * @param player Spieler
     * @param fieldId ID des betroffenen Feldes
     * @return true, bei Erfolg, sonst false
     */
    public boolean takeMortgage(Player player, int fieldId) {
        Property prop = getPropertyField(fieldId);

        LOGGER.info(String.format("%s versucht eine Hypothek fuer %s aufzunehmen.", player.getName(), prop.getName()));
        if (prop instanceof StreetField) {
            StreetField street = (StreetField) prop;
            if (street.getHouseCount() != 0) {
                LOGGER.warning(String.format("Auf %s stehen Haeuser! Hypothek kann nicht aufgenommen werden.", street.getName()));
                return false;
            }
            else if (!balanceCheck(street, 0, 0)) /* Hier gilt houseCount = 0 */ {
                LOGGER.warning(String.format("Auf dem Straßenzug von %s stehen Haeuser! Hypothek kann nicht aufgenommen werden.",
                        street.getName()));
                return false;
            }
        }
        takeMortgageUnchecked(player, prop);
        return true;
    }

    /**
     * Nimmt eine Hypothek fürs betroffene Feld auf, ohne auf den Hypotheksstatus zu prüfen.
     *
     * @param player Spieler
     * @param prop betroffenes Feld
     */
    private void takeMortgageUnchecked(Player player, Property prop) {
        PlayerManager.giveMoney(player, prop.getMortgageValue());
        prop.setMortgageTaken(true);
        LOGGER.info(String.format("Hypothek für %s wurde aufgenommen!", player.getName()));
    }

    /**
     * Zahlt eine Hypothek auf das gewählte Propery-Feld ab, soweit möglich.
     *
     * @param player Spieler
     * @param fieldId ID des betroffenen Felds
     */
    public void payMortgage(Player player, int fieldId) {
        Property prop = getPropertyField(fieldId);

        LOGGER.info(String.format("%s versucht, eine Hypthek auf %s abzuzahlen", player.getName(), prop.getName()));
        if (prop.isMortgageTaken()) {
            if (PlayerManager.checkLiquidity(player, prop.getMortgageBack())) {
                payMortgageUnchecked(player, prop);
            }
            else {
                LOGGER.warning(String.format("Hypothek fuer %s wurde nicht zurueckgezahlt.", prop.getName()));
            }
        }
        else {
            LOGGER.fine(String.format("Auf dem Feld %s lastet keine Hypothek.", prop.getName()));
        }
    }

    /**
     * Zahlt die Hypothek auf einem Feld ab, ohne auf Zahlungsfähigkeit zu prüfen.
     *
     * @param player Spieler
     * @param prop betroffenes Feld
     */
    private void payMortgageUnchecked(Player player, Property prop) {
        PlayerManager.takeMoneyUnchecked(player, prop.getMortgageBack());
        prop.setMortgageTaken(false);
        LOGGER.info(String.format("Hypothek fuer %s wurde zurueckgezahlt!", prop.getName()));
    }
}
