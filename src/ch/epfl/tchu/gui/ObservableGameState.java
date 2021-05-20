package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;

public final class ObservableGameState {

    private final ObjectProperty<PlayerState> playerStateProperty;
    private final ObjectProperty<PublicGameState> gameStateProperty;
    private final PlayerId PID;

    private final DoubleProperty ticketsGauge = new SimpleDoubleProperty(0);
    private final DoubleProperty cardsGauge = new SimpleDoubleProperty(0);
    private final List<ObjectProperty<Card>> faceUpCards = createFaceUpCards();
    private final MapProperty<Route, PlayerId> routesMap = new SimpleMapProperty<>(FXCollections.observableHashMap());

    //First element in list is always PLAYER_1 and 2nd is always PLAYER_2
    private final List<IntegerProperty> playersTicketCount = new ArrayList<>();
    private final List<IntegerProperty> playersCardCount = new ArrayList<>();
    private final List<IntegerProperty> playersCarCount = new ArrayList<>();
    private final List<IntegerProperty> playersPointsCount = new ArrayList<>();

    private final ListProperty<Ticket> tickets = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ListProperty<Integer> cardAmount = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final MapProperty<Route, Boolean> canClaimRoute = new SimpleMapProperty<>(FXCollections.observableHashMap());


    public ObservableGameState(PlayerId PID) {
        this.PID = PID;
        playerStateProperty = new SimpleObjectProperty<>();
        gameStateProperty = new SimpleObjectProperty<>();

        for(Route r : ChMap.routes()) routesMap.put(r, null);

        for(PlayerId pid : PlayerId.ALL) {
            playersTicketCount.add(new SimpleIntegerProperty(0));
            playersCardCount.add(new SimpleIntegerProperty(0));
            playersCarCount.add(new SimpleIntegerProperty(0));
            playersPointsCount.add(new SimpleIntegerProperty(0));
        }

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
        for(Route r : ChMap.routes()) {
            if(newGameState.playerState(PID).routes().contains(r)) newRoutesMap.put(r, PID);
            else if(newGameState.playerState(PID.next()).routes().contains(r)) newRoutesMap.put(r, PID.next());
            else newRoutesMap.put(r, null);
        }
        routesMap.setValue(newRoutesMap);


        for(int i = 0; i < PlayerId.ALL.size(); ++i) {
            playersTicketCount.get(i).setValue(newGameState.playerState(PlayerId.ALL.get(i)).ticketCount());
            playersCardCount.get(i).setValue(newGameState.playerState(PlayerId.ALL.get(i)).cardCount());
            playersCarCount.get(i).setValue(newGameState.playerState(PlayerId.ALL.get(i)).carCount());
            playersPointsCount.get(i).setValue(newGameState.playerState(PlayerId.ALL.get(i)).claimPoints());
        }

    }

    private List<ObjectProperty<Card>> createFaceUpCards() {

        List<ObjectProperty<Card>> cards = new ArrayList<>();
        for(int ignore : Constants.FACE_UP_CARD_SLOTS) cards.add(new SimpleObjectProperty<>());

        return cards;

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

}
