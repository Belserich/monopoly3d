package de.btu.monopoly.data;

/**
 * Created by Belserich Gremory on 13/11/2017.
 */
public class GameController
{
    private GameBoard board;
    private final Player[] players;
    
    public GameController(int playerCount) {
        this.board = new GameBoard();
        this.players = new Player[playerCount];
    }
}
