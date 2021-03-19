package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * StationPartition and Builder
 *
 * @author Daniel Polka  (326800)
 */

public final class StationPartition implements StationConnectivity {

    private final Map<Integer, List<Integer>> stationMap;

    /**
     * Private constructor of StationPartition class
     * @param stationMap map of stations
     */
    private StationPartition(Map<Integer, List<Integer>> stationMap) {
        this.stationMap = stationMap;
    }
    /**
     * Implemented method from StationConnectivity
     * @param s1 (Station) first station
     * @param s2 (Station) second station
     * @return true if stations are connected
     */
    @Override
    public boolean connected(Station s1, Station s2) {

        if(representative(s1.id()) != -1) {
            return stationMap.get(representative(s1.id())).contains(s2.id());
        } else if(representative(s2.id()) != -1) {
            return stationMap.get(representative(s2.id())).contains(s1.id());
        } else {
            return s1.equals(s2);
        }
    }


    /**
     * Nested builder class for StationPartition
     */

    public final static class Builder {

        private final Map<Integer, List<Integer>> stationMap;
        private final int stationCount;

        /**
         * Public constructor for Builder class
         * @param stationCount (int) number of stations
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            this.stationCount = stationCount;
            stationMap = new HashMap<>();
            int i = 0;
            do {
                stationMap.put(i, new ArrayList<>(List.of(i)));
                ++i;
            }while(i < stationCount);
        }


        /**
         * joins the sub-assemblies containing the two stations passed as arguments, by “electing”
         * one of the two representatives as representative of the joined sub-assembly;
         * @param s1 (Station) station 1
         * @param s2 (Station) station 2
         * @return the builder (this)
         */
        public Builder connect(Station s1, Station s2) {

            if (stationMap.get(representative(s1.id())).size() > 1 ^ stationMap.get(representative(s2.id())).size() > 1) {

                if(stationMap.get(representative(s1.id())).size() > 1) {

                    stationMap.get(representative(s1.id())).add(s2.id());
                    stationMap.remove(s2.id());

                } else {

                    stationMap.get(representative(s2.id())).add(s1.id());
                    stationMap.remove(s1.id());
                }

            } else if (stationMap.containsKey(s1.id())) {

                stationMap.get(s1.id()).add(s2.id());
                stationMap.remove(s2.id());

            } else if (stationMap.containsKey(s2.id())) {

                stationMap.get(s2.id()).add(s1.id());
                stationMap.remove(s1.id());
            }

            return this;
        }


        /**
         * returns the flattened partition of the stations corresponding to the deep partition under construction by this builder
         * @return the flattened partition of the stations corresponding to the deep partition under construction by this builder
         */
        public StationPartition build() {
            return new StationPartition(stationMap);
        }

        private int representative(int stationID) {

            int ID = -1;

            for(int i = 0; i < stationCount; ++i) {
                if(stationMap.containsKey(i)) {
                    if(stationMap.get(i).contains(stationID)) {
                        ID = i;
                    }
                }
            }

            return ID;
        }



    }


    /**
     * Private representative method for the builder
     * @param stationID (int) id number of a station
     * @return the id of the representative of the sub-assembly containing it.
     */
    private int representative(int stationID) {

        int ID = -1;

        int maximumStation = ChMap.stations().size();

        for(int i = 0; i < maximumStation; ++i) {
            if(stationMap.containsKey(i)) {
                if(stationMap.get(i).contains(stationID)) {
                    ID = i;
                }
            }
        }

        return ID;
    }




}
