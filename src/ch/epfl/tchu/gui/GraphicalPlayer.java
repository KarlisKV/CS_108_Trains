package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import javax.print.DocFlavor;
import java.util.List;
import java.util.Map;

/**
 * GraphicalPlayer class represents the graphical properties and helps to manage the game
 * @author Daniel Polka  (326800)
 * @author Karlis Velins (325180)
 */
public class GraphicalPlayer {

    private final ObservableGameState gameState;
    private final PlayerId playerId;
    private final Map<PlayerId, String> playerNames;
    private final ObservableList<Text> infos;
    private final Stage mainStage;
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
        this.mainStage = new Stage();
        /**
         * Here is the scene graph part
         */
        Node infoView = InfoViewCreator.createInfoView(playerId, playerNames, gameState, infos);
        Node mapView = MapViewCreator.createMapView(gameState, claimRouteHandler, GraphicalPlayer::chooseClaimCards);
        Node cardsView = DecksViewCreator.createCardsView(gameState, drawTicketsHandler, drawCardsHandler);
        Node handView = DecksViewCreator.createHandView(gameState);

        BorderPane borderPane = new BorderPane(mapView, null, cardsView, handView, infoView);


        mainStage.setTitle("tCHu\u2014" + playerNames.get(playerId));
        mainStage.setScene(new Scene(borderPane));
        mainStage.show();

    }

    /**
     * doing nothing but calling this method on the observable state of the player
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
     * Scene graph for the ticket and card pop-up window
     * @return
     */
    private <E> Stage selectionScene(TextFlow textFlow, Button button, ListView<E> listView, String title) {

        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);

        stage.setTitle(title);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(textFlow, listView, button);
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        scene.getStylesheets().add("chooser.css");

        return stage;
    }
    /**
     * Method for the scene graph when a player needs to choose his tickets
     * @param options             (SortedBag<Ticket>)
     * @param chooseTicketHandler (ChooseTicketsHandler)
     */
    public void chooseTickets(SortedBag<Ticket> options, ActionHandlers.ChooseTicketsHandler chooseTicketHandler) {

        assert Platform.isFxApplicationThread();


        // todo how to do the listView? Instructions say I Need to add a 'cellFactory' but i dont understand,
        // todo is it just for the List<SortedBag<Card>> or do I need it here? Or can I just cast the thing?
        ListView<Ticket> listView = new ListView<>((ObservableList<Ticket>) options.toList());
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button button = new Button();
        // TODO: 5/25/2021 button disableProperty no clue need to use actionHnadler

        Text text = new Text(String.format(StringsFr.CHOOSE_TICKETS, options.size() - Constants.DISCARDABLE_TICKETS_COUNT,
                StringsFr.plural(options.size() - Constants.DISCARDABLE_TICKETS_COUNT)));

        TextFlow textFlow = new TextFlow(text);

        selectionScene(textFlow, button, listView, StringsFr.CHOOSE_TICKETS);

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

        /* TODO: This method should modify the ActionHandler cardHandler it is given as an attribute
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
    public void chooseClaimCards(List<SortedBag<Card>> options,
            ActionHandlers.ChooseCardsHandler cardsHandler) {

        assert Platform.isFxApplicationThread();

        // todo how to do the listView? Instructions say I Need to add a 'cellFactory'
        ListView<SortedBag<Card>> listView = new ListView<>((ObservableList<SortedBag<Card>>) options);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button button = new Button();
        // TODO: 5/25/2021 button disableProperty no clue need to use actionHnadler

       Text text = new Text();

        TextFlow textFlow = new TextFlow(text);

        selectionScene(textFlow, button, listView, StringsFr.CHOOSE_CARDS);
    }

    /**
     * @param options      (List<SortedBag<Card>>)
     * @param cardsHandler (ChooseCardsHandler)
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> options, ActionHandlers.ChooseCardsHandler cardsHandler) {


        // todo how to do the listView? Instructions say I Need to add a 'cellFactory'
        ListView<SortedBag<Card>> listView = new ListView<>((ObservableList<SortedBag<Card>>) options);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button button = new Button();
        // TODO: 5/25/2021 button disableProperty no clue need to use actionHnadler

        Text text = new Text();

        TextFlow textFlow = new TextFlow(text);

        selectionScene(textFlow, button, listView, StringsFr.CHOOSE_ADDITIONAL_CARDS);
    }

}