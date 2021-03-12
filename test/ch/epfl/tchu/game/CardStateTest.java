package ch.epfl.tchu.game;
import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;
import ch.epfl.test.TestRandomizer;

import ch.epfl.tchu.game.CardState;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardStateTest {

    @Test
    void ofFailsWithDeckSmallerThanFive(){
        Deck<Card> f1= new Deck<>(SortedBag.of(1, Card.BLUE, 3, Card.RED).toList());

        assertThrows(IllegalArgumentException.class, () -> {
            CardState.of(f1);
        });
    }

    @Test
    void ofWorksWithNormalDeck(){
        Deck<Card> f1= new Deck<>(SortedBag.of(5, Card.BLUE, 3, Card.RED).toList());

        System.out.println("Deck:" + f1.topCard() + " " + f1.withoutTopCard().topCard() + " " + f1.withoutTopCard().withoutTopCard().topCard());
        CardState cardState = CardState.of(f1);
        assertEquals(5, cardState.faceUpCards().size());
        System.out.println(cardState.faceUpCards());

        assertEquals(3, cardState.deckSize());

        assertEquals(Card.BLUE, cardState.topDeckCard());
        System.out.println(cardState.topDeckCard());

        assertEquals(0, cardState.discardsSize());
    }

    @Test
    void withDrawnFaceUpCardFailsWithIndexOutsideRangeZeroToFive(){
        Deck<Card> f1= new Deck<>(SortedBag.of(5, Card.BLUE, 3, Card.RED).toList());
        CardState cardState = CardState.of(f1);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            cardState.withDrawnFaceUpCard(5);
        });
    }

    @Test
    void withDrawnFaceUpCardFailsWithEmptyDrawPile(){
        Deck<Card> f1= new Deck<>(SortedBag.of(2, Card.BLUE, 3, Card.RED).toList());
        CardState cardState = CardState.of(f1);

        assertThrows(IllegalArgumentException.class, () -> {
            cardState.withDrawnFaceUpCard(4);
        });
    }

    @Test
    void withDrawnFaceUpCardWorksWithSlotIndex(){
        Deck<Card> f1= new Deck<>(SortedBag.of(2, Card.BLUE, 4, Card.RED).toList());
        CardState cardState = CardState.of(f1);
        System.out.println(cardState.faceUpCards());
        CardState switchedCardState = cardState.withDrawnFaceUpCard(2); //Set blue instead of red
        assertEquals(Card.BLUE, switchedCardState.faceUpCard(2));
        assertEquals(Card.RED, cardState.faceUpCard(2));
    }

    @Test
    void topDeckCardFailsWithEmptyDrawPile(){
        Deck<Card> f1= new Deck<>(SortedBag.of(2, Card.BLUE, 3, Card.RED).toList());
        CardState cardState = CardState.of(f1);

        assertThrows(IllegalArgumentException.class, () -> {
            cardState.topDeckCard();
        });
    }

    @Test
    void topDeckCardWorksWithNormalPile(){
        Deck<Card> f1= new Deck<>(SortedBag.of(2, Card.BLUE, 4, Card.RED).toList());
        CardState cardState = CardState.of(f1);
        assertEquals(Card.BLUE,cardState.topDeckCard());
    }

    @Test
    void withoutTopDeckCardFailsWithEmptyDeck(){
        Deck<Card> f1= new Deck<>(SortedBag.of(2, Card.BLUE, 3, Card.RED).toList());
        CardState cardState = CardState.of(f1);

        assertThrows(IllegalArgumentException.class, () -> {
            cardState.withoutTopDeckCard();
        });
    }

    @Test
    void withoutTopDeckCardWorksWithSingleCardDeck(){
        Deck<Card> f1= new Deck<>(SortedBag.of(3, Card.BLUE, 3, Card.RED).toList());
        CardState cardState = CardState.of(f1);
        System.out.println(cardState.faceUpCards());

        assertEquals(0, cardState.withoutTopDeckCard().deckSize());
    }

    @Test
    void withoutTopDeckCardWorksWithNormalDeck(){
        Deck<Card> f1= new Deck<>(SortedBag.of(4, Card.BLUE, 5, Card.RED).toList());
        CardState cardState = CardState.of(f1);
        System.out.println(cardState.faceUpCards());

        assertEquals(3, cardState.withoutTopDeckCard().deckSize());
    }

    @Test
    void withDeckRecreatedFromDiscardsFailsWithNonEmptyDeck(){
        Deck<Card> f1= new Deck<>(SortedBag.of(4, Card.BLUE, 5, Card.RED).toList());
        CardState cardState = CardState.of(f1);
        cardState.withMoreDiscardedCards(SortedBag.of(15, Card.BLUE, 8, Card.RED));

        assertThrows(IllegalArgumentException.class, () -> {
            cardState.withDeckRecreatedFromDiscards(TestRandomizer.newRandom());
        });
    }

    @Test
    void withDeckRecreatedFromDiscardsWorksWithEmptyDeck(){
        Deck<Card> f1= new Deck<>(SortedBag.of(3, Card.BLUE, 2, Card.RED).toList());
        CardState cardState = CardState.of(f1);

        CardState initialCardState = cardState.withMoreDiscardedCards(SortedBag.of(15, Card.BLUE, 8, Card.RED));

        CardState newCardState = initialCardState.withDeckRecreatedFromDiscards(TestRandomizer.newRandom());

        assertEquals(0, newCardState.discardsSize());
        assertEquals(23, newCardState.deckSize());
    }

    @Test
    void withMoreDiscardedCardsWorksWithNormalAdditionalDiscards(){
        Deck<Card> f1= new Deck<>(SortedBag.of(3, Card.BLUE, 2, Card.RED).toList());
        CardState cardState = CardState.of(f1);

        CardState startDiscardsCardState = cardState.withMoreDiscardedCards(SortedBag.of(3, Card.BLUE, 2, Card.RED));
        assertEquals(5, startDiscardsCardState.discardsSize());

        CardState additionalDiscardsCardState = startDiscardsCardState.withMoreDiscardedCards(SortedBag.of(13, Card.BLUE, 8, Card.RED));

        assertEquals(26, additionalDiscardsCardState.discardsSize());
    }

    @Test
    void withMoreDiscardedCardsWorksWithEmptyAdditionalDiscards1(){
        Deck<Card> f1= new Deck<>(SortedBag.of(3, Card.BLUE, 2, Card.RED).toList());
        CardState cardState = CardState.of(f1);

        int initialDiscardsSize = cardState.discardsSize();
        CardState newCardState = cardState.withMoreDiscardedCards(SortedBag.of());

        assertEquals(0, newCardState.discardsSize());
        assertEquals(initialDiscardsSize, newCardState.discardsSize());
    }

    @Test
    void withMoreDiscardedCardsWorksWithEmptyAdditionalDiscards2(){
        Deck<Card> f1= new Deck<>(SortedBag.of(3, Card.BLUE, 2, Card.RED).toList());
        CardState cardState = CardState.of(f1);

        CardState initialCardState = cardState.withMoreDiscardedCards(SortedBag.of(15, Card.BLUE, 8, Card.RED));

        CardState newCardState = initialCardState.withMoreDiscardedCards(SortedBag.of());

        assertEquals(23, initialCardState.discardsSize());
        assertEquals(23, newCardState.discardsSize());
    }






}
