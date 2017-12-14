package de.btu.monopoly.core.service;

import de.btu.monopoly.core.mechanics.Auction;
import de.btu.monopoly.data.player.Player;

/**
 *
 * @author patrick
 */
public class AuctionService {

    /**
     * Startet die Auktion, ermittelt den Höchstbietenden und übergibt den Gewinner, sowie den Preis an deren Klassenvariablen
     */
    public void startAuction(Auction auc) {

        int activCount = 0;
        Player[] players = auc.getPlayers();
        int playerNumb = players.length;

        /*
        Erstelle ein int[][] welches die ID's und Gebote der Spieler speichert
         */
        int[][] aucPlayers = new int[playerNumb][3];
        for (int i = 0; i < playerNumb; i++) {
            aucPlayers[i][0] = players[i].getId();      //Speicher Spieler ID
            aucPlayers[i][1] = 0;                       //Speichert Spieler Gebot
            aucPlayers[i][2] = 1;                       //Spieler noch aktiv? 1 = ja, 0 = nein
        }

        do {

            /*
            Spielogik
             */
            //for Schleife zur Ueberpruefung der aktiven Spieleranzahl
            for (int i = 0; i < aucPlayers.length; i++) {
                activCount += aucPlayers[i][2];
            }
        } while (activCount > 1);

    }

}
