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

    
    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        flush();
        if (!GlobalSettings.isRunInConsole() && !GlobalSettings.isRunAsTest()) {
            SceneManager.playerUpdate();
            SceneManager.movePlayerUpdate();
            SceneManager.geldPlayerUpdate();
            SceneManager.propertyUpdate();
        }
        SceneManager.appendText(getFormatter().format(record));

    }
}
