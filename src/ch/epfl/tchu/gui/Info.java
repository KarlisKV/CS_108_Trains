package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;

import java.util.List;

public final class Info {

    private final String playerName;

    public Info(String playerName) {

        this.playerName = playerName;
    }


    public static String cardName(Card card, int count) {
        return String.format(StringsFr.BLACK_CARD, StringsFr.BLUE_CARD, card, StringsFr.plural(count));
    }



    public static String draw(List<String> playerNames, int points) {

        String namesOfPlayers = playerNames.get(0) + " et " + playerNames.get(1);
        return String.format(StringsFr.DRAW, namesOfPlayers, points);
    }

    public String willPlayFirst() {

        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    public String keptTickets(int count) {

        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count);
    }


    public String canPlay() {

        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    public String drewTickets(int count) {

        return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count));
    }

    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, card);
    }

    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE, playerName);
    }
}
