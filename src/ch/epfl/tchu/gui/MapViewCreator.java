package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javax.swing.text.html.ImageView;
import java.util.List;

private final class MapViewCreator {






    private final ImageView imageView = new ImageView();
    public void createMapView(ObservableGameState observableGameState,
                                     ObjectProperty<ClaimRouteHandler> claimRouteHandlerObjectProperty,
                                     CardChooser cardChooser) {

        Pane pane = new Pane();
        Scene scene = new Scene(pane);
        scene.getStylesheets().add("map.css");



    }

    @FunctionalInterface
    interface CardChooser   {
        void chooseCards (List<SortedBag<Card>> options,
                          ChooseCardsHandler handler);
    }
}
