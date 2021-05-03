package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerProxy implements Player {

    private static final String sep = " ";
    private final Socket socket;

    public RemotePlayerProxy(Socket socket) {
        this.socket = socket;
    }

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

        String additCards = receive();

        return Serdes.CARD_SORTED_BAG_SERDE.deserialize(additCards);
    }

    private void send(String serialised) {
        try (BufferedWriter w =
                     new BufferedWriter(
                             new OutputStreamWriter(socket.getOutputStream(),
                                     US_ASCII))) {
            w.write(serialised);
            w.write('\n');
            w.flush();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String receive() {

        try (
             BufferedReader r =
                     new BufferedReader(
                             new InputStreamReader(socket.getInputStream(),
                                     US_ASCII));) {

                                        String serialised = r.readLine();
                                        return serialised;

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
