package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * InfoViewCreator represents the GUI Information in the left part of the game
 * @author Karlis Velins (325180)
 * @author Daniel Polka  (326800)
 */
final class InfoViewCreator {

    /**
     * InfoViewCreator not to be instantiated
     */
    private InfoViewCreator(){}

    /**
     * Public method to create the scene graph
     * @param playerId (PlayerId) Id of the given player
     * @param playerNames (Map<PlayerId, String>) map of all players
     * @param gameState (ObservableGameState) Observable part of the gameState
     * @param infos (ObservableList<Text>) messages given during the game
     */
    public static Node createInfoView(PlayerId playerId, Map<PlayerId, String> playerNames,
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

        // Set in first order the current player in the Vbox
        List<PlayerId> sortedEnumList = new ArrayList<>(PlayerId.ALL);
        sortedEnumList.sort(Comparator.comparingInt(i -> i == playerId ? 0 : 1));
        for (PlayerId id : sortedEnumList) statsVbox.getChildren().add(playerStatistics(id, playerNames.get(id) ,gameState));



        //So first you have to add all "current" infos "manually" to textFlow

        for(Text t : infos) textFlow.getChildren().add(t);

        //Then you need a changeListener for next time an info is added

        infos.addListener((ListChangeListener<Text>) c -> {

            textFlow.getChildren().clear();
            textFlow.getChildren().addAll(infos);

        });

        mainVbox.getChildren().addAll(statsVbox, separator, textFlow);

        return mainVbox;

    }

    /**
     * Private method for the statistics part of the scene graph to better structure the class
     * @param playerId (PlayerId) of the given player
     * @return TextFlow for playerStatistics
     */
    private static TextFlow playerStatistics(PlayerId playerId, String playerName,  ObservableGameState state) {

        TextFlow textFlow = new TextFlow();
        textFlow.getStyleClass().add(String.valueOf(playerId));

        Circle circle =  new Circle();
        circle.setRadius(5);
        circle.getStyleClass().add("filled");
        textFlow.getChildren().add(circle);

        ReadOnlyIntegerProperty ticketCount = state.playersTicketCount().get(PlayerId.ALL.indexOf(playerId));
        ReadOnlyIntegerProperty cardCount = state.playersCardCount().get(PlayerId.ALL.indexOf(playerId));
        ReadOnlyIntegerProperty carCount = state.playersCarCount().get(PlayerId.ALL.indexOf(playerId));
        ReadOnlyIntegerProperty points = state.playerPoints().get(PlayerId.ALL.indexOf(playerId));

        StringExpression playerInfo = Bindings.format(StringsFr.PLAYER_STATS, playerName, ticketCount, cardCount, carCount, points);

        Text text = new Text();
        text.textProperty().bind(playerInfo);
        textFlow.getChildren().add(text);

        return textFlow;
    }


}