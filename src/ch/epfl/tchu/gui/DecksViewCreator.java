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

    public static Node createHandView(ObservableGameState state){

        HBox mainBox = new HBox();
        mainBox.getStylesheets().add("decks.css");
        mainBox.getStylesheets().add("colors.css");

        ListView<Ticket> tickets = new ListView<>();
        tickets.setId("tickets");
        state.tickets().addListener((o, oV, nV) -> tickets.getItems().addAll(SortedBag.of(nV).difference(SortedBag.of(tickets.getItems())).toList()));
        mainBox.getChildren().add(tickets);

        //Second HBox (bigger group)
        HBox secBox = new HBox();
        secBox.setId("hand-pane");

        for(Card c : Card.ALL) {

            StackPane p = cardPane(c, - 1);

            IntegerProperty ip = new SimpleIntegerProperty(state.cardAmount().get(Card.ALL.indexOf(c)));
            state.cardAmount().addListener((o, oV, nV) -> ip.setValue(nV.get(Card.ALL.indexOf(c))));

            p.visibleProperty().bind(Bindings.greaterThan(ip, 0));

            Text num = new Text(String.valueOf(ip.get()));
            num.getStyleClass().add("count");
            num.textProperty().bind(Bindings.convert(ip));
            num.visibleProperty().bind(Bindings.greaterThan(ip, 0));
            p.getChildren().add(num);

            secBox.getChildren().add(p);
        }

        mainBox.getChildren().add(secBox);

        return mainBox;
    }

    public static Node createCardsView(ObservableGameState state,
            ObjectProperty<ActionHandlers.DrawTicketsHandler> ticketsHandler,
            ObjectProperty<ActionHandlers.DrawCardHandler> cardsHandler) {

        VBox mainBox = new VBox();
        mainBox.getStylesheets().add("decks.css");
        mainBox.getStylesheets().add("colors.css");
        mainBox.setId("card-pane");

        Button ticketsButton = drawButton(state.ticketsGauge(), "Billets");
        ticketsButton.setOnMouseClicked(event -> ticketsHandler.get().onDrawTickets());
        mainBox.getChildren().add(ticketsButton);

        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; ++i) {

            StackPane p = cardPane(state.faceUpCard(i).get(), i);

            state.faceUpCard(i).addListener((o, oV, nV) -> {

                if(oV != null) {
                    if(oV.equals(Card.LOCOMOTIVE)) p.getStyleClass().remove("NEUTRAL");
                    else p.getStyleClass().remove(oV.color().toString());
                }

                if(nV.equals(Card.LOCOMOTIVE)) p.getStyleClass().add("NEUTRAL");
                else p.getStyleClass().add(nV.color().toString());

            });

            p.setOnMouseClicked(event -> handleCardPaneClick(event, cardsHandler));
            mainBox.getChildren().add(p);
        }

        Button cardsButton = drawButton(state.cardsGauge(), "Cartes");
        cardsButton.setOnMouseClicked((e -> cardsHandler.get().onDrawCard(Constants.DECK_SLOT)));

        mainBox.getChildren().add(cardsButton);

        return mainBox;
    }


    /**
     * Returns javafx Button object
     *
     * @param gaugeSize (ReadOnlyDoubleProperty)
     * @param label     (String)
     * @return Button
     */
    private static Button drawButton(ReadOnlyDoubleProperty gaugeSize, String label) {

        Button b = new Button(label);
        b.getStyleClass().add("gauged");

        Group bGroup = new Group();
        int h = 5;
        Rectangle br = new Rectangle(50, h);
        br.getStyleClass().add("background");
        Rectangle fr = new Rectangle(gaugeSize.get(), h);
        gaugeSize.addListener((o, oV, nV) -> fr.setWidth(nV.doubleValue()));

        fr.getStyleClass().add("foreground");

        bGroup.getChildren().add(br);
        bGroup.getChildren().add(fr);

        b.setGraphic(bGroup);
        b.getStyleClass().add("gauged");

        return b;
    }

    private static StackPane cardPane(Card card, int slot) {

        StackPane pane = new StackPane();
        if(slot != -1) pane.setId(String.valueOf(slot));
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


    // ================================================================================
    // Handlers
    // ================================================================================

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
