package ch.epfl.javass;

/**
 * The class Preconditions contains methods for verifying that the requirements of certain preconditions of other methods are met.
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 * 
 */
public final class Preconditions {

    /**
     * the class Preconditions is not instantiable
     */
    private Preconditions() {
    }

    /**
     * Checks whether a boolean statement is true or not, and throws an IllegalArgumentException in the latter case
     * 
     * @param b (boolean): the statement to check
     * @throws IllegalArgumentException if the statement is false
     */
    public static void checkArgument(boolean b) {
        if (!b)
            throw new IllegalArgumentException();

    }

    /**
     * Checks whether an integer is contained in the interval of 0(included) and the its size (included)
     * 
     * @param index (int): the integer to verify
     * @param size (int): the desired size
     * @return the verified index
     * @throws IllegalArgumentException if the integer is not in the interval
     */
    public static int checkIndex(int index, int size) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException();

        return index;

    }
}