/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ki;

import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.service.AuctionService;
import de.btu.monopoly.data.card.CardAction;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;
import de.btu.monopoly.input.IOService;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Christian Prinz
 */
public class HardKi {

    private static final Logger LOGGER = Logger.getLogger(HardKi.class.getCanonicalName());

    // Ab wieviel verkauften Strassen im Gefaengnis bleiben
    private static final int PROPERTY_CAP_FOR_STAYING_IN_PRISON = 9;

    // Straßenkaufzonen: (lukrative Kaufzone)
    // bis zum Begin der lukrativen Zone betrachtet die KI die Strassen als zu billig (Zone 1)
    private static final int BEGIN_LUCRATIVE_AREA = 15;
    // in der Lukrativen Zone befinden sich begehrenswerte Strassen (Zone 2)
    private static final int END_LUCTRATIVE_AREA = 25;
    // nach der lukrativen Zone teure Strassen, die aber trotzdem kaufenswert sind (Zone 3)

    // Reichtumsbereiche (bis zu...): (arm -> fluessig -> reich -> superreich)
    private static final int RICH = 800;    // reich
    private static final int LIQUID = 600;   // fluessig
    private static final int POOR = 300;    // arm

    // Auktion:
    private static final int HIGH_BID = 120;    // Maiximalgebot (in %) fuer eine gute Strasse
    private static final int LOW_BID = 50;      // Maximalgebot (in %) fuer eine schlechte

    /**
     *
     * @param player ki
     * @return int fuer die Wahl der Option im Gefaengnis 1 - wuerfeln, 2 - bezahlen, 3 - GFKarte
     */
    public static int jailOption(Player player) {
        IOService.sleep(3000);
        int days = player.getDaysInJail();
        CardStack stack = player.getCardStack();
        int soldProps = getSoldProperties();

        // Ist die Obergrenze noch nicht erreicht, versucht die KI sofort rauszukommen
        if (soldProps < PROPERTY_CAP_FOR_STAYING_IN_PRISON) {
            if (stack.countCardsOfAction(CardAction.JAIL) > 0) { // mit Karte
                return 3;
            }
            else if (player.getMoney() > 100) {                  // mit Geld
                return 2;
            }
            else {
                return 1;
            }
        }
        else {    // sonst bleibt sie so lang wie moeglich drin
            if (days >= 3 && stack.countCardsOfAction(CardAction.JAIL) > 0) {
                return 3;
            }
            else {
                return 1;
            }
        }
    }

    /**
     *
     * @param player ki
     * @param prop zu kaufende Strasse
     * @return int fuer die Wahl der Kaufentscheidung 1 - kaufen (interessiert) , 2 - nicht kaufen (nicht interessiert)
     */
    public static int buyPropOption(Player player, PropertyField prop) {
        boolean buy = false;
        int propertyId = IOService.getGame().getBoard().getFieldManager().getFieldId(prop);
        int amount = player.getMoney();

        // Ist die KI superreich kauft sie aus Zone 1, 2 und 3
        if (amount > RICH) {
            buy = true;
        }// reich nur aus Zone 2 und 3
        else if (amount > LIQUID) {
            buy = (propertyId > BEGIN_LUCRATIVE_AREA);
        }// ist sie fluessig kauft sie nur Strassen der Zone 2
        else if (amount > POOR) {
            buy = (propertyId > BEGIN_LUCRATIVE_AREA && propertyId < END_LUCTRATIVE_AREA);
        }//ist die KI arm kauft sie nicht
        else {
            //Es sei denn sie hat bereits Straßen des selben Zuges
            if (areThereAlreadyNeighboursOwned(prop, player)) {
                //Dann nur wenn sie genug Geld hat
                buy = (player.getMoney() > prop.getPrice());
            }
        }

        return buy ? 1 : 2;
    }

    public static int processActionSequence(Player player, GameBoard board) {

        return -1;
    }

    /**
     * die Ki entscheidet je nach Kaufinteresse (HardKi.buyPropOption()), ob und wie weit sie bei der Auktion mitbietet
     * (EasyKi.processBetSequence())
     *
     * @param player ki
     */
    public static void processBetSequence(Player player) {
        PropertyField prop = AuctionService.getAuc().getProperty();
        // Gebotswichtigkeit wie Kaufentscheidung
        switch (buyPropOption(player, prop)) {
            case 1: // Diese Strasse will die KI haben
                EasyKi.processBetSequence(player, HIGH_BID);
                break;
            case 2: // Diese nur wenn sie mindestens reich ist
                if (player.getMoney() > LIQUID) {
                    EasyKi.processBetSequence(player, LOW_BID);
                }
                else { // Ansonsten bietet sie nicht und steigt sofort aus
                    EasyKi.processBetSequence(player, 0);
                }
                break;
            default:
                break;
        }
    }

    /**
     *
     * @return Anzahl der Properties die einen Besitzer haben
     */
    private static int getSoldProperties() {
        return (int) Arrays.stream(IOService.getGame().getBoard().getFields())
                .filter(p -> p instanceof PropertyField).map(p -> (PropertyField) p)
                .filter(p -> p.getOwner() != null)
                .count();
    }

    /**
     *
     * @param player zu pruefende Ki
     * @return Anzahl der Hypotheken, welche die Ki insgesamt aufgenommen hat
     */
    private static int numberOfMortgages(Player player) {
        return (int) IOService.getGame().getBoard().getFieldManager().getOwnedPropertyFields(player)
                .filter(p -> p.isMortgageTaken())
                .count();
    }

    /**
     *
     * @param prop Property welche auf im Besitz befindliche Nachbarn zu pruefen ist
     * @param player Ki
     * @return Gibt an, ob bereits Strassen des selben Strassenzuges, wie dem der uebergebenen Strasse im Besitz sind
     */
    private static boolean areThereAlreadyNeighboursOwned(PropertyField prop, Player player) {
        List<PropertyField> neighborList = IOService.getGame().getBoard().getFieldManager()
                .getNeighborList(prop);
        return neighborList.stream().anyMatch((neigh) -> (neigh.getOwner() == player));
    }

}
