/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.controller.phases;

import static de.btu.monopoly.controller.GameController.CURRENCY_TYPE;
import static de.btu.monopoly.controller.GameController.logger;
import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.Property;
import de.btu.monopoly.data.field.StationField;
import de.btu.monopoly.data.field.StreetField;
import de.btu.monopoly.data.field.SupplyField;
import java.util.logging.Level;

/**
 *
 * @author Christian Prinz
 */
public class FieldManager {

    private GameBoard board;
    private Player[] players;
    private PlayerManager pm;

    public FieldManager(GameBoard board, Player[] players, PlayerManager pm) {
        this.board = board;
        this.players = players;
        this.pm = pm;
    }

    /**
     *
     * @param player Spieler der die Strasse kauft
     * @param property Strasse die gekauft werden soll
     * @param price Preis der Strasse
     * @return ob die Strasse gekauft wurde
     */
    public boolean buyStreet(Player player, Property property, int price) {
        if (pm.checkLiquidity(player, price)) {
            logger.log(Level.INFO, player.getName() + " kauft das Grundstück für " + price + CURRENCY_TYPE);
            property.setOwner(player);
            pm.takeMoney(player, price);
            return true;
        } else {
            logger.log(Level.INFO, player.getName() + " hat nicht genug Geld.");
            return false;
        }
    }

    /**
     * Kauf von Haus/Hotel - wenn der aktive Spieler genügend Geld
     *
     * @param field Feld worauf ein Haus/Hotel gekauft/gebaut wird
     * @param player Spieler dem die Strasse gehoert
     */
    public void buyBuilding(Player player, StreetField field) {
        //@Eli, added Hausbau hinzugefuegt. TODO Spectator unmöglich

        if (!(player.isSpectator()) && pm.checkLiquidity(player, field.getHousePrice())) {
            if (field.complete() && checkBalance(field, true)) {
                pm.takeMoney(player, field.getHousePrice());
                field.setHouseCount(field.getHouseCount() + 1); //Haus bauen
                logger.log(Level.INFO, "Haus wurde gekauft!");
            } else {
                logger.log(Level.INFO, "Straßenzug nicht komplett, oder unausgeglichen!");
            }
        }
    }

    /**
     * Verkauf von Haus/Hotel
     *
     * @param field Feld wovon ein haus/Hotel verkauft/abbebaut wird
     * @param player Spieler dem die Strasse gehoert
     */
    public void sellBuilding(Player player, StreetField field) {
        if (!(player.isSpectator()) && checkBalance(field, false)) {
            pm.giveMoney(player, field.getHousePrice()); //@rules MAXI du moegest bitte pruefen!
            field.setHouseCount(field.getHouseCount() - 1); // Haus abbauen
            logger.log(Level.INFO, "Haus wurde verkauft!");
        } else {
            logger.log(Level.INFO, "Straßenzug würde unausgeglichen sein");
        }
    }

    /**
     * @return true wenn eine Strasse gleiches Gewicht von Haeuser hat und false wenn nicht
     * @param field die auf Ausgeglichenheit im Strassenzug zu pruefende Strasse
     * @param buyIntend gibt an, ob der Spieler ein Haus <b>kaufen</b> möchte
     */
    public boolean checkBalance(StreetField field, boolean buyIntend) {

        for (Property nei : field.getNeighbours()) {  // Liste der Nachbarn durchgehen

            int housesHere = field.getHouseCount();      // Haueser auf der aktuellen Strasse
            int housesThere = ((StreetField) nei).getHouseCount();       // Haeuser auf der Nachbarstrasse
            if (((housesHere - housesThere) > 0) && buyIntend) {        // Wenn die Nachbarn weniger Haueser haben
                return false;
            } else if (((housesHere - housesThere) < 0) && !buyIntend) {// Wenn die Nachbarn mehr Haeuser haben
                return false;
            }
        }
        return true;
    }

    /**
     * Hypothek aufnehmen
     *
     * @param field Grundstueck, dessen Hypothek aufgenommen wird
     * @param player Spieler, dem das Grundstueck gehoert
     */
    public void takeMortgage(Player player, Property field) { //TODO Abfrage ob noch Haeuser drauf sind
        //TODO Station- und SuplyField
        if (field instanceof StationField || field instanceof SupplyField) {
            pm.giveMoney(player, field.getMortgageValue());
            field.setMortgageTaken(true);
        }
        if (field instanceof StreetField) {
            for (Property nei : ((StreetField) field).getNeighbours()) {
                if (((StreetField) nei).getHouseCount() > 0) {
                    sellBuilding(player, (StreetField) nei);

                }
            }
            if (((StreetField) field).getHouseCount() > 0) {
                sellBuilding(player, (StreetField) field);
            }
            pm.giveMoney(player, field.getMortgageValue());
        }
        field.setMortgageTaken(true);
        logger.log(Level.INFO, "Hypothek wurde aufgenommen!");
    }

    /**
     * Hypothek zurueck zahlen
     *
     * @param field Grundstueck, desseh Hypothek abgezahlt wird
     * @param player Spieler, dem das Grundstueck gehoert
     */
    public void payMortgage(Player player, Property field) {
        int mortgageBack = field.getMortgageBack();
        if (pm.checkLiquidity(player, mortgageBack)) {
            pm.takeMoney(player, field.getMortgageBack());
            field.setMortgageTaken(false);
            logger.log(Level.INFO, "Hypothek wurde zurueckgezahlt!");
        } else {
            logger.log(Level.INFO, "Hypothek kann nicht zurückgezahlt werden! (Nicht genug Geld)");
        }
    }

}
