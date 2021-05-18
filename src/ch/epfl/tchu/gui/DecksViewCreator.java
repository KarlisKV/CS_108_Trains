package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableNumberValue;
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


    private static GameState gs = GameState.initial(SortedBag.of(ChMap.tickets()), new Random());

    public static Node createHandView(ObservableGameState state){

        HBox mainBox = new HBox();
        mainBox.getStylesheets().add("decks.css");
        mainBox.getStylesheets().add("colors.css");

        List<Ticket> playerTickets = new ArrayList<>();

        if(state.playerState() != null) {
            playerTickets = state.playerState().get().tickets().toList();
        }

        ListView<Ticket> tickets = new ListView<>();
        tickets.getItems().addAll(playerTickets);
        tickets.setId("tickets");
        mainBox.getChildren().add(tickets);

        //Second HBox (bigger group)
        HBox secBox = new HBox();
        secBox.setId("hand-pane");

        final List<StackPane> panes = new ArrayList<>();
        final List<IntegerProperty> ipl = new ArrayList<>();

        for(Card c : Card.ALL) {

            StackPane p = cardPane(c);

            IntegerProperty ip = new SimpleIntegerProperty(state.playerState().getValue().cards().countOf(c));
            ipl.add(ip);
            ReadOnlyIntegerProperty count = new SimpleIntegerProperty(ip.get());

            p.visibleProperty().bind(Bindings.greaterThan(ip, 0));

            Text num = new Text(String.valueOf(count));
            num.getStyleClass().add("count");
            num.textProperty().bind(Bindings.convert(ip));
            num.visibleProperty().bind(Bindings.greaterThan(ip, 0));
            p.getChildren().add(num);

            panes.add(p);
        }

        secBox.getChildren().addAll(panes);

        state.playerState().addListener((o, oV, nV) -> {

            SortedBag<Card> newCards = nV.cards();

            for(int i = 0; i < Card.ALL.size(); ++i) {
                Card c = Card.ALL.get(i);
                ipl.get(i).setValue(newCards.countOf(c));
            }

            SortedBag<Ticket> newTickets = nV.tickets().difference(oV.tickets());
            if(!newTickets.isEmpty()) tickets.getItems().addAll(newTickets.toList());

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

        if (state.gameState() != null) {
            ticketsCount = state.gameState().get().ticketsCount();
            cardsCount = state.gameState().get().cardState().deckSize();
            faceUpCards = state.gameState().get().cardState().faceUpCards();
        }

        Button ticketsButton = drawButton((ticketsCount / ChMap.tickets().size()) * 50);
        ticketsButton.setOnAction((e) -> {

            int bound = new Random().nextInt(5);
            gs = gs.withDrawnFaceUpCard(bound);
            System.out.println(gs.topTickets(3));
            gs = gs.withChosenAdditionalTickets(gs.topTickets(3), gs.topTickets(3));

            state.setState(gs, gs.currentPlayerState());
        });

        mainBox.getChildren().add(ticketsButton);

        final List<StackPane> panes = new ArrayList<>();

        for(int i = 0; i < Constants.FACE_UP_CARDS_COUNT; ++i) {
            StackPane p = cardPane(faceUpCards.get(i));
            panes.add(p);
        }

        mainBox.getChildren().addAll(panes);

        state.gameState().addListener((o, oV, nV) -> {

            List<Integer> changedSlots = new ArrayList<>();

            for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; ++i) if(!oV.cardState().faceUpCard(i).equals(nV.cardState().faceUpCard(i))) changedSlots.add(i);

            for(Integer cs : changedSlots) {
                if(oV.cardState().faceUpCard(cs).equals(Card.LOCOMOTIVE)) panes.get(cs).getStyleClass().remove("NEUTRAL");
                else panes.get(cs).getStyleClass().remove(oV.cardState().faceUpCard(cs).color().toString());

                if(nV.cardState().faceUpCard(cs).equals(Card.LOCOMOTIVE)) panes.get(cs).getStyleClass().add("NEUTRAL");
                else panes.get(cs).getStyleClass().add(nV.cardState().faceUpCard(cs).color().toString());
            }



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

        return pane;
    }


}