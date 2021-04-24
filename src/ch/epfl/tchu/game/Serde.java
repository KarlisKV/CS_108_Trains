package ch.epfl.tchu.game;

import java.util.function.Function;

public interface Serde<O> {

    String serialize(O object);
    O deserialize(String s);



    default Serde of(Function<O, String> serialised, Function<String, O> deserialised) {


        return null;
    }

}
