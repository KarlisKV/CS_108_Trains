package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

public class PublicPlayerState {

    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;
    private final int carCount;
    private final int claimPoints;



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

    public int ticketCount()  {
        return ticketCount;
    }
    public int cardCount() {
        return cardCount;
    }
    public List<Route> routes() {
        return routes;
    }


    public int carCount() {
        return carCount;
    }
    public int claimPoints() {
        return claimPoints;
    }
}
