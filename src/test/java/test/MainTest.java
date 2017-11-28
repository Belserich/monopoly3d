package test;


import de.btu.monopoly.controller.GameController;
import de.btu.monopoly.data.GameBoard;
import java.lang.reflect.Field;

/**
 *
 * @author Christian Prinz
 */
public class MainTest {

    public static void main(String[] args) throws Exception {
        GameController gc = new GameController(2);
        Object obj = new Object();
        Field f = gc.getClass().getDeclaredField("board");
        f.setAccessible(true);
        f.get(obj);
        GameBoard gb = (GameBoard) obj;
    }

}
