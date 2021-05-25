package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
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

final class DecksViewCreator{


    private DecksViewCreator() {}

    /**
     * Main method to create the handView for the player
     * @param state (ObservableGameState) given state
     * @return the created HBox (Node)
     */
    public static Node createHandView(ObservableGameState state){

        HBox mainBox = new HBox();
        mainBox.getStylesheets().add("decks.css");
        mainBox.getStylesheets().add("colors.css");

        ListView<Ticket> tickets = new ListView<>();
        tickets.setId("tickets");
        state.tickets().addListener((o, oV, nV) -> tickets.getItems().addAll(SortedBag.of(nV).difference(SortedBag.of(tickets.getItems())).toList()));
        mainBox.getChildren().add(tickets);

        //Second HBox (bigger group)
        HBox secondBox = new HBox();
        secondBox.setId("hand-pane");

        for(Card c : Card.ALL) {

            StackPane pane = cardPane(c, Constants.DECK_SLOT);

            IntegerProperty integerProperty = new SimpleIntegerProperty(state.cardAmount().get(Card.ALL.indexOf(c)));
            state.cardAmount().addListener((o, oV, nV) -> integerProperty.setValue(nV.get(Card.ALL.indexOf(c))));

            pane.visibleProperty().bind(Bindings.greaterThan(integerProperty, 0));

            Text text = new Text(String.valueOf(integerProperty.get()));
            text.getStyleClass().add("count");
            text.textProperty().bind(Bindings.convert(integerProperty));
            text.visibleProperty().bind(Bindings.greaterThan(integerProperty, 0));
            pane.getChildren().add(text);

            secondBox.getChildren().add(pane);
        }

        mainBox.getChildren().add(secondBox);

        return mainBox;
    }

    /**
     * Main method to create the cardsView for the deck part
     * @param state (ObservableGameState) given state
     * @param ticketsHandler (ObjectProperty<ActionHandlers.DrawTicketsHandler>) given ticketsHandler
     * @param cardsHandler (ObjectProperty<ActionHandlers.DrawTicketsHandler>) given cardsHandler
     * @return the created VBox (Node)
     */
    public static Node createCardsView(ObservableGameState state,
            ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketsHandler,
            ObjectProperty<ActionHandlers.DrawCardHandler> cardsHandler) {

        VBox mainBox = new VBox();
        mainBox.getStylesheets().add("decks.css");
        mainBox.getStylesheets().add("colors.css");
        mainBox.setId("card-pane");

        Button ticketsButton = drawButton(state.ticketsGauge(), StringsFr.TICKETS);

        ticketsButton.setOnMouseClicked(event -> ticketsHandler.get().onDrawTickets());
        mainBox.getChildren().add(ticketsButton);

        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; ++i) {

            StackPane pane = cardPane(state.faceUpCard(i).get(), i);

            state.faceUpCard(i).addListener((o, oV, nV) -> {

                if(oV != null) {
                    if(oV.equals(Card.LOCOMOTIVE)) pane.getStyleClass().remove("NEUTRAL");
                    else pane.getStyleClass().remove(oV.color().toString());
                }

                if(nV.equals(Card.LOCOMOTIVE)) pane.getStyleClass().add("NEUTRAL");
                else pane.getStyleClass().add(nV.color().toString());

            });

            pane.setOnMouseClicked(event -> handleCardPaneClick(event, cardsHandler));
            mainBox.getChildren().add(pane);
        }

        Button cardsButton = drawButton(state.cardsGauge(), StringsFr.CARDS);
        cardsButton.setOnMouseClicked((e -> cardsHandler.get().onDrawCard(Constants.DECK_SLOT)));

        mainBox.getChildren().add(cardsButton);

        return mainBox;
    }


    /**
     * Private method that returns the Button part of the scene graph
     * @param gaugeSize (ReadOnlyDoubleProperty) size of the gauge
     * @param label (String) name on the button
     * @return the Button
     */
    private static Button drawButton(ReadOnlyDoubleProperty gaugeSize, String label) {

        Button button = new Button(label);
        button.getStyleClass().add("gauged");

        Group bGroup = new Group();
        int height = 5;
        Rectangle br = new Rectangle(50, height);
        br.getStyleClass().add("background");
        Rectangle fr = new Rectangle(gaugeSize.get(), height);
        gaugeSize.addListener((o, oV, nV) -> fr.setWidth(nV.doubleValue()));

        fr.getStyleClass().add("foreground");

        bGroup.getChildren().add(br);
        bGroup.getChildren().add(fr);

        button.setGraphic(bGroup);
        button.getStyleClass().add("gauged");

        return button;
    }

    /**
     * Private method for the cardPane part of the scene graph
     * @param card (Card) given card
     * @param slot (int) given slot number
     * @return the created StackPane
     */
    private static StackPane cardPane(Card card, int slot) {

        StackPane pane = new StackPane();
        if(slot != Constants.DECK_SLOT) pane.setId(String.valueOf(slot));
            // TODO: 5/26/2021   please verify where does handcard_ come from? Can't find it in the instructions
        else pane.setId("handcard_" + card);

        pane.getStyleClass().add("card");

        if(card != null) {
            if(card.equals(Card.LOCOMOTIVE)) pane.getStyleClass().add("NEUTRAL");
            else pane.getStyleClass().add(card.toString());
        }

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

    /**
     * Handler for javafx CardPane onMouseClick event
     * Picks 1 card from the face-up cards
     *
     * @param event        (EventObject)
     * @param cardsHandler (ObjectProperty<ActionHandlers.DrawCardHandler>)
     */
    private static void handleCardPaneClick(EventObject event,
            ObjectProperty<ActionHandlers.DrawCardHandler> cardsHandler) {
        final Node source = (Node) event.getSource();
        String id = source.getId();
        cardsHandler.get().onDrawCard(Integer.parseInt(id));
    }

}