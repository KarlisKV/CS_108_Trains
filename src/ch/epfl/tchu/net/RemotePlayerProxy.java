package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * RemotePlayerProxy represents the remote player proxy. It implements the interface Player
 * @author Daniel Polka (326800)
 */
public class RemotePlayerProxy implements Player {

    private static final String sep = " ";
    // TODO: 5/25/2021 can we remove the attribute and have it only inside the constructor?
    private final Socket socket;
    private final BufferedWriter writer;
    private final BufferedReader reader;

    /**
     * Constructor of the RemotePlayerProxy class
     * @param socket (Socket) used to communicate messages through the network with the client
     */
    public RemotePlayerProxy(Socket socket) {
        this.socket = socket;

        try {

            reader = new BufferedReader(
                            new InputStreamReader(socket.getInputStream(),
                                    US_ASCII));

            writer = new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream(),
                                    US_ASCII));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }


    }
// TODO: 5/22/2021 add javaDoc for everything
    /**
     * 
     * @param ownId (PlayerId) id of the player
     * @param playerNames (Map<PlayerId, String> playerNames) names of the players
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

        String init = MessageId.INIT_PLAYERS + sep + Serdes.PLAYER_ID_SERDE.serialize(ownId) + sep + Serdes.STRING_LIST_SERDE.serialize(List.of(playerNames.get(PlayerId.PLAYER_1), playerNames.get(PlayerId.PLAYER_2)));
        send(init);
    }

    @Override
    public void receiveInfo(String info) {

        String serInfo = MessageId.RECEIVE_INFO + sep + Serdes.STRING_SERDE.serialize(info);
        send(serInfo);

    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {

        String state = MessageId.UPDATE_STATE + sep + Serdes.PUBLIC_GAME_STATE_SERDE.serialize(newState) + sep + Serdes.PLAYER_STATE_SERDE.serialize(ownState);
        send(state);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

        String initialTicketOptions = MessageId.SET_INITIAL_TICKETS + sep + Serdes.TICKET_SORTED_BAG_SERDE.serialize(tickets);
        send(initialTicketOptions);

    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {

        String chooseTickets = MessageId.CHOOSE_INITIAL_TICKETS.toString();
        send(chooseTickets);

        String initialTicketsChoice = receive();

        return Serdes.TICKET_SORTED_BAG_SERDE.deserialize(initialTicketsChoice);
    }

    @Override
    public TurnKind nextTurn() {

        String isNextTurn = MessageId.NEXT_TURN.toString();
        send(isNextTurn);

        String turnKind = receive();

        return Serdes.TURN_KIND_SERDE.deserialize(turnKind);
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {

        String chooseTickets = MessageId.CHOOSE_TICKETS + sep + Serdes.TICKET_SORTED_BAG_SERDE.serialize(options);
        send(chooseTickets);

        String chosenTickets = receive();

        return Serdes.TICKET_SORTED_BAG_SERDE.deserialize(chosenTickets);
    }

    @Override
    public int drawSlot() {

        String draw = MessageId.DRAW_SLOT.toString();
        send(draw);

        String slot = receive();

        return Integer.parseInt(slot);
    }

    @Override
    public Route claimedRoute() {

        String chooseRoute = MessageId.ROUTE.toString();
        send(chooseRoute);

        String chosenRoute = receive();

        return Serdes.ROUTE_SERDE.deserialize(chosenRoute);
    }

    @Override
    public SortedBag<Card> initialClaimCards() {

        String chooseICC = MessageId.CARDS.toString();
        send(chooseICC);

        String initCC = receive();

        return Serdes.CARD_SORTED_BAG_SERDE.deserialize(initCC);
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {

        String iWantMore = MessageId.CHOOSE_ADDITIONAL_CARDS.toString() + sep + Serdes.LIST_SORTED_BAG_CARD_SERDE.serialize(options);
        send(iWantMore);

        String additionalCards = receive();

        return Serdes.CARD_SORTED_BAG_SERDE.deserialize(additionalCards);
    }






    private void send(String serialised) {
        try  {

            writer.write(serialised);
            writer.write('\n');
            writer.flush();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String receive() {

        try{

             return reader.readLine();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // TODO: 5/25/2021 do we need this method? not used anywhere
    public void closeAll() throws IOException {
        reader.close();
        writer.close();
    }

}
