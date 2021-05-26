package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public final class Stages9and10tests extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        ObservableGameState gameState = new ObservableGameState(PLAYER_1);

        ObservableList<Text> infos = FXCollections.observableArrayList(
                new Text("Première information.\n"),
                new Text("\nSeconde information.\n"));

        ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRoute =
                new SimpleObjectProperty<>(Stages9and10tests::claimRoute);
        ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTickets =
                new SimpleObjectProperty<>(Stages9and10tests::drawTickets);

        ObjectProperty<ActionHandlers.DrawCardHandler> drawCard =
                new SimpleObjectProperty<>((s) -> {
                    drawCard(s);
                    if(infos.size() == 5) infos.remove(0);
                    List<Text> texts = List.of(new Text(" aaa \n"), new Text(" bbb \n"), new Text(" ccc \n"), new Text(" ddd \n"),
                            new Text(" eee \n"), new Text(" fff \n"));
                    infos.add(texts.get(new Random().nextInt(texts.size())));
                });

        Node mapView = MapViewCreator
                .createMapView(gameState, claimRoute, Stages9and10tests::chooseCards);
        Node cardsView = DecksViewCreator
                .createCardsView(gameState, drawTickets, drawCard);
        Node handView = DecksViewCreator
                .createHandView(gameState);

        Map<PlayerId, String> playerNames =
                Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
        Node infoView = InfoViewCreator
                .createInfoView(PLAYER_2, playerNames, gameState, infos);

        BorderPane mainPane =
                new BorderPane(mapView, null, cardsView, handView, infoView);

        primaryStage.setScene(new Scene(mainPane));

        primaryStage.show();

        setState(gameState);
    }




    private void setState(ObservableGameState gameState) {

        PlayerState p1State =
                new PlayerState(SortedBag.of(ChMap.tickets().subList(0, 3)),
                        SortedBag.of(1, Card.WHITE, 3, Card.RED),
                        ChMap.routes().subList(0, 3));

        PublicPlayerState p2State =
                new PublicPlayerState(0, 0, ChMap.routes().subList(3, 6));

        Map<PlayerId, PublicPlayerState> pubPlayerStates =
                Map.of(PLAYER_1, p1State, PLAYER_2, p2State);
        PublicCardState cardState =
                new PublicCardState(Card.ALL.subList(0, 5), 110 - 2 * 4 - 5, 0);
        PublicGameState publicGameState =
                new PublicGameState(36, cardState, PLAYER_1, pubPlayerStates, null);

        gameState.setState(publicGameState, p1State);
    }

    private static void claimRoute(Route route, SortedBag<Card> cards) {
        System.out.printf("Prise de possession d'une route : %s - %s %s%n",
                route.station1(), route.station2(), cards);
    }

    private static void chooseCards(List<SortedBag<Card>> options,
                                    ActionHandlers.ChooseCardsHandler chooser) {
        chooser.onChooseCards(options.get(0));
    }

    private static void drawTickets() {
        System.out.println("Tirage de billets !");
    }

    private static void drawCard(int slot) {
        System.out.printf("Tirage de cartes (emplacement %s)!\n", slot);
    }
}