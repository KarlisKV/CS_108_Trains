package ch.epfl.tchu.game;


import ch.epfl.tchu.Preconditions;

import java.util.Map;
import java.util.Objects;

/**
 * PublicGameState class represents the public part of the gameState
 * @author Karlis Velins (325180)
 */

public class PublicGameState {

    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * Public constructor of the PublicGameState class
     * @param ticketsCount (int) number of tickets
     * @param cardState (PublicCardState) given cardState
     * @param currentPlayerId (PlayerId) id of the current player (either PLAYER_1 or PLAYER_2
     * @param playerState (Map<PlayerId, PublicPlayerState>) state of player
     * @param lastPlayer (PlayerId) id of player who played last, can be null
     * @throws IllegalArgumentException if ticketsCount is negative or playerState size doesn't equal the preset
     * @throws NullPointerException if cardState or currentPlayerId is null
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {

        Preconditions.checkArgument(ticketsCount >= 0);
        Preconditions.checkArgument(playerState.size() == PlayerId.COUNT);

        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.lastPlayer = lastPlayer;
        this.playerState = Map.copyOf(playerState);
        this.ticketsCount = ticketsCount;
    }

    /**
     * Returns the ticket count
     * @return the ticket count
     */
    public int ticketsCount() { return ticketsCount; }

    /**
     * Returns true iff it is possible to draw tickets, i.e. if the draw pile is not empty,
     * @return true iff it is possible to draw tickets, i.e. if the draw pile is not empty,
     */
    public boolean canDrawTickets() {
        return (ticketsCount != 0);
    }

    /**
     * returns the public part of the state of the wagon / locomotive cards
     * @return the public part of the state of the wagon / locomotive cards
     */
    public PublicCardState cardState() {
        return cardState;
    }

    /**
     * returns true iff it is possible to draw cards, i.e. if the draw pile and the discard pile contain at least 5 cards between them
     * @return true iff it is possible to draw cards, i.e. if the draw pile and the discard pile contain at least 5 cards between them
     */
    public boolean canDrawCards() {
        return ((cardState.deckSize() + cardState.discardsSize()) >= 5);
    }

    /**
     * Returns the identity of the current player
     * @return the identity of the current player
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * Returns the public part of the player's state of given identity
     * @param playerId (PlayerId) id of the given player
     * @return the public part of the player's state of given identity
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * Returns the public part of the current player's state
     * @return the public part of the current player's state
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }


    /**
     * Returns the PlayerId of the lastPlayer
     * @return the PlayerId of the lastPlayer
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}
