package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.Map;
import java.util.Random;

public final class GameState extends PublicGameState {


    private GameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId, Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        super(ticketsCount, cardState, currentPlayerId, playerState, lastPlayer);
    }


    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        return null;
    }

    @Override
    public PlayerState playerState(PlayerId playerId) {
        return null;
    }

    @Override
    public PlayerState currentPlayerState() {
        return null;
    }
}
