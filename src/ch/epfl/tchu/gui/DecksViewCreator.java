package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.Ticket;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public final class DecksViewCreator{

    public static void main(String[] args) {

        Node node = createHandView(new ObservableGameState(PlayerId.PLAYER_1));

    }



    private DecksViewCreator() {}

    public static Node createHandView(ObservableGameState state){

        HBox box = new HBox();
        box.getStylesheets().add("decks.css");
        box.getStylesheets().add("colors.css");

        ListView<Ticket> tickets = new ListView<>();
        tickets.getStyleClass().add("tickets");
        box.getChildren().add(tickets);

        //Second HBox (bigger group)
        HBox secBox = new HBox();
        secBox.getStyleClass().add("hand-pane");

        List<StackPane> cardPanes = cardStackPanes(state.getPlayerState().cards());

        for(StackPane pane : cardPanes) secBox.getChildren().add(pane);


        return null;
    }



    private static List<StackPane> cardStackPanes(SortedBag<Card> cardsToShow){

        List<StackPane> cardPanes = new ArrayList<>();

        for(Card card : Card.ALL) {

            if(cardsToShow.countOf(card) == 0) continue;

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

            Text counter = new Text(String.valueOf(cardsToShow.countOf(card)));
            counter.getStyleClass().add("count");

            cardPanes.add(pane);
        }

        return cardPanes;
    }




    public static Node createCardsView(ObservableGameState state, ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketsHandler, ObjectProperty<ActionHandlers.DrawCardHandler> cardsHandler){


        return null;
    }




}
