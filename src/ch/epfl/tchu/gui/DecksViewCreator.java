package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class DecksViewCreator{

    private DecksViewCreator() {}


    public static Node createHandView(ObservableGameState state){

        HBox mainBox = new HBox();
        mainBox.getStylesheets().add("decks.css");
        mainBox.getStylesheets().add("colors.css");

        List<Ticket> playerTickets = new ArrayList<>();
        List<Card> cardsToShow = new ArrayList<>();

        if(state.getPlayerState() != null) {
            playerTickets = state.getPlayerState().tickets().toList();
            cardsToShow = state.getPlayerState().cards().toList();
        }

        ListView<Ticket> tickets = new ListView<>();
        tickets.getItems().addAll(playerTickets);
        tickets.setId("tickets");
        mainBox.getChildren().add(tickets);

        //Second HBox (bigger group)
        HBox secBox = new HBox();
        secBox.setId("hand-pane");

        List<StackPane> cardPanes = cardStackPanes(cardsToShow, true);

        for(StackPane pane : cardPanes) secBox.getChildren().add(pane);

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
        mainBox.getChildren().add(ticketsButton);

        List<StackPane> stackPanes = cardStackPanes(faceUpCards, false);
        for(StackPane pane : stackPanes) mainBox.getChildren().add(pane);

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


    private static List<StackPane> cardStackPanes(List<Card> cardsToShow, boolean stackSameCards){

        List<StackPane> cardPanes = new ArrayList<>();

        List<Card> copy = List.copyOf(cardsToShow);
        HashSet<Card> set = new HashSet<>(copy);
        if(stackSameCards) copy = SortedBag.of(new ArrayList<>(set)).toList();

        System.out.printf("cardsToShow: %s | copy: %s | stackSameCards: %s", cardsToShow, copy, stackSameCards);
        System.out.println();

        for(Card card : copy) {

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

            if(stackSameCards) {
                Text counter = new Text(String.valueOf(SortedBag.of(cardsToShow).countOf(card)));
                counter.getStyleClass().add("count");
                pane.getChildren().add(counter);
            }

            cardPanes.add(pane);
        }

        return cardPanes;
    }




}