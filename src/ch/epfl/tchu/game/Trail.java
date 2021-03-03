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

        do {
            List<Trail> temp = new ArrayList<>();
            List<Route> continuingPossibleTrailRoutes = new ArrayList<>();
            for(Trail t : possibleTrails) {
                //Adding Trail t to temp
                temp.add(t);

                for (Route r : routes) {
                    if( ( !temp.get(temp.indexOf(t)).Routes.contains(r) ) &&
                            //Second boolean = true if Trail t in temp can be prolonged by Route r
                            ( temp.get(temp.indexOf(t)).Routes.get(t.Routes.size() - 1).station2().equals(r.station1()) ) ) {
                        //Adding Route r to Trail t in temp
                        temp.get(temp.indexOf(t)).Routes.add(r);
                    }
                }

            }

            possibleTrails = temp;
        } while(possibleTrails.size() > 0);

        return null;
    }

    /**
     * Returns the length of the path
     * @return the length of the path
     */
    public int length() {
        return length;
    }

    /**
     * Returns the first station of the path, or nullif (and only if) the path is zero length
     * @return the first station of the path, or nullif (and only if) the path is zero length
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
