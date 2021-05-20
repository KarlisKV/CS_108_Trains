package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;

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



    //Private stats of player PID:

    private final ListProperty<Ticket> tickets = new SimpleListProperty<>(FXCollections.observableArrayList());
    //Quantity of each type of card the player PID has (in the order of the Card Enum)
    private final ListProperty<Integer> cardAmount = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final MapProperty<Route, Boolean> canClaimRoute = new SimpleMapProperty<>(FXCollections.observableHashMap());



    //Other:

    private final PlayerId PID;
    private final ObjectProperty<PlayerState> playerStateProperty;
    private final ObjectProperty<PublicGameState> gameStateProperty;





    public ObservableGameState(PlayerId PID) {

        this.PID = PID;

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

        ticketsGauge.setValue(newGameState.ticketsCount() / (double) ChMap.tickets().size() * 100);
        cardsGauge.setValue((newGameState.cardState().deckSize() / (double) Constants.TOTAL_CARDS_COUNT * 100));

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).setValue(newCard);
        }

        MapProperty<Route, PlayerId> newRoutesMap = new SimpleMapProperty<>(FXCollections.observableHashMap());

        MapProperty<Route, Boolean> newClaimMap = new SimpleMapProperty<>(FXCollections.observableHashMap());

        for(Route r : ChMap.routes()) {

            if(newGameState.playerState(PID).routes().contains(r)) {

                newRoutesMap.put(r, PID);

                newClaimMap.put(r, false);

            } else if(newGameState.playerState(PID.next()).routes().contains(r)) {

                newRoutesMap.put(r, PID.next());

                newClaimMap.put(r, false);

            } else {

                newRoutesMap.put(r, null);

                if(newPlayerState.canClaimRoute(r)) newClaimMap.put(r, true);
                else newClaimMap.put(r, false);

            }
        }

        if(!routesMap.equals(newRoutesMap)) routesMap.setValue(newRoutesMap);

        if(!newClaimMap.equals(canClaimRoute)) canClaimRoute.setValue(newClaimMap);



        for(int i = 0; i < PlayerId.ALL.size(); ++i) {
            playersTicketCount.get(i).setValue(newGameState.playerState(PlayerId.ALL.get(i)).ticketCount());
            playersCardCount.get(i).setValue(newGameState.playerState(PlayerId.ALL.get(i)).cardCount());
            playersCarCount.get(i).setValue(newGameState.playerState(PlayerId.ALL.get(i)).carCount());
            playersPointsCount.get(i).setValue(newGameState.playerState(PlayerId.ALL.get(i)).claimPoints());
        }


        tickets.addAll((newPlayerState.tickets().difference(SortedBag.of(new ArrayList<>(tickets)))).toList());

        for(Card c : Card.ALL) if(newPlayerState.cards().contains(c)) cardAmount.set(Card.ALL.indexOf(c), newPlayerState.cards().countOf(c));
    }



    public ObjectProperty<PublicGameState> gameStateProperty() {
        return gameStateProperty;
    }

    public ObjectProperty<PlayerState> playerStateProperty() {
        return playerStateProperty;
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

    public List<ReadOnlyIntegerProperty> playersPointsCount() {
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
