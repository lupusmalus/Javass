package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Rank;

/**
 * The class PackedCard represents a standard card of a game of Jass. The information of this card is represented in the form of a bitstring of length 32, With the bits 0 to 3
 * representing the rank of the card (SIX to ACE, in increasing order), the bits 4 to 5 represent the color of the card (SPADES, HEART, DIAMOND, CLUB), the rest of the bits being
 * strictly 0
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 * @see Card
 * 
 */
public final class PackedCard {

    /**
     * Represents a PackedCard with an invalid value
     */
    public final static int INVALID = 0x3F;

    // The starting indices and the corresponding lengths of the sub-bitstrings representing the cards information contained in the bitstring
    private final static int START_RANK = 0, 
                             SIZE_RANK = 4,
                             START_COLOR = 4, 
                             SIZE_COLOR = 2,
                             START_UNUSED = 6, 
                             SIZE_UNUSED = Integer.SIZE - SIZE_RANK - SIZE_COLOR,
                             UNUSED_BITS = 0;

    /**
     * The class PackedCard is not instantiable
     */
    private PackedCard() {
    }

    /**
     * Determines if the given integer represents a valid card, the rank must be between 0(included) and 8 (included), the unused bits must be 0
     * 
     * @param pkCard (int): The bitstring to validate
     * @return (boolean): true when the given bitstring is valid, false otherwise
     */
    public static boolean isValid(int pkCard) {
        // extracts the rank as well as the bits not used to represent any information
        int rank = Bits32.extract(pkCard, START_RANK, SIZE_RANK);
        int unused = Bits32.extract(pkCard, START_UNUSED, SIZE_UNUSED);

        // verifies that the rank represented by the pkCard is within bounds as well as the unused bits have the corresponding values
        return (rank < Rank.COUNT && unused == UNUSED_BITS);
    }

    /**
     * Creates a bitstring that represents the given card in packed version, the rank is represented in the four LSB of the packed card, the color is represented in the two
     * following bits
     * 
     * @param c (Card.Color): The color of the card
     * @param r (Card.Rank): The rank of the card
     * @return (int): The given Card in form of a packed card
     */
    public static int pack(Card.Color c, Card.Rank r) {
        return Bits32.pack(r.ordinal(), SIZE_RANK, c.ordinal(), SIZE_COLOR);
    }

    /**
     * Returns the color of a card given as a packed card
     * 
     * @param pkCard (int): The card represented as an packed card, must be valid
     * @return (Card.Color): The encoded color
     */
    public static Card.Color color(int pkCard) {
        assert isValid(pkCard);

        int color = Bits32.extract(pkCard, START_COLOR, SIZE_COLOR);
        return Card.Color.ALL.get(color);
    }

    /**
     * Returns the rank of a card given as a packed card
     * 
     * @param pkCard (int): The card represented as a packed card, must be valid
     * @return (Card.Rank): The encoded rank
     */
    public static Card.Rank rank(int pkCard) {
        assert isValid(pkCard);

        int rank = Bits32.extract(pkCard, START_RANK, SIZE_RANK);
        return Card.Rank.ALL.get(rank);
    }

    /**
     * Determines is pkCardL is better than pkCardR, according to the rules of Jass
     * 
     * @param trump (Card.Color): the color that is currently trump
     * @param pkCardL (int): the first card to inspect, must be valid
     * @param pkCardR (int): the second card to inspect, must be valid
     * @return (boolean): true if both cards are comparable and pkCardL is better, false otherwise
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {
        assert isValid(pkCardL) && isValid(pkCardR);

        int rankCardL = Bits32.extract(pkCardL, START_RANK, SIZE_RANK);
        int rankCardR = Bits32.extract(pkCardR, START_RANK, SIZE_RANK);

        boolean sameColor = color(pkCardL).equals(color(pkCardR));

        // If none of the colors is trump, the standard rules of Jass apply; either the cards are of different color and hence not comparable, or they are of equal color and their
        // ranks are compared
        if (!color(pkCardL).equals(trump) && !color(pkCardR).equals(trump)) 
            return sameColor && (rankCardL > rankCardR);
        // If at least one card is trump, different rules apply: if they both are trump, their trumpOrdinal value is compared, else, the card that is trump wins the round
        else 
            return sameColor ? rank(pkCardL).trumpOrdinal() > rank(pkCardR).trumpOrdinal() : color(pkCardL).equals(trump);
    }

    /**
     * Returns the points of a given card, according to the rules of Jass
     * 
     * @param trump (Card.Color): The color that is currently trump
     * @param pkCard (int): The card to inspect
     * @return The points of the given card
     */
    public static int points(Card.Color trump, int pkCard) {
        assert isValid(pkCard);
        Card.Rank rank = rank(pkCard);
        
        if (color(pkCard).equals(trump)) {
            switch (rank) {
            case NINE:
                return 14;
            case TEN:
                return 10;
            case JACK:
                return 20;
            case QUEEN:
                return 3;
            case KING:
                return 4;
            case ACE:
                return 11;

            default:
                return 0;
            }
        } 
        else {
            switch (rank) {
            case TEN:
                return 10;
            case JACK:
                return 2;
            case QUEEN:
                return 3;
            case KING:
                return 4;
            case ACE:
                return 11;

            default:
                return 0;
            }
        }
    }

    /**
     * Returns a String containing the color and rank of a given packed card
     * 
     * @param pkCard (int): The card to represent, must be valid
     * @return (String): The String representing the card
     */
    public static String toString(int pkCard) {
        assert isValid(pkCard);

        return new StringBuilder(color(pkCard).toString())
                   .append(rank(pkCard).toString())
                   .toString();
    }
}