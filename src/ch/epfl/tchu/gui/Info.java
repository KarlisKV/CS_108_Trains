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
import java.util.Map;

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

        List<String> allCardNames = new ArrayList<>(List.of(StringsFr.BLACK_CARD, StringsFr.VIOLET_CARD, StringsFr.BLUE_CARD,
                StringsFr.GREEN_CARD, StringsFr.YELLOW_CARD, StringsFr.ORANGE_CARD, StringsFr.RED_CARD, StringsFr.WHITE_CARD, StringsFr.LOCOMOTIVE_CARD));

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
        String namesOfPlayers = playerNames.get(0) + StringsFr.AND_SEPARATOR + playerNames.get(1);
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


    /**
     * returns the message declaring that the player kept the given number of tickets
     * @param count (int) number of tickets
     * @return the message declaring that the player kept the given number of tickets
     */

    public String keptTickets(int count) {

        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, StringsFr.plural(count));
    }


    /**
     * returns the message declaring that the player can play
     * @return the message declaring that the player can play
     */
    public String canPlay() {

        return String.format(StringsFr.CAN_PLAY, playerName);
    }


    /**
     *returns the message stating that the player has drawn the given number of tickets
     * @param count (int) number of tickets
     * @return the message stating that the player has drawn the given number of tickets
     */
    public String drewTickets(int count) {

        return String.format(StringsFr.DREW_TICKETS, playerName, count, StringsFr.plural(count));
    }


    /**
     * returns the message declaring that the player has drawn a card "blind", i.e. from the top of the draw pile
     * @return the message declaring that the player has drawn a card "blind", i.e. from the top of the draw pile
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }


    /**
     * returns the message declaring that the player has drawn the face-up card given
     * @param card (Card) the given card
     * @return the message declaring that the player has drawn the face-up card given
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }


    /**
     * returns the message stating that the player has seized the given route using the given cards
     * @param route (Route) given route
     * @param cards (Card) given card
     * @return the message stating that the player has seized the given route using the given cards
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return cards.size() == 1 ?
                String.format(StringsFr.CLAIMED_ROUTE, playerName, route.station1() + StringsFr.EN_DASH_SEPARATOR + route.station2(), cardName(cards.get(0), 1)):
                String.format(StringsFr.CLAIMED_ROUTE, playerName, route.station1() + StringsFr.EN_DASH_SEPARATOR + route.station2(), cardNames(cards));
    }

    /**
     *  returns the message stating that the player wishes to seize the given tunnel route using initially the given cards
     * @param route (Route) given tunnel route
     * @param initialCards (SortedBag<Card>) initially given cards
     * @return returns the message stating that the player wishes to seize the given tunnel route using initially the given cards
     */

    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return initialCards.size() == 1 ?
                String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, route.station1() + StringsFr.EN_DASH_SEPARATOR + route.station2(), cardName(initialCards.get(0), 1)):
                String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM, playerName, route.station1() + StringsFr.EN_DASH_SEPARATOR + route.station2(), cardNames(initialCards));
    }


    /**
     *  returns the message stating that the player has fired three additional cards data, and they imply a cost not included in the number of cards given
     * @param drawnCards  (SortedBag<Card>) given cards
     * @param additionalCost (int) cost of the cards
     * @return the message stating that the player has fired three additional cards data, and they imply a cost not included in the number of cards given
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {

        String additionalCardsAre = String.format(StringsFr.ADDITIONAL_CARDS_ARE, cardNames(drawnCards));

        if(additionalCost > 0) {
            return additionalCardsAre + String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost));
        } else {
            return additionalCardsAre + StringsFr.NO_ADDITIONAL_COST;
        }
    }

    /**
     * returns the message declaring that the player could not (or wanted) to seize the given tunnel
     * @param route (Route) given route
     * @return the message declaring that the player could not (or wanted) to seize the given tunnel
     */

    public String didNotClaimRoute(Route route) {
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, route.station1() + StringsFr.EN_DASH_SEPARATOR + route.station2());
    }


    /**
     * returns the message declaring that the player has only the given number (and less than or equal to 2) of wagons, and that the last turn therefore begins
     * @param carCount (int) count of wagons left
     * @return the message declaring that the player has only the given number (and less than or equal to 2) of wagons, and that the last turn therefore begins
     */
    public String lastTurnBegins(int carCount) {
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName , carCount, StringsFr.plural(carCount));
    }


    /**
     * returns the message declaring that the player obtains the end-of-game bonus thanks to the given path, which is the longest, or one of the longest
     * @param longestTrail (Trail) longest trail
     * @return the message declaring that the player obtains the end-of-game bonus thanks to the given path, which is the longest, or one of the longest
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(StringsFr.GETS_BONUS, playerName, longestTrail.station1() + StringsFr.EN_DASH_SEPARATOR + longestTrail.station2());
    }


    /**
     * returns the message declaring that the player wins the game with the number of points given, his opponent having only obtained loserPoints
     * @param winnerPoints (int) winner's points
     * @param loserPoints (int) loser's points
     * @return the message declaring that the player wins the game with the number of points given, his opponent having only obtained loserPoints
     */
    public String won(int winnerPoints, int loserPoints) {
        return String.format(StringsFr.WINS, playerName, winnerPoints, StringsFr.plural(winnerPoints), loserPoints, StringsFr.plural(loserPoints));
    }

    /**
     * Private method added to avoid code repetition.
     * It has the same function as cardName except it works with multiple cards
     * @param cards SortedBag<Card> of the cards you want the names of
     * @return A String containing a sentence enumerating the number of colour cards
     * (the colours are specified in the sentence) followed by the number of locomotive cards
     */
    private String cardNames(SortedBag<Card> cards) {
        Preconditions.checkArgument(cards != null);

        Map<Card, Integer> cardTypes = cards.toMap();

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

            if(beforeLastCard && cardTypes.size() > 1) {
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
