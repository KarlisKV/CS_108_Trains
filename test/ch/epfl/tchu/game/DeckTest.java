package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeckTest {

    @Test
    void sizeTest(){

        Deck<Card> deck = new Deck<Card>(SortedBag.of(9, Card.BLACK).toList());

        assertEquals(9, deck.size());

    }

    @Test
    void topCardTest() {

        List<Card> list = new ArrayList<Card>(SortedBag.of(4, Card.BLACK).toList());
        list.add(Card.BLUE);

        Deck<Card> deck = new Deck<Card>(list);

        Card actual = deck.topCard();

        assertEquals(Card.BLACK,actual);
    }

    @Test
    void topCardThrowsException(){

        List<Card> list = new ArrayList<Card>();

        Deck<Card> deck = new Deck<Card>(list);

        assertThrows(IllegalArgumentException.class, () ->
        { deck.topCard(); });
    }

    @Test
    void withoutTopCardTest() {

        List<Card> list = new ArrayList<Card>(SortedBag.of(4, Card.BLACK).toList());
        list.add(Card.BLUE);

        Deck<Card> deck = new Deck<Card>(list);

        Deck<Card> newDeck = deck.withoutTopCard();

        assertEquals(4, newDeck.size());

    }

    @Test
    void withoutTopCardsTest() {

        List<Card> list = new ArrayList<Card>(SortedBag.of(4, Card.BLACK).toList());
        list.add(Card.BLUE);

        Deck<Card> deck = new Deck<Card>(list);

        Deck<Card> newDeck =  deck.withoutTopCards(3);

        assertEquals(2, newDeck.size());

    }

    @Test
    void topCardsTest(){

        List<Card> list = new ArrayList<Card>(SortedBag.of(4, Card.BLACK).toList());
        list.addAll(SortedBag.of(5, Card.BLUE).toList());

        Deck<Card> deck = new Deck<Card>(list);

        SortedBag<Card> cardsBag = SortedBag.of(3, Card.BLUE, 4, Card.BLACK);

        assertEquals(cardsBag, deck.topCards(7));

    }


    @Test
    void isEmptyTest() {

        List<Card> list = new ArrayList<Card>();

        Deck<Card> deck = new Deck<Card>(list);

        assertEquals(true, deck.isEmpty());


    }


}





