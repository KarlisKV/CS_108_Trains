package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrailTest {

    @Test
    public void longestWorks() {
        assertEquals(54, Trail.longest(ChMap.routes().subList(0,40)).length());
    }
}
