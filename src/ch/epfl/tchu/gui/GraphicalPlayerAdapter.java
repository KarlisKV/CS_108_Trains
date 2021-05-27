package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

public class GraphicalPlayerAdapter implements Player {

    private GraphicalPlayer graphicalPlayer;

    private final ObjectProperty<PublicGameState> pgs;
    private final ObjectProperty<PlayerState> ps;

    private final BlockingQueue<SortedBag<Ticket>> ticketsQueue;
    private final BlockingQueue<SortedBag<Card>> cardsQueue;
    private final BlockingQueue<Route> routesQueue;
    private final BlockingQueue<Integer> drawSlotQueue;
    private final BlockingQueue<List<SortedBag<Card>>> possibleCardsQueue;

    //Handlers which need to be defined at all times
    private final ActionHandlers.ChooseCardsHandler cch;
    private final ActionHandlers.ChooseTicketsHandler cth;
    

    /**
     * Constructor takes no arguments
     */
    public GraphicalPlayerAdapter() {

        pgs = new SimpleObjectProperty<>();
        ps = new SimpleObjectProperty<>();


        ticketsQueue = new ArrayBlockingQueue<>(1);
        cardsQueue = new ArrayBlockingQueue<>(1);
        routesQueue = new ArrayBlockingQueue<>(1);
        drawSlotQueue = new ArrayBlockingQueue<>(1);
        possibleCardsQueue = new ArrayBlockingQueue<>(1);

        cch = (c) -> {
            try{

                cardsQueue.put(c);

            } catch (InterruptedException ignored) {}
        };

        cth = (t) -> {
            try{

                ticketsQueue.put(t);

            } catch (InterruptedException ignored) {}
        };

    }

    /**
     * is called at the start of the game to communicate to the player his own identity ownId,
     * as well as the names of the different players, his own included, which can be found in playerNames
     * @param ownId       (PlayerId) id of the player
     * @param playerNames (Map<PlayerId, String> playerNames) names of the players
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
       runLater(() -> this.graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    /**
     * is called whenever information needs to be communicated to the player during the game;
     * this information is given in the form of a character string, generally produced by the class Info
     *
     * @param info (String) gives information to the player
     */
    @Override
    public void receiveInfo (String info)    {
        runLater (() -> graphicalPlayer.receiveInfo (info));


    }

    /**
     * Which is called whenever the status of the game has changed,
     * to inform the player of the public component of this new state, new State as well as its own state ownState,
     * Calls setState of graphicalPlayer
     * @param newState (PublicGameState) new public game state
     * @param ownState (PlayerState) player state
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        pgs.setValue(newState);
        ps.setValue(ownState);
        runLater (() -> graphicalPlayer.setState (newState, ownState));

    }

    /**
     * is called at the start of the game to communicate to the player the five tickets that have been distributed to him
     *
     * @param tickets (SortedBag<Ticket>) Sorted bag of tickets
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater (() -> graphicalPlayer.chooseTickets(tickets, cth));
    }

    /**
     * is called at the start of the game to ask the player which of the tickets he was initially given out (via the previous method) he is keeping
     * @return (SortedBag <Ticket>) tickets which the player is keeping
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {

        try {

            return ticketsQueue.take();

        } catch (InterruptedException ignored) {
            throw new Error();
        }


    }

    /**
     * is called at the start of a player's turn, to find out what type of action he wishes to perform during that turn
     * @return (TurnKind) the action the player wants to do in his turn (draw cards, draw tickets, claim routes)
     */
    @Override
    public TurnKind nextTurn() {

        //Shouldn't be necessary, but clearing every BlockingQueue at the start of each turn just in case is a good idea
        ticketsQueue.clear();
        cardsQueue.clear();
        routesQueue.clear();
        drawSlotQueue.clear();
        possibleCardsQueue.clear();

        BlockingQueue<TurnKind> q = new ArrayBlockingQueue<>(1);

        ActionHandlers.DrawTicketsHandler dth = () -> {

            try{

                q.put(TurnKind.DRAW_TICKETS);

            } catch (InterruptedException ignored) {
                throw new Error();
            }
        };

        ActionHandlers.DrawCardHandler dch = (s) -> {

            try{

                drawSlotQueue.put(s);
                q.put(TurnKind.DRAW_CARDS);

            } catch (InterruptedException ignored) {
                throw new Error();
            }

        };

        ActionHandlers.ClaimRouteHandler crh = (r, c) -> {

            try{

                routesQueue.put(r);
                possibleCardsQueue.put(ps.get().possibleClaimCards(r));
                q.put(TurnKind.CLAIM_ROUTE);

            } catch (InterruptedException ignored) {
                throw new Error();
            }

        };

        runLater(() -> graphicalPlayer.startTurn(dth, dch, crh));

        try{

            return q.take();

        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * is called when the player has decided to draw additional tickets during the game,
     * in order to communicate the tickets drawn and to know which ones he is keeping
     *
     * @param options (SortedBag<Ticket>) tickets that the player can choose
     * @return the tickets the player picks
     */

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {

        runLater(() -> graphicalPlayer.chooseTickets(options, cth));

        try{

            return ticketsQueue.take();

        } catch(InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * is called when the player has decided to draw railcar / locomotive cards, in order to know from where he wishes to draw them: from one of the slots containing a face-up card -
     * in which case the value returned is between 0 and 4 included -, or from the deck - in which case the returned value is Constants.DECK_SLOT(i.e. -1)
     * @return an int depending on from where the locomotive card is picked
     */
    @Override
    public int drawSlot() {

        try{
            return drawSlotQueue.take();

        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * is called when the player has decided to (attempt to) seize a road, in order to know which road it is
     *
     * @return the claimed route
     */
    @Override
    public Route claimedRoute() {
        try{

            return routesQueue.take();

        } catch (InterruptedException e){
            throw new Error();

        }
    }

    /**
     * is called when the player has decided to (attempt to) seize a road, in order to know which card (s) he initially wishes to use for it
     *
     * @return the cards he uses to claim the route
     */
    @Override
    public SortedBag<Card> initialClaimCards() {

        runLater(() -> {

            try{

                graphicalPlayer.chooseClaimCards(possibleCardsQueue.take(), cch);

            } catch (InterruptedException ignored) {
                throw new Error();
            }
        });
        
        try{
             return cardsQueue.take();

        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * is called when the player has decided to try to seize a tunnel and additional cards are needed, in order to know which card (s)
     * he wishes to use for this, the possibilities being passed to him in argument;
     * if the returned multiset is empty, it means that the player does not wish (or cannot) choose one of these possibilities
     *
     * @param options (List<SortedBag<Card>>) possible cards to be used
     * @return additional cards needed to seize a tunnel
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {

        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, cch));

        try{
            return cardsQueue.take();

        } catch (InterruptedException e) {
            throw new Error();
        }
    }
}
