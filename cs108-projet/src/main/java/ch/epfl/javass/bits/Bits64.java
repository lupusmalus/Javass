package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * The utility class Bits64 contains methods for operations on integers in their 64-bitstring form, such as extracting a certain part of the bitstring, or creating a mask.
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 * 
 */
public final class Bits64 {

    /**
     * The class Bits64 is not instantiable
     */
    private Bits64() {
    }

    /**
     * Creates a "mask": the bits from the start to start + size all have the value 1, the rest of the bits being 0
     * 
     * 
     * @param start (int): the starting bit of the mask (included), must be non-negative
     * @param size (int): the number of desired bits of the mask with value 1, must be non-negative and at most 64
     * @return The desired mask
     * @throws  IllegalArgumentException if any of the arguments is invalid
     */
    public static long mask(int start, int size) {
        Preconditions.checkArgument(start >= 0 && (start + size) <= Long.SIZE && size >= 0);
        
        // First case due to the two's complement representation of the primitive type long
        return size == Long.SIZE? -1L : ((1L << size) - 1) << start;
    }

    /**
     * 
     * Extracts a bitstring of length size out of another integer
     * 
     * @param bits (int): The original integer
     * @param start (int): The starting index of the bitstring to extract, must be non-negative
     * @param size (int): the length of the bitstring to extract, must be non-negative
     * @return a new bitstring with the extracted bitstring occupying the LSB, while the rest of the bitstring has the value 0.
     * @throws IllegalArgumentException if any of the arguments is invalid
     */
    public static long extract(long bits, int start, int size) {
        Preconditions.checkArgument(start >= 0 && size >= 0 && (start + size) <= Long.SIZE);
        return (bits & mask(start, size)) >>> start;
    }

    /**
     * Packs several bitstrings of different integers into a single bitstring. The integer vi gets packed into si bits of the new bitstring, starting from the LSB with v1 being the
     * rightmost, v2 being the following bitstring and the rest being 0
     * 
     * Each vi must be able to be represented by si bits and each si must be greater than 0.
     * 
     * @param v1 (int): the 1st integer to pack, must be non-negative and able to be represented by s1 bits
     * @param s1 (int): the number of bits to pack the 1st integer into, must be non-negative
     * @param v2 (int): the 2nd integer to pack, must be non-negative and able to be represented by s2 bits
     * @param s2 (int): the number of bits to pack the 2nd integer into, must be non-negative
     * @return the newly created pack
     * @throws  IllegalArgumentException if any of the arguments is invalid
     */
    public static long pack(long v1, int s1, long v2, int s2) {
        boolean allPairsValid = check(v1, s1) && check(v2, s2);
        Preconditions.checkArgument(allPairsValid && (s1 + s2) <= Long.SIZE);

        // shifts the second number as to leave the s1 LSB to represent the first number
        long a1 = (v2 << s1);
        long pack = (a1 | v1);

        return pack;
    }

    /**
     * Auxiliary method used for the method pack of Bits64 to verify that an integer can be packed into the given number of bits
     * 
     * @param v1 (long): The integer to be packed
     * @param s1 (int): The number of bits for the integer to be packed into
     * @return (boolean): true if the integer can be represented by the desired number of bits, false otherwise
     */
    private static boolean check(long v, int s) {
        // checks whether the size ranges from 0 to 64
        boolean sizeWithinBounds = (s > 0 && s < Long.SIZE);
        
        // checks whether v1 can be represented by s1 bits in unsigned binary representation
        boolean v1TooLargeForBits = v >= (1L << s) && (s != Long.SIZE - 1 || v > mask(0, Long.SIZE - 1));

        return sizeWithinBounds && !v1TooLargeForBits;
    }
}
