package ch.epfl.tchu.game;

import java.util.List;
import java.util.Objects;

public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;

    public Trip(Station from, Station to, int points) {

        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        if(points <= 0) {
            throw new IllegalArgumentException("Points must be larger than zero: " + points);
        }
        this.points = points;

    }
    // TODO: 2/23/2021 fix this method currently not doing anything no clue at the moment, should use ArrayList and for-each loop idk

    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        return null;
    }

    //Getters
    public Station from() {
        return this.from;
    }

    public Station to() {
        return this.to;
    }
    public int points() {
        return this.points;
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
