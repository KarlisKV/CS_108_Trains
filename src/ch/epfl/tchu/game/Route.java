package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Objects;

/**
 * Route class
 * @author Karlis Velins (325180)
 * @author Daniel Polka  (326800)
 */
public final class Route {

    /**
     * Enum that decides whether route is in a tunnel or underground
     */
    public enum Level {
        OVERGROUND,
        UNDERGROUND;
    }

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * Default constructor for route class
     * @param id (String) id of the route
     * @param station1 (Station) first station
     * @param station2 (Station) second station
     * @param length (int) length of the route
     * @param level (Level) whether the route is underground or not
     * @param color (Color) of the route
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {


        Preconditions.checkArgument(!station1.equals(station2) &&
                (length > Constants.MIN_ROUTE_LENGTH && length < Constants.MAX_ROUTE_LENGTH));

        Objects.requireNonNull(level);
        Objects.requireNonNull(id);

        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.length = length;
        this.level = level;
        this.color = color;
    }

    /**
     * Stations method returns list of both stations
     * @return list of both stations
     */
    public List<Station> stations() {
        List<Station> stations = List.of(station1, station2);

        return stations;
    }

    /**
     * Given one of the 2 stations, returns the opposite station of the selected
     * @param station (Station) given station
     * @return the opposite station of the selected
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station.equals(station1) || station.equals(station2));
        if(station.equals(station1)) {
            return station2;
        }
        else {
            return station1;
        }
    }

    // TODO: 3/2/2021 create this method
    public List<SortedBag<Card>> possibleClaimCards() {
        return null;
    }

    /**
     * claim points returns number of points gained depending on the length of the road
     * @return amount of points gained for getting the road
     */
    public int claimPoints() {

        switch(length) {
            case 1:
                return 1;

            case 2:
                return 2;

            case 3:
                return 4;

            case 4:
                return 7;

            case 5:
                return 10;

            case 6:
                return 15;

            default:
                return 0;

        }
    }

    /**
     * Returns id of route
     * @return id of route
     */
    public String id() {
        return id;
    }

    /**
     * Returns the 1st station
     * @return the 1st station
     */
    public Station station1() {
        return station1;
    }
    /**
     * Returns the 2nd station
     * @return the 2nd station
     */
    public Station station2() {
        return station2;
    }
    /**
     * Returns the length of the route
     * @return the length of the route
     */
    public int length() {
        return length;
    }

    /**
     * Returns the level of the station
     * @return the level of the station
     */
    public Level level() {
        return level;
    }

    /**
     * Returns the color of the station
     * @return the color of the station
     */
    public Color color() {
        return color;
    }


}
