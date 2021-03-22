package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class GameState extends PublicGameState {


    private final Map<PlayerId, PlayerState> playerState;
    private final Deck<Ticket> tickets;
    private final CardState cardState;

    /**
     * Private constructor of the GameState class
     * @param currentPlayerId (PlayerId) current player
     * @param playerState (Map<PlayerId, PlayerState>) playerState
     * @param lastPlayer (PlayerId) player who played last
     * @param tickets (Deck<Ticket>) deck of tickets
     * @param cardState (CardState) given cardState
     */
    private GameState(PlayerId currentPlayerId, Map<PlayerId, PlayerState> playerState,
                      PlayerId lastPlayer, Deck<Ticket> tickets, CardState cardState) {

        super(tickets.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);

        this.cardState = cardState;
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

        Map<PlayerId, PlayerState> initialState = new HashMap<>();
        initialState.put(PlayerId.PLAYER_1, PlayerState.initial(fullDeck.topCards(Constants.INITIAL_CARDS_COUNT)));
        fullDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        initialState.put(PlayerId.PLAYER_2, PlayerState.initial(fullDeck.topCards(Constants.INITIAL_CARDS_COUNT)));
        fullDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);

        PublicCardState initialCardState = new PublicCardState(null, fullDeck.size(), 0);

        return new GameState(startingPlayer, initialState, null, deckOfTickets, CardState.of(fullDeck));
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

    /**
     *  returnsthe count tickets from the top of the pile
     * @param count (int) amount of tickets to give back
     * @return SortedBag of tickets
     */
    public SortedBag<Ticket> topTickets(int count) {

        Preconditions.checkArgument(count >= 0 && count <= tickets.size());

        return tickets.topCards(count);
    }

    /**
     * returns an identical state to the receiver, but without the count tickets from the top of the pile
     * @param count (int) amount of cards to remove from the top
     * @return an identical state to the receiver, but without the count tickets from the top of the pile
     */
    public GameState withoutTopTickets(int count) {

        Preconditions.checkArgument(count >= 0 && count <= tickets.size());

        return new GameState(currentPlayerId(), playerState, lastPlayer(),tickets.withoutTopCards(count), cardState);
    }

    /**
     * Returns the top card of the draw pile
     * @return the top card of the draw pile
     */
    public Card topCard() {

        return cardState.topDeckCard();
    }

    /**
     * returns an identical state to the receiver but without the card at the top of the draw pile
     * @return an identical state to the receiver but without the card at the top of the draw pile
     */
    public GameState withoutTopCard() {

        Preconditions.checkArgument(!cardState.isDeckEmpty());

        return new GameState(currentPlayerId(), playerState, lastPlayer(), tickets, cardState.withoutTopDeckCard());
    }

    /**
     * returns an identical state to the receiver but with the given cards added to the discard pile
     * @param discardedCards (SortedBag<Card>) given cards
     * @return an identical state to the receiver but with the given cards added to the discard pile
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {

        return new GameState(currentPlayerId(), playerState, lastPlayer(), tickets, cardState.withMoreDiscardedCards(discardedCards));
    }

    /**
     * returns an identical state to the receiver unless the deck of cards is empty,
     * in which case it is recreated from the discard pile, shuffled using the given random generator
     * @param rng (Random) randomizer
     * @return an identical state to the receiver or if empty, deck is recreated from discards
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {

        if(cardState.isDeckEmpty()) {

            CardState deckFromDiscards = cardState.withDeckRecreatedFromDiscards(rng);

            return new GameState(currentPlayerId(), playerState, lastPlayer(), tickets, deckFromDiscards);
        }
        else {
            return new GameState(currentPlayerId(), playerState, lastPlayer(), tickets, cardState);
        }
    }





}
