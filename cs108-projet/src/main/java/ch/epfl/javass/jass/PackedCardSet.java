package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * The class PackedCardSet represents a set of cards, ranging from an empty set to a full set of all 36 cards in a game of Jass. The set of cards is represented as a bitstring of
 * length 64, where a bit is 1 when the card is in the set and 0 when it isn't. The bit at index i represents a card which has a PackedCard value i. Between the according sets of
 * cards of each color, exactly 9 unused bits are inserted, all at value 0.
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 * @see PackedCard
 * @see CardSet
 */
public class PackedCardSet {

    /**
     * Represents an empty CardSet
     */
    public final static long EMPTY = 0L;
    
    /**
     * Represents a CardSet containing all cards
     */
    public final static long ALL_CARDS = 0x01FF_01FF_01FF_01FFL;

    // table of bitstrings each representing a color subset, that is to say a subset of all cards of same color and distinct rank
    private final static long[] COLOR_SUBSETS = getColorSubsets();
    
    // bidimensional table of bitstrings, containing a cardset whose cards' trump ordinal is higher for every card
    private final static long[][] CARDS_ABOVE = getCardsAbove();
    
    /**
     * The class packedCardSet is not instantiable
     */
    private PackedCardSet() {
        
    }
    
    /**
     * Determines if a bitstring is a valid PackedCardSet. Checks if all of the unused bits are 0.
     * 
     * @param pkCardSet (long): The card set to be checked
     * @return true iff all the unused bits are 0, false otherwise
     */
    public static boolean isValid(long pkCardSet) {
        return (~ALL_CARDS & pkCardSet) == 0;
    }

    
    /**
     * Returns a PackedCardSet of all cards that are above the card in question, when the color of this card is trump.
     * 
     * @param pkCard (int): The card in question
     * @return (long): the PackedCardSet of all cards of the same color (trump) that are higher
     */
    public static long trumpAbove(int pkCard) {
        assert (PackedCard.isValid(pkCard));

        return CARDS_ABOVE[PackedCard.color(pkCard).ordinal()][PackedCard.rank(pkCard).ordinal()];
    }

    /**
     * Returns a PackedCardSet where only the bit corresponding to the card in question is 1
     * 
     * @param pkCard (int): the card in question
     * @return (long): The PackedCardSet with only the single card in it
     */
    public static long singleton(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return 1L << pkCard;
    }

    /**
     * Determines if the PackedCardSet is empty
     * 
     * @param pkCardSet (long): the PackedCardSet in question
     * @return (boolean): true iff the set is the empty PackedCardSet
     */
    public static boolean isEmpty(long pkCardSet) {
        assert isValid(pkCardSet);
        return pkCardSet == EMPTY;
    }

    /**
     * Determines the number of cards in the PackedCardSet, by finding the number of bits that are equal to one
     * 
     * @param pkCardSet (long): the PackedCardSet in question
     * @return (int): the number of cards in the PackedCardSet
     */
    public static int size(long pkCardSet) {
        assert isValid(pkCardSet);
        return Long.bitCount(pkCardSet);
    }

    /**
     * Gives the packed card value of the card at given index which is contained in a given PackedCardSet
     * 
     * @param pkCardSet (long): the PackedCardSet in question
     * @param index (int) : the index of the Card to be returned
     * @return (int): The corresponding packed card value of the card
     * @see PackedCard
     */
    public static int get(long pkCardSet, int index) {
        assert (index >= 0 && size(pkCardSet) - index > 0 && isValid(pkCardSet));

        // sets the cardsets bits whose value is 1 to 0 until the the leftmost bit whose value is 1 is the one desired by the index
        for (int i = 0; i < index; i++)
            pkCardSet = pkCardSet & ~Long.lowestOneBit(pkCardSet);

        return Long.numberOfTrailingZeros(pkCardSet);
    }

    /**
     * Adds a packed card to a PackedCardSet, where the PackedCardSet remains the same if the card is already contained
     * 
     * @param pkCardSet (long): the PackedCardSet to which the card is to be added
     * @param pkCard (int): the packed card to be added
     * @return (long): the PackedCardSet with the packed card added to it
     * @see PackedCard
     */
    public static long add(long pkCardSet, int pkCard) {
        assert (isValid(pkCardSet) && PackedCard.isValid(pkCard));
        return pkCardSet | singleton(pkCard);
    }

    /**
     * Removes a packed card from a PackedCardSet, where the PackedCardSet remains the same if the card isn't contained
     * 
     * @param pkCardSet (long): the PackedCardSet from which the card is to be removed
     * @param pkCard (int): the packed card to be removed
     * @return (long): the PackedCardSet without the card removed
     * @see PackedCard
     */
    public static long remove(long pkCardSet, int pkCard) {
        assert (isValid(pkCardSet) && PackedCard.isValid(pkCard));
        return pkCardSet & ~singleton(pkCard);
    }

    /**
     * Determines if a given packed card is contained in a PackedCardSet or not
     * 
     * @param pkCardSet (long): the PackedCardSet in question
     * @param pkCard (int): the PackedCardSet in question
     * @return (boolean): true if the card is contained, false otherwise
     * @see PackedCard
     */
    public static boolean contains(long pkCardSet, int pkCard) {
        assert (isValid(pkCardSet) && PackedCard.isValid(pkCard));
        return pkCardSet == add(pkCardSet, pkCard);
    }

    /**
     * Returns a PackedCardSet that is the exact complement of the given PackedCardSet
     * 
     * @param pkCardSet (long): the original PackedCardSet
     * @return (long): the complement of the original PackedCardSet
     */
    public static long complement(long pkCardSet) {
        assert isValid(pkCardSet);
        long complement = ~pkCardSet;
        
        // need to intersect with all card as to not include the unused bits
        return complement & ALL_CARDS;
    }

    /**
     * Returns the union of two PackedCardSet
     * 
     * @param pkCardSet1 (long): the first PackedCardSet
     * @param pkCardSet2 (long): the second PackedCardSet
     * @return (long): the resulting PackedCardSet from uniting the two sets
     */
    public static long union(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1) && isValid(pkCardSet2);
        return pkCardSet1 | pkCardSet2;
    }

    /**
     * Returns the intersection of two PackedCardSets
     * 
     * @param pkCardSet1 (long): the first PackedCardSet
     * @param pkCardSet2 (long): the second PackedCardSet
     * @return (long): the intersection of the two PackedCardSets
     */
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1) && isValid(pkCardSet2);
        return pkCardSet1 & pkCardSet2;
    }

    /**
     * Returns a PackedCardSet containing the difference between two PackedCardSets
     * 
     * @param pkCardSet1 (long): the first PackedCardSet
     * @param pkCardSet2 (long): the second PackedCardSet
     * @return (long): the difference between the two PackedCardSets
     */
    public static long difference(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1) && isValid(pkCardSet2);
        return pkCardSet1 & complement(pkCardSet2);
    }

    /**
     * Returns a PackedCardSet that contains just the cards of a given color that are a subset of the given PackedCardSet. Uses an auxiliary table to ensure fast access
     * 
     * @param pkCardSet (long): The PackedCardSet in question
     * @param color (Card.Color): the color of the cards wanted
     * @return (long): The PackedCardSet with only the cards of the specified color in it
     */
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        assert isValid(pkCardSet);
        return intersection(pkCardSet, COLOR_SUBSETS[color.ordinal()]);
    }

    /**
     * Returns a textual representation of all the cards currently in a PackedCardSet
     * 
     * @param pkCardSet (long): The PackedCardSet in question
     * @return (String): a textual representation of the PackedCardSet, representing all the cards contained
     */
    public static String toString(long pkCardSet) {
        StringJoiner j = new StringJoiner(",", "{", "}");

        while (!isEmpty(pkCardSet)) {
            int pkCard = get(pkCardSet, 0);
            
            j.add(PackedCard.toString(pkCard));
            pkCardSet = remove(pkCardSet, pkCard);
        }

        return j.toString();
    }

    /**
     * Auxiliary method that is called only once to initialize the table CARDS_ABOVE
     * The corresponding packed cardsets are stored by their colors ordinal in the first dimension,
     * and their rank's ordinal in the second dimension
     * 
     * @return the table containing for each card the cards that are above it, assuming the color of the card is trump
     */
    private static long[][] getCardsAbove() {
        
        long allPkCards = ALL_CARDS;
        long[][] trumpAbove = new long[Color.COUNT][Rank.COUNT];

        for (int i = 0; i < Color.COUNT * Rank.COUNT; i++) {
         
            //the card to be tested
            int testedPkCard = get(allPkCards, 0);
            allPkCards = remove(allPkCards, testedPkCard);

            Color trumpColor = PackedCard.color(testedPkCard);
            Rank rankPkCard = PackedCard.rank(testedPkCard);

            long trumpSet = EMPTY;
            long colorSubset = COLOR_SUBSETS[trumpColor.ordinal()];

            // compares the tested card to all the cards of its corresponding colorsubset,
            // adds them to the trumpset if their ordinal is strictly greater than the one of the tested card
            for (int j = 0; j < Rank.COUNT; j++) {
                int pkTrumpCard = get(colorSubset, 0);
                colorSubset = remove(colorSubset, pkTrumpCard);

                if (PackedCard.isBetter(trumpColor, pkTrumpCard, testedPkCard)) 
                    trumpSet = add(trumpSet, pkTrumpCard);
            }
            
            trumpAbove[trumpColor.ordinal()][rankPkCard.ordinal()] = trumpSet;
        }
        return trumpAbove;
    }

    /**
     * Auxiliary method to create the table COLOR_SUBSETS, only called once
     * 
     * @return a table containing all subsets of the different colors of a PackedCardSet
     */
    private static long[] getColorSubsets() {
        
        long[] colorSubsets = new long[Color.COUNT];
        
        for(int i = 0; i < Color.COUNT; ++i) {
            colorSubsets[i] = Bits64.mask(i * (Long.SIZE / Color.COUNT), Rank.COUNT);
        }
        
        
        return colorSubsets;
    }
}