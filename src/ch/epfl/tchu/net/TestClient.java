package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import java.util.List;
import java.util.Map;


public final class TestClient {
    public static void main(String[] args) {
        System.out.println("Starting client!");
        RemotePlayerClient playerClient =
                new RemotePlayerClient(new TestPlayer(),
                        "localhost",
                        5108);
        playerClient.run();
        System.out.println("Client done!");
    }

    private final static class TestPlayer implements Player {
        @Override
        public void initPlayers(PlayerId ownId,
                                Map<PlayerId, String> names) {
            System.out.printf("ownId: %s\n", ownId);
            System.out.printf("playerNames: %s\n", names);
        }

        @Override
        public void receiveInfo(String info) {

            System.out.println(info);

        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            System.out.println(newState + " / " + ownState);

        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            System.out.println(tickets);

        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            return SortedBag.of(1, ChMap.tickets().get(0), 1, ChMap.tickets().get(1));
        }

        @Override
        public TurnKind nextTurn() {
            return TurnKind.DRAW_CARDS;
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            return options;
        }

        @Override
        public int drawSlot() {
            return 420;
        }

        @Override
        public Route claimedRoute() {
            return ChMap.routes().get(3);
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return SortedBag.of(2, Card.BLUE, 500, Card.LOCOMOTIVE);
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return options.get(0);
        }
    }
}