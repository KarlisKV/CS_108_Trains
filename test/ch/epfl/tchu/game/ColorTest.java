package ch.epfl.tchu.game;

import ch.epfl.tchu.net.Serde;
import org.junit.jupiter.api.Test;

import java.util.List;

import static ch.epfl.tchu.game.Color.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ColorTest {
    @Test
    void colorValuesAreDefinedInTheRightOrder() {
        Serde<Integer> intSerde = null;
        
        assertEquals ( "2021" , intSerde.serialize ( 2021 ));
        assertEquals ( 2021 , intSerde.deserialize ( "2021" ));
    }

    @Test
    void colorAllIsDefinedCorrectly() {
        assertEquals(List.of(Color.values()), ALL);
    }

    @Test
    void colorCountIsDefinedCorrectly() {
        assertEquals(8, COUNT);
    }
}