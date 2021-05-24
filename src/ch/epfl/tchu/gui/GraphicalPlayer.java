package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.PlayerState;
import ch.epfl.tchu.game.PublicGameState;
import ch.epfl.tchu.game.Ticket;
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

    ObservableGameState gameState;
    PlayerId playerId;
    Map<PlayerId, String> playerNames;
    ObservableList<Text> infos;

    ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHandler;
    ObjectProperty<ActionHandlers.DrawCardHandler> drawCardsHandler;
    ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandler;

    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        this.playerId = playerId;
        this.playerNames = playerNames;
        this.gameState = new ObservableGameState(playerId);
        this.infos = FXCollections.observableArrayList();
        this.drawTicketsHandler = new SimpleObjectProperty<>(() -> {});
        this.drawCardsHandler = new SimpleObjectProperty<>(s -> {});
        this.claimRouteHandler = new SimpleObjectProperty<>((r, cs) -> {});

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
        gameState.setState(newGameState, newPlayerState);
    }

    /**
     * @param message (String)
     */
    public void receiveInfo(String message) {
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
        this.drawTicketsHandler = new SimpleObjectProperty<>(ticketsHandler);
        this.drawCardsHandler = new SimpleObjectProperty<>(cardsHandler);
        this.claimRouteHandler = new SimpleObjectProperty<>(claimRouteHandler);
    }

    /**
     * @param options             (SortedBag<Ticket>)
     * @param chooseTicketHandler (ChooseTicketsHandler)
     */
    public void chooseTickets(SortedBag<Ticket> options, ActionHandlers.ChooseTicketsHandler chooseTicketHandler) {
        // Qui ouvre une fenêtre similaire à celle des figures 3 et 4, permettant au
        // joueur de faire son choix ; une fois celui-ci confirmé, le gestionnaire de
        // choix est appelé avec ce choix en argument
        System.out.println("@GraphicalPlayer (chooseTickets) - My options: " + options);
    }

    /**
     * @param cardHandler (DrawCardHandler)
     */
    public void drawCard(ActionHandlers.DrawCardHandler cardHandler) {
        // Qui autorise le joueur a choisir une carte wagon/locomotive, soit l'une des
        // cinq dont la face est visible, soit celle du sommet de la pioche ; une fois
        // que le joueur a cliqué sur l'une de ces cartes, le gestionnaire est appelé
        // avec le choix du joueur ; cette méthode est destinée à être appelée lorsque
        // le joueur a déjà tiré une première carte et doit maintenant tirer la seconde,
        System.out.println("@GraphicalPlayer (drawCard) - Handler: " + cardHandler);
    }

    /**
     * @param options      (List<SortedBag<Card>>)
     * @param cardsHandler (ChooseCardsHandler)
     */
    private static void chooseClaimCards(List<SortedBag<Card>> options,
            ActionHandlers.ChooseCardsHandler cardsHandler) {
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
        // Qui ouvre une fenêtre similaire à celle de la figure 6 permettant au joueur
        // de faire son choix ; une fois que celui-ci a été fait et confirmé, le
        // gestionnaire de choix est appelé avec le choix du joueur en argument.
    }

}