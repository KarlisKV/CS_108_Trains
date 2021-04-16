package ch.epfl.tchu.game;
import java.util.List;

/**
 * PlayerId enum sets how many players are in the game and has a
 * method that says which player goes next
 * @author Karlis Velins (325180)
 */
public enum PlayerId {
    PLAYER_1,
    PLAYER_2;

    public static final List<PlayerId> ALL = List.of(PLAYER_1, PLAYER_2);
    public static final int COUNT = ALL.size();

    /**
     * Returns the next player. If given player 1 return player 2 etc.
     * @return the next player. If given player 1 return player 2 etc.
     */
    public PlayerId next() {
        return this.equals(PLAYER_1)?
                PLAYER_2:
                PLAYER_1;
    }
}
