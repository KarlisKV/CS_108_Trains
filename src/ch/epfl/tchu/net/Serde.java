package ch.epfl.tchu.net;

import java.util.List;
import java.util.function.Function;

public interface Serde<T> {

    String serialize(T object);

    Serde<T> deserialize(String s);



}
