package ch.epfl.tchu.game;

import java.util.List;

/**
 * Card enum represents the cards used in the game
 * @author Karlis Velins (325180)
 */
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

    //List of all cards
    public static final List<Card> ALL = List.of(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE, LOCOMOTIVE);
    //Size of the enum
    public static final int COUNT = ALL.size();
    //List of all cars (without the locomotives)
    public static final List<Card> CARS = List.of(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE);

    private final Color color;

    /**
     * Default Card constructor
     * @param color (Color) color of the card
     */
    Card(Color color) {
        this.color = color;
    }


    /**
     * 'color' method
     * @return returns the color of the type of card to which it is applied
     * if it is a wagon type, or null if it is the locomotive type
     */
    public Color color(){
        return this.color;
    }

    /**
     *  'of' method, given a color, return the respective card
     * @param color (Color) color of the card
     * @return returns the type of wagon card corresponding to the given color
     */
    public static Card of(Color color) {

        for( Card c : CARS) {
            if(color != null) {
                if(color.equals(c.color)) {
                    return c;
                }
            } else {
                return Card.LOCOMOTIVE;
            }
        }
        return null;
    }



}
