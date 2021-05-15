package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import com.sun.javafx.collections.ImmutableObservableList;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

public final class DecksViewCreator{

    private DecksViewCreator() {}

    public static Node createHandView(ObservableGameState state){

        HBox mainBox = new HBox();
        mainBox.getStylesheets().add("decks.css");
        mainBox.getStylesheets().add("colors.css");

        ObservableList<Ticket> obsTickets = new ObservableListWrapper<>(state.getPlayerState().tickets().toList());
        ListView<Ticket> tickets = new ListView<>(obsTickets);
        tickets.getStyleClass().add("tickets");
        mainBox.getChildren().add(tickets);

        //Second HBox (bigger group)
        HBox secBox = new HBox();
        secBox.getStyleClass().add("hand-pane");

        List<StackPane> cardPanes = cardStackPanes(state.getPlayerState().cards().toList(), true);

        for(StackPane pane : cardPanes) secBox.getChildren().add(pane);

        mainBox.getChildren().add(secBox);

        return mainBox;
    }


    public static Node createCardsView(ObservableGameState state, ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketsHandler, ObjectProperty<ActionHandlers.DrawCardHandler> cardsHandler){

        VBox mainBox = new VBox();
        mainBox.getStylesheets().add("decks.css");
        mainBox.getStylesheets().add("colors.css");
        mainBox.getStyleClass().add("card-pane");

        Button ticketsButton = drawButton(state.getGameState().ticketsCount() / ChMap.tickets().size() * 50);
        mainBox.getChildren().add(ticketsButton);
        Button cardsButton = drawButton(state.getGameState().cardState().deckSize() / Constants.TOTAL_CARDS_COUNT * 50);
        mainBox.getChildren().add(cardsButton);

        List<StackPane> stackPanes = cardStackPanes(state.getGameState().cardState().faceUpCards(), false);
        for(StackPane pane : stackPanes) mainBox.getChildren().add(pane);

        return mainBox;
    }




    private static Button drawButton(int gaugeSize) {

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

        for(Card card : copy) {

            StackPane pane = new StackPane();

            pane.getStyleClass().add("card");

            if(card.equals(Card.LOCOMOTIVE)) pane.getStyleClass().add("NEUTRAL");
            else pane.getStyleClass().add(card.toString());

            Rectangle outside = new Rectangle(60, 90);
            outside.getStyleClass().add("outside");
            Rectangle filledInside = new Rectangle(40, 70);
            filledInside.getStyleClass().add("filled");
            filledInside.getStyleClass().add("inside");
            Rectangle trainImage = new Rectangle(40, 70);
            trainImage.getStyleClass().add("train-image");

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