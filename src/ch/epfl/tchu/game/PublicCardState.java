package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * PublicCardState class represents the public part of the cardsState
 * @author Daniel Polka  (326800)
 */

public class PublicCardState {

    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * Default constructor for PublicCardState class
     * @param faceUpCards (List<Card>) list of faceUpCards
     * @param deckSize (int) size of deck
     * @param discardsSize (int) discard pile
     * @throws IllegalArgumentException if the deckSize or discardSize is negative and if the given faceUpCard size doesn't equal the preset
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(deckSize >= 0 && discardsSize >= 0 && faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT);
        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * Return List of faceUpCards
     * @return List of faceUpCards
     */
    public List<Card> faceUpCards() {
        return new ArrayList<>(faceUpCards);
    }

    /**
     * Return List of deckSize
     * @return List of deckSize
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * Return List of discardsSize
     * @return List of discardsSize
     */
    public int discardsSize() {
        return discardsSize;
    }

    /**
     * Returns true if deck is empty
     * @return true if deck is empty
     */
    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    /**
     * turns the card face up at the given index
     * @param slot (int) index of card to turn over
     * @return the card face up at the given index
     * @throws IndexOutOfBoundsException if the index is not between 0 included and 5 excluded
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        return faceUpCards.get(slot);
    }
}
