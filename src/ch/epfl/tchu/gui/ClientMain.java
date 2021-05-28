package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * Main class used to launch the Client. IMPORTANT: Server must have been launched before the client
 * and the hostname and port number must be correct,
 * otherwise the program becomes subject to a sudden, tragic, fatal accident...
 *
 * ClientMain class shouldn't be instantiated, however it is impossible to add a private constructor or else
 * the program crashes on launch, thus causing the aforementioned sudden, tragic, fatal accident.
 * @author Daniel Polka (326800)
 */
public final class ClientMain extends Application {

    /**
     * Method to start the client
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
     * @throws Exception "if something goes wrong" - JavaFX creators, giving me chills...
     */
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
