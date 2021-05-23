package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.net.MessageId;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javax.swing.*;
import java.util.Map;

public class InfoViewCreator {

    public void createInfoView(PlayerId playerId, Map<PlayerId, String> playerNames,
                               ObservableGameState gameState, ObservableList<Text> infos) {
        //main VBox
        VBox mainVbox = new VBox();
        mainVbox.getStylesheets().add("colors.css");
        mainVbox.getStylesheets().add("info.css");
        //Messages at start empty
        TextFlow textFlow = new TextFlow();
        textFlow.setId("game-info");
        //Separator (horizontal)
        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);
        //Statistics of both players
        VBox statsVbox = new VBox();
        statsVbox.setId("player-stats");

        // TODO: 5/23/2021 not sure if have to add this using PlayerId or not
        for(int i = 0; i < PlayerId.COUNT; i++) {
            statsVbox.getChildren().add(playerStatistics(PlayerId.ALL.get(i)));
        }

        mainVbox.getChildren().add(statsVbox);
        mainVbox.getChildren().add(separator);
        mainVbox.getChildren().add(textFlow);


    }

    private TextFlow playerStatistics(PlayerId playerId) {
        TextFlow textFlow = new TextFlow();
        textFlow.getStyleClass().add(String.valueOf(playerId));

        Circle circle =  new Circle();
        circle.setRadius(5);
        circle.getStyleClass().add("filled");
        textFlow.getChildren().add(circle);

        Text text = new Text();
        text.getStyleClass().add("filled");
        textFlow.getChildren().add(text);
        return textFlow;
    }

}
