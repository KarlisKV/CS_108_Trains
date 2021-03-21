package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class GameState extends PublicGameState {

    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * Private constructor of GameState class
     * @param ticketsCount
     * @param cardState
     * @param currentPlayerId
     * @param playerState
     * @param lastPlayer
     */
    // TODO: 3/21/2021 the playerState is marked for the reason see 3.2.1. i cba to type it
    private GameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId,
                      Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {

        super(ticketsCount, cardState, currentPlayerId, playerState, lastPlayer);
        this.ticketsCount = ticketsCount;
        this.cardState = cardState;
        this.currentPlayerId = currentPlayerId;
        this.playerState = playerState;
        this.lastPlayer = lastPlayer;
    }

    /**
     * This method initializes the game to the starting GameState
     * @param tickets (SortedBag<Ticket>) all the tickets at the start
     * @param rng (Random) randomizer for shuffling and choosing which player starts
     * @return initial GameState
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {

        SortedBag<Card> allCards = Constants.ALL_CARDS;
        Deck<Card> fullDeck = Deck.of(allCards, rng);

        List<PlayerId> players = PlayerId.ALL;
        PlayerId startingPlayer = players.get(rng.nextInt());

        Map<PlayerId, PlayerState> initialMap = new HashMap<>();
        initialMap.put(PlayerId.PLAYER_1, PlayerState.initial(fullDeck.topCards(Constants.INITIAL_CARDS_COUNT)));
        fullDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        initialMap.put(PlayerId.PLAYER_2, PlayerState.initial(fullDeck.topCards(Constants.INITIAL_CARDS_COUNT)));
        fullDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);

        PublicCardState initialCardState = new PublicCardState(null, fullDeck.size(), 0);
        
        return new GameState(tickets.size(), initialCardState, startingPlayer, initialMap, null);
    }

    /**
     * Redefined playerState method
     * @param playerId (PlayerId) id of the given player
     * @return the player state of the given player
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * Redefined currentPlayerState method
     * @return the player state of the current player
     */
    @Override
    public PlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }
    
    
    
}
