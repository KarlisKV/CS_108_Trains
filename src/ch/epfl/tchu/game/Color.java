package ch.epfl.tchu.game;
import java.util.List;

/**
 * Color enum
 *
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

    public static final List<Color> ALL = List.of(Color.values());
    public static final int COUNT = ALL.size();

}

