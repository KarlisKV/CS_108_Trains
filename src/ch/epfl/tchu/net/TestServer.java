package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static java.nio.charset.StandardCharsets.US_ASCII;

public final class TestServer {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            Player playerProxy = new RemotePlayerProxy(socket);
            var playerNames = Map.of(PLAYER_1, "Ada",
                    PLAYER_2, "Charles");
            playerProxy.receiveInfo("PLAYER_1, playerNames");
        }
        System.out.println("Server done!");
    }
}