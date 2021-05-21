package ch.epfl.tchu.game;
import java.util.List;

/**
 * Color enum represents the different colors used in the game
 * @author Karlis Velins (325180)
 */
public enum  Color {

    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    //List of all Colors
    public static final List<Color> ALL = List.of(Color.values());

    //Size of Color enum
    public static final int COUNT = ALL.size();

}

