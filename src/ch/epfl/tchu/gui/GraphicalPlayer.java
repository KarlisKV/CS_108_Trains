package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
        Node mapView = MapViewCreator.createMapView(gameState, claimRouteHandler, this::chooseClaimCards);
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
        if(infos.size() == 5) infos.remove(0);
        infos.add(new Text(message));
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
            cardsHandler.onDrawCard(s);
            drawCard(cardsHandler);
        });

        this.claimRouteHandler.setValue((r, c) -> {
            claimRouteHandler.onClaimRoute(r, c);
            this.claimRouteHandler.setValue(null);
        });

    }



    /**
     * Method for the scene graph when a player needs to choose his tickets
     * @param options             (SortedBag<Ticket>)
     * @param chooseTicketHandler (ChooseTicketsHandler)
     */
    public void chooseTickets(SortedBag<Ticket> options, ActionHandlers.ChooseTicketsHandler chooseTicketHandler) {

        assert Platform.isFxApplicationThread();

        ListView<Ticket> listView = new ListView<>();
        listView.getItems().addAll(options.toList());
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        String instruction = String.format(StringsFr.CHOOSE_TICKETS, options.size() - Constants.DISCARDABLE_TICKETS_COUNT,
                StringsFr.plural(options.size() - Constants.DISCARDABLE_TICKETS_COUNT));

        Text text = new Text(instruction);

        TextFlow textFlow = new TextFlow(text);

        selectionScene(textFlow, listView, StringsFr.TICKETS);

    }

    /**
     * @param cardHandler (DrawCardHandler)
     */
    public void drawCard(ActionHandlers.DrawCardHandler cardHandler) {

        assert Platform.isFxApplicationThread();

        if(gameState.canDrawCards()) this.drawCardsHandler.setValue((s) -> {
            cardHandler.onDrawCard(s);
            this.drawCardsHandler.setValue(null);
                });
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

       Text text = new Text();

        TextFlow textFlow = new TextFlow(text);

        selectionScene(textFlow, listView, StringsFr.CHOOSE_CARDS);
    }

    /**
     * @param options      (List<SortedBag<Card>>)
     * @param cardsHandler (ChooseCardsHandler)
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> options, ActionHandlers.ChooseCardsHandler cardsHandler) {

        assert Platform.isFxApplicationThread();

        ListView<SortedBag<Card>> listView = new ListView<>();
        listView.setCellFactory(v ->
                new TextFieldListCell<>(new StringConverter<>() {
                    @Override
                    public String toString(SortedBag<Card> object) {
                        return Info.cardNames(object);
                    }

                    @Override
                    public SortedBag<Card> fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                }));
        listView.getItems().addAll(options);
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Text text = new Text(StringsFr.CHOOSE_ADDITIONAL_CARDS);

        TextFlow textFlow = new TextFlow(text);

        selectionScene(textFlow, listView, StringsFr.CHOOSE_ADDITIONAL_CARDS);
    }





    /**
     * Scene graph for the ticket and card pop-up window
     */
    private <E> void selectionScene(TextFlow textFlow, ListView<E> listView, String title) {

        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(mainStage);
        stage.initModality(Modality.WINDOW_MODAL);

        stage.setTitle(title);

        Button button = new Button("Choisir");
        //TODO: add proper functionality to button
        button.setOnMouseClicked((e) -> stage.close());

        VBox vBox = new VBox();
        vBox.getChildren().addAll(textFlow, listView, button);
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");
        stage.setScene(scene);
        stage.show();
    }

}