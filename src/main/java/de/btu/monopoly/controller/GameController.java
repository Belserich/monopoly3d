/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.btu.monopoly.controller;

import de.btu.monopoly.data.GameBoard;
import de.btu.monopoly.data.Player;

public class GameController {

    private GameBoard board;
    private final Player[] players;
    private boolean gameOver;
    private boolean repeatFieldPhase;
    private Player activePlayer;
    private int doubletCounter;
    private boolean isDoublet;

    public GameController(int playerCount) {
        this.board = new GameBoard();
        this.players = new Player[playerCount];
        init();
    }

    /**
     * Spielinitialisierung
     */
    public void init() {
        startGame();
    }

    public void startGame() {
        // Schleife für Spieler
        for (Player p : players) {
            // Rundenphase
            activePlayer = p;
            if (!(p.isSpectator())) {
                turnPhase();
            }

        }
    }

    private void turnPhase() {
        doubletCounter = 0;

        if (activePlayer.isInJail()) {
            prisonPhase;
        }
        while (isDoublet) {
            rollPhase();
            while (repeatFieldPhase) {
                fieldPhase();
            }
            actionPhase();
        }
    }
}
