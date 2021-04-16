package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Trip class represents the trips in the game
 * @author Karlis Velins (325180)
 * @author Daniel Polka  (326800)
 */
public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;

    /**
     *  Default Trip constructor
     * @param from (Station) Departure station
     * @param to (Station) Arrival station
     * @param points (int) points for connecting the 2 stations
     * @throws NullPointerException if one of the two stations is zero and
     * IllegalArgumentException if the number of points is not strictly positive
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
     * @throws IllegalArgumentException if one of the lists is empty, or if the number of points is not strictly positive
     * @return possibleTrips List<Trip> returns list of all possible trips between 2 stations
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Objects.requireNonNull(from);
        Objects.requireNonNull(to);
        Preconditions.checkArgument(points > 0);

        List<Trip> possibleTrips = new ArrayList<>();

        //Goes through all the stations in from and to and adds the trip to the list if the trip isn't already in that list
        for(Station s1 : from) {
            for (Station s2 : to) {
                if(!possibleTrips.contains(new Trip(s1, s2, points))) {
                    possibleTrips.add(new Trip(s1, s2, points));
                }
            }
        }

        return possibleTrips;
    }

    /**
     * Returns the departure Station
     * @return the departure Station
     */
    public Station from() {
        return from;
    }

    /**
     * Returns the arrival Station
     * @return the arrival Station
     */
    public Station to() {
        return to;
    }

    /**
     * Returns the points for connecting the stations
     * @return (int) the points for connecting the stations
     */
    public int points() {
        return points;
    }


    /**
     * 'points' method gives points if 2 stations are connected and negative points if not
     * @param connectivity boolean - true if stations are connected by cars/locomotives, false otherwise
     * @return points or -points (int) gained/lost depending whether 2 Stations are connected
     */
    public int points(StationConnectivity connectivity){

        return connectivity.connected(from, to)?
                points:
                -points;

    }
}
