package de.btu.monopoly.core;

import static de.btu.monopoly.core.Game.LOGGER;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.*;

/**
 *
 * @author Christian Prinz
 */
public class FieldManager {

    /**
     * Sammlung aller Nachbar-Ids in aufsteigender Reihenfolge. Der Erste beider Indizes steht immer für eine eigene Property
     * (Bahnhof, Werk oder Straße). Die Zuordnung ist <bold>nicht</bold> 1:1 zu {@code GameBoard.FIELD_STRUCTURE}, d.h. die IDs an
     * der Stelle {@code i} stehen hier nicht für die Nachbarn des Feldes mit dem Index {@code i}, sondern für das {@code i}-te
     * Property Feld. Die Aufzählung beginnt bei der Ersten Straße und schreitet dann im Uhrzeigersinn fort.
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
        PlayerService.toJail(player);
    }

    /**
     * Bewegt einen Spieler um die festgelegte Anzahl Feldern.
     *
     * @param player Spieler
     * @param amount Anzahl Felder
     * @param goFieldAmount Geld welches beim Ueberqueren des LOS Feldes bekommen wird
     */
    public void movePlayer(Player player, int amount, int goFieldAmount) {
        int newPos = PlayerService.movePlayer(player, amount);
        if (newPos > fields.length) {
            player.setPosition(newPos % fields.length);
            PlayerService.giveMoney(player, goFieldAmount);
        }
    }

    /**
     * Kauft ein Grundstück, sofern der Spieler zahlungsfähig ist.
     *
     * @param player Käufer
     * @param price des Grundstueckes (der sich bei einer Auktion aendern kann)
     * @return true, wenn das Feld gekauft wurde, false sonst
     */
    public boolean buyProperty(Player player, Property prop, int price) {

        if (PlayerService.checkLiquidity(player, price)) {
            LOGGER.info(String.format("%s kauft das Grundstück %s", player.getName(), prop.getName()));
            prop.setOwner(player);
            PlayerService.takeMoneyUnchecked(player, price);
            return true;
        } else {
            LOGGER.warning(String.format("%s hat nicht genug Geld, um %s zu kaufen!", player.getName(), prop.getName()));
            return false;
        }
    }

    /**
     * Kauft ein Haus auf dem gewählten Feld, sofern es ein Straßenfeld ist und der Spieler genug Geld hat.
     *
     * @return true, wenn der Kauf erfolgreich war, false sonst
     */
    public boolean buyHouse(StreetField street) {
        Player player = street.getOwner();

        LOGGER.info(String.format("%s versucht, ein Haus auf %s zu kaufen.", player.getName(), street.getName()));
        if (balanceCheck(street, 1, 0)
                && PlayerService.checkLiquidity(player, street.getHousePrice())) {
            if (street.getHouseCount() < 5) {
                buyHouseUnchecked(street);
                return true;
            } else {
                LOGGER.warning(String.format("Auf %s steht bereits die maximale Anzahl an Haeusern.", street.getName()));
            }
        }
        return false;
    }

    /**
     * Kauft ein Haus auf dem gewählten Feld ohne auf die gegebenen Umstände zu prüfen.
     *
     * @param street Straßenfeld
     */
    public void buyHouseUnchecked(StreetField street) {
        Player player = street.getOwner();
        PlayerService.takeMoneyUnchecked(player, street.getHousePrice());
        street.setHouseCount(street.getHouseCount() + 1);
    }

    /**
     * Verkauft ein Haus auf dem gewählten Feld, sofern es ein Straßenfeld ist und bereits bebaut wurde.
     *
     * @return true, wenn der Verkauf erfolgreich war, false sonst
     */
    public boolean sellHouse(StreetField street) {
        Player player = street.getOwner();

        LOGGER.info(String.format("%s versucht, ein Haus auf %s zu verkaufen.", player.getName(), street.getName()));
        if (balanceCheck(street, 0, 1)) {
            if (street.getHouseCount() > 0) {
                sellHouseUnchecked(street);
                return true;
            } else {
                LOGGER.warning(String.format("Auf %s stehen keine Haeuser.", street.getName()));
            }
        }
        return false;
    }

    /**
     * Verkauft ein Haus auf dem gewählten Feld ohne auf die gegebenen Umstände zu prüfen.
     *
     * @param street Die betroffene Straße
     */
    public void sellHouseUnchecked(StreetField street) {
        Player player = street.getOwner();
        PlayerService.giveMoney(player, street.getHousePrice());
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

    public boolean isComplete(Property prop) {
        Player owner = prop.getOwner();
        int[] neighbourIds = NEIGHBOUR_IDS[getPropertyId(prop)];
        for (Integer neigh : neighbourIds) {
            if (((Property) fields[neigh]).getOwner() != owner) {
                return false;
            }
        }
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

    /**
     * Nimmt eine Hypothek auf das gewählte Propery-Feld auf, soweit möglich.
     *
     * @return true, bei Erfolg, sonst false
     */
    public boolean takeMortgage(Property prop) {
        Player player = prop.getOwner();

        LOGGER.info(String.format("%s versucht eine Hypothek fuer %s aufzunehmen.", player.getName(), prop.getName()));
        if (prop instanceof StreetField) {
            StreetField street = (StreetField) prop;
            if (street.getHouseCount() != 0) {
                LOGGER.warning(String.format("Auf %s stehen Haeuser! Hypothek kann nicht aufgenommen werden.", street.getName()));
                return false;
            } else if (!balanceCheck(street, 0, 0)) // Hier gilt houseCount = 0
            {
                LOGGER.warning(String.format("Auf dem Straßenzug von %s stehen Haeuser! Hypothek kann nicht aufgenommen werden.",
                        street.getName()));
                return false;
            }
        }
        takeMortgageUnchecked(prop);
        return true;
    }

    /**
     * Nimmt eine Hypothek fürs betroffene Feld auf, ohne auf den Hypotheksstatus zu prüfen.
     *
     * @param prop betroffenes Feld
     */
    public void takeMortgageUnchecked(Property prop) {
        Player player = prop.getOwner();
        PlayerService.giveMoney(player, prop.getMortgageValue());
        prop.setMortgageTaken(true);
        LOGGER.info(String.format("Hypothek für %s wurde aufgenommen!", player.getName()));
    }

    /**
     * Zahlt eine Hypothek auf das gewählte Propery-Feld ab, soweit möglich.
     *
     * @param prop betroffenes Feld
     */
    public void payMortgage(Property prop) {
        Player player = prop.getOwner();

        LOGGER.info(String.format("%s versucht, eine Hypthek auf %s abzuzahlen", player.getName(), prop.getName()));
        if (prop.isMortgageTaken()) {
            if (PlayerService.checkLiquidity(player, prop.getMortgageBack())) {
                payMortgageUnchecked(prop);
            } else {
                LOGGER.warning(String.format("Hypothek fuer %s wurde nicht zurueckgezahlt.", prop.getName()));
            }
        } else {
            LOGGER.fine(String.format("Auf dem Feld %s lastet keine Hypothek.", prop.getName()));
        }
    }

    /**
     * Zahlt die Hypothek auf einem Feld ab, ohne auf Zahlungsfähigkeit zu prüfen.
     *
     * @param prop betroffenes Feld
     */
    public void payMortgageUnchecked(Property prop) {
        Player player = prop.getOwner();
        PlayerService.takeMoneyUnchecked(player, prop.getMortgageBack());
        prop.setMortgageTaken(false);
        LOGGER.info(String.format("Hypothek fuer %s wurde zurueckgezahlt!", prop.getName()));
    }

}
