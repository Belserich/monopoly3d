package de.btu.monopoly.core.mechanics;

import de.btu.monopoly.data.field.PropertyField;
import de.btu.monopoly.data.player.Player;

/**
 * Auktions Klasse Auktionen zum ersteigern von Grundstuecken, welche zuvor nicht gekauft wurden.
 *
 * @author Patrick Kalweit
 */
public class Auction {

    private int price;

    private Player[] players;

    private Player winner;

    private PropertyField property;

    /**
     *
     * @param property Die Strasse die versteigert werden soll
     * @param players Spieler die an der Auktion teilnehmen
     */
    public Auction(PropertyField property, Player[] players) {
        this.property = property;
        this.players = players;
    }

    /**
     * Startet die Auktion, ermittelt den Höchstbietenden und übergibt den Gewinner, sowie den Preis an deren Klassenvariablen
     */
    public void startAuction() {

        int playerNumb = this.players.length;

        /*
        Erstelle ein int[][] welches die ID's und Gebote der Spieler speichert
         */
        int[][] aucPlayers = new int[playerNumb][2];
        for (int i = 0; i < playerNumb; i++) {
            aucPlayers[i][0] = players[i].getId();
            aucPlayers[i][1] = 0;
        }

        //PLATZHALTER:
        this.price = 0;
        this.winner = players[0];
    }

    /**
     * Diese Methode ermoeglicht es einem Spieler, die Auktion zu verlassen.
     *
     * @param activePlayers
     * @return
     */
    public int[][] playerExit(int[][] activePlayers, int id) {

        //neues und kleineres Spieler Array
        int playerNumb = activePlayers.length;
        int[][] refreshedPlayers = new int[playerNumb][2];

        //geht jeden Platz des neuen Arrays durch
        for (int i = 0; i < refreshedPlayers.length; i++) {
            //verschiebt nach und nach die aktiven Spieler in das neue Array
            for (int j = 0; j < activePlayers.length; j++) {
                if (activePlayers[j][0] != -1) {
                    if (activePlayers[j][0] != id) {
                        refreshedPlayers[i][0] = activePlayers[j][0];
                        activePlayers[j][0] = -1;
                        refreshedPlayers[i][1] = activePlayers[j][1];
                        break;
                    }
                }
            }
        }

        return refreshedPlayers;
    }

    /**
     * Das hoechste Gebot aller Bieter wird ermittelt. Es wird die ID des Spielers mit dem hoechsten Gebot zurueck gegeben
     *
     * @param activePlayers
     * @return playerID
     */
    public int getHighestBid(int[][] activePlayers) {

        int playerID = -1;
        int highestBid = -1;

        for (int i = 0; i < activePlayers.length; i++) {
            if (highestBid < activePlayers[i][1] && activePlayers[i][1] != 0) {
                playerID = activePlayers[i][0];
                highestBid = activePlayers[i][1];
            }
        }

        return playerID;
    }

    /**
     * @return the price
     */
    public int getPrice() {
        return price;
    }

    /**
     * @return the winner
     */
    public Player getWinner() {
        return winner;
    }

}
