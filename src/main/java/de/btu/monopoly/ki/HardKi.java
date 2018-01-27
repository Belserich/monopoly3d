/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.ki;

import de.btu.monopoly.core.GameBoard;
import de.btu.monopoly.core.service.AuctionService;
import de.btu.monopoly.core.service.IOService;
import de.btu.monopoly.data.card.Card;
import de.btu.monopoly.data.card.CardStack;
import de.btu.monopoly.data.field.FieldManager;
import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.field.StreetField;
import de.btu.monopoly.data.player.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Christian Prinz
 */
public class HardKi {

    private static final Logger LOGGER = Logger.getLogger(HardKi.class.getCanonicalName());
    private static final FieldManager FIELDMANAGER = IOService.getGame().getBoard().getFieldManager();

    // Ab wieviel verkauften Strassen im Gefaengnis bleiben (x von 24)
    private static final int PROPERTY_CAP_FOR_STAYING_IN_PRISON = 9;
    // Ab wieviel verkauften Strassen sicherer Spielen
    private static final int BEGINNING = 12;

    // Straßenkaufzonen: (lukrative Kaufzone)
    // bis zum Begin der lukrativen Zone betrachtet die KI die Strassen als zu billig (Zone 1)
    private static final int BEGIN_LUCRATIVE_AREA = 15;
    // in der Lukrativen Zone befinden sich begehrenswerte Strassen (Zone 2)
    private static final int END_LUCTRATIVE_AREA = 25;
    // nach der lukrativen Zone teure Strassen, die aber trotzdem kaufenswert sind (Zone 3)

    // Reichtumsbereiche (bis zu...): (arm -> fluessig -> reich -> superreich)
    private static final int RICH = 800;        // reich
    private static final int LIQUID = 600;      // fluessig
    private static final int POOR = 300;        // arm

    // Auktion:
    private static final int HIGH_BID = 120;    // Maiximalgebot (in %) fuer eine gute Strasse
    private static final int LOW_BID = 50;      // Maximalgebot (in %) fuer eine schlechte

    // Aktionsphase:
    private static int chosenFieldId;

    /**
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
            if (stack.countCardsOfAction(Card.Action.JAIL) > 0) { // mit Karte
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
            if (days >= 3 && stack.countCardsOfAction(Card.Action.JAIL) > 0) {
                return 3;
            }
            else {
                return 1;
            }
        }
    }

    /**
     * @param player ki
     * @param prop zu kaufende Strasse
     * @return int fuer die Wahl der Kaufentscheidung 1 - kaufen (interessiert) , 2 - nicht kaufen (nicht interessiert)
     */
    public static int buyPropOption(Player player, PropertyField prop) {
        boolean buy = false;
        int propertyId = FIELDMANAGER.getFieldId(prop);
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

    /**
     * Hier entscheidet die KI nach den vordefinierten Kriterien (siehe git -> Readme.md), wie sie in der ActionPhase() vorgeht.
     * Geht es darum eine Strasse zu manipulieren (Hypothek, Haus), dann speichert sie die ID des Feldes in der statischen
     * Variablen chosenFieldId, welche später in der ActionPhase() wieder aufgerufen wird. Hier wird lediglich die Art der Aktion
     * entschieden und das jeweilige Feld festgelegt.
     *
     * @param player Ki
     * @param board board
     * @return int fuer die choice in der actionPhase() in game()
     */
    public static int processActionSequence(Player player, GameBoard board) {
        int amount = player.getMoney();
        int buildings = FIELDMANAGER.getHouseAndHotelCount(player)[0] + FIELDMANAGER.getHouseAndHotelCount(player)[1];

        if (amount < POOR) {                     // Wenn die ki arm ist
            if (buildings > 0) {                        // verkauft sie erst Haeuser
                sellBuilding(player);
                return 3;
            }                                           // und nimmt dann Hypotheken auf
            else if (numberOfMortgages(player) < (int) FIELDMANAGER.getOwnedPropertyFields(player).count()) {
                takeMortgage(player);
                return 4;
            }
            else {                                      // ist nichts vorhanden, beendet sie
                return 1;
            }
        }
        else if (amount < LIQUID) {              // Wenn sie fluessig ist
            return 1;                                   // beendet sie die AktionsPhase
        }
        else if (amount < RICH) {                // Wenn sie reich ist
            if (getSoldProperties() < BEGINNING) {      // zu Spielbeginn
                if (numberOfMortgages(player) > 0) {        // zahlt sie zuerst Hypotheken ab
                    payMortgage(player);
                    return 5;
                }                                           // und kauft dann Haeuser
                else if (!buyableBuildingsList(player).isEmpty()) {
                    buyBuilding(player);
                    return 2;
                }
                else {
                    return 1;
                }
            }
            else {                                      // zum Spielende hin
                return 1;                                   // beendet sie die Aktionsphase
            }
        }
        else {                                   // Wenn sie superreich ist
            if (numberOfMortgages(player) > 0) {        // zahlt sie zuerst Hypotheken ab
                payMortgage(player);
                return 5;
            }                                           // und kauft dann Haeuser
            else if (!buyableBuildingsList(player).isEmpty()) {
                buyBuilding(player);
                return 2;
            }
            else {
                return 1;
            }
        }
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

    //________________________HILFSMETHODEN________________________________________________________
    /**
     * @return Anzahl der Properties die einen Besitzer haben
     */
    private static int getSoldProperties() {
        return (int) Arrays.stream(IOService.getGame().getBoard().getFields())
                .filter(p -> p instanceof PropertyField).map(p -> (PropertyField) p)
                .filter(p -> p.getOwner() != null)
                .count();
    }

    /**
     * @param player zu pruefende Ki
     * @return Anzahl der Hypotheken, welche die Ki insgesamt aufgenommen hat
     */
    private static int numberOfMortgages(Player player) {
        return (int) FIELDMANAGER.getOwnedPropertyFields(player).filter(p -> p.isMortgageTaken()).count();
    }

    /**
     * @param prop Property welche auf im Besitz befindliche Nachbarn zu pruefen ist
     * @param player Ki
     * @return Gibt an, ob bereits Strassen des selben Strassenzuges, wie dem der uebergebenen Strasse im Besitz sind
     */
    private static boolean areThereAlreadyNeighboursOwned(PropertyField prop, Player player) {
        List<PropertyField> neighborList = FIELDMANAGER.getNeighborList(prop);
        return neighborList.stream().anyMatch((neigh) -> (neigh.getOwner() == player));
    }

    /**
     * @return die ID des Feldes, welches in der Aktionsphase behandelt werden soll
     */
    public static int getChosenFieldId() {
        return chosenFieldId;
    }

    /*
     * _____________________AKTIONSPHASE-METHODEN____________________________________________________________________________
     * Diese Methoden werden von der processActionSequence aufgerufen und setzen die lokale Variable chosenFieldId auf die ID des
     * jeweils in der Methode ausgewählten Feldes. Diese ID wird später in Game.java für die actionPhase verwendet, um die
     * wirklichen Methoden auszuführen.
     */
    /**
     * sucht die billigste Strasse aus, auf der noch Haueser zum verkauf stehen
     *
     * @param player Ki
     */
    private static void sellBuilding(Player player) {
        /*
         * potentielle Strassen auf denen Haeuser verkauft werden koennen: Grundstuecke im Besitz -> Strassen im Besitz -> aus
         * komplettem Strassenzug -> bei denen beim verkauf eines Hauses die Balance gewaehrleistet bleibt -> die noch Haueser zum
         * Verkauf haben
         */
        List<PropertyField> props = FIELDMANAGER.getOwnedPropertyFields(player)
                .filter(p -> p instanceof StreetField).map(p -> (StreetField) p)
                .filter(p -> FIELDMANAGER.isComplete(p)).filter((StreetField p) -> FIELDMANAGER.balanceCheck(p, 0, 1))
                .filter(p -> p.getHouseCount() > 0)
                .collect(Collectors.toList());
        // check auf isEmpty() bereits in processActionPhase()
        chosenFieldId = FIELDMANAGER.getFieldId(props.get(0));
    }

    /**
     * sucht die billigste Strasse fuer das Aufnehmen einer Hypothek aus
     *
     * @param player
     */
    private static void takeMortgage(Player player) {
        List<PropertyField> props = FIELDMANAGER.getOwnedPropertyFields(player)
                .filter(p -> !p.isMortgageTaken()).collect(Collectors.toList());
        // check auf isEmpty bereit in processActionPhase()
        chosenFieldId = FIELDMANAGER.getFieldId(props.get(0));
    }

    /**
     * sucht die billigste Strasse fuer das Auszahlen einer Hypothek aus (um soviele wie möglich wieder in den Umlauf zu bringen)
     *
     * @param player
     */
    private static void payMortgage(Player player) {
        List<PropertyField> props = FIELDMANAGER.getOwnedPropertyFields(player)
                .filter(p -> p.isMortgageTaken()).collect(Collectors.toList());
        // check auf isEmpty bereit in processActionPhase()
        chosenFieldId = FIELDMANAGER.getFieldId(props.get(0));
    }

    /**
     * sucht die beste Strasse aus, auf der ein Haus gebaut werden soll
     *
     * @param player Ki
     */
    private static void buyBuilding(Player player) {
        /*
         * potentielle Strassen auf denen Haeuser gekauft werden koennen: Grundstuecke im Besitz -> Strassen im Besitz -> aus
         * komplettem Strassenzug -> bei denen beim kauf eines Hauses die Balance gewaehrleistet bleibt -> die noch Platz fuer
         * Haeuser haben
         */
        List<PropertyField> props = buyableBuildingsList(player);

        // lukrative Strassen aussuchen:
        List<PropertyField> best = new ArrayList<>();
        props.stream().filter((b) -> (FIELDMANAGER.getFieldId(b) > BEGIN_LUCRATIVE_AREA
                && FIELDMANAGER.getFieldId(b) < END_LUCTRATIVE_AREA)).forEachOrdered(best::add);

        // chosenFieldId uebergeben (wenn moeglich lukrative Strasse)
        chosenFieldId = (best.isEmpty()) ? chosenFieldId = FIELDMANAGER.getFieldId(props.get(0))
                : FIELDMANAGER.getFieldId(best.get(0));
    }

    /**
     * Hilfsmethode für <code>buyBuilding(Player player)</code>
     *
     * @param player Ki
     * @return Liste aus Properties, auf denen ein Haus gebaut werden kann.
     */
    private static List<PropertyField> buyableBuildingsList(Player player) {
        List<PropertyField> props = FIELDMANAGER.getOwnedPropertyFields(player)
                .filter(p -> p instanceof StreetField).map(p -> (StreetField) p)
                .filter(p -> FIELDMANAGER.isComplete(p)).filter(p -> FIELDMANAGER.balanceCheck(p, 1, 0))
                .filter(p -> p.getHouseCount() < 5)
                .collect(Collectors.toList());

        return props;
    }
}
