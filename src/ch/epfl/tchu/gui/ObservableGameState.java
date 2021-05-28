package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;

/**
 * ObservableGameSate, represents the observable part of the GameState
 * @author Daniel Polka  (326800)
 * @author Karlis Velins (325180)
 * This is a combined state that includes:
 * the public part of the state of the game, i.e. the information contained in an instance of PublicGameState,
 * the entire state of a given player, that is, the information contained in an instance of PlayerState.
 */
public final class ObservableGameState {

    //Public stats of the game
    private final DoubleProperty ticketsGauge = new SimpleDoubleProperty(0);
    private final DoubleProperty cardsGauge = new SimpleDoubleProperty(0);
    private final List<ObjectProperty<Card>> faceUpCards = new ArrayList<>();
    private final MapProperty<Route, PlayerId> routesMap = new SimpleMapProperty<>(FXCollections.observableHashMap());

    //First element in list is always PLAYER_1 and 2nd is always PLAYER_2
    private final List<IntegerProperty> playersTicketCount = new ArrayList<>();
    private final List<IntegerProperty> playersCardCount = new ArrayList<>();
    private final List<IntegerProperty> playersCarCount = new ArrayList<>();
    private final List<IntegerProperty> playersPointsCount = new ArrayList<>();


    //Private stats of player PlayerId
    private final ListProperty<Ticket> tickets = new SimpleListProperty<>(FXCollections.observableArrayList());
    //Quantity of each type of card the player PlayerId has (in the order of the Card Enum)
    private final ListProperty<Integer> cardAmount = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final MapProperty<Route, Boolean> canClaimRoute = new SimpleMapProperty<>(FXCollections.observableHashMap());

    //Other attributes
    private final PlayerId playerId;
    private final ObjectProperty<PlayerState> playerStateProperty;
    private final ObjectProperty<PublicGameState> gameStateProperty;

    /**
     * Default constructor of ObservableGameState
     * @param playerId the ID of the given player
     */
    public ObservableGameState(PlayerId playerId) {

        this.playerId= playerId;

        playerStateProperty = new SimpleObjectProperty<>();
        gameStateProperty = new SimpleObjectProperty<>();

        for(int ignore : Constants.FACE_UP_CARD_SLOTS) faceUpCards.add(new SimpleObjectProperty<>());

        MapProperty<Route, PlayerId> initRoutesMap = new SimpleMapProperty<>(FXCollections.observableHashMap());

        MapProperty<Route, Boolean> initCanClaimCards = new SimpleMapProperty<>(FXCollections.observableHashMap());

        for(Route r : ChMap.routes()) {
            initRoutesMap.put(r, null);

            initCanClaimCards.put(r, false);
        }

        routesMap.setValue(initRoutesMap);
        canClaimRoute.setValue(initCanClaimCards);

        for(PlayerId ignore : PlayerId.ALL) {
            playersTicketCount.add(new SimpleIntegerProperty(0));
            playersCardCount.add(new SimpleIntegerProperty(0));
            playersCarCount.add(new SimpleIntegerProperty(0));
            playersPointsCount.add(new SimpleIntegerProperty(0));
        }

        for(Card ignore : Card.ALL) cardAmount.add(0);
    }

    /**
     * Method to update the state of the different properties controlling the GUI
     * @param newGameState (PublicGameState) given gameState to update
     * @param newPlayerState (PlayerState) given playerState to update
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {

        gameStateProperty.setValue(newGameState);
        playerStateProperty.setValue(newPlayerState);

        ticketsGauge.setValue(newGameState.ticketsCount() / (double) ChMap.tickets().size() * 50);
        cardsGauge.setValue((newGameState.cardState().deckSize() / (double) Constants.TOTAL_CARDS_COUNT * 50));

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).setValue(newCard);
        }

        MapProperty<Route, PlayerId> newRoutesMap = new SimpleMapProperty<>(FXCollections.observableHashMap());

        MapProperty<Route, Boolean> newClaimMap = new SimpleMapProperty<>(FXCollections.observableHashMap());

        for(Route r : ChMap.routes()) {

            if(newGameState.playerState(playerId).routes().contains(r)) {

                newRoutesMap.put(r, playerId);

                newClaimMap.put(r, false);

            } else if(newGameState.playerState(playerId.next()).routes().contains(r)) {

                newRoutesMap.put(r, playerId.next());

                newClaimMap.put(r, false);

            } else {

                newRoutesMap.put(r, null);

                boolean sideRouteNotClaimed = true;

                if(ChMap.routes().indexOf(r) > 0 && ChMap.routes().indexOf(r) < ChMap.routes().size() - 1) {

                    Route lastRoute = ChMap.routes().get(ChMap.routes().indexOf(r) - 1);
                    Route nextRoute = ChMap.routes().get(ChMap.routes().indexOf(r) + 1);

                    if(lastRoute.stations().equals(r.stations())) {
                        for(PlayerId pid : PlayerId.ALL) if(newGameState.playerState(pid).routes().contains(lastRoute)) sideRouteNotClaimed = false;

                    } else if (nextRoute.stations().equals(r.stations())) {
                        for(PlayerId pid : PlayerId.ALL) if(newGameState.playerState(pid).routes().contains(nextRoute)) sideRouteNotClaimed = false;

                    }
                } else if(ChMap.routes().indexOf(r) == ChMap.routes().size() - 1)
                    for(PlayerId pid : PlayerId.ALL)
                        if(newGameState.playerState(pid).routes().contains(ChMap.routes().get(ChMap.routes().indexOf(r) - 1))) sideRouteNotClaimed = false;

                        if(newPlayerState.canClaimRoute(r) && sideRouteNotClaimed) newClaimMap.put(r, true);
                        else newClaimMap.put(r, false);

            }
        }

        System.out.println();

        if(!routesMap.equals(newRoutesMap)) routesMap.setValue(newRoutesMap);

        if(!newClaimMap.equals(canClaimRoute)) canClaimRoute.setValue(newClaimMap);

        for(int i = 0; i < PlayerId.ALL.size(); ++i) {
            playersTicketCount.get(i).setValue(newGameState.playerState(PlayerId.ALL.get(i)).ticketCount());
            playersCardCount.get(i).setValue(newGameState.playerState(PlayerId.ALL.get(i)).cardCount());
            playersCarCount.get(i).setValue(newGameState.playerState(PlayerId.ALL.get(i)).carCount());
            playersPointsCount.get(i).setValue(newGameState.playerState(PlayerId.ALL.get(i)).claimPoints());
        }

        tickets.addAll((newPlayerState.tickets().difference(SortedBag.of(new ArrayList<>(tickets)))).toList());

        for(Card c : Card.ALL) cardAmount.set(Card.ALL.indexOf(c), newPlayerState.cards().countOf(c));
    }

    /**
     * property for the faceUpCard
     * @param slot (int) slot number
     * @return faceUpCard with the given slot
     */
    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     * a property that contains the size of the gauge for the tickets remaining in the draw pile
     * @return the property containing the size of the gauge for the tickets remaining in the draw pile
     */
    public ReadOnlyDoubleProperty ticketsGauge() {
        return ticketsGauge;
    }

    /**
     * a property that contains the size of the gauge for the cards remaining in the draw pile
     * @return the property containing the size of the gauge for the cards remaining in the draw pile
     */
    public ReadOnlyDoubleProperty cardsGauge() {
        return cardsGauge;
    }

    /**
     * Returns as many properties as there are routes in the tCHu network and containing,
     * for each of them, the identity of the player owning it, or null, if it does not belong to anyone
     * @return as many properties as there are routes in the tCHu network and containing,
     * for each of them, the identity of the player owning it, or null, if it does not belong to anyone
     */
    public ReadOnlyMapProperty<Route, PlayerId> routesMapProperty() {
        return routesMap;
    }


    /**
     * Returns the property per player containing the number of tickets he has in hand
     * @return the property per player containing the number of tickets he has in hand,
     */
    public List<ReadOnlyIntegerProperty> playersTicketCount() {
        return new ArrayList<>(playersTicketCount);
    }

    /**
     * Return the property per player containing the number of cards they have in their hand
     * @return the property per player containing the number of cards they have in their hand
     */
    public List<ReadOnlyIntegerProperty> playersCardCount() {
        return new ArrayList<>(playersCardCount);
    }

    /**
     * Return the property per player containing the number of wagons they have
     * @return the property per player containing the number of wagons they have
     */
    public List<ReadOnlyIntegerProperty> playersCarCount() {
        return new ArrayList<>(playersCarCount);
    }

    /**
     * Return the property per player containing the number of construction points he has obtained
     * @return the property per player containing the number of construction points he has obtained
     */
    public List<ReadOnlyIntegerProperty> playerPoints() {
        return new ArrayList<>(playersPointsCount);
    }

    /**
     * Return the property containing the list of player ticket
     * @return the property containing the list of player ticket
     */
    public ReadOnlyListProperty<Ticket> tickets() {
        return tickets;
    }

    /**
     * Return nine properties containing, for each type of wagon / locomotive card,
     * the number of cards of this type that the player has in hand
     * @return nine properties containing, for each type of wagon / locomotive card,
     * the number of cards of this type that the player has in hand
     */
    public ReadOnlyListProperty<Integer> cardAmount() {
        return cardAmount;
    }

    /**
     * Return the boolean value for each of the routes (true) when the player is able to claim the route
     * @return the boolean value for each of the routes (true) when the player is able to claim the route
     */
    public ReadOnlyMapProperty<Route, Boolean> canClaimRoute() {
        return canClaimRoute;
    }

    /**
     * Returns true if it's possible to draw Tickets
     * @return true if it's possible to draw Tickets
     */
    public boolean canDrawTickets() {
        return gameStateProperty.get().canDrawTickets();
    }

    /**
     * Returns true if it's possible to draw Cards
     * @return true if it's possible to draw Cards
     */
    public boolean canDrawCards() {
        return gameStateProperty.get().canDrawCards();
    }

    /**
     * Returns the list of the sortedBag of the possible claimCards for the given route
     * @return the list of the sortedBag of the possible claimCards for the given route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route r) {
        return playerStateProperty.get().possibleClaimCards(r);
    }
}
