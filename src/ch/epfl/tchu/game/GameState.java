package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * Route class
 * @author Karlis Velins (325180)
 * @author Daniel Polka  (326800)
 */
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

        Deck<Ticket> deckOfTickets = Deck.of(tickets, rng);

        List<PlayerId> players = PlayerId.ALL;
        PlayerId startingPlayer = players.get(rng.nextInt(2));

        Deck<Card> fullDeck = Deck.of(Constants.ALL_CARDS, rng);

        Map<PlayerId, PlayerState> initialState = new HashMap<>();

        for(int i = 0; i < PlayerId.COUNT; i++) {
            initialState.put(PlayerId.ALL.get(i), PlayerState.initial(fullDeck.topCards(Constants.INITIAL_CARDS_COUNT)));
            fullDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        }

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
     *  returns the count tickets from the top of the pile
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

        CardState deckFromDiscards = cardState.withDeckRecreatedFromDiscards(rng);
        // how to write using ? : operator instead of if else?
        return cardState.isDeckEmpty() ?
                new GameState(currentPlayerId(), playerState, lastPlayer(), tickets, deckFromDiscards) :
                new GameState(currentPlayerId(), playerState, lastPlayer(), tickets, cardState);

    }



    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {

        Preconditions.checkArgument(playerState.get(playerId).tickets().isEmpty());

        Map<PlayerId, PlayerState> newPlayerState = new HashMap<>(playerState);
        newPlayerState.get(playerId).withAddedTickets(chosenTickets);

        return new GameState(playerId, newPlayerState, null, tickets, cardState);
    }


    //TODO: check that this works

    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {

        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

        Deck<Ticket> newTickets = Deck.of(tickets.topCards(tickets.size()).difference(chosenTickets), new Random());

        Map<PlayerId, PlayerState> newPlayerState = new HashMap<>(playerState);
        newPlayerState.get(currentPlayerId()).withAddedTickets(chosenTickets);

        return new GameState(currentPlayerId(), newPlayerState, lastPlayer(), newTickets, cardState);
    }


    //TODO: check that this works

    public GameState withDrawnFaceUpCard(int slot) {

        Preconditions.checkArgument(canDrawCards());

        Map<PlayerId, PlayerState> newPlayerState = new HashMap<>(playerState);
        newPlayerState.get(currentPlayerId()).withAddedCard(cardState.faceUpCard(slot));

        CardState newCardState = cardState.withDrawnFaceUpCard(slot);


        return new GameState(currentPlayerId(), newPlayerState, lastPlayer(), tickets, newCardState);
    }


    //TODO: check that this works

    public GameState withBlindlyDrawnCard() {

        Preconditions.checkArgument(canDrawCards());

        Map<PlayerId, PlayerState> newPlayerState = new HashMap<>(playerState);
        newPlayerState.get(currentPlayerId()).withAddedCard(cardState.topDeckCard());

        CardState newCardState = cardState.withoutTopDeckCard();

        return new GameState(currentPlayerId(), newPlayerState, lastPlayer(), tickets, newCardState);
    }


    //TODO: check that this works

    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {

        Map<PlayerId, PlayerState> newPlayerState = new HashMap<>(playerState);
        newPlayerState.get(currentPlayerId()).withClaimedRoute(route, cards);

        CardState newCardState = cardState.withMoreDiscardedCards(cards);

        return new GameState(currentPlayerId(), newPlayerState, lastPlayer(), tickets, newCardState);
    }





    /**
     * Returns true if the current player has less or equal than 2 cars left
     * @return true if the current player has less or equal than 2 cars left
     */
    public boolean lastTurnBegins() {
        return (playerState.get(currentPlayerId()).carCount() <= 2);
    }

    /**
     * ends the current player's turn, ie returns an identical state to the receiver except that
     * the current player is the one following the current current player;
     * furthermore, if lastTurnBegins returns true, the current current player becomes the last player.
     * @return GameState equivalent to this but if its the last turn, the current player becomes the last player

     */

    public GameState forNextTurn() {

        return lastTurnBegins() ?
                new GameState(currentPlayerId().next(), playerState, currentPlayerId(), tickets, cardState) :
                new GameState(currentPlayerId().next(), playerState, lastPlayer(), tickets, cardState);

    }
}
