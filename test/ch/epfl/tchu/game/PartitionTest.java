package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.StationPartition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;



public class PartitionTest {

    @Test
    void Works() {
        List<Station> listOfStations = new ArrayList<>(ChMap.stations());
        StationPartition.Builder builder = new StationPartition.Builder(listOfStations.size());
        StationPartition partition = builder
                .connect(listOfStations.get(0), listOfStations.get(3))
                .connect(listOfStations.get(2), listOfStations.get(5))
                .connect(listOfStations.get(10), listOfStations.get(9))
                .build();
        assertTrue(partition.connected(listOfStations.get(0), listOfStations.get(3)));
        assertTrue(partition.connected(listOfStations.get(2), listOfStations.get(5)));
        assertTrue(partition.connected(listOfStations.get(10), listOfStations.get(9)));
        listOfStations = listOfStations.stream()
                .filter((s) -> !(s.id() == 0 || s.id() == 3 || s.id() == 2 || s.id() == 5 || s.id() == 10 || s.id() == 9))
                .collect(Collectors.toList());
        for (Station s1 : listOfStations) {
            for (Station s2 : listOfStations) {
                if (s1.id() == s2.id()) {
                    assertTrue(partition.connected(s1, s2));
                } else { System.out.println(s1.id() + " " + s2.id());
                    assertFalse(partition.connected(s1, s2));
                }
            }
        }
    }

    @Test
    void WorksTwo() {
        List<Station> listOfStations = new ArrayList<>(ChMap.stations());
        StationPartition.Builder builder = new StationPartition.Builder(listOfStations.size());
        StationPartition partition = builder
                .connect(listOfStations.get(0), listOfStations.get(3))
                .connect(listOfStations.get(3), listOfStations.get(4))
                .connect(listOfStations.get(4), listOfStations.get(6))
                .connect(listOfStations.get(6), listOfStations.get(9))
                .connect(listOfStations.get(9), listOfStations.get(21))
                .connect(listOfStations.get(22), listOfStations.get(21))
                .build();
        assertTrue(partition.connected(listOfStations.get(0), listOfStations.get(3)));
        assertTrue(partition.connected(listOfStations.get(3), listOfStations.get(4)));
        assertTrue(partition.connected(listOfStations.get(4), listOfStations.get(6)));
        assertTrue(partition.connected(listOfStations.get(6), listOfStations.get(9)));
        assertTrue(partition.connected(listOfStations.get(9), listOfStations.get(21)));
        assertTrue(partition.connected(listOfStations.get(22), listOfStations.get(21)));
        assertTrue(partition.connected(listOfStations.get(21), listOfStations.get(0)));
        assertTrue(partition.connected(listOfStations.get(22), listOfStations.get(0)));


        listOfStations = listOfStations.stream()
                .filter((s) -> !(s.id() == 0 || s.id() == 3 || s.id() == 4 || s.id() == 6 || s.id() == 9 || s.id() == 21 || s.id() == 22 ))
                .collect(Collectors.toList());
        for (Station s1 : listOfStations) {
            for (Station s2 : listOfStations) {
                if (s1.id() == s2.id()) {
                    assertTrue(partition.connected(s1, s2));
                } else { System.out.println(s1.id() + " " + s2.id());
                    assertFalse(partition.connected(s1, s2));
                }
            }
        }
    }

    @Test
    void builderConstructorFailsWithNegativeArgument(){
        IntStream.range(-10, 0).forEach((n) -> {
            assertThrows(IllegalArgumentException.class, () -> {
                new StationPartition.Builder(n);
            });
        });
    }

    @Test
    void builderWorksCorrectlyWithUniquePartition(){
        StationPartition.Builder builder = new StationPartition.Builder(ChMap.stations().size());
        List<Station> stations = ChMap.stations();
        for (int i = 0; i < stations.size() - 1; ++i){
            builder.connect(stations.get(i), stations.get(i + 1));
        }
        StationPartition partition = builder.build();
        for (Station s1 : ChMap.stations()){
            for (Station s2 : ChMap.stations()) {
                assertTrue(partition.connected(s1, s2));
            }
        }
    }

    @Test
    void BuilderWorksWithoutConnectingAnyStation(){
        StationPartition.Builder builder = new StationPartition.Builder(ChMap.stations().size());
        StationPartition partition = builder.build();
        for (Station s1: ChMap.stations()){
            for (Station s2: ChMap.stations()){
                if (s1.id() == s2.id()){
                    assertTrue(partition.connected(s1, s2));
                }else{
                    assertFalse(partition.connected(s1, s2));
                }
            }
        }
    }

    @Test
    void BuilderWorksWithStationsOutOfBound(){
        StationPartition partition = new StationPartition.Builder(15).build();
        List<Station> stationsOutOfBounds = ChMap.stations().subList(15, 51);
        for (Station s1 : stationsOutOfBounds){
            for (Station s2 : stationsOutOfBounds){
                if (partition.connected(s1, s2)){
                    assertEquals(s1, s2);
                }
                else {
                    assertNotEquals(s1, s2);
                }
            }
        }
        assertFalse(partition.connected(ChMap.stations().get(15), ChMap.stations().get(16)));
        assertFalse(partition.connected(ChMap.stations().get(16), ChMap.stations().get(15)));
        assertFalse(partition.connected(ChMap.stations().get(7), ChMap.stations().get(16)));
        assertFalse(partition.connected(ChMap.stations().get(16), ChMap.stations().get(7)));
        assertTrue(partition.connected(ChMap.stations().get(15), ChMap.stations().get(15)));
    }

    @Test
    void BuilderWorksConnectingSomeStationsInDifferentOrders(){
        StationPartition.Builder builder1 = new StationPartition.Builder(ChMap.stations().size());
        StationPartition.Builder builder2 = new StationPartition.Builder(ChMap.stations().size());
        int[] pairs= {0, 1, 2, 3, 7, 8, 9, 12, 13, 15, 17, 10};
        for (int i = 0; i < pairs.length; i += 2){
            Station s1 = ChMap.stations().get(pairs[i]);
            Station s2 = ChMap.stations().get(pairs[i + 1]);
            builder1.connect(s1, s2);
            builder2.connect(s2, s1);
        }
        StationPartition partition1 = builder1.build();
        StationPartition partition2 = builder2.build();
        for (int i = 0; i < pairs.length; i += 2){
            Station s1 = ChMap.stations().get(pairs[i]);
            Station s2 = ChMap.stations().get(pairs[i + 1]);
            assertEquals(partition1.connected(s1, s2), partition2.connected(s1, s2));
        }
    }
}

