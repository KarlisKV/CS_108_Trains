package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class StationPartition implements StationConnectivity {

    private final Map<Integer, List<Integer>> stationMap;

    private StationPartition(Map<Integer, List<Integer>> stationMap) {
        this.stationMap = stationMap;
    }

    @Override
    public boolean connected(Station s1, Station s2) {
        if(!stationMap.containsKey(s1.id()) || !stationMap.containsKey(s2.id())) {

            return s1.equals(s2);

        } else if(stationMap.containsKey(s1.id())) {

            return stationMap.get(s1.id()).contains(s2.id());

        } else if(stationMap.containsKey(s2.id())) {

            return stationMap.get(s2.id()).contains(s1.id());

        } else return false;
    }




    public final static class Builder {

        private final Map<Integer, List<Integer>> stationMap;

        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            stationMap = new HashMap<>();
            int i = 0;
            do {
                stationMap.put(i, new ArrayList<>(List.of(i)));
                ++i;
            }while(i < stationCount);
        }


        public Builder connect(Station s1, Station s2) {
            if(stationMap.containsKey(s1.id())) {
                stationMap.get(s1.id()).add(s2.id());
                stationMap.remove(s2.id());
            } else if (stationMap.containsKey(s2.id())) {
                stationMap.get(s2.id()).add(s1.id());
                stationMap.remove(s1.id());
            } else {
                //Should never happen
                throw new IllegalArgumentException();
            }
            return this;
        }

        public StationPartition build() {
            return new StationPartition(stationMap);
        }



    }



}
