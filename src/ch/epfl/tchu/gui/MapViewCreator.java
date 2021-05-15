package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import javafx.scene.image.ImageView ;

import java.util.ArrayList;
import java.util.List;

public final class MapViewCreator {

    private MapViewCreator(){}


    public static Node createMapView(ObservableGameState observableGameState, ObjectProperty<ClaimRouteHandler> handlerObjectProperty, CardChooser cardChooser) {

        Pane pane = new Pane();
        pane.getStylesheets().add("map.css");
        pane.getStylesheets().add("colors.css");
        ImageView imageView = new ImageView("map.png");
        pane.getChildren().add(imageView);
        for(Route r : ChMap.routes()) pane.getChildren().add(getRouteGroup(r));

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

    private static List<Group> getCaseGroups(Route route) {

        List<Group> groups = new ArrayList<>();

        for (int i = 1; i <= route.length(); i++) {
            Group group = new Group();
            group.setId(route.id() + "_" + i);
            group.getChildren().add(createVoieRectangle());
            group.getChildren().add(getWagonGroup());
            groups.add(group);
        }

        return groups;
    }

    private static Group getRouteGroup(Route route) {

        Group group = new Group();
        group.setId(route.id());
        group.getStyleClass().add("route");
        if(route.level().equals(Route.Level.UNDERGROUND)) group.getStyleClass().add("UNDERGROUND");

        if(route.color() == null) group.getStyleClass().add("NEUTRAL");
        else group.getStyleClass().add(route.color().toString());

        List<Group> groups = getCaseGroups(route);
        for (Group g : groups) group.getChildren().add(g);

        return group;
    }


    @FunctionalInterface
    public
    interface CardChooser   {
        void chooseCards (List<SortedBag<Card>> options,
                          ChooseCardsHandler handler);
    }
}
