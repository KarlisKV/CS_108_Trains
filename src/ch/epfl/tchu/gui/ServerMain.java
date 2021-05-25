package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }



    @Override
    public void start(Stage primaryStage) throws Exception {

        List<String> params = getParameters().getRaw();
        Map<PlayerId, String> playerNames = new HashMap<>();

        for(PlayerId p : PlayerId.ALL) {
            int pIndex = PlayerId.ALL.indexOf(p);
            if(pIndex > params.size() - 1) playerNames.put(p, pIndex == 0 ? "Ada" : "Charles");
            else playerNames.put(p, params.get(pIndex));
        }



    }
}
