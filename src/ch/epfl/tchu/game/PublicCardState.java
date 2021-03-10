package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

public class PublicCardState {


    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(deckSize >= 0 && discardsSize >= 0 && faceUpCards.size() == 5);
        this.faceUpCards = faceUpCards;
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    public int deckSize() {
        return deckSize;
    }

    public int discardsSize() {
        return discardsSize;
    }

    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    public Card faceUpCard(int slot) {
        Preconditions.checkArgument(slot < 5 && slot >= 0);
        return faceUpCards.get(slot);
    }

    public int totalSize() {
        return deckSize + discardsSize + faceUpCards.size();
    }


}
