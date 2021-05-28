package ch.epfl.tchu;

/**
 * Preconditions class, is only used to check certain arguments passed to certain methods
 * and throw an IllegalArgumentException if that method shouldn't be called with that/those argument(s)
 * @author Karlis Velins (325180)
 */
public final class Preconditions {

    /**
     * Preconditions class not to be instantiated
     */
    private Preconditions() {}

    /**
     * method checkArgument checks whether argument is true. Throws IllegalArgumentExceptions if argument not true
     * @param shouldBeTrue (Boolean)
     */
    public static void checkArgument(boolean shouldBeTrue) { if (!shouldBeTrue) throw new IllegalArgumentException(); }

}
