package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.*;

public final class DecksViewCreator{

    private DecksViewCreator() {}


    public static Node createHandView(ObservableGameState state){

        HBox mainBox = new HBox();
        mainBox.getStylesheets().add("decks.css");
        mainBox.getStylesheets().add("colors.css");

        List<Ticket> playerTickets = new ArrayList<>();
        SortedBag<Card> cardsToShow = SortedBag.of();

        if(state.getPlayerState() != null) {
            playerTickets = state.getPlayerState().tickets().toList();
            cardsToShow = state.getPlayerState().cards();
        }

        ListView<Ticket> tickets = new ListView<>();
        tickets.getItems().addAll(playerTickets);
        tickets.setId("tickets");
        mainBox.getChildren().add(tickets);

        //Second HBox (bigger group)
        HBox secBox = new HBox();
        secBox.setId("hand-pane");

        final SortedBag<Card> cts = SortedBag.of(cardsToShow);
        final List<StackPane> lsp = new ArrayList<>();

        for(Card c : Card.ALL) {
            StackPane p = cardPane(c, cardsToShow.countOf(c));
            lsp.add(p);
            secBox.getChildren().add(p);
        }

        ObservableValue<List<Card>> obsPCards = new SimpleObjectProperty<>(state.getGameState().cardState().faceUpCards());
        obsPCards.addListener((o, oV, nV) -> {
            mainBox.getChildren().removeAll(lsp);
            lsp.clear();
            for(Card c : Card.ALL) secBox.getChildren().add(cardPane(c, cts.countOf(c)));

            System.out.println("success in handView");
        });

        mainBox.getChildren().add(secBox);

        return mainBox;
    }


    public static Node createCardsView(ObservableGameState state, ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketsHandler, ObjectProperty<ActionHandlers.DrawCardHandler> cardsHandler){

        VBox mainBox = new VBox();
        mainBox.getStylesheets().add("decks.css");
        mainBox.getStylesheets().add("colors.css");
        mainBox.setId("card-pane");

        double ticketsCount = 0, cardsCount = 0;
        List<Card> faceUpCards = new ArrayList<>();

        if (state.getGameState() != null) {
            ticketsCount = state.getGameState().ticketsCount();
            cardsCount = state.getGameState().cardState().deckSize();
            faceUpCards = state.getGameState().cardState().faceUpCards();
        }

        Button ticketsButton = drawButton((ticketsCount / ChMap.tickets().size()) * 50);
        ticketsButton.setOnAction((e) -> {
            state.setState(GameState.initial(SortedBag.of(ChMap.tickets()), new Random()), state.getPlayerState().withAddedCard(Card.LOCOMOTIVE));
            System.out.println(state.getGameState().cardState().faceUpCards());
        });
        mainBox.getChildren().add(ticketsButton);


        final List<Card> copyOfFUC = List.copyOf(faceUpCards);

        for(int i = 0; i < Constants.FACE_UP_CARDS_COUNT; ++i) {
            StackPane p = cardPane(faceUpCards.get(i));
            mainBox.getChildren().add(p);
        }

        ObservableValue<List<Card>> obsPCards = new SimpleObjectProperty<>(state.getGameState().cardState().faceUpCards());
        obsPCards.addListener((o, oV, nV) -> {
            for(int i = 0; i < Constants.FACE_UP_CARDS_COUNT; ++i) {
                StackPane p = cardPane(copyOfFUC.get(i));
              //  mainBox.getChildren().set(mainBox.getChildren().indexOf(), p);
            }
            System.out.println("success in cardsView");
        });

        Button cardsButton = drawButton((cardsCount / Constants.TOTAL_CARDS_COUNT) * 50);
        mainBox.getChildren().add(cardsButton);

        return mainBox;
    }



    private static Button drawButton(double gaugeSize) {

        Button b = new Button();
        b.getStyleClass().add("gauged");

        Group bGroup = new Group();
        int h = 5;
        Rectangle br = new Rectangle(50, h);
        br.getStyleClass().add("background");
        Rectangle fr = new Rectangle(gaugeSize, h);
        fr.getStyleClass().add("foreground");

        bGroup.getChildren().add(br);
        bGroup.getChildren().add(fr);

        b.setGraphic(bGroup);
        b.getStyleClass().add("gauged");

        return b;
    }

    private static StackPane cardPane(Card card) {
        return cardPane(card, -1);
    }

    private static StackPane cardPane(Card card, int count) {

        StackPane pane = new StackPane();

        pane.getStyleClass().add("card");

        if(card.equals(Card.LOCOMOTIVE)) pane.getStyleClass().add("NEUTRAL");
        else pane.getStyleClass().add(card.toString());

        Rectangle outside = new Rectangle(60, 90);
        outside.getStyleClass().add("outside");
        pane.getChildren().add(outside);

        Rectangle filledInside = new Rectangle(40, 70);
        filledInside.getStyleClass().add("filled");
        filledInside.getStyleClass().add("inside");
        pane.getChildren().add(filledInside);

        Rectangle trainImage = new Rectangle(40, 70);
        trainImage.getStyleClass().add("train-image");
        pane.getChildren().add(trainImage);


        if(count != -1) {

            ReadOnlyIntegerProperty readOnlyCount = new SimpleIntegerProperty(count);
            pane.visibleProperty().bind(Bindings.greaterThan(readOnlyCount, 0));

            Text num = new Text(String.valueOf(readOnlyCount.getValue()));
            num.getStyleClass().add("count");
            num.textProperty().bind(Bindings.convert(readOnlyCount));
            num.visibleProperty().bind(Bindings.greaterThan(readOnlyCount, 0));
            pane.getChildren().add(num);
        }

        return pane;
    }




}