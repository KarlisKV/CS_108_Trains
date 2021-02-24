package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

public final class Ticket implements Comparable<Ticket> {

    private static Station from;
    private static Station to;
    private static int points;

    Ticket(List<Trip> trips) {
        Objects.requireNonNull(trips);
        // TODO: 2/24/2021 how to check if all stations from which the journey was started dont have the same name

    }

    // TODO: 2/24/2021 how to call using 'this' 
    Ticket(Station from, Station to, int points) {
        this.from = from;
        this.to = to;
        this.points = points;
    }



    public String text() {
        return from + " - " + to + " (" + points + ")";
    }

    // TODO: 2/24/2021 is this correct? 
    public int points(StationConnectivity connectivity) {
        if(connectivity.connected(to, from)) {
            return points;
        }
        else {
            return -points;
        }
    }

    @Override
    public int compareTo(Ticket that) {
        return 0;
    }

    @Override
    public String toString() {
        return from + " - " + to + " (" + points + ")";
    }
}
