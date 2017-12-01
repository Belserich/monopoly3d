/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.controller.phases;

import static de.btu.monopoly.controller.GameController.CURRENCY_TYPE;
import static de.btu.monopoly.controller.GameController.logger;
import de.btu.monopoly.data.Card;
import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.CardField;
import de.btu.monopoly.data.field.Property;
import de.btu.monopoly.data.field.SupplyField;
import de.btu.monopoly.data.field.TaxField;
import java.util.logging.Level;

/**
 *
 * @author Christian Prinz
 */
public class FieldPhase {

    GameBoard board;
    Player[] players;
    PlayerManager pm;
    FieldManager fm;
    InputManager im;
    //int[] rollResult;

    public FieldPhase(GameBoard board, Player[] players, PlayerManager pm, FieldManager fm, InputManager im) {
        this.board = board;
        this.players = players;
        this.pm = pm;
        this.fm = fm;
        this.im = im;
    }

    /**
     * die Feldphase (Feldaktionen)
     *
     * @param player Spieler in der Feldphase
     */
    public void compute(Player player, int[] rollResult) {
        GameBoard.FieldType type = GameBoard.FIELD_STRUCTURE[player.getPosition()];
        switch (type) {
            case STREET:
            // siehe case SUPPLY

            case STATION:
            // siehe case SUPPLY

            case SUPPLY: // Strasse / Bahnhof / Werk
                processPlayerOnPropertyField(player, (Property) board.getFields()[player.getPosition()], rollResult);
                break;

            case TAX: // Steuerfeld
                processPlayerOnTaxField(player, (TaxField) board.getFields()[player.getPosition()]);
                break;

            case CARD: // Kartenfeld
                processPlayerOnCardField(player, (CardField) board.getFields()[player.getPosition()]);
                break;

            case GO_JAIL: // "Gehen Sie Ins Gefaengnis"-Feld
                logger.log(Level.INFO, player.getName() + " muss ins Gefaengnis!");
                pm.moveToJail(player);
                break;

            default:
                break; // "Los"-Feld
        }
    }

    private void processPlayerOnCardField(Player player, CardField field) {
        logger.fine(String.format("%s steht auf einem Kartenfeld (%s).", player.getName(),
                board.getFields()[player.getPosition()].getName()));
        Card nextCard = field.getCardStack().nextCard();
        Card.Action[] actions = nextCard.getActions();

        assert actions.length > 0;

        switch (actions[0]) {
            case JAIL:
                player.addJailCard();
                break;
            case GIVE_MONEY:
                pm.giveMoney(player, nextCard.getArgs()[0]); // TODO check args
                break;
            case GO_JAIL:
                pm.moveToJail(player);
                break;
            case PAY_MONEY:
                pm.takeMoney(player, nextCard.getArgs()[0]);
                break;
            case MOVE_PLAYER:
                pm.movePlayer(player, nextCard.getArgs()[0]);
                break;
            case SET_POSITION:
                pm.movePlayer(player, nextCard.getArgs()[0] - player.getPosition());
                break;
            case PAY_MONEY_ALL:
                int amount = nextCard.getArgs()[0];
                pm.takeMoney(player, amount * players.length);
                for (Player other : players) {
                    pm.giveMoney(other, amount);
                }
                break;
            case NEXT_SUPPLY:
                int fields = 0;
                while (GameBoard.FIELD_STRUCTURE[player.getPosition() + (++fields)] != GameBoard.FieldType.SUPPLY);
                pm.movePlayer(player, fields);
            case NEXT_STATION_RENT_AMP:
                fields = 0;
                while (GameBoard.FIELD_STRUCTURE[player.getPosition() + (++fields)] != GameBoard.FieldType.STATION);
                pm.movePlayer(player, fields); // TODO Amplifier
            case BIRTHDAY: // TODO
            case RENOVATE: // TODO
        }
    }

    private void processPlayerOnTaxField(Player player, TaxField field) {
        logger.log(Level.FINE, player.getName() + " steht auf einem Steuerfeld.");
        if (pm.checkLiquidity(player, field.getTax())) {
            pm.takeMoney(player, field.getTax());
        } else {
            logger.log(Level.INFO, player.getName() + " kann seine Steuern nicht abzahlen!");
            pm.bankrupt(player);
        }
    }

    private void processPlayerOnPropertyField(Player player, Property field, int[] rollResult) { //TODO was wenn im eigenen Besitz
        // prüft den Besitzer
        Player other = field.getOwner();
        if (other == null) { // Feld frei
            logger.log(Level.INFO, player.getName() + " steht auf einem freien Grundstück und kann es: \n[1] Kaufen \n[2] Nicht Kaufen");
            switch (im.getUserInput(2)) { //@GUI
                case 1: //Kaufen
                    logger.info(player.getName() + " >> " + field.getName());
                    if (!fm.buyStreet(player, field, field.getPrice())) {
                        logger.info(player.getName() + "hat nicht genug Geld! " + field.getName() + " wird nun zwangsversteigert.");
                        //TODO betPhase(field);
                    }
                    break;
                case 2: //Auktion @multiplayer
                    logger.info(player.getName() + "hat sich gegen den Kauf entschieden, die Straße wird nun versteigert.");
                    //TODO betPhase(field);
                    break;
                default:
                    logger.log(Level.WARNING, "getUserInput() hat index außerhalb des zurückgegeben.");
                    break;
            }
        } else if (other == player) { // Property im eigenen Besitz
            logger.log(Level.FINE, player.getName() + " steht auf seinem eigenen Grundstück.");
        } else {                      // Property nicht in eigenem Besitz
            logger.log(Level.INFO, player.getName() + " steht auf dem Grundstück von " + other.getName() + ".");

            int rent = field.getRent();
            if (field instanceof SupplyField) {
                rent = rent * (rollResult[0] + rollResult[1]);
            }

            if (pm.checkLiquidity(player, rent)) {
                logger.log(Level.INFO, player.getName() + " zahlt " + rent + CURRENCY_TYPE + " Miete.");
                pm.takeMoney(player, rent);
                pm.giveMoney(field.getOwner(), rent);
            } else {
                logger.log(Level.INFO, player.getName() + " kann die geforderte Miete nicht zahlen!");
                pm.bankrupt(player);
            }
        }
    }

}
