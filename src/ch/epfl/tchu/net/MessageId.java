package ch.epfl.tchu.net;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public enum MessageId {

    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS;

    public static final List<MessageId> ALL = List.of(INIT_PLAYERS, RECEIVE_INFO, UPDATE_STATE, SET_INITIAL_TICKETS, CHOOSE_INITIAL_TICKETS,
            NEXT_TURN, CHOOSE_TICKETS, DRAW_SLOT, ROUTE, CARDS, CHOOSE_ADDITIONAL_CARDS);
    public static final int COUNT = ALL.size();

    public static final Map<MessageId, String> nameMap = MessageId.ALL.stream().collect(Collectors.toMap(mID -> mID, Enum::name));
    public static final List<String> nameList = MessageId.ALL.stream().map(Object::toString).collect(Collectors.toList());

}

