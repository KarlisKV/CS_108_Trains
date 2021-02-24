package ch.epfl.tchu.game;

import java.util.List;

public enum Card {
    BLACK (Color.BLACK),
    VIOLET (Color.VIOLET),
    BLUE (Color.BLUE),
    GREEN (Color.GREEN),
    YELLOW (Color.YELLOW),
    ORANGE (Color.ORANGE),
    RED (Color.RED),
    WHITE (Color.WHITE),
    LOCOMOTIVE (null);

    public static final List<Card> ALL = List.of(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE, LOCOMOTIVE);
    public static final int COUNT = ALL.size();
    public static final List<Card> CARS = List.of(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE);

    private final Color color;

    Card(Color color) {
        this.color = color;
    }

    // TODO: 2/23/2021 Help needed with this method + that other one named color, I legit have no clue what he wants from us
    public Color color(){
        return this.color;
    }


    public static Card of(Color color) {
        return Card.of(color);
    }



}
