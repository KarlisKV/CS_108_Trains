package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CardState extends PublicCardState {

    private final List<Card> faceUpCards;
    private final Deck<Card> deck;
    private final SortedBag<Card> discardPile;



    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discardPile) {
        super(faceUpCards, deck.size(), discardPile.size());
        this.deck = deck;
        this.discardPile = discardPile;
        this.faceUpCards = faceUpCards;
    }



    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= 5);

        SortedBag<Card> discard = deck.topCards(5);
        List<Card> faceUp = new ArrayList<>(discard.toList());
        discard = SortedBag.of();
        Deck<Card> cards = deck.withoutTopCards(5);

        return new CardState(faceUp, cards, discard);
    }

    public CardState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(!deck.isEmpty());
        if(slot >= 5 || slot < 0) {
            throw new IndexOutOfBoundsException();
        }
        List<Card> fu = new ArrayList<>(faceUpCards);
        fu.set(slot, deck.topCard());
        return new CardState(fu, deck.withoutTopCard(), discardPile);
    }

    public Card topDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return deck.topCard();
    }



    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!deck.isEmpty());
        return new CardState(faceUpCards, deck.withoutTopCard(), discardPile);
    }


    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(deck.isEmpty());
        return new CardState(faceUpCards, Deck.of(discardPile, rng), SortedBag.of());
    }


    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(faceUpCards, deck, discardPile.union(additionalDiscards));
    }

}
