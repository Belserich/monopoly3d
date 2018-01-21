package de.btu.monopoly.ui;

import de.btu.monopoly.GlobalSettings;
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
        if (!GlobalSettings.RUN_IN_CONSOLE && !GlobalSettings.RUN_AS_TEST) {
            super.publish(record);
            flush();
            if (boo) {
                SceneManager.playerUpdate();
                boo = false;
            }
            SceneManager.movePlayerUpdate();
            SceneManager.geldPlayerUpdate();
            SceneManager.propertyUpdate();
            SceneManager.propertyStateUpdate();
            SceneManager.initStreets();
            SceneManager.appendText(getFormatter().format(record));
        }

    }
}
