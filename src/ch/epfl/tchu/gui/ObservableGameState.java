package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;

public class ObservableGameState {

    private PublicGameState gameState;
    private PlayerState playerState;
    private PlayerId PID;

    private int myCarCount = playerState.carCount();
    private int otherDudesCarCount = gameState.playerState(PID.next()).carCount();

    private SortedBag<Card> myCards = playerState.cards();
    private int otherDudesCardCount = gameState.playerState(PID.next()).cardCount();

    private SortedBag<Ticket> myTickets = playerState.tickets();
    private int otherDudesTicketCount = gameState.playerState(PID.next()).ticketCount();

    private List<Route> myRoutes = playerState.routes();
    private List<Route> otherDudesRoutes = gameState.playerState(PID.next()).routes();


    ObservableGameState(PlayerId PID) {
        this.PID = PID;
    }

    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {

    }

}
