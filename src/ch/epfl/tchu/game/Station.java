package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

public final class Station {

    private int id;
    private String name;

    /**
     * Default Constructor for Station
     * @param id (int) identification number raging from 0 to 50
     * @param name (String) name of the Station
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);

        this.id = id;
        this.name = name;
    }

    //Getters
    public int id() { return id; }

    public String name() { return name; }

    @Override
    public String toString() {
        return name;
    }
}
