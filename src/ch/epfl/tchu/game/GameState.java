package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class GameState extends PublicGameState {


    private final Map<PlayerId, PlayerState> playerState;
    private final Deck<Ticket> tickets;
    private final CardState privateCardState;

    /**
     * Private constructor of GameState class
     * @param ticketsCount
     * @param cardState
     * @param playerState
     * @param lastPlayer
     * @param currentPlayerId
     */
    // TODO: 3/21/2021 the playerState is marked for the reason see 3.2.1. i cba to type it
    private GameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId,
                      Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer, Deck<Ticket> tickets, CardState privateCardState) {

        super(ticketsCount, cardState, currentPlayerId, playerState, lastPlayer);

        this.privateCardState = privateCardState;
        this.tickets = tickets;
        this.playerState = playerState;
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

        Deck<Ticket> deckOfTickets = Deck.of(tickets, rng);

        List<PlayerId> players = PlayerId.ALL;
        PlayerId startingPlayer = players.get(rng.nextInt());

        Map<PlayerId, PlayerState> initialMap = new HashMap<>();
        initialMap.put(PlayerId.PLAYER_1, PlayerState.initial(fullDeck.topCards(Constants.INITIAL_CARDS_COUNT)));
        fullDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        initialMap.put(PlayerId.PLAYER_2, PlayerState.initial(fullDeck.topCards(Constants.INITIAL_CARDS_COUNT)));
        fullDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);

        PublicCardState initialCardState = new PublicCardState(null, fullDeck.size(), 0);
        CardState privateState = CardState.of(fullDeck);

        return new GameState(tickets.size(), initialCardState, startingPlayer, initialMap, null, deckOfTickets, privateState);
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
        return playerState.get(currentPlayerId());
    }

    public SortedBag<Ticket> topTickets(int count) {

        // TODO: 3/22/2021 dont understand is it the sortedbag of tickets? What does sommet de la pioche means
        // do i need to create a deck of tickets and then if yes how do I initialize it?
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());

        return tickets.topCards(count);
    }

    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= tickets.size());
        return new GameState(ticketsCount(), cardState(), currentPlayerId(), playerState, lastPlayer(),tickets.withoutTopCards(count), privateCardState);
    }

    public Card topCard() {
        return privateCardState.topDeckCard();
    }
}
