package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import com.sun.prism.image.Coords;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Route class represents the routes used for the game
 * @author Karlis Velins (325180)
 * @author Daniel Polka  (326800)
 */
public final class Route {

    /**
     * Enum that decides whether route is in a tunnel or underground
     */
    public enum Level {
        OVERGROUND,
        UNDERGROUND
    }

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;
    private final int posX;
    private final int posY;

    /**
     * Default constructor for route class
     * @param id (String) id of the route
     * @param station1 (Station) first station
     * @param station2 (Station) second station
     * @param length (int) length of the route
     * @param level (Level) whether the route is underground or not
     * @param color (Color) of the route
     * @throws IllegalArgumentException if the two stations are equal (in the sense of the method equals)
     * or if the length is not within the acceptable limits (provided by the interface Constants),
     * @throws NullPointerException if the identity, one of the two stations or the level are zero.
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color, int posX, int posY) {
        Preconditions.checkArgument(!station1.equals(station2) &&
                (length >= Constants.MIN_ROUTE_LENGTH && length <= Constants.MAX_ROUTE_LENGTH));
        Objects.requireNonNull(level);
        Objects.requireNonNull(id);
        Objects.requireNonNull(station1);
        Objects.requireNonNull(station2);

        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.length = length;
        this.level = level;
        this.color = color;
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Stations method returns list of both stations
     * @return list of both stations
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * Given one of the 2 stations, returns the opposite station of the selected
     * @param station (Station) given station
     * @return the opposite station of the selected
     * @throws IllegalArgumentException if the given station is neither the first nor the second station of the route
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


    /**
     * possibleClaimCards method
     * @return the list of all the sets of cards that could be played to
     * (attempt to) grab the road, sorted in ascending order of number of locomotive cards, then by suit,
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        List<SortedBag<Card>> possibleCards = new ArrayList<>();

        int necessaryCards = length;
        List<Color> allColours = List.of(Color.BLACK, Color.VIOLET ,Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED, Color.WHITE);

        for (int numberOfLocomotives = 0; numberOfLocomotives < necessaryCards; ++numberOfLocomotives) {

                //Mix of locomotives & colour cards (1x locomotive + other cards, 2x locomotives + other cards, ..., only locomotives)
                for(Color c : allColours) {
                    if(color == null || color.equals(c)) {

                        List<Card> cards = new ArrayList<>();

                        if(level.equals(Level.UNDERGROUND)) {
                            for (int locomotives = 0; locomotives < numberOfLocomotives; ++locomotives) {
                                cards.add(Card.LOCOMOTIVE);
                            }
                        }

                         for (int colourCards = 0; colourCards < (necessaryCards - numberOfLocomotives); ++colourCards) {
                             cards.add(Card.of(c));
                         }


                        possibleCards.add(SortedBag.of(cards));
                        cards.clear();
                    }
                }

            if(level.equals(Level.OVERGROUND)) {
                numberOfLocomotives = necessaryCards;
            }

        }

        if(level.equals(Level.UNDERGROUND)) {
            possibleCards.add(SortedBag.of(length, Card.LOCOMOTIVE));
        }

        return possibleCards;
    }



    /**
     * additionalClaimCardsCount returns (int) the number of drawn cards that are equal to the played cards
     * @param claimCards (SortedBag<Card>) list of played cards to claim the route
     * @param drawnCards (SortedBag<Card>) list of cards to draw after playing
     * @return number of drawn cards that correspond to the played cards
     * @throws IllegalArgumentException if the road to which it is applied is not a tunnel, or if drawnCardsdoes not contain exactly 3 maps
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {

        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(level.equals(Level.UNDERGROUND));

        int nrOfPts = 0;

        for(int i = 0; i < drawnCards.size(); i++) {

            if(claimCards.contains(drawnCards.get(i)) || drawnCards.get(i).equals(Card.LOCOMOTIVE)) {
                nrOfPts += 1;
            }

        }

        return nrOfPts;
    }

    /**
     * claim points returns number of points gained depending on the length of the road
     * @return amount of points gained for getting the road
     */
    public int claimPoints() {

        return Constants.ROUTE_CLAIM_POINTS.get(length);

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


    /**
     * @param that route from which you want to know the distance
     * @return the distance between this route and that route
     * @throws NullPointerException if that is null
     */
    public double distance(Route that) {

        Objects.requireNonNull(that);

        int distX = this.posX - that.posX;
        int distY= this.posY - that.posY;

        return Math.sqrt(distX*distX + distY*distY);
    }

}
