package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class Serdes {

    public static final Serde<Integer> INTEGER_SERDE = Serde.of((Objects::toString), (Integer::parseInt));

    public static final Serde<String> STRING_SERDE = Serde.of((string -> Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8))),
                    ((base64 -> new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8))));

    public static final Serde<PlayerId> PLAYER_ID_SERDE = Serde.oneOf(PlayerId.ALL);

    public static final Serde<Player.TurnKind> TURN_KIND_SERDE = Serde.oneOf(Player.TurnKind.ALL);

    public static final Serde<Card> CARD_SERDE = Serde.oneOf(Card.ALL);

    public static final Serde<Route> ROUTE_SERDE = Serde.oneOf(ChMap.routes());
    public static final Serde<Ticket> TICKET_SERDE = Serde.oneOf(ChMap.tickets());


    public static final Serde<List<String>> STRING_LIST_SERDE = Serde.listOf(STRING_SERDE,',');
    public static final Serde<List<Card>> CARD_LIST_SERDE = Serde.listOf(CARD_SERDE, ',');
    public static final Serde<List<Ticket>> TICKET_LIST_SERDE = Serde.listOf(TICKET_SERDE, ',');
    public static final Serde<List<Route>> ROUTE_LIST_SERDE = Serde.listOf(ROUTE_SERDE, ',');

    public static final Serde<SortedBag<Card>> CARD_SORTED_BAG_SERDE = Serde.bagOf(CARD_SERDE, ',');
    public static final Serde<SortedBag<Ticket>> TICKET_SORTED_BAG_SERDE = Serde.bagOf(TICKET_SERDE, ',');
    public static final Serde<List<SortedBag<Card>>> LIST_SORTED_BAG_CARD_SERDE = Serde.listOf(CARD_SORTED_BAG_SERDE, ';');

    public static final Serde<PublicCardState> PUBLIC_CARD_STATE_SERDE = Serde.of(cardState -> String.format("%s; %s; %s",
            CARD_LIST_SERDE.serialize(cardState.faceUpCards()),
            INTEGER_SERDE.serialize(cardState.deckSize()),
            INTEGER_SERDE.serialize(cardState.discardsSize())),

            (string -> {
                String [] tempString = string.split(Pattern.quote(";"), -1);
                return  new PublicCardState(CARD_LIST_SERDE.deserialize(tempString[0]),
                        INTEGER_SERDE.deserialize(tempString[1]), INTEGER_SERDE.deserialize(tempString[2]));
            }));


    public static final Serde<PublicPlayerState> PUBLIC_PLAYER_STATE_SERDE = Serde.of(publicPlayerState -> String.format("%s; %s; %s",
            INTEGER_SERDE.serialize(publicPlayerState.ticketCount()),
            INTEGER_SERDE.serialize(publicPlayerState.cardCount()),
            ROUTE_LIST_SERDE.serialize(publicPlayerState.routes())),

            (string -> {
                String[] tempString = string.split(Pattern.quote(";"), -1);
                return new PublicPlayerState(INTEGER_SERDE.deserialize(tempString[0]),
                        INTEGER_SERDE.deserialize(tempString[1]), ROUTE_LIST_SERDE.deserialize(tempString[2]));
            }));

    public static final Serde<PlayerState> PLAYER_STATE_SERDE = Serde.of(playerState -> String.format("%s; %s; %s",
            TICKET_SORTED_BAG_SERDE.serialize(playerState.tickets()),
            CARD_SORTED_BAG_SERDE.serialize(playerState.cards()),
            ROUTE_LIST_SERDE.serialize(playerState.routes())),

            (string -> {
                String [] tempString = string.split(Pattern.quote(";"), -1);
                return new PlayerState(TICKET_SORTED_BAG_SERDE.deserialize(tempString[0]),
                        CARD_SORTED_BAG_SERDE.deserialize(tempString[1]), ROUTE_LIST_SERDE.deserialize(tempString[2]));
            }));





}





