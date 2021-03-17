package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * The class PackedTrick represents one Trick on a game of Jass as a bitstring of length 32; 
 * The 24 LSB represent the four cards played in each trick, where the 6 first bits are
 * the first card played, the 6 following represent the second card etc. If a card in a Trick has not been played already, 
 * it is represented as an invalid card; Bits 24 until 27 represent the index of the current Trick, between 0 and 8; 
 * The two following bits represent the player who is the first to play in this Trick; The two MSB represent the
 * color that is currently trump
 * 
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 * @see Trick
 */
public final class PackedTrick {

    // Constants used to extract and pack the single values out of a packed trick as well as their upper bounds (included)
    private final static int START_CARD = 0,
                             SIZE_CARD = 6,
                             COUNT_CARD = PlayerId.COUNT,
                             
                             START_INDEX = 24,
                             SIZE_INDEX = 4,
                             MAX_TRICKS = Jass.TRICKS_PER_TURN - 1,
                             INITIAL_INDEX = 0,
                             
                             START_PLAYERID = 28,
                             SIZE_PLAYERID = 2,
                             MAX_PLAYERS = PlayerId.COUNT - 1,
                             
                             START_TRUMP = 30,
                             SIZE_TRUMP = 2,
                             MAX_TRUMP = Color.COUNT - 1;


    // Bitstring of length 32 representing an invalid packed trick, its bits all having value 1;
    public static final int INVALID = Bits32.mask(0, 32);

    /**
     * The class PackedTrick is not instantiable
     */
    private PackedTrick() {
    }

    /**
     * Determines if a trick is valid or not The index must be between 0 and 8 (included), trump and the first player must be in their 
     * respective bounds. A sequence of played cardsthat are valid cannot be interrupted by invalid cards, that is: The cards must 
     * either be all invalid when no card has been played, or the last 3 cards are invalid when the first card has been played etc.
     * 
     * @param pkTrick (int): the bitstring that represents the packed trick
     * @return (boolean): true if the packed trick is valid, false otherwise
     */
    public static boolean isValid(int pkTrick) {
        int index = Bits32.extract(pkTrick, START_INDEX, SIZE_INDEX);
        int player = Bits32.extract(pkTrick, START_PLAYERID, SIZE_PLAYERID);
        int trump = Bits32.extract(pkTrick, START_TRUMP, SIZE_TRUMP);

        boolean trickMetaWithinBounds = (trump <= MAX_TRUMP && player <= MAX_PLAYERS && index <= MAX_TRICKS);
        
        //used for determining whether the trick is valid
        boolean allValid = true;
        boolean hasEmptyCards = false;

        // checks all of the cards, if an invalid card has been found, the cards of the following indices must be invalid too
        for (int i = 0; i < COUNT_CARD; i++) {
            int pkCard = Bits32.extract(pkTrick, i * SIZE_CARD, SIZE_CARD);

            // if the card is not valid, it must be the invalid card representing a free spot in the trick, otherwise the trick is not valid
            if (!PackedCard.isValid(pkCard)) {
                hasEmptyCards = true;
                allValid = (pkCard == PackedCard.INVALID) && allValid;
            }

            allValid = (!hasEmptyCards || !PackedCard.isValid(pkCard)) && allValid;
        }

        return allValid && trickMetaWithinBounds;
    }

    /**
     * Returns a packed trick that represents the first trick of a turn where no cards have been played
     * 
     * @param trump (Color): The color to be considered as trump
     * @param firstPlayer (PlayerId): To playerId of the player who plays first
     * @return a bitstring of length 32 representing a packed trick with no cards played, the index being 0 and the playerId
     *  as well as the color of trump corresponding to the given arguments
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        return Bits32.pack(PackedCard.INVALID, SIZE_CARD,
                           PackedCard.INVALID, SIZE_CARD,
                           PackedCard.INVALID, SIZE_CARD,
                           PackedCard.INVALID, SIZE_CARD,
                           INITIAL_INDEX, SIZE_INDEX,
                           firstPlayer.ordinal(), SIZE_PLAYERID, 
                           trump.ordinal(), SIZE_TRUMP);
    }

    /**
     * Returns a packed trick that represents the following trick without any cards being played based on the given bitstring
     * 
     * @param pkTrick (int): The PackedTrick whose following trick should be created, must be strictly less than the maximum number of tricks
     * @return a bitstring of length 32 representing a packed trick with no cards played, the index and the playerId are incremented by 1, 
     * the trumpcolor reains corresponding to the given bitstring; returns an invalid packed trick if the index is equal to the maximal number of tricks
     */
    public static int nextEmpty(int pkTrick) {
        assert (isValid(pkTrick));

        int index = index(pkTrick);
        
        if (isLast(pkTrick))
            return INVALID;
        
        return Bits32.pack(PackedCard.INVALID, SIZE_CARD,
                           PackedCard.INVALID, SIZE_CARD,
                           PackedCard.INVALID, SIZE_CARD,
                           PackedCard.INVALID, SIZE_CARD,
                           (index + 1), SIZE_INDEX,
                           winningPlayer(pkTrick).ordinal(), SIZE_PLAYERID,
                           trump(pkTrick).ordinal(), SIZE_TRUMP);
    }

    /**
     * Determines whether the given trick is the last of its turn
     * 
     * @param pkTrick (int): the packed trick to inspect
     * @return true if the value of the packed trick's substring representing the index is equal to the maximal number of tricks, false otherwise
     */
    public static boolean isLast(int pkTrick) {
        assert isValid(pkTrick);
        return index(pkTrick) == MAX_TRICKS;
    }

    /**
     * Determines whether the given trick contains four played cards
     * 
     * @param pkTrick (int): the packed trick to inspect
     * @return true if the value of the packed trick's substrings representing the cards correspond to valid packed card values, false otherwise
     */
    public static boolean isFull(int pkTrick) {
        assert isValid(pkTrick);
        return size(pkTrick) == COUNT_CARD;
    }

    /**
     * Determines whether the given trick contains no played cards
     * 
     * @param pkTrick (int): the packed trick to inspect
     * @return true if the value of the packed tricks substrings representing the cards all correspond to invalid packed card values, false otherwise
     */
    public static boolean isEmpty(int pkTrick) {
        assert isValid(pkTrick);
        return size(pkTrick) == 0;
    }

    /**
     * Returns the size of a packed trick, that is, how many cards have been played
     * 
     * @param pkTrick (int): the packed trick to inspect
     * @return The amount of valid cards found in the packed tricks bitstring
     */
    public static int size(int pkTrick) {
        assert isValid(pkTrick);

        for (int i = 0; i < COUNT_CARD; i++)
            if (!PackedCard.isValid(card(pkTrick, i)))
                return i;
        
        //if all of the cards are valid, the trick is of size 4
        return COUNT_CARD;
    }

    /**
     * Returns the color that is currently trump in the given trick
     * 
     * @param pkTrick (int): the packed trick to inspect
     * @return The color that is trump in the given trick
     */
    public static Color trump(int pkTrick) {
        assert isValid(pkTrick);
        return Color.ALL.get(Bits32.extract(pkTrick, START_TRUMP, SIZE_TRUMP));
    }

    /**
     * Returns the player playing at the given index in the given trick
     * 
     * @param pkTrick (int): the packed trick to inspect
     * @param index (int): the index of the player in the trick
     * @return (PlayerId): The player playing at the given index
     */
    public static PlayerId player(int pkTrick, int index) {
        assert isValid(pkTrick) && index >= INITIAL_INDEX && index <= MAX_PLAYERS;
        return PlayerId.ALL.get((Bits32.extract(pkTrick, START_PLAYERID, SIZE_PLAYERID) + index) % PlayerId.COUNT);
    }

    /**
     * Returns the index of the given trick
     * 
     * @param pkTrick (int): the packed trick to inspect
     * @return The ordinal number of the turns trick
     */
    public static int index(int pkTrick) {
        assert isValid(pkTrick);
        return Bits32.extract(pkTrick, START_INDEX, SIZE_INDEX);
    }

    /**
     * Returns the desired PackedCard of a given packed trick, with index 0 being the first played card, index 1 the second, etc.
     * 
     * @param pkTrick (int): The packed trick to extract the card from
     * @param index (int): The desired index of the card, must be greater than 0 and strictly less than 4
     * @return A bitstring representing the packed version of the desired card
     */
    public static int card(int pkTrick, int index) {
        assert (isValid(pkTrick) && index >= INITIAL_INDEX && index <= MAX_PLAYERS);
        return Bits32.extract(pkTrick, index * SIZE_CARD, SIZE_CARD);
    }

    /**
     * Adds a given PackedCard to the cards being played in a given packed trick
     * 
     * @param pkTrick (int): The packed trick to add the card to
     * @param pkCard (int): The packed card to add to the played cards
     * @return a new bitstring representing the old packed trick with the additional card being played
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        assert (isValid(pkTrick) && PackedCard.isValid(pkCard));

        int size = size(pkTrick);
        int start = size * SIZE_CARD;
        
        int mask = Bits32.mask(start, SIZE_CARD);

        pkTrick = pkTrick & ~mask;

        return pkTrick | pkCard << start;
    }

    /**
     * Returns the basecolor of the trick, that is, the color of the card first played
     * 
     * @param pkTrick (int): The packed trick to inspect
     * @return the color of the card played by the first player
     */
    public static Color baseColor(int pkTrick) {
        assert isValid(pkTrick);
        return PackedCard.color(card(pkTrick, START_CARD));
    }

    /**
     * Returns a bitstring representing a PackedCardSet containing all of the cards that can be played based on the cards that have been played and the cards that are on
     * the hand according to the rules of jass
     * 
     * @param pkTrick (int): The packed version of the current trick
     * @param pkHand (long): The packed cardset representing a hand of cards
     * @return a bitstring representing a packed cardset containing all of the playable cards
     */
    public static long playableCards(int pkTrick, long pkHand) {
        assert (isValid(pkTrick) && PackedCardSet.isValid(pkHand));

        // if no cards have been played, nothing has to be verified, any card on the hand can be played
        if (isEmpty(pkTrick))
            return pkHand;

        Color baseColor = baseColor(pkTrick);
        Color trumpColor = trump(pkTrick);

        // all the colors on the hand that are of the base color or trump color, respectively
        long baseColorSubset = PackedCardSet.subsetOfColor(pkHand, baseColor);
        long trumpColorSubset = PackedCardSet.subsetOfColor(pkHand, trumpColor);

        // the cards on the hand that are trump and whose rank is higher than those of the cards all ready played
        long trumpAboveSubset = trumpColorSubset;

        // all the cards that are neither of trumpcolor or basecolor
        long otherCards = PackedCardSet.intersection(pkHand, PackedCardSet.complement(PackedCardSet.union(trumpColorSubset, baseColorSubset)));

        // creates a subset with the cards whose trump is above based on the already played cards
        for (int i = 0; i < size(pkTrick); i++) {
            int pkCard = card(pkTrick, i);

            if (PackedCard.color(pkCard).equals(trumpColor))
                trumpAboveSubset = PackedCardSet.intersection(trumpColorSubset, PackedCardSet.trumpAbove(pkCard));

        }

        // Trump is base color: player must follow, unless they have trumpcards or their only trump card has the rank of jack
        if (trumpColor.equals(baseColor)) {
            return PackedCardSet.isEmpty(trumpColorSubset) || trumpColorSubset == PackedCardSet.singleton(PackedCard.pack(trumpColor, Rank.JACK)) ? pkHand : trumpColorSubset;
        }
        // base color is not trump: player must follow or can play any trump above
        else if (!PackedCardSet.isEmpty(baseColorSubset)) {
            return PackedCardSet.union(trumpAboveSubset, baseColorSubset);
        }
        // base color is not trump, but player cannot follow normally
        else {
            // player has to either play a trump with rank above or any other card, if possible if the player has neither, they may play any trump card whose rank is below the
            // played ones' rank
            long trumpAboveOrOther = PackedCardSet.union(trumpAboveSubset, otherCards);
            return !PackedCardSet.isEmpty(trumpAboveOrOther) ? trumpAboveOrOther : trumpColorSubset;
        }
    }

    /**
     * Returns the PlayerId of the player winning the trick based on the cards played according to the rules of jass
     * 
     * @param pkTrick (int): The packed trick to inspect
     * @return the PlayerId of the player having won the trick
     */
    public static PlayerId winningPlayer(int pkTrick) {
        assert isValid(pkTrick);

        int winningCard = card(pkTrick, START_CARD);
        int winningIndex = INITIAL_INDEX;

        for (int i = 0; i < size(pkTrick); i++) {
            int otherCard = card(pkTrick, i);
            if (PackedCard.isBetter(trump(pkTrick), otherCard, winningCard)) {
                winningCard = otherCard;
                winningIndex = i;
            }
        }
        return player(pkTrick, winningIndex);
    }

    /**
     * Returns the points of the played cards according to the rules of jass
     * 
     * @param pkTrick (int) The packed trick to inspect
     * @return The points the given trick is worth
     */
    public static int points(int pkTrick) {
        assert isValid(pkTrick);

        //has 0 points initially
        int points = 0;

        for (int i = 0; i < size(pkTrick); i++)
            points += PackedCard.points(trump(pkTrick), card(pkTrick, i));

        if (isLast(pkTrick))
            points += Jass.LAST_TRICK_ADDITIONAL_POINTS;

        return points;
    }

    /**
     * Returns a textual representation of the trick
     * 
     * @param pkTrick (int): The packed trick to be represented
     * @return A string containing information about the current trick, trumpcolor, first player as well as the played cards
     */
    public static String toString(int pkTrick) {
        assert isValid(pkTrick);

        StringJoiner j = new StringJoiner(",", "{", "}");

        for (int i = 0; i < size(pkTrick); i++)
            j.add(PackedCard.toString(card(pkTrick, i)));

        return new StringBuilder("Trick: ")
                   .append(index(pkTrick))
                   .append(", starded by ")
                   .append(player(pkTrick, 0))
                   .append(": ")
                   .append(j.toString())
                   .toString();
    }
}