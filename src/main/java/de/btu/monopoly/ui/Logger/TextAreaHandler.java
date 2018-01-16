package de.btu.monopoly.ui.Logger;

import de.btu.monopoly.GlobalSettings;
import de.btu.monopoly.ui.SceneManager;

import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 *
 * @author augat
 */
public class TextAreaHandler extends StreamHandler {

    private static boolean boo = true;

    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        flush();
        if (!GlobalSettings.isRunInConsole() && !GlobalSettings.isRunAsTest()) {
            if (boo) {
                SceneManager.playerUpdate();
                boo = false;
            }
            SceneManager.movePlayerUpdate();
            SceneManager.geldPlayerUpdate();
            SceneManager.propertyUpdate();
            SceneManager.hausUpdate();
        }
        SceneManager.appendText(getFormatter().format(record));

    }
}
