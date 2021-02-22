package ch.epfl.tchu.game;

import java.util.Arrays;
import java.util.List;

public enum Card {
    BLACK (wagon noir),
    VIOLET (wagon violet),
    BLUE (wagon bleu),
    GREEN (wagon vert),
    YELLOW (wagon jaune),
    ORANGE (wagon orange),
    RED (wagon rouge),
    WHITE (wagon blanc),
    LOCOMOTIVE (locomotive);

    public static final List<Card> ALL = Arrays.asList(Card.values());
    public static final int COUNT = Card.values().length;

    public static final List<Card> CARS = Arrays.asList(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE);


    public Color color(){
        return null;
    }

    public static Color of(Color color) {
        return color;
    }
}
