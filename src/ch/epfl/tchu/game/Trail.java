package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Trail class represents a path in a player's network
 * @author Karlis Velins (325180)
 * @author Daniel Polka  (326800)
 */

public final class Trail {

    private final List<Route> Routes;
    private final Station station1;
    private final Station station2;
    private final int length;

    /**
     * Private constructor for Trail class (shouldn't be instantiated outside of Trail)
     * @param Routes (List<Route>) List of Routes
     * @param station1 (Station) First station
     * @param station2 (Station) Second station
     */
    private Trail(List<Route> Routes, Station station1, Station station2) {

        this.station2 = station2;
        this.station1 = station1;
        this.Routes = Routes;

        int tempLength = 0;
        if(Routes != null && station1 != null && station2 != null){
            for (Route r : Routes)  {
                tempLength = tempLength + r.length();
            }
        }
        length = tempLength;
    }

    /**
     * longest method
     * @param routes (List<Route>) List of routes
     * @return the longest path of the network made up of the given routes
     */
    public static Trail longest(List<Route> routes) {

        //Avoids error if routes happens to be an immutable list
        routes = new ArrayList<>(routes);
        List<Trail> possibleTrails = new ArrayList<>();

        for(Route r : routes) {
            Trail trail = new Trail(new ArrayList<>(List.of(r)), r.station1(), r.station2());
            possibleTrails.add(trail);
            Trail trail2 = new Trail(new ArrayList<>(List.of(r)), r.station2(), r.station1());
            possibleTrails.add(trail2);
        }

        List<Trail> cs = new ArrayList<>(possibleTrails);
        Trail longestTrail = new Trail(null, null, null);
        int maxLength = 0;

        do{


            List<Trail> cs2 = new ArrayList<>();

            for(Trail c : cs) {

                List<Route> rs = new ArrayList<>();

                for(Route r : routes) {
                    boolean canBeAdded = c.station2.equals(r.station1()) || c.station2.equals(r.station2());
                    boolean doesntContain = !(c.Routes.contains(r));

                    if(canBeAdded && doesntContain) {
                        rs.add(r);
                    }
                }

                for(Route r : rs) {

                    //Can't add r directly to c because otherwise it would potentially mess up the other trails (see algorithm)
                    List<Route> prolongedRoutes = new ArrayList<>(c.Routes);
                    prolongedRoutes.add(r);

                    //station2 of new trail = the station that wasn't already c.station2
                    Station definedStation2 = r.stationOpposite(c.station2);

                    //Have to make a new Trail because all Trail's attributes are final, therefore can't be updated by adding the route directly
                    Trail trail = new Trail(prolongedRoutes, c.station1, definedStation2);
                    cs2.add(trail);

                    if(trail.length > maxLength) {
                        longestTrail = trail;
                        maxLength = longestTrail.length;
                    }
                }
            }

            cs = cs2;

        }while(!cs.isEmpty());


        if(longestTrail.station1 == null) {
            maxLength = 0;
            for(Route r : routes) {
                if(r.length() > maxLength) {
                    longestTrail = new Trail(new ArrayList<>(List.of(r)), r.station1(), r.station2());
                    maxLength = r.length();
                }
            }
        }

        return longestTrail;
    }

    /**
     * Returns the length of the path
     * @return the length of the path
     */
    public int length() {
        return length;
    }

    /**
     * Returns the first station of the path, or null if (and only if) the path is zero length
     * @return the first station of the path, or null if (and only if) the path is zero length
     */
    public Station station1() {

        return length == 0?
                null:
                station1;

    }

    /**
     * returns the last station of the path, or null, if (and only if) the path is zero length
     * @return the last station of the path, or null, if (and only if) the path is zero length
     */

    public Station station2() {

        return length == 0?
                null:
                station2;
    }

    /**
     * toString method returns a textual representation of the path
     * @return (String) a textual representation of the path
     */
    @Override
    public String toString() {

        Station station;
        StringBuilder complete = new StringBuilder();
        if (Routes != null)
            if (!Routes.isEmpty()) {
                station = station1;
                complete.append(station1.toString());
                complete.append(" - ");


            for (Route r : Routes) {
                complete.append(r.stationOpposite(station).toString());
                station = r.stationOpposite(station);


                if (!(Routes.indexOf(r) == (Routes.size() - 1))) {
                    complete.append(" - ");
                }
            }
        }

        if(Routes != null) {
            if(!Routes.isEmpty()) complete.append(" (").append(length).append(")");
            else complete.append("Empty trail");
        } else complete.append("Empty trail");

        return complete.toString();
    }


}
