package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.*;
import javafx.scene.layout.Pane;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import javafx.scene.image.ImageView ;

import java.util.ArrayList;
import java.util.List;

/**
 * MapViewCreator class representing the middle part of the map
 * @author Karlis Velins (325180)
 * @author Daniel Polka  (326800)
 */
final class MapViewCreator {


    /**
     * MapViewCreator not to be instantiated
     */
    private MapViewCreator(){}

    /**
     * MapView creation method
     * @param state (ObservableGameState) given ObservableGameState
     * @param handlerObjectProperty (ObjectProperty<ClaimRouteHandler>) given handlerObjectProperty
     * @param cardChooser (CardChooser) functional interface argument
     * @return the created MapView
     */
    public static Node createMapView(ObservableGameState state, ObjectProperty<ClaimRouteHandler> handlerObjectProperty,
                                     CardChooser cardChooser, ListProperty<Route> highlightedRoutes) {

        Pane pane = new Pane();
        pane.getStylesheets().add("map.css");
        pane.getStylesheets().add("colors.css");
        ImageView imageView = new ImageView("map.png");
        pane.getChildren().add(imageView);

        //For each of the routes in the network
        for (Route route : ChMap.routes()) {
            Group routeGroup = getRouteGroup(route);

            BooleanProperty actionable = new SimpleBooleanProperty(handlerObjectProperty.isNull().get() || (!state.canClaimRoute().get(route)));
            state.canClaimRoute().addListener((o, oV, nV) -> actionable.setValue(!nV.get(route) || handlerObjectProperty.isNull().get()));
            handlerObjectProperty.addListener((o, oV, nV) -> actionable.setValue(!state.canClaimRoute().get(route) || handlerObjectProperty.isNull().get()));
            routeGroup.disableProperty().bind(actionable);

            state.routesMapProperty().addListener((o, oV, nV) -> {
                if(oV.get(route) == null && nV.get(route) != null) routeGroup.getStyleClass().add(nV.get(route).toString());
            });

            // Add action handler to route group before adding it to the pane
            routeGroup.setOnMouseClicked(event -> {

                List<SortedBag<Card>> possibleClaimCards = state.possibleClaimCards(route);

                if (possibleClaimCards.size() == 1)
                    handlerObjectProperty.get().onClaimRoute(route, possibleClaimCards.get(0));
                else if (!possibleClaimCards.isEmpty())
                    cardChooser.chooseCards(possibleClaimCards, ((chosenCards) -> handlerObjectProperty.get().onClaimRoute(route, chosenCards)));

            });

            highlightedRoutes.addListener((o, oV, nV) -> {

                if(nV.contains(route))
                    routeGroup.setEffect(new Glow(0.5));

            });

            pane.getChildren().add(routeGroup);
        }

        return pane;
    }

    /**
     * Private method to remove code duplication representing the WagonRectangle
     * @return the created rectangle
     */
    private static Rectangle createWagonRectangle() {
        Rectangle r = new Rectangle();
        r.setWidth(36);
        r.setHeight(12);
        r.getStyleClass().add("filled");
        return r;
    }
    /**
     * Private method to remove code duplication representing the RouteRectangle
     * @return the created rectangle
     */
    private static Rectangle createRouteRectangle() {
        Rectangle r = new Rectangle();
        r.setWidth(36);
        r.setHeight(12);
        r.getStyleClass().add("filled");
        r.getStyleClass().add("track");
        return r;
    }

    /**
     * Private class to create a Circle
     * @param setCenterX (int) x position of the circle
     * @return the created circle
     */
    private static Circle createCircle(int setCenterX) {
        Circle c =  new Circle();
        c.setCenterX(setCenterX);
        c.setCenterY(6);
        c.setRadius(3);
        c.getStyleClass().add("filled");
        return c;
    }

    /**
     * Private method to create the WagonGroup part
     * @return WagonGroup
     */
    private static Group getWagonGroup() {
        Group group = new Group();
        group.getStyleClass().add("car");
        group.getChildren().add(createWagonRectangle());
        group.getChildren().add(createCircle(12));
        group.getChildren().add(createCircle(24));
        return group;
    }

    /**
     * Private method to get the list of groups
     * @param route (Route) given route
     * @return the List of Groups
     */
    private static List<Group> getCaseGroups(Route route) {

        List<Group> groups = new ArrayList<>();

        for (int i = 1; i <= route.length(); i++) {
            Group group = new Group();
            group.setId(route.id() + "_" + i);
            group.getChildren().add(createRouteRectangle());
            group.getChildren().add(getWagonGroup());
            groups.add(group);
        }

        return groups;
    }

    /**
     * Private method to create the route group
     * @param route (Route) given Route
     * @return the Route Group
     */
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

    /**
     * The method chooseCards of this interface is intended
     * to be called when the player must choose the cards he wishes to use to seize a route.
     */
    @FunctionalInterface
    public
    interface CardChooser   {
        void chooseCards (List<SortedBag<Card>> options,
                          ChooseCardsHandler handler);
    }
}
