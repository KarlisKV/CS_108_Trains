package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeMap;

import static ch.epfl.tchu.game.Color.ALL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeckTest {

    private static final List<Integer> FACE_UP_CARD_SLOTS = List.of(0, 1, 2, 3, 4);

    /**
     * Nombre d'emplacements pour les cartes face visible.
     */
    private static final int FACE_UP_CARDS_COUNT = FACE_UP_CARD_SLOTS.size();

    @Test
    void deckCreated() {
        assertEquals(5, FACE_UP_CARDS_COUNT);
    }
}






