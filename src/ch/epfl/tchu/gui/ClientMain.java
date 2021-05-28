package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

public class ClientMain extends Application {


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {

        List<String> params = getParameters().getRaw();

        String hostName = "localhost";
        int port = 5108;

        if(params != null) {
            if(params.size() > 0) hostName = params.get(0);
            if(params.size() > 1) port = Integer.parseInt(params.get(1));
        }



        Player luigi = new GraphicalPlayerAdapter();

        System.out.printf("Connecting to host %s on port %s...", hostName, port);
        System.out.println();

        RemotePlayerClient client = new RemotePlayerClient(luigi, hostName, port);

        System.out.println("Connected, starting game!");

        new Thread(() -> {
            client.run();
            System.out.println("Game finished, disconnected");
        }).start();
    }
}
