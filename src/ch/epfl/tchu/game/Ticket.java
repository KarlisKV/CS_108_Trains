package ch.epfl.tchu.game;

import java.util.List;

public final class Ticket implements Comparable<Ticket> {

    private static Station from;
    private static Station to;
    private static int points;

    Ticket(List<Trip> trips) {

    }

    Ticket(Station from, Station to, int points) {
        this();
    }


    public String text() {
        return (this.from + " - " + this.to + " (" + this.points + ")");
    }

    @Override
    public int compareTo(Ticket o) {
        return 0;
    }
}
