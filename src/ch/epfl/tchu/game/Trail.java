package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Trail class
 * @author Karlis Velins (325180)
 * @author Daniel Polka  (326800)
 */

public final class Trail {

    private final List<Route> Routes;
    private final Station station1;
    private final Station station2;
    private final int length;

    /**
     * Private constructor for Trail class
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

        List<Trail> possibleTrails = new ArrayList<>();
        List<Route> twoWayRoutes = new ArrayList<>(routes);

        for(Route r : routes) {
            Trail t = new Trail (new ArrayList<>(List.of(r)), r.station1(), r.station2());
            if (!possibleTrails.contains(t)) {
                possibleTrails.add(t);
            }
            twoWayRoutes.add(new Route(r.id(), r.station2(), r.station1(), r.length(), r.level(), r.color()));
        }

        Trail longestTrail = new Trail(List.of(), null, null);
        List<Trail> possibleTrailsCopy = new ArrayList<>(possibleTrails);
        List<Trail> possibleTrailsCopy2;

        int debug3 = 1;
        int debug4 = 0;

        do {

            //This integer helps determine which trail is the longest of them all
            int maxLength = 0;
            //Boolean added determines whether a route has been added to ANY Trail t in possibleTrails, and if added remains false it clears possibleTrails, thus terminating the loop
            boolean added = false;
            //Index of possibleTrailsCopy
            int index = 0;

            int debug = 0;
            int debug2 = 1;

            for(Trail t : possibleTrails) {

                //Index of routes (necessary because of a bug with .contains(Object o))
                int rIndex = 0;



                if(debug3 == 2) {
                    System.out.println("index: " + index + " / iteration: " + debug3);
                    System.out.println("trail: " + t);
                    System.out.println();
                    System.out.println();

                }



                for(Route r : twoWayRoutes) {


                    boolean verifyNoCopy = !(possibleTrailsCopy.get(index).Routes.contains(twoWayRoutes.get(rIndex)));


                    //TODO: fix trailStation2 or "duplicate" roads so they go both ways
                    //Trying with duplication
                    Station trailStation2 = possibleTrailsCopy.get(index).station2;
                    Station routeStation1 = twoWayRoutes.get(rIndex).station1();
                    Station routeStation2 = twoWayRoutes.get(rIndex).station2();
                    //firstEqualsSecond = true if Trail t in possibleTrailsCopy can be prolonged by Route r
                    boolean firstEqualsSecond = trailStation2.equals(routeStation1);




                    if(debug3 == 2) {
                        System.out.println("rIndex: " + rIndex + " /iteration: " + debug3);

                        System.out.println("verifyNoCopy: " + verifyNoCopy);
                        System.out.println("trailStation2: " + trailStation2);
                        System.out.println("routeStation1: " + routeStation1);

                        System.out.println("firstEqualsSecond: " + firstEqualsSecond);
                        System.out.println();

                    }



                    if( verifyNoCopy && firstEqualsSecond ) {

                        /* Adding Route r to Trail t in possibleTrailsCopy
                          !!! Route r needs to be added to Trail t this way, do not modify,

                          (= setting Trail t in possibleTrailsCopy to a new trail, which
                          contains all routes it contained before, plus Route r)

                          because Trail's length attribute is final and therefore won't be updated
                          unless you create a new instance of Trail
                         */

                        if(debug3 == 2) {
                            System.out.println();
                            System.out.println("Adding route: " + twoWayRoutes.get(rIndex));
                        }

                        t.Routes.add(twoWayRoutes.get(rIndex));
                        Trail trail = new Trail(t.Routes, t.station1, twoWayRoutes.get(rIndex).station2());
                        possibleTrailsCopy.set(index,trail);
                        added = true; //Self-explanatory

                        if(debug3 == 2) {
                            System.out.println("new Trail: " + possibleTrailsCopy.get(index));
                            System.out.println();
                        }
                        ++debug;
                        ++debug4;

                        //Extracting the longest trail from possibleTrails
                        if(possibleTrailsCopy.contains(trail)) {
                            if(maxLength < possibleTrailsCopy.get(index).length) {
                                maxLength = possibleTrailsCopy.get(index).length;
                                longestTrail = possibleTrailsCopy.get(possibleTrailsCopy.indexOf(trail));

                                /*
                                System.out.println("#" + debug3 + "." + debug2 + ". longest trail: " + longestTrail);
                                System.out.println();
                                 */
                                ++debug2;
                            }
                        }
                    }
                    ++rIndex;
                }
                if(debug3 == 2) {
                    System.out.println();
                }
                ++index;
            }


            System.out.println("Trail 2: " + possibleTrailsCopy.get(2));

            possibleTrailsCopy2 = new ArrayList<>(possibleTrailsCopy);

            if(!added) {
                possibleTrailsCopy.clear();
            //    System.out.println("cleared, " + debug4 + " total elements added, " + debug3 + " total iterations");
            }

            possibleTrails = new ArrayList<>(possibleTrailsCopy2);


            System.out.println(debug + " elements added (iteration #" + debug3 + ")");
            System.out.println();


            ++debug3;

        } while(!possibleTrailsCopy.isEmpty());

        System.out.println(longestTrail.Routes);

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
        if (length == 0) {
            return null;
        } else {
            return station1;
        }
    }

    /**
     * returns the last station of the path, or null, if (and only if) the path is zero length
     * @return the last station of the path, or null, if (and only if) the path is zero length
     */

    public Station station2() {
        if (length == 0) {
            return null;
        } else {
            return station2;
        }
    }

    /**
     * toString method returns a textual representation of the path
     * @return (String) a textual representation of the path
     */
    @Override
    public String toString() {

        String complete = station1.toString() + " - ";
        Station station = station1;

        for (Route r : Routes) {
            complete = complete + r.stationOpposite(station).toString();
            station = r.stationOpposite(station);


            if(!(Routes.indexOf(r) == (Routes.size() - 1))) {
                complete = complete + " - ";
            }
        }

        complete = complete + " (" + length + ")";

        return complete;
    }


}
