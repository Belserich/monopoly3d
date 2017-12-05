package de.btu.monopoly.data.field;

import com.sun.istack.internal.NotNull;
import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.service.FieldService;
import de.btu.monopoly.core.service.PlayerService;
import de.btu.monopoly.data.player.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author Christian Prinz
 */
public class FieldManager {
    
    private static final Logger LOGGER = Logger.getLogger(FieldService.class.getCanonicalName());

    /**
     * Die Felder des Spielbretts
     */
    private final Field[] fields;
    
    /**
     * Ordnet Spielern die PropertyField-Felder in ihrem Besitz zu. Vereinfacht die Logik mancher Karten
     */
    private Map<Player, Integer[]> houseCounters;
    
    /**
     * Diese Manager-Klasse verwaltet das Feld-Array eines Spielbretts
     *
     * @param fields Feld-Array
     */
    public FieldManager(Field[] fields) {
        this.fields = fields;
        Arrays.asList(fields).forEach(f -> f.fieldManager = this);
        houseCounters = new HashMap<>();
    }
    
    /**
     * @return Feld-Array
     */
    @NotNull
    public Field[] getFields() {
        return fields;
    }
    
    /**
     * @return das "LOS"-Feld
     */
    public GoField getGoField() {
        return (GoField) fields[FieldService.GO_FIELD_ID];
    }
    
    /**
     * Gibt die Gesamtanzahl der Häuser im Spielerbesitz zurück.
     *
     * @param player Spieler
     * @return Gesamtanzahl der Häuser im Spielerbesitz
     */
    public int getHouseCount(Player player) {
        return houseCounters.getOrDefault(player, new Integer[1])[0];
    }
    
    /**
     * Gibt die Gesamtanzahl der Hotels im Spielerbesitz zurück.
     *
     * @param player Spieler
     * @return Gesamtanzahl der Hotels im Spielerbesitz
     */
    public int getHotelCount(Player player) {
        return houseCounters.getOrDefault(player, new Integer[2])[1];
    }

    /**
     * Bewegt einen Spieler um die festgelegte Anzahl Feldern.
     *
     * @param player Spieler
     * @param amount Anzahl Felder
     */
    @NotNull
    public Field movePlayer(Player player, int amount) {
        int pos = PlayerService.movePlayer(player, amount);
        if (pos >= fields.length) {
            pos %= fields.length;
            player.setPosition(pos);
            PlayerService.giveMoney(player, getGoField().getAmount());
        }
        return fields[pos];
    }
    
    /**
     * Bewegt den Spieler zum nächsten Feld des angegebenen Typs. Wird für einige Karten benötigt.
     *
     * @param player Spieler
     * @param nextFieldType Feldtyp
     * @return das neue Feld auf dem sich der Spieler befindet
     */
    @NotNull
    public Field movePlayer(Player player, GameBoard.FieldType nextFieldType) {
        int pos = player.getPosition();
        int fields = 0;
        while (GameBoard.FIELD_STRUCTURE[pos + fields] != nextFieldType) {
            fields++;
        }
        return movePlayer(player, fields);
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
                if (!street.isMortgageTaken()) {
                    buyHouseUnchecked(street);
                    return true;
                }
                else {
                    LOGGER.warning(String.format("Auf %s lastet eine Hypothek, es kann kein Haus gekauft werden!", street.getName()));
                }
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
    private void buyHouseUnchecked(StreetField street) {
        Player player = street.getOwner();
    
        LOGGER.info(String.format("%s kauft ein Haus auf %s.", player.getName(), street.getName()));
        PlayerService.takeMoneyUnchecked(player, street.getHousePrice());
        
        int newNumHouses = street.getHouseCount() + 1;
        street.setHouseCount(newNumHouses);
        
        Integer[] defaultArray = new Integer[]{ new Integer(0), new Integer(0) };
        Integer[] houseCounter = houseCounters.getOrDefault(player, defaultArray);
        houseCounter[0] += (newNumHouses == 5) ? -5 : 1;
        houseCounter[1] += (newNumHouses == 5) ? 1 : 0;
        houseCounters.put(player, houseCounter);
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
    private void sellHouseUnchecked(StreetField street) {
        Player player = street.getOwner();
        
        LOGGER.info(String.format("%s verkauft ein Haus auf %s.", player.getName(), street.getName()));
        PlayerService.giveMoney(player, street.getHousePrice() / 2);
        
        int newNumHouses = street.getHouseCount() - 1;
        street.setHouseCount(newNumHouses);
    
        Integer[] houseCounter = houseCounters.get(player);
        houseCounter[0] += (newNumHouses == 4) ? 4 : -1;
        houseCounter[1] += (newNumHouses == 4) ? -1 : 0;
        houseCounters.put(player, houseCounter);
    }

    /**
     * Prüft auf gleichmäßige Bebauung eines Straßenzugs innerhalb bestimmter Toleranzgrenzen.
     *
     * @param street betroffene Straße
     * @param posTolerance obere Toleranzgrenze
     * @param negTolerance untere Toleranzgrenze
     * @return ob die Straße innerhalb der Toleranzgrenzen gleichmäßig bebaut wurde
     */
    public boolean balanceCheck(StreetField street, int posTolerance, int negTolerance) {
        int hc = street.getHouseCount();
        int[] neighbours = FieldService.NEIGHBOUR_IDS[getPropertyId(street)];
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
     * Prüft ob alle Straßen des betroffenen Straßenzugs demselben Spieler gehören.
     *
     * @param prop betroffenes Grundstück
     * @return ob alle Straßen des betroffenen Straßenzugs demselben Spieler gehören
     */
    public boolean isComplete(PropertyField prop) {
        Player owner = prop.getOwner();
        int[] neighbourIds = FieldService.NEIGHBOUR_IDS[getPropertyId(prop)];
        for (Integer neighId : neighbourIds) {
            PropertyField neigh = (PropertyField) fields[neighId];
            if (neigh.getOwner() != owner && !neigh.isMortgageTaken()) {
                return false;
            }
        }
        return true;
    }

    /**
     * siehe {@code NEIGHBOUR_IDS}
     *
     * @param prop betroffenes Feld
     * @return Anzahl der PropertyField Felder vor diesem +1
     */
    private int getPropertyId(PropertyField prop) {
        int propertyId = -1;
        for (Field field : fields) {
            if (field instanceof PropertyField) {
                propertyId++;
                if (field == prop) {
                    return propertyId;
                }
            }
        }
        if (propertyId > 0 && propertyId < FieldService.NEIGHBOUR_IDS.length) {
            throw new IllegalArgumentException("Given property is not a field of the current board.");
        }
        return propertyId;
    }
    
    public Stream<PropertyField> getOwnedNeighbours(PropertyField prop) {
        Player owner = prop.getOwner();
        Objects.requireNonNull(owner);
        
        Stream.Builder<PropertyField> builder = Stream.builder();
        int id = getPropertyId(prop);
        
        int[] neighbourIds = FieldService.NEIGHBOUR_IDS[id];
        for (Integer neighId : neighbourIds) {
            if (neighId != id) {
                PropertyField neigh = (PropertyField) fields[neighId];
                if (neigh.getOwner() == owner) {
                    builder.accept(neigh);
                }
            }
        }
        return builder.build();
    }
    
    /**
     * Setzt alle gekauften Gebäude des Spielers zurück
     *
     * @param player betroffener Spieler
     */
    public void bankrupt(Player player) {
        for (Field field : fields) {
            
            if (field instanceof PropertyField) {
                PropertyField prop = (PropertyField) field;
                
                if (prop.getOwner() == player) {
                    prop.setOwner(null);
                    prop.setMortgageTaken(false);
                    
                    if (prop instanceof StreetField) {
                        StreetField street = (StreetField) prop;
                        street.setHouseCount(0);
                        houseCounters.remove(player);
                    }
                }
            }
        }
    }
    
    /**
     * Nimmt eine Hypothek auf das gewählte Propery-Feld auf, soweit möglich.
     *
     * @param prop betroffenes Grundstück
     */
    public void takeMortgage(PropertyField prop) {
        Player player = prop.getOwner();

        // TODO
        LOGGER.info(String.format("%s versucht eine Hypothek fuer %s aufzunehmen.", player.getName(), prop.getName()));
        if (prop instanceof StreetField) {
            StreetField street = (StreetField) prop;
            if (street.getHouseCount() != 0) {
                LOGGER.warning(String.format("Auf %s stehen Haeuser! Hypothek kann nicht aufgenommen werden.", street.getName()));
            }
            else if (!balanceCheck(street, 0, 0)) // Hier gilt houseCount = 0
            {
                LOGGER.warning(String.format("Auf dem Straßenzug von %s stehen Haeuser! Hypothek kann nicht aufgenommen werden.",
                        street.getName()));
            }
            else if (street.isMortgageTaken()) {
                LOGGER.warning("Es wurde bereits eine Hypothek für dieses gebäude aufgenommen!");
            }
            else takeMortgageUnchecked(prop);
        }
    }
    
    /**
     * Nimmt eine Hypothek fürs betroffene Feld auf, ohne auf den Hypotheksstatus zu prüfen.
     *
     * @param prop betroffenes Feld
     */
    private void takeMortgageUnchecked(PropertyField prop) {
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
    public void payMortgage(PropertyField prop) {
        Player player = prop.getOwner();
        
        LOGGER.info(String.format("%s versucht, eine Hypothek auf %s abzuzahlen.", player.getName(), prop.getName()));
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
    private void payMortgageUnchecked(PropertyField prop) {
        Player player = prop.getOwner();
        PlayerService.takeMoneyUnchecked(player, prop.getMortgageBack());
        prop.setMortgageTaken(false);
        LOGGER.info(String.format("Hypothek fuer %s wurde zurueckgezahlt!", prop.getName()));
    }
}