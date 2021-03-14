package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * CardState class
 * @author Daniel Polka  (326800)
 */

public class CardState extends PublicCardState {

    private final List<Card> faceUpCards;
    private final Deck<Card> deck;
    private final SortedBag<Card> discardPile;


    /**
     * Private constructor of CardState class. gets called from 'of' method
     * @param faceUpCards (List<Card>) list of face up cards
     * @param deck (Deck<Card>) the deck
     * @param discardPile (SortedBag<Card>) SortedBag of discarded cards
     */
    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discardPile) {
        super(faceUpCards, deck.size(), discardPile.size());
        this.deck = deck;
        this.discardPile = discardPile;
        this.faceUpCards = faceUpCards;
    }


    /**
     * Method returns a state in which the 5 cards placed face up are the first 5 of the given pile,
     * the draw pile consists of the remaining cards of the pile, and the discard pile is empty
     * @param deck (Deck<Card>) the deck
     * @return returns the CardState explained above
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

        SortedBag<Card> discard = deck.topCards(Constants.FACE_UP_CARDS_COUNT);
        List<Card> faceUp = new ArrayList<>(discard.toList());
        discard = SortedBag.of();

        Deck<Card> cards = deck.withoutTopCards(Constants.FACE_UP_CARDS_COUNT);

        return new CardState(faceUp, cards, discard);
    }

    /**
     * returns a set of identical cards to the receiver ( this), except that the face-up index card slot has
     * been replaced by the one at the top of the draw pile, which is removed at the same time
     * @param slot (int) index of the face-up card
     * @return a set with the removed face-up card slot
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(!deck.isEmpty());
        if(slot >= Constants.FACE_UP_CARDS_COUNT || slot < 0) {
            throw new IndexOutOfBoundsException();
        }

        List<Card> faceUp = new ArrayList<>(faceUpCards);
        faceUp.set(slot, deck.topCard());

        return new CardState(faceUp, deck.withoutTopCard(), discardPile);
    }

    /**
     * turns over the card at the top of the draw pile
     * @return the card at the top of the draw pile
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return deck.topCard();
    }


    /**
     * returns a set of cards identical to the receiver ( this), but without the card at the top of the deck
     * @return set of cards identical to the receiver ( this), but without the card at the top of the deck
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return new CardState(faceUpCards, deck.withoutTopCard(), discardPile);
    }

    /**
     * returns a set of identical cards to the receiver ( this), except that the cards
     * from the discard pile have been shuffled by means of the given random generator
     * @param rng (Random) randomizer for shuffling the cards
     * @return a shuffled set of identical cards to the receiver ( this)
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deck.isEmpty());
        return new CardState(faceUpCards, Deck.of(discardPile, rng), SortedBag.of());
    }

    /**
     * returns a set of cards identical to the receiver ( this), but with the given cards added to the discard pile
     * @param additionalDiscards (SortedBag<Card>) of discarded cards
     * @return a set of cards identical to the receiver ( this), but with the given cards added to the discard pile
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(faceUpCards, deck, discardPile.union(additionalDiscards));
    }

}
