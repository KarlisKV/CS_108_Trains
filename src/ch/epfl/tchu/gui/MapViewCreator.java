package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javafx.scene.image.ImageView ;
import java.util.List;

public final class MapViewCreator {

    private MapViewCreator(){}

    public static void main(String[] args) {

        Node node = createMapView(new ObservableGameState(PlayerId.PLAYER_1), new SimpleObjectProperty<>(), new CardChooser() {
            @Override
            public void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler) {

            }
        });

    }


    public static Node createMapView(ObservableGameState observableGameState, ObjectProperty<ClaimRouteHandler> handlerObjectProperty, CardChooser cardChooser) {

        Pane pane = new Pane();
        pane.getStylesheets().add("map.css");
        pane.getStylesheets().add("colors.css");
        ImageView imageView = new ImageView("map.png");
        pane.getChildren().add(imageView);
        for(Route r : ChMap.routes()) pane.getChildren().add(getRouteGroup(r.id()));

        return pane;
    }

    private static Rectangle createWagonRectangle() {
        Rectangle r = new Rectangle();
        r.setWidth(36);
        r.setHeight(12);
        r.getStyleClass().add("filled");
        return r;
    }

    private static Rectangle createVoieRectangle() {
        Rectangle r = new Rectangle();
        r.setWidth(36);
        r.setHeight(12);
        r.getStyleClass().add("filled");
        r.getStyleClass().add("track");
        return r;
    }

    private static Circle createCircle(int setCenterX) {
        Circle c =  new Circle();
        c.setCenterX(setCenterX);
        c.setCenterY(6);
        c.setRadius(3);
        c.getStyleClass().add("filled");
        return c;
    }

    private static Group getWagonGroup() {
        Group group = new Group();
        group.getStyleClass().add("car");
        group.getChildren().add(createWagonRectangle());
        group.getChildren().add(createCircle(12));
        group.getChildren().add(createCircle(24));
        return group;
    }

    private static Group getCaseGroup(String id) {
        Group group = new Group();
        group.setId(id);
        group.getChildren().add(createVoieRectangle());
        group.getChildren().add(getWagonGroup());
        return group;
    }

    private static Group getRouteGroup(String id) {
        Group group = new Group();
        group.setId(id);
        group.getStyleClass().add("route");
        group.getStyleClass().add("UNDERGROUND");
        group.getStyleClass().add("NEUTRAL");
        group.getChildren().add(getCaseGroup(id));
        return group;
    }


    @FunctionalInterface
    public
    interface CardChooser   {
        void chooseCards (List<SortedBag<Card>> options,
                          ChooseCardsHandler handler);
    }
}
