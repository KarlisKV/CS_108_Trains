package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;

    /**
     *  Default Trip constructor
     * @param from (Station) Departure station
     * @param to (Station) Arrival station
     * @param points (int) points for connecting the 2 stations
     */
    public Trip(Station from, Station to, int points) {

        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        Preconditions.checkArgument(points > 0);
        this.points = points;

    }

    /**
     * 'all' method to get List of possible journeys between 2 stations with the given points
     * @param from (Station) Departure station
     * @param to (Station) Arrival station
     * @param points (int) points for connecting the 2 stations
     * @return possibleTrips List<Trip> returns list of all possible trips between 2 stations
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Preconditions.checkArgument(points > 0);

        List<Trip> possibleTrips = new ArrayList<Trip>();

        //Goes through all the stations in from and to and adds the trip to the list if the trip isn't already in that list
        for(Station s1 : from) {
            for (Station s2 : to) {
                if(! possibleTrips.contains(new Trip(s1, s2, points))) {
                    possibleTrips.add(new Trip(s1, s2, points));
                }
            }
        }

        return possibleTrips;
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


    /**
     * 'points' method gives points if 2 stations are connected and negative points if not
     * @param connectivity
     * @return points or -points (int) gained/lost depending whether 2 Stations are connected
     */
    public int points(StationConnectivity connectivity){
        if(connectivity.connected(from, to)) {
            return points;
        }
        else {
            return -points;
        }
    }



}
