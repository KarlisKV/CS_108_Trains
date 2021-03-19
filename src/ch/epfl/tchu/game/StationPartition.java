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

        if(representative(s1.id()) != -1) {
            return stationMap.get(representative(s1.id())).contains(s2.id());
        } else if(representative(s2.id()) != -1) {
            return stationMap.get(representative(s2.id())).contains(s1.id());
        } else {
            return s1.equals(s2);
        }
    }




    public final static class Builder {

        private final Map<Integer, List<Integer>> stationMap;
        private final int stationCount;

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


    //I wouldn't have copy-pasted code if we were bloody allowed to make bloody public methods lol
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
