package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import ch.epfl.tchu.gui.ActionHandlers.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import javafx.scene.image.ImageView ;

import java.util.ArrayList;
import java.util.List;

final class MapViewCreator {

    private MapViewCreator(){}


    public static Node createMapView(ObservableGameState state, ObjectProperty<ClaimRouteHandler> handlerObjectProperty, CardChooser cardChooser) {

        Pane pane = new Pane();
        pane.getStylesheets().add("map.css");
        pane.getStylesheets().add("colors.css");
        ImageView imageView = new ImageView("map.png");
        pane.getChildren().add(imageView);
        for (Route route : ChMap.routes()) {
            Group routeGroup = getRouteGroup(route);

            BooleanProperty actionnable = new SimpleBooleanProperty(handlerObjectProperty.isNull().get() || (!state.canClaimRoute().get(route)));
            state.canClaimRoute().addListener((o, oV, nV) -> actionnable.setValue(!nV.get(route) || handlerObjectProperty.isNull().get()));
            handlerObjectProperty.addListener((o, oV, nV) -> actionnable.setValue(!state.canClaimRoute().get(route) || handlerObjectProperty.isNull().get()));
            routeGroup.disableProperty().bind(actionnable);

            state.routesMapProperty().addListener((o, oV, nV) -> {
                if(oV.get(route) == null && nV.get(route) != null) routeGroup.getStyleClass().add(nV.get(route).toString());
            });

            // Add action handler to route group before adding it to the pane
            routeGroup.setOnMouseClicked(event -> handleRouteClick(route, state, handlerObjectProperty, cardChooser));
            pane.getChildren().add(routeGroup);
        }

        return pane;
    }

    /** 
     * Route onClick handler (3.4.3 Event handler)
     * @param route (Route)
     * @param state (ObservableGameState)
     * @param claimRouteH (ObjectProperty<ClaimRouteHandler>)
     * @param cardChooser (CardChooser)
     */
    private static void handleRouteClick(Route route, ObservableGameState state,
                                         ObjectProperty<ClaimRouteHandler> claimRouteH, CardChooser cardChooser) {

        List<SortedBag<Card>> possibleClaimCards = state.possibleClaimCards(route);

        if (possibleClaimCards.size() == 1) {
            claimRouteH.get().onClaimRoute(route, possibleClaimCards.get(0));

        } else if (!possibleClaimCards.isEmpty()) {
            ChooseCardsHandler chooseCardsH = chosenCards -> claimRouteH.get().onClaimRoute(route, chosenCards);
            cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
        }
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
