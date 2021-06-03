package ch.epfl.tchu.game;

import java.util.*;

/**
 * Trail class represents a path in a player's network
 * @author Karlis Velins (325180)
 * @author Daniel Polka  (326800)
 */

public final class Trail {

    private final List<Route> routes;
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
        this.routes = Routes;

        int tempLength = 0;
        if(Routes != null && station1 != null && station2 != null){
            for (Route r : Routes)  {
                tempLength = tempLength + r.length();
            }
        }
        length = tempLength;
    }


    /**
     *
     * @param ticket ticket for which you want the path to be shown
     * @param avoid list of routes to be avoided, can be empty (but not null)
     * @return the shortest possible trail connecting the stations of the ticket,
     * avoiding list of routes avoid, or returns null if no path is possible.
     * In the case of a ticket with multiple possible destinations, automatically
     * chooses shortest possible trip. Works most of the time :)
     * @throws NullPointerException if ticket is null or if avoid is null
     */
    public static Trail shortest(Ticket ticket, List<Route> avoid) {

        Objects.requireNonNull(ticket);
        Objects.requireNonNull(avoid);

        List<Station> from = ticket.from();
        List<Station> to = ticket.to();

        List<Route> firstRoutes = new ArrayList<>();
        List<Route> lastRoutes = new ArrayList<>();

        Station firstStation = null;
        Station lastStation = null;

        List<Route> possibleRoutes = new ArrayList<>(ChMap.routes());
        possibleRoutes.removeAll(avoid);

        for(Route r : possibleRoutes) {
            for(Station s1 : from) {
                if(r.stations().contains(s1))
                    firstRoutes.add(r);
            }

            for(Station s2 : to) {
                if(r.stations().contains(s2) && !firstRoutes.contains(r))
                    lastRoutes.add(r);
            }
        }

        if(!firstRoutes.isEmpty() && !lastRoutes.isEmpty()) {

            Route firstRoute = firstRoutes.get(0);
            Route lastRoute = lastRoutes.get(0);
            double minDistance = firstRoute.distance(lastRoute);

            for (Route fr : firstRoutes)
                for (Route lr : lastRoutes) {
                    double d = fr.distance(lr);
                    if (d <= minDistance) {
                        minDistance = d;
                        firstRoute = fr;
                        lastRoute = lr;
                    }
                }

            for (Station s1 : from) if (firstRoute.stations().contains(s1)) firstStation = s1;
            for (Station s2 : to) if (lastRoute.stations().contains(s2)) lastStation = s2;

            boolean end = false;
            List<Route> connectingRoutes = new ArrayList<>(List.of(firstRoute));

            while (!end) {

                Route next = null;

                for (Route r : possibleRoutes) {

                    if (connectingRoutes.contains(r)) continue;

                    boolean connected = (r.stations().contains(connectingRoutes.get(connectingRoutes.size() - 1).station1())
                            || r.stations().contains(connectingRoutes.get(connectingRoutes.size() - 1).station2()));

                    double checkDistance = r.distance(lastRoute);

                    if (connected) {
                        if(checkDistance <= minDistance || next == null) {
                            minDistance = checkDistance;
                            next = r;
                        }
                    }
                }


                if (next != null)
                    connectingRoutes.add(next);
                 else {

                    end = true;
                }

                if (connectingRoutes.get(connectingRoutes.size() - 1).stations().contains(lastStation)) end = true;

            }

            if(connectingRoutes.get(0).stations().contains(firstStation) &&
                    connectingRoutes.get(connectingRoutes.size() - 1).stations().contains(lastStation))
                return new Trail(connectingRoutes, firstStation, lastStation);


            //Only returns null if there is no possible starting point, ending point or any possible middle points
        } return null;
    }



    /**
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
                    boolean doesntContain = !(c.routes.contains(r));

                    if(canBeAdded && doesntContain) {
                        rs.add(r);
                    }
                }

                for(Route r : rs) {

                    //Can't add r directly to c because otherwise it would potentially mess up the other trails (see algorithm)
                    List<Route> prolongedRoutes = new ArrayList<>(c.routes);
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
     * @return the length of the path
     */
    public int length() {
        return length;
    }

    /**
     * @return the first station of the path, or null if (and only if) the path is zero length
     */
    public Station station1() {

        return length == 0?
                null:
                station1;

    }

    /**
     * @return the last station of the path, or null, if (and only if) the path is zero length
     */

    public Station station2() {

        return length == 0?
                null:
                station2;
    }

    /**
     * @return (String) a textual representation of the path
     */
    @Override
    public String toString() {

        Station station;
        StringBuilder complete = new StringBuilder();
        if (routes != null)
            if (!routes.isEmpty()) {
                station = station1;
                complete.append(station1.toString());
                complete.append(" - ");


            for (Route r : routes) {
                complete.append(r.stationOpposite(station).toString());
                station = r.stationOpposite(station);


                if (!(routes.indexOf(r) == (routes.size() - 1))) {
                    complete.append(" - ");
                }
            }
        }

        if(routes != null) {
            if(!routes.isEmpty()) complete.append(" (").append(length).append(")");
            else complete.append("Empty trail");
        } else complete.append("Empty trail");

        return complete.toString();
    }


    /**
     * Returns immutable list of routes in this trail. Useful for highlighting trails
     * @return immutable list of routes in this trail
     */
    public List<Route> routes() {
        return List.copyOf(routes);
    }


}
