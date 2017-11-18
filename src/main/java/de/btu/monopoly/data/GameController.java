package de.btu.monopoly.data;

public class GameController {

    private GameBoard board;
    private final Player[] players;

    public GameController(int playerCount) {
        this.board = new GameBoard();
        this.players = new Player[playerCount];
    }
}
