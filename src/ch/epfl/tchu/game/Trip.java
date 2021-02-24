package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;

    public Trip(Station from, Station to, int points) {

        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        Preconditions.checkArgument(points > 0);
        this.points = points;

    }

    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Preconditions.checkArgument(points > 0);

        // TODO: 2/24/2021 I don't get how should we return the list
        return null;
    }

    //Getters
    public Station from() {
        return from;
    }

    public Station to() {
        return to;
    }

    public int points() {
        return points;
    }

    public int points(StationConnectivity connectivity){
        if(connectivity.connected(from, to)) {
            return points;
        }
        else {
            return -points;
        }
    }



}
