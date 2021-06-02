package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Map;

/**
 * GraphicalPlayer class represents the graphical properties and helps to manage the game
 * @author Daniel Polka  (326800)
 * @author Karlis Velins (325180)
 */
public final class GraphicalPlayer {

    private final ObservableGameState gameState;
    private final ObservableList<Text> infos;
    private final Stage mainStage;

    private final ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTicketsHandler;
    private final ObjectProperty<ActionHandlers.DrawCardHandler> drawCardsHandler;
    private final ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRouteHandler;

    private final ListProperty<Route> highlightedRoutes;


    /**
     * Constructor of the GraphicalPlayer creates the main GUI window of tCHu
     * @param playerId (PlayerId) given player's Id
     * @param playerNames (Map<PlayerId, String>) map of player names
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {

        this.gameState = new ObservableGameState(playerId);
        this.infos = FXCollections.observableArrayList();
        this.drawTicketsHandler = new SimpleObjectProperty<>();
        this.drawCardsHandler = new SimpleObjectProperty<>();
        this.claimRouteHandler = new SimpleObjectProperty<>();
        this.mainStage = new Stage();
        this.highlightedRoutes = new SimpleListProperty<>(FXCollections.observableArrayList());

        //Here the scene graph is created
        Node mapView = MapViewCreator.createMapView(gameState, claimRouteHandler, (this::chooseClaimCards), highlightedRoutes);
        Node cardsView = DecksViewCreator.createCardsView(gameState, drawTicketsHandler, drawCardsHandler);
        Node handView = DecksViewCreator.createHandView(gameState, new DecksViewCreator.HighlightHandler() {
            @Override
            public void addHighlight(Route route) {
                highlightedRoutes.add(route);
            }

            @Override
            public void removeAllHighlights() {
                highlightedRoutes.clear();
            }
        });
        Node infoView = InfoViewCreator.createInfoView(playerId, playerNames, gameState, infos);

        BorderPane borderPane = new BorderPane(mapView, null, cardsView, handView, infoView);

        mainStage.setTitle("tCHu\u2014" + playerNames.get(playerId));
        mainStage.setScene(new Scene(borderPane));
        mainStage.show();

    }


    /**
     * Calling this method on the observable state of the player
     * @param newGameState   (PublicGameState) given public part of the game state
     * @param newPlayerState (PlayerState) given playerState
     */
    public void setState(PublicGameState newGameState, PlayerState newPlayerState) {
        assert Platform.isFxApplicationThread();
        gameState.setState(newGameState, newPlayerState);
    }

    /**
     * taking a message - of type String and adding it to the bottom of
     * the game progress information, which is presented in the lower part of the information view
     * @param message (String) given message
     */
    public void receiveInfo(String message) {

        assert Platform.isFxApplicationThread();
        if(infos.size() == 5) infos.remove(0);
        infos.add(new Text(message));
    }

    /**
     * takes as arguments three action handlers, one for each type of action the player can perform during a turn,
     * and which allows the player to perform one
     * @param ticketsHandler    (DrawTicketsHandler) ticketsHandler
     * @param cardsHandler      (DrawCardHandler) cardsHandler
     * @param claimRouteHandler (ClaimRouteHandler) routeHandler
     */
    public void startTurn(ActionHandlers.DrawTicketsHandler ticketsHandler, ActionHandlers.DrawCardHandler cardsHandler,
            ActionHandlers.ClaimRouteHandler claimRouteHandler) {

        assert Platform.isFxApplicationThread();

        if(gameState.canDrawTickets()) this.drawTicketsHandler.setValue(() -> {
            this.claimRouteHandler.setValue(null);
            this.drawCardsHandler.setValue(null);

            ticketsHandler.onDrawTickets();
            this.drawTicketsHandler.setValue(null);
        });

        if(gameState.canDrawCards()) this.drawCardsHandler.setValue((s) -> {
            this.drawTicketsHandler.setValue(null);
            this.claimRouteHandler.setValue(null);

            cardsHandler.onDrawCard(s);
            drawCard(cardsHandler);
        });

        this.claimRouteHandler.setValue((r, c) -> {
            this.drawTicketsHandler.setValue(null);
            this.drawCardsHandler.setValue(null);

            claimRouteHandler.onClaimRoute(r, c);
            this.claimRouteHandler.setValue(null);
        });

    }

    /**
     * Method for the scene graph when a player needs to choose his tickets
     * @param options             (SortedBag<Ticket>) a multiset containing five or three banknotes that the player can choose
     * @param chooseTicketHandler (ChooseTicketsHandler) ticketsHandler
     */
    public void chooseTickets(SortedBag<Ticket> options, ActionHandlers.ChooseTicketsHandler chooseTicketHandler) {

        assert Platform.isFxApplicationThread();

        Stage stage = new Stage(StageStyle.UTILITY);

        ListView<Ticket> listView = new ListView<>();
        listView.getItems().addAll(options.toList());
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Text text = new Text(String.format(StringsFr.CHOOSE_TICKETS, options.size() - Constants.DISCARDABLE_TICKETS_COUNT,
                StringsFr.plural(options.size() - Constants.DISCARDABLE_TICKETS_COUNT)));
        TextFlow textFlow = new TextFlow(text);

        Button button = new Button(StringsFr.CHOOSE);
        button.setOnAction(event -> {
            if(listView.getSelectionModel().getSelectedItems().size() < (options.size() - Constants.DISCARDABLE_TICKETS_COUNT)) return;
            chooseTicketHandler.onChooseTickets(SortedBag.of(listView.getSelectionModel().getSelectedItems()));
            stage.close();
        });
        selectionScene(textFlow, listView, StringsFr.TICKETS_CHOICE, stage, button, false);

    }

    /**
     * Method allowing the player to select the cards either from the deck or from the 5 faceUpCards and
     * then calling the manager when the first card is chosen
     * @param cardHandler (DrawCardHandler) cardHandler
     */
    public void drawCard(ActionHandlers.DrawCardHandler cardHandler) {

        assert Platform.isFxApplicationThread();

        if(gameState.canDrawCards()) this.drawCardsHandler.setValue((s) -> {
            cardHandler.onDrawCard(s);
            this.drawCardsHandler.setValue(null);
                });
    }

    /**
     * Allows the player to choose the cards to claim the route
     * @param options (List<SortedBag<Card>>) a list of multi sets of maps, which are the initial maps it can use to grab a route,
     * @param cardsHandler (ChooseCardsHandler) map selection manager
     */
    public void chooseClaimCards(List<SortedBag<Card>> options,
            ActionHandlers.ChooseCardsHandler cardsHandler) {

        assert Platform.isFxApplicationThread();

        ListView<SortedBag<Card>> listView = createListView(options);

        Text text = new Text(StringsFr.CHOOSE_CARDS);
        Stage stage = new Stage(StageStyle.UTILITY);
        TextFlow textFlow = new TextFlow(text);

        Button button = new Button(StringsFr.CHOOSE);
        button.setOnAction(event -> {
            cardsHandler.onChooseCards(listView.getSelectionModel().getSelectedItem());
            stage.close();
        });

        selectionScene(textFlow, listView, StringsFr.CARDS_CHOICE, stage, button, false);
    }


    /**
     * Used to choose the additional cards that need to be used to seize the tunnel
     * @param options (List<SortedBag<Card>>) a list of SortedBag of cards, which are the additional cards that it can use to seize a tunnel
     * @param cardsHandler (ChooseCardsHandler) manager of choice of cards
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> options, ActionHandlers.ChooseCardsHandler cardsHandler) {

        assert Platform.isFxApplicationThread();
        Stage stage = new Stage();

        stage.setOnCloseRequest((e) -> cardsHandler.onChooseCards(SortedBag.of()));
        ListView<SortedBag<Card>> listView = createListView(options);

        Button button = new Button(StringsFr.CHOOSE);
        button.setOnAction(event -> {
            cardsHandler.onChooseCards(listView.getSelectionModel().getSelectedItem());
            stage.close();
        });

        Text text = new Text(StringsFr.CHOOSE_ADDITIONAL_CARDS);

        TextFlow textFlow = new TextFlow(text);
        selectionScene(textFlow, listView, StringsFr.CARDS_CHOICE, stage, button, true);
    }


    /**
     *
     * @param route
     */
    public void highlightTrail(Route route) {

        assert  Platform.isFxApplicationThread();

        highlightedRoutes.add(route);
    }



    /**
     * Private method to reduce code duplication for listView cell factory creation for card choosing.
     * @param options (List<SortedBag<Card>>) given list of a SortedBag of cards
     * @return the created ListView used for the scene graph
     */
    private ListView <SortedBag<Card>> createListView(List<SortedBag<Card>> options) {
        ListView<SortedBag<Card>> listView = new ListView<>();
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new StringConverter<>() {
                    @Override
                    public String toString(SortedBag<Card> cards) {
                        return Info.cardNames(cards);
                    }

                    @Override
                    public SortedBag<Card> fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                }));
        listView.getItems().addAll(options);
        return listView;
    }


    /**
     * Private generic method that creates the small pop-up window for ticket or card selection
     * @param textFlow (TextFlow) textFlow of the scene graph
     * @param listView (ListView) listView of the scene graph
     * @param title (String) name of the window
     * @param stage (Stage) given stage of the scene graph
     * @param button (Button) button to confirm selection
     * @param closeable (boolean) dictates that the red cross can't be used to close the pop-up window
     */
    private <E> void selectionScene(TextFlow textFlow, ListView<E> listView, String title, Stage stage, Button button, boolean closeable) {

        stage.initOwner(mainStage);
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.WINDOW_MODAL);
        if(!closeable) stage.setOnCloseRequest(Event::consume);
        stage.setTitle(title);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(textFlow, listView, button);
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        stage.setScene(scene);
        stage.show();
    }
}