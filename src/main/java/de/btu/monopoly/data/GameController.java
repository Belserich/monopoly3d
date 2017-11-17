package de.btu.monopoly.data;

/**
 * Created by Maximilian Bels on 13/11/2017.
 */
public class GameController {

    private GameBoard board;
    private final Player[] players;

    public GameController(int playerCount) {
        this.board = new GameBoard();
        this.players = new Player[playerCount];
    }
}
