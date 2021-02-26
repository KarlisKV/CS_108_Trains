package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Ticket implements Comparable<Ticket> {

    private static Station from;
    private static Station to;
    private static int points;

    Ticket(List<Trip> trips) {
        Objects.requireNonNull(trips);
        for (Trip t : trips) {
            Preconditions.checkArgument(trips.get(0).from().equals(t.from()));
        }
        // TODO: 2/24/2021 how to check if all stations from which the journey was started dont have the same name

        //The from station can take any "from" station from the "trips" list, since they should all be the same
        from = trips.get(0).from();

        // TODO: I have no clue how to "choose" the destination station from all possible destination stations in "trips", so for now it's random

        int rand = (int) (trips.size()*Math.random());
        to = trips.get(rand).to();
        points = trips.get(rand).points();

    }

    // TODO: 2/24/2021 how to call using 'this' 
    Ticket(Station from, Station to, int points) {
        Preconditions.checkArgument(from != null && to != null && points > 0);

        List<Trip> trip = new ArrayList<>();
        trip.add(new Trip(from, to, points));

        new Ticket(trip);
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
