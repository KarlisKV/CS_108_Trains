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
    private final List<Station> Stations;
    private final int length;

    /**
     * Private constructor of the Trail class
     * @param Routes (List<Route>) List of routes
     */
    private Trail(List<Route> Routes) {
        Stations = new ArrayList<>();
        int tempLength = 0;
        for (Route r : Routes)  {
            Stations.add(r.station1());
            tempLength = tempLength + r.length();
        }
        Stations.add(Routes.get(Routes.size() - 1).station2());
        length = tempLength;
        this.Routes = Routes;
    }


    //TODO: 3/3/2021 algorithm incorrect (for now it runs infinitely, I think the adding elements part is correct but I don't know how to make it end yet)

    /**
     * longest method
     * @param routes (List<Route>) List of routes
     * @return the longest path of the network made up of the given routes
     */
    public static Trail longest(List<Route> routes) {

        List<Trail> possibleTrails = new ArrayList<>();

        for(Route r : routes) {
            Trail t = new Trail (List.of(r));
            if (!possibleTrails.contains(t)) {
                possibleTrails.add(t);
            }
        }

        List<Trail> possibleTrailsCopy = new ArrayList<>(possibleTrails);
        Trail longestTrail = new Trail(List.of());

        do {

            //This integer helps determine which trail is the longest of them all
            int maxLength = 0;

            for(Trail t : possibleTrailsCopy) {

                //Boolean added determines whether a route has been added to ANY Trail t in possibleTrailsCopy, and if added remains false it clears possibleTrailsCopy, thus terminating the loop
                boolean added = false;

                for(Route r : routes) {
                    if( !(possibleTrailsCopy.get(possibleTrailsCopy.indexOf(t)).Routes.contains(r)) &&
                            //Second boolean = true if Trail t in possibleTrailsCopy can be prolonged by Route r
                            ( possibleTrailsCopy.get(possibleTrailsCopy.indexOf(t)).Routes.get(possibleTrailsCopy.get(possibleTrailsCopy.indexOf(t)).Routes.size() - 1).station2().equals(r.station1()) ) ) {

                        /* Adding Route r to Trail t in possibleTrailsCopy
                          !!! Route r needs to be added to Trail t this way, do not modify,

                          (= setting Trail t in possibleTrailsCopy to a new trail, which
                          contains all routes it contained before, plus Route r)

                          because Trail's length attribute is final and therefore won't be updated
                          unless you create a new instance of Trail
                         */

                        t.Routes.add(r);
                        possibleTrailsCopy.set(possibleTrailsCopy.indexOf(t), new Trail(t.Routes));
                        added = true; //Self-explanatory

                        //Extracting the longest trail from possibleTrailsCopy
                        if(maxLength < possibleTrailsCopy.get(possibleTrailsCopy.indexOf(t)).length) {
                            maxLength = possibleTrailsCopy.get(possibleTrailsCopy.indexOf(t)).length;
                            longestTrail = possibleTrailsCopy.get(possibleTrailsCopy.indexOf(t));
                        }
                    }
                }
                if(!added) {
                    possibleTrails = possibleTrailsCopy;
                    possibleTrailsCopy.clear();
                }
            }

        } while(!possibleTrailsCopy.isEmpty());

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
            return Stations.get(0);
        }
    }

    /**
     * returns the last station of the path, or null, if (and only if) the path is zero length
     * @return the last station of the path, or null, if (and only if) the path is zero length
     */
    // TODO: 3/3/2021 ask assistant whether stations.size() -1 can be changed to -1
    public Station station2() {
        if (length == 0) {
            return null;
        } else {
            return Stations.get(Stations.size() - 1);
        }
    }

    /**
     * toString method returns a textual representation of the path
     * @return (String) a textual representation of the path
     */
    @Override
    public String toString() {
        String complete = "";
        for (Route r : Routes) {
            complete = complete + r.station1().toString();
            if(Routes.indexOf(r) == (Routes.size() - 1)) {
                complete = complete + " - ";
            } else {
                complete = complete + " - " + r.station2().toString();
            }
        }

        complete = complete + " (" + length + ")";

        return complete;
    }


}
