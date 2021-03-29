package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class PlayerState extends  PublicPlayerState {


    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    /**
     * Public constructor of the PlayerState class
     * @param tickets (SortedBag<Ticket>) SortedBag of tickets
     * @param cards (SortedBag<Card>) SortedBag of cards
     * @param routes (List<Route>) List of routes
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);
        this.tickets = tickets;
        this.cards = cards;
    }

    /**
     * returns the initial state of a player to whom the given initial cards were dealt;
     * in this initial state, the player does not yet have any tickets, and has not taken any road
     * @param initialCards (SortedBag<Card>) initial 4 cards the player has
     * @return the initial state of a player to whom the given initial cards were dealt
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, new ArrayList<>());
    }

    /**
     * Returns the players tickets
     * @return the players tickets
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    /**
     * returns an identical state to the receiver, except that the player also has the given tickets,
     * @param newTickets (SortedBag<Ticket>) new tickets
     * @return an identical state to the receiver, except that the player also has the given tickets,
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, this.routes());
    }

    /**
     * Returns the player's wagon / locomotive cards
     * @return the player's wagon / locomotive cards
     */
    public SortedBag<Card> cards() {
        return cards;
    }

    /**
     * returns an identical state to the receiver, except that the player also has the given card
     * @param card (Card) given card
     * @return an identical state to the receiver, except that the player also has the given card
     */
    public PlayerState withAddedCard(Card card) {
        return new PlayerState(tickets, cards.union(SortedBag.of(card)), this.routes());
    }

    /**
     * returns an identical state to the receiver, except that the player also has the given cards,
     * @param additionalCards (SortedBag<Card>) aditional cards that the player has
     * @return an identical state to the receiver, except that the player also has the given cards,
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(tickets, cards.union(additionalCards), this.routes());
    }

    /**
     * returns true iff the player can seize the given route
     * @param route (Route) route the player wants to seize
     * @return true iff the player can seize the given route
     */
    public boolean canClaimRoute(Route route) {

        if(route.color() == null) {

            boolean cardsOfOneColour = false;

            for(Color c : Color.ALL) {
                if(cards.contains(SortedBag.of(route.length(), Card.of(c)))) {
                    cardsOfOneColour = true;
                }
            }

            return cardsOfOneColour && this.carCount() >= route.length();

        } else {
            return cards.contains(SortedBag.of(route.length(), Card.of(route.color()))) && this.carCount() >= route.length();
        }
    }



    /**
     * returns the list of all the sets of cards the player could use to take possession of the given route
     * @param route (Route) given route
     * @return the list of all the sets of cards the player could use to take possession of the given route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(this.carCount() >= route.length());

        List<SortedBag<Card>> allPossibleClaimCards = route.possibleClaimCards();
        List<SortedBag<Card>> playerPossibleClaimCards = new ArrayList<>();

        for(SortedBag<Card> sc : allPossibleClaimCards) {
            if(cards.contains(sc) && !(sc.contains(Card.LOCOMOTIVE) && route.level().equals(Route.Level.OVERGROUND))) {
                playerPossibleClaimCards.add(sc);
            }
        }

        return playerPossibleClaimCards;
    }

    /**
     * returns the list of all the sets of cards that the player could use to seize a tunnel,
     * sorted in ascending order of the number of locomotive cards
     * @param additionalCardsCount (int) number of additional cards
     * @param initialCards (SortedBag<Card>) initially placed cards
     * @param drawnCards (SortedBag<Card>) 3 drawn cards from the top puke
     * @return the list of all sets of cards that the player could use to seize a tunnel
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {

        Map<Card, Integer> twoTypesMax = initialCards.toMap();
        Preconditions.checkArgument(additionalCardsCount >= 1 && additionalCardsCount <= 3 && !initialCards.isEmpty() && twoTypesMax.size() <= 2 && drawnCards.size() == 3);

        List<SortedBag<Card>> possibleCards = new ArrayList<>();
        SortedBag<Card> cardsDifference = SortedBag.of(cards).difference(initialCards);

        for(int i = 0; i <= additionalCardsCount; ++i) {
            if(cardsDifference.contains(SortedBag.of(i, Card.LOCOMOTIVE)) && cardsDifference.contains(SortedBag.of(additionalCardsCount - i, initialCards.get(0)))
            && !possibleCards.contains(SortedBag.of(additionalCardsCount - i, initialCards.get(0), i, Card.LOCOMOTIVE))
            && SortedBag.of(additionalCardsCount - i, initialCards.get(0), i, Card.LOCOMOTIVE).size() == additionalCardsCount) {

                possibleCards.add(SortedBag.of(additionalCardsCount - i, initialCards.get(0), i, Card.LOCOMOTIVE));
            }
        }

        return possibleCards;
    }

    /**
     * returns an identical state to the receiver, except that the player has also seized the given route by means of the given cards
     * @param route (Route) given route
     * @param claimCards (SortedBag<Card>) given cards
     * @return an identical state to the receiver, except that the player has also seized the given route by means of the given cards
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        List<Route> newRoutes = new ArrayList<>(routes());
        newRoutes.add(route);
        return new PlayerState(tickets, cards.difference(claimCards), newRoutes);
    }

    /**
     * returns the number of points - possibly negative - obtained by the player thanks to his tickets
     * @return the number of points - possibly negative - obtained by the player thanks to his tickets
     */
    public int ticketPoints() {

        int maxID = 0;

        for(Route r : routes()) {
            if(r.station1().id() > maxID) {
                maxID = r.station1().id();
            }
            if(r.station2().id() > maxID) {
                maxID = r.station2().id();
            }
        }

        StationPartition.Builder connectivityBuilder = new StationPartition.Builder(maxID + 1);

        for(Route r1 : routes()) {
            for(Route r2 : routes()) {
                if(!r1.equals(r2)) {
                    if(r1.station1().equals(r2.station1())) {
                        connectivityBuilder.connect(r1.station1(), r2.station2());
                        connectivityBuilder.connect(r1.station2(), r2.station1());
                        connectivityBuilder.connect(r1.station2(), r2.station2());
                    } else if(r1.station1().equals(r2.station2())) {
                        connectivityBuilder.connect(r1.station1(), r2.station1());
                        connectivityBuilder.connect(r1.station2(), r2.station1());
                        connectivityBuilder.connect(r1.station2(), r2.station2());
                    } else if (r1.station2().equals(r2.station1())) {
                        connectivityBuilder.connect(r1.station1(), r2.station1());
                        connectivityBuilder.connect(r1.station1(), r2.station2());
                        connectivityBuilder.connect(r1.station2(), r2.station2());
                    } else if(r1.station2().equals(r2.station2())) {
                        connectivityBuilder.connect(r1.station1(), r2.station1());
                        connectivityBuilder.connect(r1.station1(), r2.station2());
                        connectivityBuilder.connect(r1.station2(), r2.station1());
                    }
                }
            }
        }

        StationPartition connectivity = connectivityBuilder.build();

        int points = 0;

        for(Ticket t : tickets) {
            points = points + t.points(connectivity);
        }

        return points;
    }

    /**
     * returns all the points obtained by the player at the end of the game
     * @return all the points obtained by the player at the end of the game
     */
    public int finalPoints() {
        return ticketPoints() + claimPoints();
    }

}
