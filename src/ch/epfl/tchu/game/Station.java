package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Station
 *
 * @author Daniel Polka  (326800)
 */
public final class Station {

    private int id;
    private String name;

    /**
     * Default Constructor for Station
     * @throws IllegalArgumentException if id < 0
     * @param id (int) identification number raging from 0 to 50
     * @param name (String) name of the Station
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);

        this.id = id;
        this.name = name;
    }

    /**
     * Returns the id of the Station
     * @return the id of the Station
     */
    public int id() { return id; }

    /**
     * Returns the name of the Station
     * @return the name of the Station
     */
    public String name() { return name; }

    /**
     * Returns the name of the Station
     * @return the name of the Station
     */
    @Override
    public String toString() {
        return name;
    }
}
