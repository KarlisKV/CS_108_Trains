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
 */
public final class ObservableGameState {

    //Public stats of the game:

    private final DoubleProperty ticketsGauge = new SimpleDoubleProperty(0);
    private final DoubleProperty cardsGauge = new SimpleDoubleProperty(0);
    private final List<ObjectProperty<Card>> faceUpCards = new ArrayList<>();
    private final MapProperty<Route, PlayerId> routesMap = new SimpleMapProperty<>(FXCollections.observableHashMap());



    //Public stats of each player:

    //First element in list is always PLAYER_1 and 2nd is always PLAYER_2
    private final List<IntegerProperty> playersTicketCount = new ArrayList<>();
    private final List<IntegerProperty> playersCardCount = new ArrayList<>();
    private final List<IntegerProperty> playersCarCount = new ArrayList<>();
    private final List<IntegerProperty> playersPointsCount = new ArrayList<>();



    //Private stats of player PlayerId:

    private final ListProperty<Ticket> tickets = new SimpleListProperty<>(FXCollections.observableArrayList());
    //Quantity of each type of card the player PlayerId has (in the order of the Card Enum)
    private final ListProperty<Integer> cardAmount = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final MapProperty<Route, Boolean> canClaimRoute = new SimpleMapProperty<>(FXCollections.observableHashMap());



    //Properties

    private final PlayerId playerId;
    private final ObjectProperty<PlayerState> playerStateProperty;
    private final ObjectProperty<PublicGameState> gameStateProperty;


    /**
     * Default constructor of ObservableGameState
     * @param playerId the Id of the given player
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




    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    public ReadOnlyDoubleProperty ticketsGauge() {
        return ticketsGauge;
    }

    public ReadOnlyDoubleProperty cardsGauge() {
        return cardsGauge;
    }

    public ReadOnlyMapProperty<Route, PlayerId> routesMapProperty() {
        return routesMap;
    }



    public List<ReadOnlyIntegerProperty> playersTicketCount() {
        return new ArrayList<>(playersTicketCount);
    }

    public List<ReadOnlyIntegerProperty> playersCardCount() {
        return new ArrayList<>(playersCardCount);
    }

    public List<ReadOnlyIntegerProperty> playersCarCount() {
        return new ArrayList<>(playersCarCount);
    }

    public List<ReadOnlyIntegerProperty> playerPoints() {
        return new ArrayList<>(playersPointsCount);
    }



    public ReadOnlyListProperty<Ticket> tickets() {
        return tickets;
    }

    public ReadOnlyListProperty<Integer> cardAmount() {
        return cardAmount;
    }

    public ReadOnlyMapProperty<Route, Boolean> canClaimRoute() {
        return canClaimRoute;
    }




    public boolean canDrawTickets() {
        return gameStateProperty.get().canDrawTickets();
    }

    public boolean canDrawCards() {
        return gameStateProperty.get().canDrawCards();
    }

    public List<SortedBag<Card>> possibleClaimCards(Route r) {
        return playerStateProperty.get().possibleClaimCards(r);
    }



}
