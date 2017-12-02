/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.controller.phases;

import static de.btu.monopoly.controller.GameController.LOGGER;
import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;
import de.btu.monopoly.data.field.Field;
import de.btu.monopoly.data.field.Property;
import de.btu.monopoly.data.field.StreetField;
import java.util.logging.Level;

/**
 *
 * @author Christian Prinz
 */
public class ActionPhase {

    GameBoard board;
    Player[] players;
    Player player;
    FieldManager fm;
    InputManager im;

    public ActionPhase(GameBoard board, Player[] players, FieldManager fm, InputManager im) {
        this.board = board;
        this.players = players;
        this.fm = fm;
        this.im = im;
    }

    /**
     *
     * Die Aktionsphase (Bebauung, Hypothek, aktivieren der Handelsphase)
     *
     * @param player Spieler in der Aktionsphase
     */
    public void actionPhase(int playerIndex) { //@optimize switches vereinfachen
        // TODO hier muss später noch der Handel implementiert werden
        this.player = players[playerIndex];

        int choice;
        do {
            LOGGER.log(Level.INFO, player.getName() + "! Waehle eine Aktion:\n[1] - Nichts\n[2] - Haus kaufen\n[3] - Haus verkaufen\n[4] - "
                    + "Hypothek aufnehmen\n[5] - Hypothek abbezahlen");

            choice = im.getUserInput(5);
            if (choice != 1) {
                Field currField = board.getFields()[im.askForField(player) - 1]; // Wahl der Strasse

                if (currField instanceof Property) {
                    Property property = (Property) currField;
                    switch (choice) {
                        case 2: /*
                         * Haus kaufen
                         */ {
                            if (!(currField instanceof StreetField)) {
                                LOGGER.log(Level.INFO, "Gewähltes Feld ist keine Straße!");
                                break;
                            }
                            StreetField streetField = (StreetField) property;
                            if ((streetField.getOwner() == player) && (streetField.getHouseCount() < 5)) {
                                // wenn im Besitz und nicht vollgebaut
                                fm.buyBuilding(player, streetField);
                            } else {
                                LOGGER.log(Level.INFO, "Diese Straße gehört dir nicht, oder ist voll bebaut.");
                            }
                            break;
                        }
                        case 3: /*
                         * Haus verkaufen
                         */ {
                            if (!(currField instanceof StreetField)) {
                                LOGGER.log(Level.INFO, "Gewähltes Feld ist keine Straße!");
                                break;
                            }
                            StreetField streetField = (StreetField) property;
                            if ((streetField.getOwner() == player) && (streetField.getHouseCount() > 0)) {
                                // wenn im Besitz und nicht 'hauslos'
                                fm.sellHouse(player, streetField);
                            } else {
                                LOGGER.log(Level.INFO, "Diese Straße gehört dir nicht, oder hat keine Häuser zum verkaufen.");
                            }
                            break;
                        }
                        case 4: /*
                         * Hypothek aufnehmen
                         */ {
                            // wenn im Besitz und noch keine Hypothek aufgenommen
                            if (property.getOwner() == player && (!(property.isMortgageTaken()))) {
                                fm.takeMortgage(player, property);
                                LOGGER.log(Level.INFO, "Hypothek aufgenommen.");
                            } else {
                                LOGGER.log(Level.INFO, "Diese Straße gehört dir nicht, oder hat schon eine Hypothek.");
                            }
                            break;
                        }
                        case 5: /*
                         * Hypothek abbezahlen
                         */ {
                            // wenn im Besitz und Hypothek aufgenommen
                            if (property.getOwner() == player && (property.isMortgageTaken())) {
                                fm.payMortgage(player, property);
                                LOGGER.log(Level.INFO, "Hypothek abgezahlt.");
                            } else {
                                LOGGER.log(Level.INFO, "Diese Straße gehört dir nicht, oder hat keine Hypothek zum abzahlen.");
                            }
                            break;
                        }
                        default:
                            LOGGER.log(Level.WARNING, "FEHLER: StreetFieldSwitch überlaufen.");
                            break;
                    }
                }
            }
        } while (choice != 1);
    }
}
