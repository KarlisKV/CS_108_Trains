package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public interface Serde<T> {

    String serialize(T object);

    T deserialize(String string);

    static <T> Serde<T> of(Function<T, String> serialization, Function<String, T> deserialization) {

        return new Serde<>() {
            @Override
            public String serialize(T object) {
                return serialization.apply(object);
            }

            @Override
            public T deserialize(String string) {
                return deserialization.apply(string);
            }
        };
    }

    static <T> Serde<T> oneOf(List<T> all) {
        Function<T,String> serialize = s  -> Integer.toString(all.indexOf(s));
        Function<String, T> deserialize = d -> all.get(Integer.parseInt(d));
        return Serde.of(serialize, deserialize);
    }


    static <T> Serde<List<T>> listOf(Serde<T> serde, char separator) {

        return new Serde<>() {


            @Override
            public String serialize(List<T> object) {
                ArrayList<String> tempList = new ArrayList<>();
                for (T t : object) {
                    tempList.add(serde.serialize(t));
                }

                return String.join(String.valueOf(separator), tempList);
            }

            @Override
            public List<T> deserialize(String string) {
                List<T> tempList = new ArrayList<>();
                String [] tempString = string.split(Pattern.quote(String.valueOf(separator)), -1);

                for(String s: tempString) {
                    tempList.add(serde.deserialize(s));
                }

                return tempList;
            }
        };
    }


    static <T extends Comparable<T>> Serde <SortedBag<T>> bagOf(Serde <T> serde, char separator) {

        return new Serde<>() {
            @Override
            public String serialize(SortedBag<T> object) {

                ArrayList<String> tempList = new ArrayList<>();
                for (T t : object) {
                    tempList.add(serde.serialize(t));
                }
                return String.join(String.valueOf(separator), tempList);
            }

            @Override
            public SortedBag<T> deserialize(String string) {

                List<T> tempList = new ArrayList<>();
                String [] tempString = string.split(Pattern.quote(String.valueOf(separator)), -1);

                for(String s: tempString) {
                    tempList.add(serde.deserialize(s));
                }
                return SortedBag.of(tempList);
            }
        };
    }


}
