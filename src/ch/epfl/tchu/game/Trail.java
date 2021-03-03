package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Tickets
 * @author Karlis Velins (325180)
 * @author Daniel Polka  (326800)
 */

public final class Trail {

    private final List<Route> Routes;
    private final List<Station> Stations;
    private final int length;


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


    //TODO: 3/3/2021 algorithm incorrect
    /**
     *
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
                for (Route r : routes) {
                    if( ( !t.Routes.contains(r) ) &&
                            //Second boolean = true if Trail t can be prolonged by Route r
                            ( t.Routes.get(t.Routes.size() - 1).station2().equals(r.station1()) ) ) {
                        //Adding Trail t to temp
                        temp.add(t);
                        //Adding Route r to Trail t
                        temp.get(temp.size() - 1).Routes.add(r);
                    }
                }
            }


            possibleTrails = temp;
        } while(possibleTrails.size() > 0);

        return null;
    }


    public int length() {
        return length;
    }

    public Station station1() {
        if (length == 0) {
            return null;
        } else {
            return Stations.get(0);
        }
    }

    public Station station2() {
        if (length == 0) {
            return null;
        } else {
            return Stations.get(Stations.size() - 1);
        }
    }

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
