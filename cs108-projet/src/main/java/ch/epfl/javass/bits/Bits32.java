package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * The utility class Bits32 contains methods for operations of integers in their 32-bitstring form such as extracting a certain part of the bitstring, or creating a mask.
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 * 
 */
public final class Bits32 {

    /**
     * The class Bits32 is not instantiable
     */
    private Bits32() {
    }

    /**
     * Creates a "mask": the bits from the start to start + size all have the value 1, the rest of the bits being 0
     * 
     * 
     * @param start (int): the starting bit of the mask (included), must be non-negative
     * @param size (int): the number of desired bits of the mask with value 1, must be non-negative and at most 32
     * @return The desired mask
     * @throws  IllegalArgumentException if any of the arguments is invalid
     */
    public static int mask(int start, int size) {
        Preconditions.checkArgument(start >= 0 && (start + size) <= Integer.SIZE && size >= 0);
        
        //First case: due to the two's complement representation of the primitive type int
        return size == Integer.SIZE ? -1 : ((1 << size) - 1) << start;
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
    public static int extract(int bits, int start, int size) {
        Preconditions.checkArgument(start >= 0 && size >= 0 && (start + size) <= Integer.SIZE);
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
     * 
     */
    public static int pack(int v1, int s1, int v2, int s2) {
        boolean allPairsValid = check(v1, s1) && check(v2, s2);
        Preconditions.checkArgument(allPairsValid && (s1 + s2) <= Integer.SIZE);

        // shifts the second number as to leave the s1 LSB to represent the first number
        int a1 = (v2 << s1);
        int pack = (a1 | v1);

        return pack;
    }

    /**
     * Packs several bitstrings of different integers into a single bitstring. The integer vi gets packed into si bits of the new bitstring, starting from the LSB with v1 being the
     * rightmost, v2 being the following bitstring, v3 being the following bitstring, with the remaining bits being 0
     * 
     * Each vi must be able to be represented by si bits and each si must be greater than 0.
     * 
     * @param v1 (int): the 1st integer to pack, must be non-negative and able to be represented by s1 bits
     * @param s1 (int): the number of bits to pack the 1st integer into, must be non-negative
     * @param v2 (int): the 2nd integer to pack, must be non-negative and able to be represented by s2 bits
     * @param s2 (int): the number of bits to pack the 2nd integer into, must be non-negative
     * @param v3 (int): the 3rd integer to pack, must be non-negative and able to be represented by s3 bits
     * @param s3 (int): the number of bits to pack the 3rd integer into, must be non-negative
     * @return the newly created pack
     * @throws  IllegalArgumentException if any of the arguments is invalid
     * 
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3) {
        boolean allPairsValid = check(v1, s1) && check(v2, s2) && check(v3, s3);
        Preconditions.checkArgument(allPairsValid && (s1 + s2 + s3) <= Integer.SIZE);

        int a1 = (v3 << s1 + s2);
        int a2 = (v2 << s1);

        int pack = (a1 | a2 | v1);

        return pack;
    }

    /**
     * Packs several bitstrings of different integers into a single bitstring.
     * 
     * The integer vi gets packed into si bits of the new bitstring, starting from the LSB with v1 being the rightmost, v2 being the following bitstring, v3 being the following
     * bitstring, etc... with the remaining bits being 0
     * 
     * 
     * Each vi must be able to be represented by si bits and each si must be greater than 0.
     * 
     * @param v1 (int): the 1st integer to pack, must be non-negative and able to be represented by s1 bits
     * @param s1 (int): the number of bits to pack the 1st integer into, must be non-negative
     * @param v2 (int): the 2nd integer to pack, must be non-negative and able to be represented by s2 bits
     * @param s2 (int): the number of bits to pack the 2nd integer into, must be non-negative
     * @param v3 (int): the 3rd integer to pack, must be non-negative and able to be represented by s3 bits
     * @param s3 (int): the number of bits to pack the 3rd integer into, must be non-negative
     * @param v4 (int): the 4th integer to pack, must be non-negativend able to be represented by s4 bits
     * @param s4 (int): the number of bits to pack the 4th integer into, must be non-negative
     * @param v5 (int): the 5th integer to pack, must be non-negativend able to be represented by s5 bits
     * @param s5 (int): the number of bits to pack the 5th integer into, must be non-negative
     * @param v6 (int): the 6th integer to pack, must be non-negativend able to be represented by s6 bits
     * @param s6 (int): the number of bits to pack the 6th integer into, must be non-negative
     * @param v7 (int): the 7th integer to pack, must be non-negativend able to be represented by s7 bits
     * @param s7 (int): the number of bits to pack the 7th integer into, must be non-negative
     * @return the newly created pack
     * @throws IllegalArgumentException if any of the arguments is invalid
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3, int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7) {
        boolean allPairsValid = check(v1, s1) && check(v2, s2) && check(v3, s3) && check(v4, s4) && check(v5, s5) && check(v6, s6) && check(v7, s7);
        Preconditions.checkArgument(allPairsValid && (s1 + s2 + s3 + s4 + s5 + s6 + s7) <= Integer.SIZE);

        int a1 = (v7 << s1 + s2 + s3 + s4 + s5 + s6);
        int a2 = (v6 << s1 + s2 + s3 + s4 + s5);
        int a3 = (v5 << s1 + s2 + s3 + s4);
        int a4 = (v4 << s1 + s2 + s3);
        int a5 = (v3 << s1 + s2);
        int a6 = (v2 << s1);

        int pack = (a1 | a2 | a3 | a4 | a5 | a6 | v1);

        return pack;
    }

    /**
     * Auxiliary method used for the method pack of Bits32 to verify that an integer can be packed into the given number of bits
     * 
     * @param v (int): The integer to be packed
     * @param s (int): The number of bits for the integer to be packed into
     * @return (boolean): true if the integer can be represented by the desired number of bits, false otherwise
     */
    private static boolean check(int v, int s) {
        //checks whether the size ranges from 1 to 32
        boolean sizeWithinBounds = (s > 0 && s < Integer.SIZE);
        
        //checks whether v1 can be represented by s1 bits in unsigned binary representation 
        boolean v1TooLargeForBits = v >= (1 << s) && (s != Integer.SIZE - 1 || v > mask(0, Integer.SIZE - 1));

        return sizeWithinBounds && !v1TooLargeForBits;
    }
}