package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

/**
 * InfoViewCreator represents the GUI Information in the left part of the game
 * @author Karlis Velins (325180)
 * @author Daniel Polka  (326800)
 */
final class InfoViewCreator {

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

        // TODO: 5/23/2021 not sure if have to add this using the parameter PlayerId or not like do I add 1 here
        // or do I add both at the same time.
        for(int i = 0; i < PlayerId.COUNT; i++) {
            statsVbox.getChildren().add(playerStatistics(PlayerId.ALL.get(i), playerNames.get(PlayerId.ALL.get(i)) ,gameState));
        }

        mainVbox.getChildren().addAll(statsVbox, separator, textFlow);
        //TODO: for(Text t : infos) mainVbox.getChildren().add(t);

        return mainVbox;

    }

    /**
     * Private method for the statistics part of the scene graph
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
        text.getStyleClass().add("filled");
        textFlow.getChildren().add(text);

        return textFlow;
    }


}