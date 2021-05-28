package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Game;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Main class used to launch the server. Make sure the client chose the correct hostname and port,
 * or else you'll suffer a great deal of pain from it (actually mostly the person using the client will).
 *
 * ServerMain class shouldn't be instantiated, however it is impossible to add a private constructor or else
 * the program crashes on launch, thus subjecting the user to the aforementioned pain.
 * @author Daniel Polka  (326800)
 */
public final class ServerMain extends Application {

    /**
     * Method to start the server
     * @param args program arguments, set in Run -> Edit Configurations... -> this class
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * "The main entry point for all JavaFX applications. The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     * NOTE: This method is called on the JavaFX Application Thread."
     * @param primaryStage "the primary stage for this application, onto which the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be primary stages."
     * @throws Exception "if something goes wrong" - So beautifully said by the JavaFX creators
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        List<String> params = getParameters().getRaw();
        Map<PlayerId, String> playerNames = new HashMap<>();
        Map<PlayerId, Player> players = new HashMap<>();

        int port = 5108;

        RemotePlayerProxy luigi;

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Waiting for client to connect...");

            Socket socket = serverSocket.accept();
            luigi = new RemotePlayerProxy(socket);

            System.out.println("Client connected!");
        }

        //NOTE: PlayerId.PLAYER_1 is always this player

        for(PlayerId p : PlayerId.ALL) {
            int pIndex = PlayerId.ALL.indexOf(p);
            if(pIndex > params.size() - 1) playerNames.put(p, pIndex == 0 ? "Ada" : "Charles");
            else playerNames.put(p, params.get(pIndex));
        }

        players.put(PlayerId.PLAYER_2, luigi);

        Player mario = new GraphicalPlayerAdapter();
        players.put(PlayerId.PLAYER_1, mario);

        System.out.println("Starting game!");

        new Thread(() -> {
            Game.play(players, playerNames, SortedBag.of(ChMap.tickets()), new Random());

            System.out.println("Game finished, disconnecting...");

            try{
                luigi.closeAll();
                System.out.println("Disconnected");

            } catch(IOException e) {
                e.printStackTrace();
            }

        }).start();
    }
}
