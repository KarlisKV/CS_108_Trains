package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;
import java.util.*;


/**
 * Game class is the brains of the machine. Makes the game run using all methods in classes from game package
 * @author Daniel Polka (326800)
 * @author Karlis Velins (325180)
 */
public final class Game {

    /**
     * Game class not to be instantiated
     */
    private Game(){}

    /**
     * The method where all the magic happens. This method calls all other methods in order for the players to actually play the game
     * @param players  HashMap mapping a player's PlayerId to Player
     * @param playerNames HashMap mapping a player's PlayerId to the player's name
     * @param tickets SortedBag containing the Tickets used for the game
     * @param rng Random used for the game
     * @throws IllegalArgumentException if given amount of players are not equal to the default setting and also the length of the player
     * names are equal to the set player count
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {

    }






    /**
     * Method which is called whenever there is information to announce, and announces it to
     * both players at once to avoid code duplication
     * @param players  HashMap mapping a player's PlayerId to Player
     * @param info the String of information each player is going to receive
     */
    private static void allPlayersReceiveInfo(Map<PlayerId, Player> players, String info) {
        for(PlayerId p : PlayerId.ALL) {
            players.get(p).receiveInfo(info);
        }
    }

    /**
     * Method which is called whenever the status of the game has changed to update both player's state
     * at the same time and avoid code duplication
     * @param players  HashMap mapping a player's PlayerId to Player
     * @param newState the new public GameState
     */
    private static void allPlayersUpdateState(Map<PlayerId, Player> players, GameState newState) {
        for (PlayerId playerId : PlayerId.ALL) {
            players.get(playerId).updateState(newState, newState.playerState(playerId));
        }
    }


}
