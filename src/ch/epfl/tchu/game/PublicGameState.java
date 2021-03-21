package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PublicGameState {

    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {

        Preconditions.checkArgument(ticketsCount >= 0);
        Preconditions.checkArgument(playerState.size() == PlayerId.COUNT);
        Objects.requireNonNull(cardState);
        Objects.requireNonNull(currentPlayerId);

        this.cardState = cardState;
        this.currentPlayerId = currentPlayerId;
        this.lastPlayer = lastPlayer;
        this.playerState = playerState;
        this.ticketsCount = ticketsCount;
    }

    public int ticketsCount() {
        return ticketsCount;
    }

    public boolean canDrawTickets() {
        return (cardState.deckSize() != 0);
    }

    public PublicCardState cardState() {
        return cardState;
    }

    public boolean canDrawCards() {
        return ((cardState.deckSize() + cardState.discardsSize()) >= 5);
    }

    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    public List<Route> claimedRoutes() {
        List<Route> listOfCombinedRoutes = playerState.get(currentPlayerId).routes();
        PlayerId nextPlayer = currentPlayerId.next();
        listOfCombinedRoutes.addAll(playerState.get(nextPlayer).routes());

        return listOfCombinedRoutes;
    }

    public PlayerId lastPlayer() {
        return lastPlayer;
    }

}
