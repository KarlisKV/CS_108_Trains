package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.GraphicalPlayerAdapter;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static javafx.application.Application.launch;

public  final  class Stage11Test extends Application {
    public static void main (String [] args)     {launch (args); }

    @Override
    public void start (Stage primaryStage)    {
        SortedBag <Ticket> tickets = SortedBag.of (ChMap.tickets ());
        Map <PlayerId, String> names =
                Map.of (PLAYER_1, "Alix" , PLAYER_2, "Daniel" );
        Map <PlayerId, Player> players =
                Map.of (PLAYER_1, new GraphicalPlayerAdapter(),
                        PLAYER_2, new GraphicalPlayerAdapter ());
        Random rng = new Random ();
        new Thread (() -> Game.play (players, names, tickets, rng))
                .start ();
    }
}