package ch.epfl.tchu;

public final class Preconditions {

    /**
     * Private constructor for Preconditions
     */
    private Preconditions() {}

    /**
     * method checkArgument checks whether argument is true. Throws IllegalArgumentExceptions if argument not true
     * @param shouldBeTrue (Boolean)
     */
    public static void checkArgument(boolean shouldBeTrue) { if (!shouldBeTrue) throw new IllegalArgumentException(); }

}
