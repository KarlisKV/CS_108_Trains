package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
/**
 * Serde interface used to serialize and deserialize information during the game to
 * be able to transfer information using the client
 * @author Karlis Velins (325180)
 */
public interface Serde<T> {

    /**
     * Abstract serialize method
     * @param object (T) an object to serialize
     * @return returns the corresponding string
     */
    String serialize(T object);

    /**
     * Abstract deserialize method
     * @param string respective String
     * @return the corresponding Object
     */
    T deserialize(String string);

    /**
     * Generic method 'of'
     * @param serialization a serialization function
     * @param deserialization a deserialization function
     * @return the created Serde
     */
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

    /**
     * oneOf method creates a serde taking the list of all of the values of an enum
     * @param all (List<T>) of a set of enumerated values
     * @return the Serde
     */
    static <T> Serde<T> oneOf(List<T> all) {
        Function<T,String> serialize = s -> Integer.toString(all.indexOf(s));
        Function<String, T> deserialize = d -> all.get(Integer.parseInt(d));
        return Serde.of(serialize, deserialize);
    }

    /**
     * listOf method for de (serializing) lists of values by the given serde
     * @param serde (Serde) given Serde
     * @param separator (Char) given separator for the serde
     * @return a serde capable of (de) serializing lists of values (de) serialized by the given serde,
     */
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

    /**
     * Same as listOf but instead of a list it returns a SortedBag
     * @param serde (Serde) given Serde
     * @param separator (Char) separator used
     * @return  a SortedBag of a serde capable of (de) serializing lists of values
     */
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
