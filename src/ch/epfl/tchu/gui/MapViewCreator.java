package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.beans.property.ObjectProperty;

import java.util.List;

private final class MapViewCreator {

    public void createMapView(ObservableGameState observableGameState,
                              ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandlerObjectProperty, CardChooser cardChooser) {

    }

    @FunctionalInterface
    interface CardChooser   {
        void chooseCards (List<SortedBag<Card>> options,
                          ActionHandlers.ChooseCardsHandler handler) ;
    }
}
