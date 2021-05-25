package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Ticket;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class GraphicalPlayer {

    private final ObservableGameState gameState;
    private final PlayerId playerId;
    private final Map<PlayerId, String> playerNames;
    private final ObservableList<Text> infos;

    private final ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHandler;
    private final ObjectProperty<ActionHandlers.DrawCardHandler> drawCardsHandler;
    private final ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandler;

    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        this.playerId = playerId;
        this.playerNames = playerNames;
        this.gameState = new ObservableGameState(playerId);
        this.infos = FXCollections.observableArrayList();
        this.drawTicketsHandler = new SimpleObjectProperty<>();
        this.drawCardsHandler = new SimpleObjectProperty<>();
        this.claimRouteHandler = new SimpleObjectProperty<>();

        /**
         * Here is the scene graph part
         */
        Node infoView = InfoViewCreator.createInfoView(playerId, playerNames, gameState, infos);
        Node mapView = MapViewCreator.createMapView(gameState, claimRouteHandler, GraphicalPlayer::chooseClaimCards);
        Node cardsView = DecksViewCreator.createCardsView(gameState, drawTicketsHandler, drawCardsHandler);
        Node handView = DecksViewCreator.createHandView(gameState);

        BorderPane borderPane = new BorderPane(mapView, null, cardsView, handView, infoView);

        Stage stage = new Stage();
        stage.setTitle("tCHu\u2014" + playerNames.get(playerId));
        stage.setScene(new Scene(borderPane));
        stage.show();

    }

    /**
     * doing nothing but calling this method on the observable state of the player
     * 
     * @param newGameState   (PublicGameState) given public part of the game state
     * @param newPlayerState (PlayerState) given playerState
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {

        assert Platform.isFxApplicationThread();

        gameState.setState(newGameState, newPlayerState);
    }

    /**
     * @param message (String)
     */
    public void receiveInfo(String message) {

        assert Platform.isFxApplicationThread();

        infos.add(new Text(message));
        System.out.println("@GraphicalPlayer (receiveInfo) - Info: " + message);
    }

    /**
     * @param ticketsHandler    (DrawTicketsHandler)
     * @param cardsHandler      (DrawCardHandler)
     * @param claimRouteHandler (ClaimRouteHandler)
     */
    public void startTurn(ActionHandlers.DrawTicketsHandler ticketsHandler, ActionHandlers.DrawCardHandler cardsHandler,
            ActionHandlers.ClaimRouteHandler claimRouteHandler) {

        assert Platform.isFxApplicationThread();

        if(gameState.canDrawTickets()) this.drawTicketsHandler.setValue(() -> {
            ticketsHandler.onDrawTickets();
            this.drawTicketsHandler.setValue(null);
        });

        if(gameState.canDrawCards()) this.drawCardsHandler.setValue((s) -> {
            // TODO: 25.05.21
        });

        this.claimRouteHandler.setValue((r, c) -> {
            claimRouteHandler.onClaimRoute(r, c);
            this.claimRouteHandler.setValue(null);
        });
    }

    /**
     * @param options             (SortedBag<Ticket>)
     * @param chooseTicketHandler (ChooseTicketsHandler)
     */
    public void chooseTickets(SortedBag<Ticket> options, ActionHandlers.ChooseTicketsHandler chooseTicketHandler) {

        assert Platform.isFxApplicationThread();

        // Qui ouvre une fenêtre similaire à celle des figures 3 et 4, permettant au
        // joueur de faire son choix ; une fois celui-ci confirmé, le gestionnaire de
        // choix est appelé avec ce choix en argument
        System.out.println("@GraphicalPlayer (chooseTickets) - My options: " + options);
    }

    /**
     * @param cardHandler (DrawCardHandler)
     */
    public void drawCard(ActionHandlers.DrawCardHandler cardHandler) {

        assert Platform.isFxApplicationThread();

        // Qui autorise le joueur a choisir une carte wagon/locomotive, soit l'une des
        // cinq dont la face est visible, soit celle du sommet de la pioche ; une fois
        // que le joueur a cliqué sur l'une de ces cartes, le gestionnaire est appelé
        // avec le choix du joueur ; cette méthode est destinée à être appelée lorsque
        // le joueur a déjà tiré une première carte et doit maintenant tirer la seconde,

        /* TODO: This method modifies the ActionHandler cardHandler it is given as an attribute
         * in a similar way to startTurn(...): cardHandler has to be not null when drawing cards is possible
         * and then set itself to null when drawing cards isn't possible anymore
         * (i.e. cardHandler isn't null if it's your turn and you chose to draw cards and you've drawn either 0 or 1 card for now,
         * but it's null when it's not your turn or when you've already drawn 2 cards or when you chose a different turn kind than drawing cards)
         */

        System.out.println("@GraphicalPlayer (drawCard) - Handler: " + cardHandler);
    }

    /**
     * @param options      (List<SortedBag<Card>>)
     * @param cardsHandler (ChooseCardsHandler)
     */
    private static void chooseClaimCards(List<SortedBag<Card>> options,
            ActionHandlers.ChooseCardsHandler cardsHandler) {

        assert Platform.isFxApplicationThread();

        // qui ouvre une fenêtre similaire à celle de la figure 5 permettant au joueur
        // de faire son choix ; une fois que celui-ci a été fait et confirmé, le
        // gestionnaire de choix est appelé avec le choix du joueur en argument
        System.out.println("@GraphicalPlayer (chooseClaimCards) - List of bags of Cards: " + options);
    }

    /**
     * @param options      (List<SortedBag<Card>>)
     * @param cardsHandler (ChooseCardsHandler)
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> options, ActionHandlers.ChooseCardsHandler cardsHandler) {

        assert Platform.isFxApplicationThread();

        // Qui ouvre une fenêtre similaire à celle de la figure 6 permettant au joueur
        // de faire son choix ; une fois que celui-ci a été fait et confirmé, le
        // gestionnaire de choix est appelé avec le choix du joueur en argument.
    }

}