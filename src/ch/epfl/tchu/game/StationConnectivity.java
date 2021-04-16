package ch.epfl.tchu.game;

/**
 * Connectivity of stations interface
 * @author Daniel Polka  (326800)
 */
public interface StationConnectivity {

    /**
     * Abstract method to check whether 2 Stations are connected
     * @param s1 (Station) first station
     * @param s2 (Station) second station
     * @return true if stations are connected
     */
    boolean connected(Station s1, Station s2);
}
