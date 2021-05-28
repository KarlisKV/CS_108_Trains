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
 * ServerMain
 * @author Daniel Polka  (326800)
 */
public class ServerMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    //PlayerId.PLAYER_1 is always this player

    @Override
    public void start(Stage primaryStage) throws Exception {

        List<String> params = getParameters().getRaw();
        Map<PlayerId, String> playerNames = new HashMap<>();
        Map<PlayerId, Player> players = new HashMap<>();

        int port = 5108;

        Player luigi;

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Waiting for client to connect...");

            Socket socket = serverSocket.accept();
            luigi = new RemotePlayerProxy(socket);

            System.out.println("Client connected!");
        }

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
                ((RemotePlayerProxy) luigi).closeAll();
                System.out.println("Disconnected");

            } catch(IOException e) {
                e.printStackTrace();
            }

        }).start();
    }
}
