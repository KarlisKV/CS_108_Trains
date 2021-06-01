package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * RemotePlayerProxy represents a non-host player who is connected to the host.
 * Used by the server to communicate between said player (client) and host (server).
 * Instantiated in ServerMain
 * @author Daniel Polka (326800)
 */
public final class RemotePlayerProxy implements Player {

    public static final String sep = " ";
    private final BufferedWriter writer;
    private final BufferedReader reader;
    private final Socket socket;

    /**
     * Constructor of RemotePlayerProxy
     * @param socket (Socket) used to communicate messages through the network with the client
     */
    public RemotePlayerProxy(Socket socket) {

        this.socket = socket;

        try {

            reader = new BufferedReader(
                            new InputStreamReader(this.socket.getInputStream(),
                                    US_ASCII));

            writer = new BufferedWriter(
                            new OutputStreamWriter(this.socket.getOutputStream(),
                                    US_ASCII));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }


    }


    /**
     * Sends a message indicating to the client that it should call initPlayers with specified parameters to update the GUI
     * @param ownId (PlayerId) id of the player
     * @param playerNames (Map<PlayerId, String> playerNames) names of the players
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

        String init = MessageId.INIT_PLAYERS + sep + Serdes.PLAYER_ID_SERDE.serialize(ownId) + sep + Serdes.STRING_LIST_SERDE.serialize(List.of(playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_2)));
        send(init);
    }

    /**
     * Sends a message indicating to the client that the player it's representing should receive the information "info"
     * @param info (String) information for the player
     */
    @Override
    public void receiveInfo(String info) {

        String serInfo = MessageId.RECEIVE_INFO + sep + Serdes.STRING_SERDE.serialize(info);
        send(serInfo);

    }

    /**
     * Sends a message indicating to the client that it should call updateState with specified tickets to update the GUI
     * @param newState (PublicGameState) new public game state
     * @param ownState (PlayerState) player state
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {

        String state = MessageId.UPDATE_STATE + sep + Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState) + sep + Serdes.PLAYER_STATE_SERDE.serialize(ownState);
        send(state);
    }

    /**
     * Sends a message indicating to the client that it should call setInitialTicketChoice with specified tickets to update the GUI
     * @param tickets (SortedBag<Ticket>) Sorted bag of tickets
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

        String initialTicketOptions = MessageId.SET_INITIAL_TICKETS + sep + Serdes.TICKET_SORTED_BAG_SERDE.serialize(tickets);
        send(initialTicketOptions);

    }

    /**
     * Sends a message indicating to the client that it should call chooseInitialTickets and send the chosen tickets back to the proxy (this)
     * @return (SortedBag<Ticket>) tickets which the player is keeping
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {

        String chooseTickets = MessageId.CHOOSE_INITIAL_TICKETS.toString();
        send(chooseTickets);

        String initialTicketsChoice = receive();

        return Serdes.TICKET_SORTED_BAG_SERDE.deserialize(initialTicketsChoice);
    }

    /**
     * Sends a message indicating to the client that it should call nextTurn and then send the chosen TurnKind back
     * @return (TurnKind) the action the player wants to do in his turn (draw cards, draw tickets, claim routes)
     */
    @Override
    public TurnKind nextTurn() {

        String isNextTurn = MessageId.NEXT_TURN.toString();
        send(isNextTurn);

        String turnKind = receive();

        return Serdes.TURN_KIND_SERDE.deserialize(turnKind);
    }

    /**
     * Sends a message indicating to the client that it should call chooseTickets with specified tickets to update the GUI,
     * and then send back the tickets which the player chose to keep
     * @param options (SortedBag<Ticket>) tickets that the player can choose
     * @return the tickets the player picks
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {

        String chooseTickets = MessageId.CHOOSE_TICKETS + sep + Serdes.TICKET_SORTED_BAG_SERDE.serialize(options);
        send(chooseTickets);

        String chosenTickets = receive();

        return Serdes.TICKET_SORTED_BAG_SERDE.deserialize(chosenTickets);
    }

    /**
     * Sends a message indicating to the client that it should call drawSlot and send back the slot number
     * of the card the player chose to draw
     * @return the slot number of the card the player chose to draw
     */
    @Override
    public int drawSlot() {

        String draw = MessageId.DRAW_SLOT.toString();
        send(draw);

        String slot = receive();

        return Integer.parseInt(slot);
    }

    /**
     * Sends a message indicating to the client that it should call claimedRoute and send back the route
     * that the player chose to claim
     * @return the claimed route
     */
    @Override
    public Route claimedRoute() {

        String chooseRoute = MessageId.ROUTE.toString();
        send(chooseRoute);

        String chosenRoute = receive();

        return Serdes.ROUTE_SERDE.deserialize(chosenRoute);
    }

    /**
     * Sends a message indicating to the client that it should call initialClaimCards and send back the claim cards
     * that the player wants to claim a route with
     * @return the cards the player uses to claim a route
     */
    @Override
    public SortedBag<Card> initialClaimCards() {

        String chooseICC = MessageId.CARDS.toString();
        send(chooseICC);

        String initCC = receive();

        return Serdes.CARD_SORTED_BAG_SERDE.deserialize(initCC);
    }

    /**
     * Sends a message indicating to the client that it should call chooseAdditionalCards with
     * the possibilities the player has and then send back the additional claim cards/an empty
     * SortedBag of cards if the player wants to give up the route claim
     * @param options (List<SortedBag<Card>>) possible cards to be used
     * @return additional cards needed to seize a tunnel
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {

        String iWantMore = MessageId.CHOOSE_ADDITIONAL_CARDS + sep + Serdes.LIST_SORTED_BAG_CARD_SERDE.serialize(options);
        send(iWantMore);

        String additionalCards = receive();

        return Serdes.CARD_SORTED_BAG_SERDE.deserialize(additionalCards);
    }

    @Override
    public void highlightRoute(Route route) {
        send(MessageId.HIGHLIGHT_TRAIL + sep + Serdes.ROUTE_SERDE.serialize(route));
    }


    /**
     * Private method added to avoid code repetition.
     * Sends the message received as an argument to the client
     * @param serialised message to be sent
     */
    private void send(String serialised) {
        try  {

            writer.write(serialised);
            writer.write('\n');
            writer.flush();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    /**
     * Private method added to avoid code repetition.
     * Receives messages
     */
    private String receive() {

        try{

             return reader.readLine();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    /**
     * I don't know if we're allowed to add public methods just like that yet,
     * but I added this to close the reader, writer and the socket once the game has ended
     * @throws IOException shouldn't happen, but even if it does it doesn't matter because it was the end of the game anyway :)
     */
    public void closeAll() throws IOException {
        writer.close();
        reader.close();
        socket.close();
    }

}
