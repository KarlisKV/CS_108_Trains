package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Serdes contains all of the serdes used for the game
 * @author Karlis Velins (325180)
 */
public final class Serdes {

    /**
     * Serdes class not to be instantiated
     */
    private Serdes() {}

    //Integer serde
    public static final Serde<Integer> INTEGER_SERDE = Serde.of((Objects::toString), (Integer::parseInt));

    //String serde
    public static final Serde<String> STRING_SERDE = Serde.of((string -> Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8))),
                    ((base64 -> new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8))));

    //PlayerId serde
    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);

    //TurnKind serde
    public static final Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);
    //Card serde
    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);
    //Route serde
    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());
    //Ticket serde
    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());

    //List String serde
    public static final Serde<List<String>> STRING_LIST_SERDE = Serde.listOf(STRING_SERDE,',');
    // Card List serde
    public static final Serde<List<Card>> CARD_LIST_SERDE = Serde.listOf(CARD_SERDE, ',');
    //Ticket List serde
    public static final Serde<List<Ticket>> TICKET_LIST_SERDE = Serde.listOf(TICKET_SERDE, ',');
    //Route List serde
    public static final Serde<List<Route>> ROUTE_LIST_SERDE = Serde.listOf(ROUTE_SERDE, ',');
    //SortedBag of Cards serde
    public static final Serde<SortedBag<Card>> CARD_SORTED_BAG_SERDE = Serde.bagOf(CARD_SERDE, ',');
    //SortedBag of Tickets serde
    public static final Serde<SortedBag<Ticket>> TICKET_SORTED_BAG_SERDE = Serde.bagOf(TICKET_SERDE, ',');
    //List of SortedBag of Cards serde
    public static final Serde<List<SortedBag<Card>>> LIST_SORTED_BAG_CARD_SERDE = Serde.listOf(CARD_SORTED_BAG_SERDE, ';');

    //PublicCardState serde
    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = Serde.of(cardState -> String.format("%s;%s;%s",
            //First serialize the specific serdes for the cardState
            CARD_LIST_SERDE.serialize(cardState.faceUpCards()),
            INTEGER_SERDE.serialize(cardState.deckSize()),
            INTEGER_SERDE.serialize(cardState.discardsSize())),
            //Then deserialize the serdes for the CardState and split them using the given separator
            (string -> {
                String [] tempString = string.split(Pattern.quote(";"), -1);
                return  new PublicCardState(CARD_LIST_SERDE.deserialize(tempString[0]),
                        INTEGER_SERDE.deserialize(tempString[1]), INTEGER_SERDE.deserialize(tempString[2]));
            }));

    //PublicPlayerState serde
    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = Serde.of(publicPlayerState ->
                    String.format("%s;%s;%s",
                        //First serialize the specific serdes for the publicPlayerState
                        INTEGER_SERDE.serialize(publicPlayerState.ticketCount()),
                        INTEGER_SERDE.serialize(publicPlayerState.cardCount()),
                        ROUTE_LIST_SERDE.serialize(publicPlayerState.routes())),

                        //Then deserialize the serdes for the CardState and split them using the given separator
            string -> {
                String[] tempString = string.split(Pattern.quote(";"), -1);

                return tempString[2].isBlank() ?
                        new PublicPlayerState(INTEGER_SERDE.deserialize(tempString[0]),
                                INTEGER_SERDE.deserialize(tempString[1]), new ArrayList<>()):
                        new PublicPlayerState(INTEGER_SERDE.deserialize(tempString[0]),
                        INTEGER_SERDE.deserialize(tempString[1]), ROUTE_LIST_SERDE.deserialize(tempString[2]));
            });

    //PlayerState serde
    public static final Serde<PlayerState> PLAYER_STATE_SERDE = Serde.of(playerState -> String.format("%s;%s;%s",
            //First serialize the specific serdes for the cardState
            TICKET_SORTED_BAG_SERDE.serialize(playerState.tickets()),
            CARD_SORTED_BAG_SERDE.serialize(playerState.cards()),
            ROUTE_LIST_SERDE.serialize(playerState.routes())),

            (string -> {
                String [] tempString = string.split(Pattern.quote(";"), -1);

                SortedBag<Ticket> tickets = SortedBag.of();
                SortedBag<Card> cards = SortedBag.of();
                List<Route> routes = new ArrayList<>();
                //Deserialization part
                if(!tempString[0].isBlank())
                    tickets = TICKET_SORTED_BAG_SERDE.deserialize(tempString[0]);

                if(!tempString[1].isBlank())
                    cards = CARD_SORTED_BAG_SERDE.deserialize(tempString[1]);

                if(!tempString[2].isBlank())
                    routes = ROUTE_LIST_SERDE.deserialize(tempString[2]);

                return new PlayerState(tickets, cards, routes);
            }));

    //PublicGameState serde
    public static final Serde<PublicGameState> PUBLIC_GAME_STATE_SERDE = Serde.of(
            publicGameState -> {
                //Checks whether there was a last player, used in the return on line 123
                String lastPlayer = publicGameState.lastPlayer() == null ?
                        ""                                                     :
                        PLAYER_ID_SERDE.serialize(publicGameState.lastPlayer());

                return String.format("%s:%s:%s:%s:%s:%s",
                        INTEGER_SERDE.serialize(publicGameState.ticketsCount()),
                        PUBLIC_CARD_STATE_SERDE.serialize(publicGameState.cardState()),
                        PLAYER_ID_SERDE.serialize(publicGameState.currentPlayerId()),
                        PUBLIC_PLAYER_STATE_SERDE.serialize(publicGameState.playerState(PlayerId.PLAYER_1)),
                        PUBLIC_PLAYER_STATE_SERDE.serialize(publicGameState.playerState(PlayerId.PLAYER_2)),
                        lastPlayer);
                },

            string -> {
                String [] tempString = string.split(Pattern.quote(":"), -1);
                Map<PlayerId, PublicPlayerState> playerStateMap = new EnumMap<>(PlayerId.class);
                playerStateMap.put(PlayerId.PLAYER_1, PUBLIC_PLAYER_STATE_SERDE.deserialize(tempString[3]));
                playerStateMap.put(PlayerId.PLAYER_2, PUBLIC_PLAYER_STATE_SERDE.deserialize(tempString[4]));
                PlayerId lastPlayer = tempString[5].equals("0") || tempString[5].equals("1") ?
                        PLAYER_ID_SERDE.deserialize(tempString[5]):
                        null;

                return new PublicGameState(
                        INTEGER_SERDE.deserialize(tempString[0]),
                        PUBLIC_CARD_STATE_SERDE.deserialize(tempString[1]),
                        PLAYER_ID_SERDE.deserialize(tempString[2]),
                        playerStateMap, lastPlayer
                );

            });
}





