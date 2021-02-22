package ch.epfl.tchu.game;
import java.util.Arrays;
import java.util.List;

public enum  Color {
    BLACK (noir),
    VIOLET (violet),
    BLUE (bleu),
    GREEN (vert),
    YELLOW (jaune),
    ORANGE (orange),
    RED (rouge),
    WHITE (blanc);

    public static final List<Color> ALL = Arrays.asList(Color.values());
    public static final int COUNT = Color.values().length;

}
