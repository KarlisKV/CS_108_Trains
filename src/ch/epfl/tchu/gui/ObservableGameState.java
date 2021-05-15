package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.List;

public class ObservableGameState {

    private PublicGameState gameState;
    private PlayerState playerState;
    private PlayerId PID;

    private int myCarCount = 0;
    private int otherDudesCarCount = 0;

    private SortedBag<Card> myCards = null;
    private int otherDudesCardCount = 0;

    private SortedBag<Ticket> myTickets = null;
    private int otherDudesTicketCount = 0;

    private List<Route> myRoutes = null;
    private List<Route> otherDudesRoutes = null;

    private final List<SimpleObjectProperty<Card>> faceUpCards = createFaceUpCards();


    public ObservableGameState(PlayerId PID) {
        this.PID = PID;
    }




    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {

        gameState = newGameState;
        playerState = newPlayerState;

        myCarCount = playerState.carCount();
        otherDudesCarCount = gameState.playerState(PID.next()).carCount();

        myCards = playerState.cards();
        gameState.playerState(PID.next()).cardCount();

        myTickets = playerState.tickets();
        otherDudesTicketCount = gameState.playerState(PID.next()).ticketCount();

        myRoutes = playerState.routes();
        otherDudesRoutes = gameState.playerState(PID.next()).routes();

    }





    private static List<SimpleObjectProperty<Card>> createFaceUpCards() {

        List<SimpleObjectProperty<Card>> cards = new ArrayList<>();
        for(int slot : Constants.FACE_UP_CARD_SLOTS)
            cards.add(new SimpleObjectProperty<>());

        return cards;

    }


    public PublicGameState getGameState() {
        return gameState;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

}
