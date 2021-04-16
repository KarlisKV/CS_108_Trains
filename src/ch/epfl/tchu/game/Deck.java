package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Deck class represents all of the possible decks of cards
 * used during the game
 * @author Karlis Velins (325180)
 */

public final class Deck < C extends Comparable <C >>{

    private final List<C> cards;


    /**
     * Public method to initialize deck
     * @param cards (SortedBag<C>) list of cards to place in deck
     * @param rng (Random) randomizer for card shuffling
     * @return deck of cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {

        List<C> shuffledCards = cards.toList();
        Collections.shuffle(shuffledCards, rng);

        return new Deck<>(shuffledCards);
    }

    /**
     * Private constructor of Deck class
     * @param cards (List<C>) list of cards
     */
    public Deck(List<C> cards) { this.cards = cards; }

    /**
     * Returns the size of the deck
     * @return the size of the deck
     */
    public int size() {
        return cards.size();
    }

    /**
     * Returns true if the deck is empty
     * @return true if the deck is empty
     */
    public boolean isEmpty() { return cards.isEmpty(); }

    /**
     * returns a multiset containing the card at the top of the pile
     * @return a multiset containing the card at the top of the pile
     * @throws IllegalArgumentException if the deck is empty
     */
    public C topCard() {
        Preconditions.checkArgument(!isEmpty());
        return cards.get(0);
    }

    /**
     * withoutTopCard returns deck without top card
     * @return Deck<C> without top card
     * @throws IllegalArgumentException if the deck is empty
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!isEmpty());

        List<C> cardsWithoutTop = new ArrayList<>(cards);
        cardsWithoutTop.remove(0);


        return new Deck<>(cardsWithoutTop);
    }

    /**
     * topCards returns a multiset containing the top (count) cards from a deck
     * @param count (int) amount of cards to return
     * @return count amount of cards from the top of the deck
     * @throws IllegalArgumentException if the count is negative or larger than the total number of cards
     */
    public SortedBag<C> topCards(int count) {

        Preconditions.checkArgument(count >= 0 && count <= cards.size());
        List<C> cardsAtTop = new ArrayList<>(cards.subList(0, count));
        SortedBag.Builder<C> cardsToReturn = new SortedBag.Builder<>();

        for(int i = 0; i < count; ++i) {
            cardsToReturn.add(cardsAtTop.get(i));
        }

        return cardsToReturn.build();

    }

    /**
     * Returns a deck without the top cards
     * @param count (int) amount of cards to remove from new deck
     * @return a deck without the top cards
     * @throws IllegalArgumentException if the count is negative or larger than the total number of cards
     */

    public Deck<C> withoutTopCards(int count) {

        Preconditions.checkArgument(count >= 0 && count <= cards.size());

        List<C> cardsWithoutTop = new ArrayList<>(cards.subList(count, cards.size()));

        return new Deck<>(cardsWithoutTop);

    }



}
