package ch.epfl.javass.jass;

import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * The class CardSet represents a set of Cards, ranging from an empty set to a full set of 36 cards in a game of Jass
 *
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 * @see PackedCardSet
 * @see Card
 */
public final class CardSet {
    
    /**
     * Represents an empty CardSet
     */
    public static final CardSet EMPTY = new CardSet(PackedCardSet.EMPTY);
    
    /**
     * Represents a CardSet containing every distinct card in a game of Jass
     */
    public static final CardSet ALL_CARDS = new CardSet(PackedCardSet.ALL_CARDS);
    
    // the packed version of the CardSet, contained in a binary string of length 64
    private final long pkCardSet;
    

    /**
     * The standard constructor of the class CardSet. The class CardSet is not directly instantiable
     * 
     * @param pkCardSet (long): The packed version of the CardSet
     */
    private CardSet(long pkCardSet) {
        this.pkCardSet = pkCardSet;
    }

    /**
     * Creates a new CardSet containing the given cards
     * 
     * @param cards (List<>) The list of cards to be included in the CardSet, must not be null
     * @return A new CardSet containing all of the different the cards from the list
     */
    public static CardSet of(List<Card> cards) {
        long pkCardSet = PackedCardSet.EMPTY;

        for (Card c : cards)
            pkCardSet = PackedCardSet.add(pkCardSet, c.packed());

        return new CardSet(pkCardSet);
    }

    /**
     * Creates a new CardSet based on the given packed CardSet
     * 
     * @param pkCardSet (long): The bitstring representing the packed version of the CardSet, must be valid
     * @return A new CardSet containing the Cards as indicated by the packed set of cards
     * @throws IllegalArgumentException if the packed version of the CardSet is invalid
     * @see PackedCardSet
     */
    public static CardSet ofPacked(long pkCardSet) {
       Preconditions.checkArgument(PackedCardSet.isValid(pkCardSet));
        return new CardSet(pkCardSet);
    }

    /**
     * Returns a bitstring representing the packed version of the CardSet
     * 
     * @return The packed version of the CardSet
     */
    public long packed() {
        return pkCardSet;
    }

    /**
     * Indicates whether a CardSet is empty or not
     * 
     * @return True if the CardSet contains no cards, false otherwise
     */
    public boolean isEmpty() {
        return PackedCardSet.isEmpty(this.packed());
    }

    /**
     * Returns a card from the CardSet based on the given index, with the index 0 representing the rightmost bit with value 1 of the CardSets packed version
     * 
     * @param index (int): The index of the card to be returned, must be non-negative
     * @return The card corresponding to the index
     */
    public Card get(int index) {
        return Card.ofPacked(PackedCardSet.get(this.packed(), index));
    }

    /**
     * Adds a given card to the CardSet the method is applied on. If the Card is already in the set, the set will remain unchanged
     * 
     * @param card (Card): The card to be added to the CardSet, must not be null
     * @return A new CardSet containing the former as well as the given card
     */
    public CardSet add(Card card) {
        return new CardSet(PackedCardSet.add(this.packed(), card.packed()));
    }

    /**
     * Returns the size of the CardSet, that is, the number of Cards the CardSet contains
     * 
     * @return The size of the CardSet
     */
    public int size() {
        return PackedCardSet.size(this.packed());
    }

    /**
     * Creates a complement of the CardSet the method is applied on, meaning that all Cards that were formerly included in the set will be excluded and vice-versa
     * 
     * @return A new CardSet, which acts as a complement
     */
    public CardSet complement() {
        return new CardSet(PackedCardSet.complement(this.packed()));
    }

    /**
     * Removes a given card from the CardSet the method is applied on, if the card is not included in the CardSet, the set will remain unchanged
     * 
     * @param card (Card): The card to be removed from the CardSet
     * @return A new CardSet containing all former cards apart form the given card
     */
    public CardSet remove(Card card) {
        return new CardSet(PackedCardSet.remove(this.packed(), card.packed()));
    }

    /**
     * Indicates whether a given card is in the CardSet the method is applied on or not.
     * 
     * @param card (Card): The card to be verified
     * @return True if the Card is in the set, false otherwise
     */
    public boolean contains(Card card) {
        return PackedCardSet.contains(this.packed(), card.packed());
    }

    /**
     * Unites the CardSet the method is applied on with a given CardSet
     * 
     * @param that (CardSet): The CardSet to be united with
     * @return A new CardSet containing all the Cards of the former and all the Cards of the latter CardSet
     */
    public CardSet union(CardSet that) {
        return new CardSet(PackedCardSet.union(this.packed(), that.packed()));
    }

    /**
     * Intersects the CardSet the method is applied to with another CardSet
     * 
     * @param that (CardSet): The CardSet to be intersected with
     * @return A new CardSet containing only the Cards that were both contained in the former as well as in the latter set
     */
    public CardSet intersection(CardSet that) {
        return new CardSet(PackedCardSet.intersection(this.packed(), that.packed()));
    }

    /**
     * Creates a difference from the CardSet the method is applied on with the given CardSet
     * 
     * @param that (CardSet): The CardSet the difference should be created with
     * @return A new CardSet containing only the cards that were contained in the former, but not in the latter set
     */
    public CardSet difference(CardSet that) {
        return new CardSet(PackedCardSet.difference(this.packed(), that.packed()));
    }

    /**
     * Creates a subset of the CardSet containing only the cards of the given color
     * 
     * @param color (Card.Color): The desired color of the subset
     * @return A new CardSet containing all the Cards of the CardSet with the given color
     */
    public CardSet subsetOfColor(Card.Color color) {
        return new CardSet(PackedCardSet.subsetOfColor(this.packed(), color));
    }

    /**
     * Returns whether two CardSets are equal, meaning that they are both of the class CardSet and contain the same cards
     * 
     * @return true if two CardSets are equal, false otherwise
     */
    @Override
    public boolean equals(Object that) {
        return that instanceof CardSet && this.packed() == ((CardSet)that).packed();
    }

    /**
     * Returns a hash value corresponding to the cardSet
     * 
     * @return a specific hashCode based on the bitstring of the CardSets packed version
     */
    @Override
    public int hashCode() {
        return Long.hashCode(pkCardSet);
    }

    /**
     * Returns a textual representation of the CardSet
     * 
     * @return a String containing a textual representation of the CardSet, with all the Cards currently contained in the set
     */
    @Override
    public String toString() {
        return PackedCardSet.toString(pkCardSet);
    }
}