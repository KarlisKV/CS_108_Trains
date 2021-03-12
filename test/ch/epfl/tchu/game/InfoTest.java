package ch.epfl.tchu.game;

import ch.epfl.tchu.gui.Info;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InfoTest {
    @Test
    void testMessages() {
        List<String> playerNames = new ArrayList<>();
        playerNames.add("Karlis");
        playerNames.add("Daniel");
        assertEquals("Karlis et Daniel sont ex æqo avec 20 points !", Info.draw(playerNames, 20));
    }
}