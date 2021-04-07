package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * PublicCardState class
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
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(deckSize >= 0 && discardsSize >= 0 && faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT);
        this.faceUpCards = new ArrayList<>(faceUpCards);
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
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(slot, Constants.FACE_UP_CARDS_COUNT);
        return faceUpCards.get(slot);
    }

    /**
     * returns the total number of cards that are not in the players' hand
     * @return the total number of cards that are not in the players' hand
     */
    public int totalSize() {
        return deckSize + discardsSize + faceUpCards.size();
    }


}
