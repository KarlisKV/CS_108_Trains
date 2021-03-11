package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * CardState class
 * @author Daniel Polka  (326800)
 * @author Karlis Velins (325180)
 */

public final class Info {

    private final String playerName;


    /**
     * Primary constructor for Info class
     * @param playerName name of the player
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Method to return the name of a card in French
     * @param card the card you want the name of
     * @param count the number of cards designated in the sentence
     * @return the name of Card card in French
     */
    public static String cardName(Card card, int count) {

        List<String> allCardNames = new ArrayList<>(List.of(StringsFr.BLACK_CARD, StringsFr.VIOLET_CARD, StringsFr.BLUE_CARD, StringsFr.GREEN_CARD, StringsFr.YELLOW_CARD, StringsFr.ORANGE_CARD, StringsFr.RED_CARD, StringsFr.WHITE_CARD, StringsFr.LOCOMOTIVE_CARD));

        if(!Card.ALL.contains(card)) {
            return "ERROR: Card doesn't exist";
        } else {
            return allCardNames.get(Card.ALL.indexOf(card)) + StringsFr.plural(count);
        }
    }


    /**
     * Method to call when the game is a draw
     * @param playerNames names of the two players as a List<String>
     * @param points the number of points each player has
     * @return String containing a sentence in French announcing
     * that the two players drew
     */
    public static String draw(List<String> playerNames, int points) {
        String namesOfPlayers = playerNames.get(0) + " et " + playerNames.get(1);
        return String.format(StringsFr.DRAW, namesOfPlayers, points);
    }


    /**
     * Method to return which player will start the game
     * @return String containing a sentence in French announcing
     * which player starts the game
     */
    public String willPlayFirst() {

        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }



    public String keptTickets(int count) {

        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
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
        return String.format(StringsFr.CLAIMED_ROUTE, playerName, route.station1() + StringsFr.EN_DASH_SEPARATOR + route.station2(), cardNames(cards));
    }



    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, route.station1() + StringsFr.EN_DASH_SEPARATOR + route.station2(), cardNames(initialCards));
    }



    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {

        String additionalCardsAre = String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardNames(drawnCards));

        if(additionalCost > 0) {
            return additionalCardsAre + String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost));
        } else {
            return additionalCardsAre + StringsFr.NO_ADDITIONAL_COST;
        }
    }



    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, route.station1() + StringsFr.EN_DASH_SEPARATOR + route.station2());
    }



    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName , carCount, StringsFr.plural(carCount));
    }



    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, playerName, longestTrail.station1() + StringsFr.EN_DASH_SEPARATOR + longestTrail.station2());
    }



    public String won(int winnerPoints, int loserPoints) {
        return String.format(StringsFr.WINS, playerName, winnerPoints, StringsFr.plural(winnerPoints), loserPoints, StringsFr.plural(loserPoints));
    }





    /**
     * Private method added to avoid code repetition (is this allowed?).
     * It has the same function as cardName except it works with multiple cards
     * @param cards SortedBag<Card> of the cards you want the names of
     * @return A String containing a sentence enumerating the number of colour cards
     * (the colours are specified in the sentence) followed by the number of locomotive cards
     */
    public String cardNames(SortedBag<Card> cards) {
        Preconditions.checkArgument(cards != null);

        HashMap<Color, Integer> cardCount = new HashMap<>();
        StringBuilder cardNameBuilder = new StringBuilder();

        for(Card clr : Card.ALL) {

            cardCount.put(clr.color(), 0);

            for(Card c : cards) {
                if(c.equals(clr)) {
                    cardCount.put(clr.color(), (cardCount.get(clr.color()) + 1));
                }
            }

            //These booleans work because cards is a SortedBag
            boolean notFirstCard = !cards.get(0).equals(clr);
            boolean beforeLastCard = cards.get(cards.size() - 2).equals(clr);
            boolean notLastCard = !cards.get(cards.size() - 1).equals(clr);

            if(beforeLastCard) {
                cardNameBuilder.append(StringsFr.AND_SEPARATOR);
            }

            if(cardCount.get(clr.color()) > 0) {

                if(notFirstCard && notLastCard && !beforeLastCard) {
                    cardNameBuilder.append(", ");
                }

                cardNameBuilder.append(cardCount.get(clr.color()));
                cardNameBuilder.append(" ");
                cardNameBuilder.append(cardName(clr, cardCount.get(clr.color())));

            }
        }

        return cardNameBuilder.toString();
    }

}
