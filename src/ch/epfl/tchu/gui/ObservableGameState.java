package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.List;

public class ObservableGameState {

 //   private PublicGameState gameState;
 //   private PlayerState playerState;

    private ObjectProperty<PlayerState> playerState;
    private ObjectProperty<PublicGameState> gameState;
    private final PlayerId PID;

    private final List<SimpleObjectProperty<Card>> faceUpCards = createFaceUpCards();


    public ObservableGameState(PlayerId PID) {
        this.PID = PID;
        playerState = new SimpleObjectProperty<>();
        gameState = new SimpleObjectProperty<>();
    }




    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {

        gameState.setValue(newGameState);
        playerState.setValue(newPlayerState);

    }





    private static List<SimpleObjectProperty<Card>> createFaceUpCards() {

        List<SimpleObjectProperty<Card>> cards = new ArrayList<>();
        for(int slot : Constants.FACE_UP_CARD_SLOTS)
            cards.add(new SimpleObjectProperty<>());

        return cards;

    }


    public ObjectProperty<PublicGameState> gameState() {
        return gameState;
    }

    public ObjectProperty<PlayerState> playerState() {
        return playerState;
    }

}
