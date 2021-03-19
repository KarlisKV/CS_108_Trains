package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PlayerState extends  PublicPlayerState {


    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;


    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
    }

    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == 4);
        return new PlayerState(SortedBag.of(), initialCards, new ArrayList<>());
    }




    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, this.routes());
    }

    public SortedBag<Card> cards() {
        return cards;
    }

    public PlayerState withAddedCard(Card card) {
        return new PlayerState(tickets, cards.union(SortedBag.of(card)), this.routes());
    }

    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(tickets, cards.union(additionalCards), this.routes());
    }

    public boolean canClaimRoute(Route route) {
        return cards.contains(SortedBag.of(route.length(), Card.of(route.color()))) && this.carCount() >= route.length();
    }

    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(this.carCount() >= route.length());

        List<SortedBag<Card>> allPossibleClaimCards = route.possibleClaimCards();
        List<SortedBag<Card>> playerPossibleClaimCards = new ArrayList<>();

        for(SortedBag<Card> sc : allPossibleClaimCards) {
            if(!sc.union(cards).equals(sc)) {
                playerPossibleClaimCards.add(sc);
            }
        }

        return playerPossibleClaimCards;
    }

    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        Map<Card, Integer> twoTypesMax = initialCards.toMap();
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= 3 && !initialCards.isEmpty() && twoTypesMax.size() <= 2 && drawnCards.size() == 3);

        List<SortedBag<Card>> possibleCards = new ArrayList<>();

        for(int i = 0; i <= additionalCardsCount; ++i) {
            if(cards.contains(SortedBag.of(i, Card.LOCOMOTIVE)) && cards.contains(SortedBag.of(additionalCardsCount - i, initialCards.get(0)))) {
                    possibleCards.add(SortedBag.of(additionalCardsCount - i, initialCards.get(0), i, Card.LOCOMOTIVE));
            }
        }

        return possibleCards;
    }

    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> newRoutes = new ArrayList<>(routes());
        newRoutes.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), newRoutes);
    }

    //TODO
    public int ticketPoints() {
        int ticketScore = 0;

        

        for(Ticket t : tickets) {

        }

        return ticketScore;
    }

    public int finalPoints() {
        return ticketPoints() + claimPoints();
    }

}
