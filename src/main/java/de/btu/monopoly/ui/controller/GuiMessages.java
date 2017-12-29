package de.btu.monopoly.ui.controller;

/**
 *
 * @author augat
 */
public class GuiMessages {

    // Variable um Fehler aus Logik in Gui zu senden
    private static boolean errorConnect = true;

    //--------------------------------------------------
    public static void setConnectionError(boolean boo) {
        errorConnect = boo;
    }

    public static boolean getConnectionError() {
        return errorConnect;
    }

}
