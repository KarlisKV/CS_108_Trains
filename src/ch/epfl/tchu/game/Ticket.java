package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;


import java.util.*;

/**
 * Tickets represents the tickets used in the game
 * @author Karlis Velins (325180)
 * @author Daniel Polka  (326800)
 */
public final class Ticket implements Comparable<Ticket> {


    private final List<Trip> trips;
    private final String text;

    /**
     * Primary constructor for Ticket class
     * @param trips (List<Trip>) list of trips in the Ticket
     * @throws IllegalArgumentException if the list of trips are empty or if the departure station names are the same
     */
    public Ticket(List<Trip> trips) {

        Preconditions.checkArgument(!trips.isEmpty());

        for (Trip t : trips) Preconditions.checkArgument(trips.get(0).from().name().equals(t.from().name()));


        this.text = computeText(trips);
        this.trips = List.copyOf(trips);

    }

    /**
     * Secondary constructor for Ticket class
     * @param from (Station) departure station
     * @param to (Station) arrival station
     * @param points (int) points for connecting stations
     */
    public Ticket(Station from, Station to, int points) { this(List.of(new Trip(from, to, points))); }

    /**
     * computeText sets up the strings of the names of the departures-arrivals with
     * the corresponding points. For reference use paragraph 2.4.5 in the instructions
     * @param trips (List<Trip>) list of trips
     * @return the correct representation of the strings
     */

    private static String computeText(List<Trip> trips) {

        String tempText = trips.get(0).from().name() + " - ";
        TreeSet<String> sortedDestinations = new TreeSet<>();

        for(Trip t: trips) {
            sortedDestinations.add(t.to().name() + " ("+t.points() + ")");
        }

        String tempString = String.join(", ", sortedDestinations);

        if(trips.size() > 1) {
            tempText += "{" + tempString + "}";

        }
        else {
            tempText += tempString;
        }

        return tempText;
    }


    /**
     * points gives the points depending on whether the 2 stations
     * are connected or not
     * @param connectivity (StationConnectivity) connectivity of 2 stations
     * @return the maxValue of the list. Max points gained if the trip is connected
     * Min points lost if trip is not connected
     */
    public int points(StationConnectivity connectivity) {

        int maxValue = Integer.MIN_VALUE;

        for (Trip trip : trips) {

            if (trip.points(connectivity) > maxValue) {
                maxValue = trip.points(connectivity);
            }

        }

        return maxValue;
    }

    /**
     * Compares two ticket alphabetic order
     * @param that (Ticket)
     * @return this is strictly less than that,
     * a strictly positive integer if this is strictly greater than that,
     * and zero if the two are equal
     */
    @Override
    public int compareTo(Ticket that) { return this.text().compareTo(that.text()); }

    /**
     * Returns the text created from the computeText method
     * @return text created from the computeText method
     */
    public String text() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }


    /**
     * @return all the stations from which the ticket "can begin"
     */
    public List<Station> from() {

        Set<Station> fromStations = new TreeSet<>((s1, s2) -> {

            if(s1.id() == s2.id())
                return 0;

            return s1.id() > s2.id() ?
                    1 : -1;
        });

        for(Trip t : trips) fromStations.add(t.from());

        return new ArrayList<>(fromStations);
    }

    /**
     * @return all the stations at which the ticket "can end"
     */
    public List<Station> to() {

        Set<Station> toStations = new TreeSet<>((s1, s2) -> {

            if(s1.id() == s2.id())
                return 0;

            return s1.id() > s2.id() ?
                    1 : -1;
        });
        for(Trip t : trips) toStations.add(t.to());

        return new ArrayList<>(toStations);
    }


}


