package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.tchu.game.PlayerState.initial;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerStateTests {

    SortedBag<Ticket> st = new SortedBag.Builder<Ticket>().add(1, ChMap.tickets().get(0)).add(1, ChMap.tickets().get(1)).add(1, ChMap.tickets().get(2)).add(ChMap.tickets().get(3)).build();
    SortedBag<Ticket> sf = SortedBag.of(1, ChMap.tickets().get(0), 1, ChMap.tickets().get(1));
    SortedBag<Card> ct = new SortedBag.Builder<Card>().add(1, Card.BLACK).add(1, Card.BLUE).add(1, Card.YELLOW).add(1, Card.RED).build();
    SortedBag<Card> cf = new SortedBag.Builder<Card>().add(1, Card.BLACK).build();
    List<Route> rt = List.of(ChMap.routes().get(0), ChMap.routes().get(1), ChMap.routes().get(7));


    SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().get(0));
    SortedBag<Card> cards = SortedBag.of(5, Card.BLUE, 3, Card.LOCOMOTIVE);
    List<Route> routes = List.of(ChMap.routes().get(3), ChMap.routes().get(5));
    PlayerState state = new PlayerState(tickets, cards, routes);
    Route route = ChMap.routes().get(0);


    @Test
    void initialWorks() {
        PlayerState p0;
        PlayerState p1 = null;
        PlayerState p2 = initial(ct);
        assertEquals(SortedBag.of(), p2.tickets());
        assertEquals(ct, p2.cards());
        assertEquals(List.of(), p2.routes());

    }

    @Test
    void initialFails() {
        PlayerState p1 = null;
        assertThrows(IllegalArgumentException.class, () -> {
            initial(cf);
        });
    }


    @Test
    void withAddedTicketsWorks() {
        PlayerState p1 = new PlayerState(st, ct, rt);
        SortedBag<Ticket> st1 = new SortedBag.Builder<Ticket>().add(2, ChMap.tickets().get(0)).add(1, ChMap.tickets().get(1)).add(1, ChMap.tickets().get(2)).add(ChMap.tickets().get(3)).build();
        SortedBag<Ticket> plus = SortedBag.of(ChMap.tickets().get(0));
        assertEquals(st1, p1.withAddedTickets(plus).tickets());

    }


    @Test
    void withAddedCardWorks() {
        PlayerState p1 = new PlayerState(st, ct, rt);
        SortedBag c1 = new SortedBag.Builder<Card>().add(2, Card.BLACK).add(1, Card.BLUE).add(1, Card.YELLOW).add(1, Card.RED).build();
        Card plus = Card.BLACK;
        assertEquals(c1, p1.withAddedCard(plus).cards());

    }

    @Test
    void withAddedCardsWorks() {
        PlayerState p1 = new PlayerState(st, ct, rt);
        SortedBag c1 = new SortedBag.Builder<Card>().add(2, Card.BLACK).add(2, Card.BLUE).add(1, Card.YELLOW).add(1, Card.RED).build();
        SortedBag plus = new SortedBag.Builder<Card>().add(1, Card.BLACK).add(1, Card.BLUE).build();
        assertEquals(c1, p1.withAddedCards(plus).cards());

    }


    @Test
    void canClaimRouteWorks() {
        assertTrue(state.canClaimRoute(route));
    }

    @Test
    void canClaimRouteFails() {
        Route route1 = ChMap.routes().get(3);
        assertFalse(state.canClaimRoute(route1));
    }


    @Test
    void possibleAdditionalCardsWorks() {
        List<SortedBag<Card>> c = List.of(SortedBag.of(1, Card.BLUE, 1, Card.LOCOMOTIVE),
                SortedBag.of(2, Card.LOCOMOTIVE));
        SortedBag<Card> initialCards = SortedBag.of(4, Card.BLUE);
        SortedBag<Card> drawnCards = SortedBag.of(2, Card.BLUE, 1, Card.WHITE);
        assertEquals(c, state.possibleAdditionalCards(2, initialCards, drawnCards));
    }

    @Test
    void possibleAdditionalCardsWorksExample() {
        SortedBag<Ticket> tickets = SortedBag.of(1, ChMap.tickets().get(0), 1, ChMap.tickets().get(1));

        SortedBag<Card> playerCards = new SortedBag.Builder<Card>()
                .add(6, Card.ORANGE)
                .add(2, Card.LOCOMOTIVE)
                .build();

        SortedBag<Card> ic = new SortedBag.Builder<Card>()
                .add(1, Card.ORANGE)
                .build();

        SortedBag<Card> dc = new SortedBag.Builder<Card>()
                .add(1, Card.ORANGE)
                .add(1, Card.GREEN)
                .add(1, Card.LOCOMOTIVE)
                .build();

        Route tunnelRoute = ChMap.routes().get(57);
        System.out.println("TunnelRoute:" + tunnelRoute.stations());
        PlayerState playerState = new PlayerState(tickets, playerCards, routes);

        List<SortedBag<Card>> possibleAdditionalCards = playerState.possibleAdditionalCards(tunnelRoute.additionalClaimCardsCount(ic, dc), ic, dc);

        SortedBag<Card> expected11 = new SortedBag.Builder<Card>()
                .add(2, Card.ORANGE)
                .build();
        SortedBag<Card> expected12 = new SortedBag.Builder<Card>()
                .add(1, Card.ORANGE)
                .add(1, Card.LOCOMOTIVE)
                .build();
        SortedBag<Card> expected13 = new SortedBag.Builder<Card>()
                .add(2, Card.LOCOMOTIVE)
                .build();
        List<SortedBag<Card>> expected1 = List.of(expected11, expected12, expected13);

        assertEquals(expected1, possibleAdditionalCards);
    }

    @Test
    void possibleAdditionalCardsWorks2(){
        SortedBag<Ticket> tickets = SortedBag.of(1, ChMap.tickets().get(0), 1, ChMap.tickets().get(1));

        SortedBag<Card> playerCards = new SortedBag.Builder<Card>()
                .add(7, Card.BLUE)
                .add(3, Card.LOCOMOTIVE)
                .build();

        SortedBag<Card> ic = new SortedBag.Builder<Card>()
                .add(2, Card.BLUE)
                .add(1, Card.LOCOMOTIVE)
                .build();

        SortedBag<Card> dc = new SortedBag.Builder<Card>()
                .add(1, Card.BLUE)
                .add(1, Card.RED)
                .add(1, Card.LOCOMOTIVE)
                .build();

        Route tunnelRoute = ChMap.routes().get(39);
        System.out.println("TunnelRoute:" + tunnelRoute.stations());
        PlayerState playerState = new PlayerState(tickets, playerCards, routes);

        List<SortedBag<Card>> possibleAdditionalCards = playerState.possibleAdditionalCards(tunnelRoute.additionalClaimCardsCount(ic, dc), ic, dc);

        SortedBag<Card> expected11 = new SortedBag.Builder<Card>()
                .add(2, Card.BLUE)
                .build();
        SortedBag<Card> expected12 = new SortedBag.Builder<Card>()
                .add(1, Card.BLUE)
                .add(1, Card.LOCOMOTIVE)
                .build();
        SortedBag<Card> expected13 = new SortedBag.Builder<Card>()
                .add(2, Card.LOCOMOTIVE)
                .build();

        List<SortedBag<Card>> expected1 = List.of(expected11, expected12, expected13);
        System.out.println(expected1);
        assertEquals(expected1, possibleAdditionalCards);
    }

    //Case: ClaimCardsUsed: 3x Blue, DrawnCards: 3x Locomotives
    @Test
    void possibleAdditionalCardsWorks3(){
        SortedBag<Ticket> tickets = SortedBag.of(1, ChMap.tickets().get(0), 1, ChMap.tickets().get(1));

        SortedBag<Card> playerCards = new SortedBag.Builder<Card>()
                .add(7, Card.BLUE)
                .add(4, Card.LOCOMOTIVE)
                .build();

        SortedBag<Card> ic = new SortedBag.Builder<Card>()
                .add(3, Card.BLUE)
                .build();

        SortedBag<Card> dc = new SortedBag.Builder<Card>()
                .add(3, Card.LOCOMOTIVE)
                .build();

        Route tunnelRoute = ChMap.routes().get(39);
        System.out.println("TunnelRoute:" + tunnelRoute.stations());
        PlayerState playerState = new PlayerState(tickets, playerCards, routes);

        List<SortedBag<Card>> possibleAdditionalCards = playerState.possibleAdditionalCards(tunnelRoute.additionalClaimCardsCount(ic, dc), ic, dc);

        SortedBag<Card> expected11 = new SortedBag.Builder<Card>()
                .add(3, Card.BLUE)
                .build();
        SortedBag<Card> expected12 = new SortedBag.Builder<Card>()
                .add(2, Card.BLUE)
                .add(1, Card.LOCOMOTIVE)
                .build();
        SortedBag<Card> expected13 = new SortedBag.Builder<Card>()
                .add(1, Card.BLUE)
                .add(2, Card.LOCOMOTIVE)
                .build();
        SortedBag<Card> expected14 = new SortedBag.Builder<Card>()
                .add(3, Card.LOCOMOTIVE)
                .build();

        List<SortedBag<Card>> expected1 = List.of(expected11, expected12, expected13, expected14);

        assertEquals(expected1, possibleAdditionalCards);
    }

    @Test
    void possibleAdditionalCardsWorksLocomotives(){
        SortedBag<Ticket> tickets = SortedBag.of(1, ChMap.tickets().get(0), 1, ChMap.tickets().get(1));

        SortedBag<Card> playerCards = new SortedBag.Builder<Card>()
                .add(6, Card.LOCOMOTIVE)
                .add(3, Card.BLUE)
                .build();

        SortedBag<Card> ic = new SortedBag.Builder<Card>()
                .add(3, Card.LOCOMOTIVE)
                .build();

        SortedBag<Card> dc = new SortedBag.Builder<Card>()
                .add(2, Card.BLUE)
                .add(1, Card.LOCOMOTIVE)
                .build();

        Route tunnelRoute = ChMap.routes().get(39);
        System.out.println("TunnelRoute:" + tunnelRoute.stations());
        PlayerState playerState = new PlayerState(tickets, playerCards, routes);

        List<SortedBag<Card>> possibleAdditionalCards = playerState.possibleAdditionalCards(tunnelRoute.additionalClaimCardsCount(ic, dc), ic, dc);

        SortedBag<Card> expected11 = new SortedBag.Builder<Card>()
                .add(1, Card.LOCOMOTIVE)
                .build();
        List<SortedBag<Card>> expected1 = List.of(expected11);

        assertEquals(expected1, possibleAdditionalCards);
        //======
        SortedBag<Card> dc2 = new SortedBag.Builder<Card>()
                .add(3, Card.LOCOMOTIVE)
                .build();

        List<SortedBag<Card>> possibleAdditionalCards2 = playerState.possibleAdditionalCards(tunnelRoute.additionalClaimCardsCount(ic, dc2), ic, dc2);

        SortedBag<Card> expected12 = new SortedBag.Builder<Card>()
                .add(3, Card.LOCOMOTIVE)
                .build();

        List<SortedBag<Card>> expected2 = List.of(expected12);

        assertEquals(expected2, possibleAdditionalCards2);

    }

    @Test
    void possibleAdditionalCardsFailsWithWrongNumberOfCards() {
        SortedBag<Card> initialCards = SortedBag.of(4, Card.BLUE);
        SortedBag<Card> drawnCards = SortedBag.of(2, Card.BLUE, 1, Card.WHITE);
        assertThrows(IllegalArgumentException.class, () -> {
            state.possibleAdditionalCards(0, initialCards, drawnCards);
        });
    }

    @Test
    void possibleAdditionalCardsFailsWithWrongInitialCards() {
        SortedBag<Card> initialCards = new SortedBag.Builder<Card>().add(Card.GREEN).add(Card.LOCOMOTIVE).add(Card.BLUE).build();
        SortedBag<Card> drawnCards = SortedBag.of(2, Card.BLUE, 1, Card.WHITE);
        assertThrows(IllegalArgumentException.class, () -> {
            state.possibleAdditionalCards(2, initialCards, drawnCards);
        });
    }

    @Test
    void possibleAdditionalCardsFailsWithWrongDrawnCards() {
        SortedBag<Card> initialCards = new SortedBag.Builder<Card>().add(Card.GREEN).add(Card.LOCOMOTIVE).add(Card.BLUE).build();
        SortedBag<Card> drawnCards = SortedBag.of(2, Card.BLUE);
        assertThrows(IllegalArgumentException.class, () -> {
            state.possibleAdditionalCards(2, initialCards, drawnCards);
        });
    }

    @Test
    void withClaimedRouteWorks() {
        SortedBag<Card> claimCards = SortedBag.of(2, Card.BLUE, 2, Card.LOCOMOTIVE);
        SortedBag<Card> newCards = SortedBag.of(3, Card.BLUE, 1, Card.LOCOMOTIVE);
        assertEquals(newCards, state.withClaimedRoute(route, claimCards).cards());
        List<Route> newRoutes = List.of(ChMap.routes().get(3), ChMap.routes().get(5), route);
        assertEquals(newRoutes, state.withClaimedRoute(route, claimCards).routes());
    }

    @Test
    void possibleClaimCardsWorks() {
        //Cards owned by player:
        //SortedBag<Card> cards = SortedBag.of(5, Card.BLUE, 3, Card.LOCOMOTIVE);

        List<SortedBag<Card>> expected = List.of(
                SortedBag.of(3, Card.BLUE),
                SortedBag.of(2, Card.BLUE, 1, Card.LOCOMOTIVE),
                SortedBag.of(1, Card.BLUE, 2, Card.LOCOMOTIVE),
                SortedBag.of(3, Card.LOCOMOTIVE));

        Route route = ChMap.routes().get(31);

        assertEquals(expected, state.possibleClaimCards(route));
    }


    //TODO: Complete Test
    @Test
    void ticketPointsWorks(){}

    //TODO: Complete Test
    @Test
    void finalPointsWorks(){}
}



