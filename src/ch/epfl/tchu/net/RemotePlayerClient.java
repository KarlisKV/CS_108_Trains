package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * RemotePlayerClient is used for the non-host player (client) to connect
 * and communicate with the host (server), instantiated in ClientMain
 * @author Daniel Polka (326800)
 */
public final class RemotePlayerClient {


    private final BufferedReader reader;
    private final Player player;
    private final BufferedWriter writer;
    private final Socket socket;



    /**
     * Constructor of RemotePlayerClient
     * @param player the instance of Player to represent
     * @param hostName name of the host (server) that the client should connect to
     * @param port port number that the host (server) has hosted the game on
     */
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


    /**
     * This method is used to communicate with the host (server) and should be called
     * in ClientMain by a new Thread
     */
    public void run() {

        try {

            String received;

            do {

                received = reader.readLine();

                if(received != null) {

                    if(received.length() > 0) {

                        String[] typeAndArgs = received.split(RemotePlayerProxy.sep);
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

                                List<SortedBag<Card>> possibleAdditionalCards = Serdes.LIST_SORTED_BAG_CARD_SERDE.deserialize(typeAndArgs[1]);
                                SortedBag<Card> additionalCards = player.chooseAdditionalCards(possibleAdditionalCards);
                                send(Serdes.CARD_SORTED_BAG_SERDE.serialize(additionalCards));

                                break;

                            case "HIGHLIGHT_TRAIL":

                                List<Route> routes = Serdes.ROUTE_LIST_SERDE.deserialize(typeAndArgs[1]);
                                for(Route r : routes) player.highlightRoute(r);

                                break;

                            default:
                                throw new IOException("Communication error");
                        }
                    }
                }

            } while(received != null);

            reader.close();
            writer.close();
            socket.close();

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }


    /**
     * Private method added to avoid code repetition.
     * Sends the message received as an argument to the host (server)
     * @param serialised message to be sent
     */
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