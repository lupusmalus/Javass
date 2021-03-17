package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * The class Card represents a card of a standard game of Jass, with each Card having a rank, ranging from six to ace, such as a color (spade, heart, diamond or club)
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 * 
 * @see PackedCard
 */
public final class Card {

    /**
     * Represents the color of a Card in a game of Jass
     * 
     * @author Hannah Laureen Casey (300981)
     * @author Erik Alessandro Wengle (297099)
     *
     */
    public enum Color {

        SPADE("\u2660"), 
        HEART("\u2661"), 
        DIAMOND("\u2662"),
        CLUB("\u2663");

        /**
         * A List containing all of the Colors
         */
        public final static List<Color> ALL = Collections.unmodifiableList(Arrays.asList(values()));
        
        /**
         * A List containing the number of distinct colors in a game of Jass
         */
        public final static int COUNT = ALL.size();
        
        private final String symbol;

        /**
         * The standard constructor of Color
         * 
         * @param symbol (String): The textual representation of the symbol of the given color
         */
        private Color(String symbol) {
            this.symbol = symbol;
        }

        /**
         * Returns the unicode-escape representation of the symbol
         * 
         * @returns the corresponding symbol
         */
        @Override
        public String toString() {
            return symbol;
        }
    }

    /**
     * Represents a rank of a Card in a game of Jass
     * 
     * @author Hannah Laureen Casey (300981)
     * @author Erik Alessandro Wengle (297099)
     *
     */
    public enum Rank {

        SIX("6", 0), 
        SEVEN("7", 1), 
        EIGHT("8", 2),
        NINE("9", 7),
        TEN("10", 3),
        JACK("J", 8),
        QUEEN("Q", 4), 
        KING("K", 5), 
        ACE("A", 6);

        /**
         * A list containing all of the distinct ranks
         */
        public final static List<Rank> ALL = Collections.unmodifiableList(Arrays.asList(values()));
        
        /**
         * The total number of distinct ranks
         */
        public final static int COUNT = ALL.size();
        
        private final String name;
        private final int trumpOrdinal;

        /**
         * The standard Rank constructor
         * 
         * @param (String): The textual representation of the rank
         */
        private Rank(String s, int t) {
            name = s;
            trumpOrdinal = t;
        }

        /**
         * Returns the ordinal of a rank assuming that the given card's color is trump
         * 
         * @return The ordinal of the card according to the rules of Jass
         */
        public int trumpOrdinal() {
            return trumpOrdinal;
        }

        /**
         * Returns the textual representation of the rank
         * 
         * @returns the alternative representation of the rank
         */
        @Override
        public String toString() {
            return name;
        }
    }
    

    // the packed version of the card, encoded into a bitstring of length 32
    private final int pkCard;
    
    /**
     * Standard constructor of Card
     * 
     * @param c (Color): the color of the card
     * @param r (Rank): the rank of the card
     */
    private Card(Color c, Rank r) {
        pkCard = PackedCard.pack(c, r);
    }

    /**
     * Generates a new Card with the given color and rank
     * 
     * @param c (Color): The desired color
     * @param r (Rank): The desired Rank
     * @return the new Card
     */
    public static Card of(Color c, Rank r) {
        return new Card(c, r);
    }

    /**
     * Generates a new Card with the given packed version of the card
     * 
     * @param packed (int): The bitstring containing the information for the card, must be valid
     * @return the new card
     * @throws IllegalArgumentException if the packed version of the card is invalid
     * @see PackedCard
     */
    public static Card ofPacked(int packed) {
        Preconditions.checkArgument(PackedCard.isValid(packed));

        return new Card(PackedCard.color(packed), PackedCard.rank(packed));
    }

    /**
     * Returns the packed version of the card
     * 
     * @return The bitstring representing the packed version of the card
     * @see PackedCard
     */
    public int packed() {
        return pkCard;
    }

    /**
     * Returns the Color of a card
     * 
     * @return The color of the card
     */
    public Color color() {
        return PackedCard.color(this.packed());
    }

    /**
     * Returns the Rank of a card
     * 
     * @return The rank of the given card
     */
    public Rank rank() {
        return PackedCard.rank(this.packed());
    }

    /**
     * Determines whether the card the method is applied on is better than the card given as argument, according to the rules of jass
     * 
     * @param trump (Card.Color): the color that is currently trump
     * @param that (Card): the other Card to be compared, must not be null
     * @return true if both cards are comparable and the Card the method is applied on is better, false otherwise
     */
    public boolean isBetter(Color trump, Card that) {
        return PackedCard.isBetter(trump, this.packed(), that.packed());
    }

    /**
     * Returns the points of the card, according to the rules of Jass
     * 
     * @param trump (Card.Color): The color that is currently trump
     * @return (int): The points of the given card
     */
    public int points(Color trump) {
        return PackedCard.points(trump, this.packed());
    }

    /**
     * Determines whether two different instances of Cards are equal (in color and rank)
     * 
     * @returns true if the cards are equal, false otherwise
     */
    @Override
    public boolean equals(Object that) {
        return that instanceof Card 
               && this.color().equals(((Card) that).color()) 
               && this.rank().equals(((Card) that).rank());
    }

    /**
     * Returns a hash value corresponding to the Card it is applied on
     * 
     * @return the same value as the method packed()
     */
    @Override
    public int hashCode() {
        return packed();
    }

    /**
     * Returns a textual representation of the card
     * 
     * @return A string containing the card's symbol and textual representation the card's rank
     * 
     */
    @Override
    public String toString() {
        return PackedCard.toString(this.packed());
    }
}
