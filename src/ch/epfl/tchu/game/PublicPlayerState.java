package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 * PublicPlayerState class
 *
 * @author Karlis Velins (325180)
 */
public class PublicPlayerState {

    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;
    private final int carCount;
    private final int claimPoints;


    /**
     * Public constructor of the PublicPlayerState class
     * @param ticketCount (int) number of tickets
     * @param cardCount (int) number of cards
     * @param routes (List<Route>) list of routes
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {

        Preconditions.checkArgument(ticketCount >= 0);
        Preconditions.checkArgument(cardCount >= 0);
        this.ticketCount = ticketCount;
        this.routes = routes;
        this.cardCount = cardCount;
        int tempPoints = 0;

        for (Route route : routes) {
            tempPoints += route.claimPoints();
        }

        this.claimPoints = tempPoints;

        this.carCount = Constants.INITIAL_CAR_COUNT - routes.size();
    }

    /**
     * Returns the number of tickets the player has
     * @return the number of tickets the player has
     */
    public int ticketCount()  {
        return ticketCount;
    }

    /**
     * Returns the number of cards the player has
     * @return the number of cards the player has
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * Returns the list of routes the player has seized
     * @return the list of routes the player has seized
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * Returns the amount of available wagons/cars the player has
     * @return the amount of available wagons/cars the player has
     */
    public int carCount() {
        return carCount;
    }

    /**
     * Returns the number of construction points obtained by the player
     * @return the number of construction points obtained by the player
     */
    public int claimPoints() {
        return claimPoints;
    }
}