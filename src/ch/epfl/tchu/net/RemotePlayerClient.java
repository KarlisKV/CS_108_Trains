package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerClient {

    private final Player player;
    private final Socket socket;
    private final BufferedWriter writer;
    private final BufferedReader reader;

    public RemotePlayerClient(Player player, String hostName, int port) {

        this.player = player;

        try {
            socket = new Socket(hostName, port);

            writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(),
                            US_ASCII));

            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(),
                            US_ASCII));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    public void run() {

        try {

            do {

                String received = reader.readLine();

                if(received.length() > 0) {

                    String[] typeAndArgs = received.split(" ");
                    String type = typeAndArgs[0];

                    switch (type) {

                        case "INIT_PLAYERS":

                            PlayerId ownId = Serdes.PLAYER_ID_SERDE.deserialize(typeAndArgs[1]);
                            String[] serNames = typeAndArgs[2].split(",");

                            Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, Serdes.STRING_SERDE.deserialize(serNames[0]),
                                    PlayerId.PLAYER_2, Serdes.STRING_SERDE.deserialize(serNames[1]));

                            player.initPlayers(ownId, playerNames);

                            break;

                        case "RECEIVE_INFO":

                            player.receiveInfo(Serdes.STRING_SERDE.deserialize(typeAndArgs[1]));

                            break;

                        case "UPDATE_STATE":

                            PublicGameState newState = Serdes.PUBLIC_GAME_STATE_SERDE.deserialize(typeAndArgs[1]);
                            PlayerState newPlayerState = Serdes.PLAYER_STATE_SERDE.deserialize(typeAndArgs[2]);

                            player.updateState(newState, newPlayerState);

                            break;

                        case "SET_INITIAL_TICKETS":

                            SortedBag<Ticket> tickets = Serdes.TICKET_SORTED_BAG_SERDE.deserialize(typeAndArgs[1]);
                            player.setInitialTicketChoice(tickets);

                            break;

                        case "CHOOSE_INITIAL_TICKETS":

                            SortedBag<Ticket> chosenTickets = player.chooseInitialTickets();
                            send(Serdes.TICKET_SORTED_BAG_SERDE.serialize(chosenTickets));

                            break;

                        case "NEXT_TURN":

                            Player.TurnKind turnKind = player.nextTurn();
                            send(Serdes.TURN_KIND_SERDE.serialize(turnKind));

                            break;

                        case "CHOOSE_TICKETS":

                            SortedBag<Ticket> options = Serdes.TICKET_SORTED_BAG_SERDE.deserialize(typeAndArgs[1]);
                            SortedBag<Ticket> keptTickets = player.chooseTickets(options);
                            send(Serdes.TICKET_SORTED_BAG_SERDE.serialize(keptTickets));

                            break;

                        case "DRAW_SLOT":

                            int drawSlot = player.drawSlot();
                            send(Serdes.INTEGER_SERDE.serialize(drawSlot));

                            break;

                        case "ROUTE":

                            Route claimedRoute = player.claimedRoute();
                            send(Serdes.ROUTE_SERDE.serialize(claimedRoute));

                            break;

                        case "CARDS":

                            SortedBag<Card> initCC = player.initialClaimCards();
                            send(Serdes.CARD_SORTED_BAG_SERDE.serialize(initCC));

                            break;

                        case "CHOOSE_ADDITIONAL_CARDS":

                            List<SortedBag<Card>> possibleAdditCards = Serdes.LIST_SORTED_BAG_CARD_SERDE.deserialize(typeAndArgs[1]);
                            SortedBag<Card> additCards = player.chooseAdditionalCards(possibleAdditCards);
                            send(Serdes.CARD_SORTED_BAG_SERDE.serialize(additCards));

                            break;

                        default:
                            throw new IOException("Communication error");

                    }
                }


            } while (reader.readLine() != null);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    private void send(String serialised) {
        try {

            writer.write(serialised);
            writer.write('\n');
            writer.flush();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}