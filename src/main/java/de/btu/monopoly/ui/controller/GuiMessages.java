package de.btu.monopoly.ui.controller;

import javafx.scene.paint.Color;

/**
 *
 * @author augat
 */
public class GuiMessages {

    // Variable um Fehler aus Logik in Gui zu senden
    private static boolean errorConnect = true;

    // Setzen der Standardfarben (weiß)
    private static Color[] playerColors = {Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE};

    // Speichern der Player ID für Disable von GUI Elementen
    public static int PlayerID;

    //--------------------------------------------------
    public static void setConnectionError(boolean boo) {
        errorConnect = boo;
    }

    public static boolean getConnectionError() {
        return errorConnect;
    }

    //--------------------------------------------------
    public static void setPlayerColors(Color[] colors) {
        playerColors = colors;
    }

    public static Color[] getPlayerColors() {
        return playerColors;
    }

}
