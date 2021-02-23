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

    public static final List<Card> ALL = List.of(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE, LOCOMOTIVE);
    public static final int COUNT = ALL.size();

    public static final List<Card> CARS = List.of(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE);

    // TODO: 2/23/2021 Help needed with this method + that other one named color, I legit have no clue what he wants from us
    public Color color(){
        return this.color();
    }

    /**
     * Method of returns the tupe of wagon card respodning to the given color
     * @param color
     * @return
     */
    public static Card of(Color color) {
        for(int i = 0; i < COUNT; i++) {
            if(ALL.get(i).equals()) {
                return ALL.get(i);
            }
        }
    }

    // TODO: 2/23/2021 here no clue :((
    public Color color() {
        return null;
    }

}
