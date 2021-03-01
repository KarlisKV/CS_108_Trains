package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

public final class Route {

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

    public List<Station> stations() {
        List<Station> stations = List.of(station1, station2);

        return stations;
    }

    public Station stationOpposite(Station station) {

    }

    public String id() {
        return id;
    }

    public Station station1() {
        return station1;
    }

    public Station station2() {
        return station2;
    }

    public int length() {
        return length;
    }

    public Level level() {
        return level;
    }

    public Color color() {
        return color;
    }


}
