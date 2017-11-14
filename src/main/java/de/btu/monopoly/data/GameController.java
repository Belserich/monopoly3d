package de.btu.monopoly.data;

/**
 * Created by Belserich Gremory on 13/11/2017. Muss diese Klasse eventuell
 * umbenannt werden in GameInit oder InitGame? Der GameController an sich ist ja
 * ein gesamtes Paket, bzw. eine Klasse im Paket GameController.
 *
 */
public class GameController {

    private GameBoard board;
    private final Player[] players;

    public GameController(int playerCount) {
        this.board = new GameBoard();
        this.players = new Player[playerCount];
    }
}
