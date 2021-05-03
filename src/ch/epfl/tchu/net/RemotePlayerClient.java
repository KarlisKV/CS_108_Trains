package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

                            break;

                        case "SET_INITIAL_TICKETS":

                            break;

                        case "CHOOSE_INITIAL_TICKETS":

                            break;

                        case "NEXT_TURN":

                            break;

                        case "CHOOSE_TICKETS":

                            break;

                        case "DRAW_SLOT":

                            break;

                        case "ROUTE":

                            break;

                        case "CARDS":

                            break;

                        case "CHOOSE_ADDITIONAL_CARDS":

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