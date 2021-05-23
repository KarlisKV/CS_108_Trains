package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Map;

public class GraphicalPlayer {

    ObservableGameState gameState;
    PlayerId playerId;
    Map<PlayerId, String> playerNames;



    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        this.playerId = playerId;
        this.playerNames = playerNames;
        this.gameState = new ObservableGameState(playerId);

        /**
         * Here is the scene graph part
         */
        // TODO: 5/23/2021  need to create these 4 nodes to add them to the borderPane, not too sure how
        // todo to get the information for infos, claimRoute, chooseCards, drawTickets and drawCard


        /*
        Node infoView = InfoViewCreator
                .createInfoView (playerId, playerNames, gameState, infos);
        Node mapView = MapViewCreator
                .createMapView(gameState, claimRoute, Stage9Test::chooseCards);
        Node cardsView = DecksViewCreator
                .createCardsView(gameState, drawTickets, drawCard);
        Node handView = DecksViewCreator
                .createHandView(gameState);



        BorderPane borderPane = new BorderPane(mapView, null, cardsView, handView, infoView);


        Scene scene = new Scene(borderPane);
        Stage stage = new Stage();
        stage.setTitle("tCHu\u2014"+playerNames.get(playerId));
        stage.setScene(scene);

         */
    }

    /**
     * doing nothing but calling this method on the observable state of the player
     * @param newGameState (PublicGameState) given public part of the game state
     * @param newPlayerState (PlayerState) given playerState
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        gameState.setState(newGameState, newPlayerState);
    }
    //todo no idea how to write this, need to add messages to info part but how?
    public void receiveInfo(String message) {

    }





}