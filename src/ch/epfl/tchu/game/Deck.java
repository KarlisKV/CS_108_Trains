package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class Deck < C extends Comparable <C >>{

    private final List<C> cards;


    /**
     * Public method to intialize deck
     * @param cards (SortedBag<C>) list of cards to place in deck
     * @param rng (Random) randomizer for card shuffling
     * @return deck of cards
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {

        List<C> shuffledCards = new ArrayList<>();

        for(C card: cards){
            shuffledCards.add(card);
        }
        Collections.shuffle(shuffledCards, rng);

        Deck <C> deck = new Deck<C>(shuffledCards);

        return deck;
    }

    /**
     * Private constructor of Deck class
     * @param cards (SortedBag<C>)
     */
    private Deck(List<C> cards) {

        this.cards = cards;

    }

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {

        return cards == null;
    }

    public C topCard() {
        Preconditions.checkArgument(!cards.isEmpty());
        return cards.get(0);
    }

    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!cards.isEmpty());

        Deck<C> cardsWithoutTopCard;

        return cardsWithoutTop;
    }



}
