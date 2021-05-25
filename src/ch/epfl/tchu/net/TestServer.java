package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


public final class TestServer {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
             Player playerProxy = new RemotePlayerProxy(socket);

         //   PlayerState playerState = PlayerState.initial(SortedBag.of(4, Card.BLUE));
/*
            playerState = playerState.withAddedCard(Card.RED);
            playerState = playerState.withAddedCards(SortedBag.of(2,Card.RED));
            playerState = playerState.withClaimedRoute(ChMap.routes().get(2), SortedBag.of(3, Card.RED));

 */

            GameState state = GameState.initial(SortedBag.of(ChMap.tickets()), new Random());

         /*   state = state.withBlindlyDrawnCard();
            state = state.withDrawnFaceUpCard(2);
            state = state.withBlindlyDrawnCard();
            state = state.withClaimedRoute(ChMap.routes().get(2),SortedBag.of(3, Card.RED));
            */

            PlayerState playerState = state.playerState(PlayerId.PLAYER_2);

            playerProxy.updateState(state, playerState);
        }
        System.out.println("Server done!");
    }
}