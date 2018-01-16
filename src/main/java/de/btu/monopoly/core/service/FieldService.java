package de.btu.monopoly.core.service;

import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.field.SupplyField;
import de.btu.monopoly.data.field.TaxField;
import de.btu.monopoly.data.player.Player;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Maximilian Bels (belsmaxi@b-tu.de)
 */
public class FieldService {

    private static final Logger LOGGER = Logger.getLogger(FieldService.class.getCanonicalName());

    /**
     * Sammlung aller Nachbar-Ids in aufsteigender Reihenfolge. Der Erste beider
     * Indizes steht immer für eine eigene PropertyField (Bahnhof, Werk oder
     * Straße). Die Zuordnung ist <bold>nicht</bold> 1:1 zu
     * {@code GameBoard.FIELD_STRUCTURE}, d.h. die IDs an der Stelle {@code i}
     * stehen hier nicht für die Nachbarn des Feldes mit dem Index {@code i},
     * sondern für das {@code i}-te PropertyField. Die Aufzählung beginnt
     * bei der Ersten Straße und schreitet dann im Uhrzeigersinn fort.
     */
    public static final int[][] NEIGHBOUR_IDS = {
        {3}, {1}, {15, 25, 35}, {8, 9}, {6, 9}, {6, 8}, // Erste Reihe
        {13, 14}, {28}, {11, 14}, {11, 13}, {5, 25, 35}, {18, 19}, {16, 19}, {16, 18}, // Zweite Reihe
        {23, 24}, {21, 24}, {23, 21}, {5, 15, 35}, {27, 29}, {26, 29}, {12}, {26, 27}, // Dritte Reihe
        {32, 34}, {31, 34}, {32, 31}, {5, 15, 25}, {39}, {37} // Vierte Reihe
    };
    
    /**
     * Gesamtanzahl der Felder
     */
    public static final int FIELD_COUNT = 40;

    /**
     * ID des "LOS"-Feldes
     */
    public static final int GO_FIELD_ID = 0;
    
    /**
     * ID des "Gefängnis"-Feldes
     */
    public static final int JAIL_FIELD_ID = 10;

    /**
     * Setzt die Spielerposition auf die des Gefängnisfelds und führt
     * {@link PlayerService#toJail(Player)} aus.
     *
     * @param player Spieler
     */
    public static void toJail(Player player) {
        player.setPosition(JAIL_FIELD_ID);
        PlayerService.toJail(player);
    }

    /**
     * Kauft ein Grundstück, sofern der Spieler zahlungsfähig ist.
     *
     * @param player Käufer
     * @param price des Grundstueckes (der sich bei einer Auktion aendern kann)
     * @return true, wenn das Feld gekauft wurde, false sonst
     */
    public static boolean buyPropertyField(Player player, PropertyField prop, int price) {

        StringBuilder builder = new StringBuilder(String.format("%s versucht das Grundstück %s zu kaufen.", player.getName(), prop.getName()));
        boolean retVal = false;

        if (PlayerService.takeMoney(player, price)) {
            builder.append(String.format("%s kauft das Grundstück %s", player.getName(), prop.getName()));
            prop.setOwner(player);
            retVal = true;
        }
        else {
            builder.append(String.format("%s hat nicht genug Geld, um %s zu kaufen!", player.getName(), prop.getName()));
        }
        LOGGER.log(retVal ? Level.INFO : Level.WARNING, builder.toString());

        return retVal;
    }
    
    /**
     * Convenience-Methode fürs Kaufen eines Grundstücks.
     *
     * @see #buyPropertyField(Player, PropertyField, int)
     */
    public static boolean buyPropertyField(Player player, PropertyField prop) {
        return buyPropertyField(player, prop, prop.getPrice());
    }

    /**
     * Berechnet den wahren Mietswert eines Grundstücks unter Berücksichtigung
     * des Würfelergebnisses und eines Multiplikators (für bestimmte Karten).
     *
     * @param prop betroffenes Grundstück
     * @param rollResult Würfelergebnis
     * @param amplifier Multiplikator
     * @return den wahren Mietswert des Grundstücks
     */
    public static int getRent(PropertyField prop, int[] rollResult, int amplifier) {
    
        Objects.requireNonNull(prop);
        Objects.requireNonNull(rollResult);
        
        int rent = prop.getRent();
        if (prop instanceof SupplyField) {
            rent *= (rollResult[0] + rollResult[1]);
        }
        return rent * amplifier;
    }

    /**
     * Hilfsmethode
     *
     * @see #getRent(PropertyField, int[], int)
     */
    public static int getRent(PropertyField prop, int[] rollResult) {
        return getRent(prop, rollResult, 1);
    }

    /**
     * Zieht dem Konto des Spielers Geld in Höhe des Mietswertes des angegebenen
     * PropertyField-Felds ab und gibt das Geld an den Besitzer des Feldes
     * weiter.
     *
     * @param player Spieler
     * @param prop Feld
     * @return ob die Transaktion erfolgreich war, also ob das angegebene Feld
     * einen Besitzer hat
     */
    public static boolean payRent(Player player, PropertyField prop, int[] rollResult, int amplifier) {
        
        Player owner = prop.getOwner();
        if (owner != null && owner == player) {
            
            int rent = getRent(prop, rollResult, amplifier);
            PlayerService.takeAndGiveMoneyUnchecked(player, owner, rent);
            LOGGER.info(String.format("%s zahlt %d Miete an %s.", player.getName(), rent, owner.getName()));
            
            return true;
        }
        else return false;
    }

    /**
     * Hilfsmethode um auf den Fall, der Spieler betritt ein Steuerfeld, zu
     * reagieren.
     *
     * @param player Spieler
     * @param taxField Steuerfeld
     */
    public static void payTax(Player player, TaxField taxField) {
        
        LOGGER.fine(String.format("%s steht auf einem Steuerfeld. Er muss Steuern in Höhe von %d zahlen",
                player.getName(), taxField.getTax()));
        PlayerService.takeMoneyUnchecked(player, taxField.getTax());
    }
}
