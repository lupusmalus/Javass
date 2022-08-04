package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * The class trick represents a single Trick in a game of Jass
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 * @see PackedTrick
 */
public final class Trick {

    /**
     * Represents an invalid Trick
     */
    public final static Trick INVALID = new Trick(PackedTrick.INVALID);

    // the packed version of the Trick
    private final int pkTrick;

    /**
     * The standard constructor for a Trick
     * 
     * @param pkTrick(int): the packed version of the Trick
     */
    private Trick(int pkTrick) {
        this.pkTrick = pkTrick;
    }

    /**
     * returns a new empty trick, with its given trump color, the index at 0 and the firstPlayer as the first player
     * 
     * @param trump(Color): The color that is should be trump
     * @param firstplayer (PlayerId): The player that will be the first to play in the trick
     * @return (Trick): the initial trick with no cards and new first player
     */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return new Trick(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * creates a new Trick corresponding to the packed version
     * 
     * @throws IllegalArgumentException if the packed Trick is invalid
     * @param packed (int): the bitstring version of the trick, must be valid
     * @return (Trick): new Trick corresponding to the packed version
     */
    public static Trick ofPacked(int packed) {
        Preconditions.checkArgument(PackedTrick.isValid(packed));
        return new Trick(packed);
    }

    /**
     * returns the packedTrick version of the Trick
     * 
     * @return (int): the packedTrick version of the Trick
     */
    public int packed() {
        return pkTrick;
    }

    /**
     * Returns a new trick with no cards played, the index increased by 1 and the winning player of the last Trick as first player of the new Trick
     * 
     * @throws IllegalArgumentException if the given Trick is not full
     * 
     * @return (Trick): the new trick
     */
    public Trick nextEmpty() {
        if (!isFull())
            throw new IllegalStateException();
        return new Trick(PackedTrick.nextEmpty(this.packed()));

    }

    /**
     * Checks if the trick is empty
     * 
     * @return (boolean): true iff the trick is empty
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(this.packed());
    }

    /**
     * Checks if the trick is full
     * 
     * @return (boolean) : true iff the trick is full
     */
    public boolean isFull() {
        return PackedTrick.isFull(this.packed());
    }

    /**
     * Checks if the trick is the last trick of the Turn
     * 
     * @return (boolean): true iff the index of the Trick is 8
     */
    public boolean isLast() {
        return PackedTrick.isLast(this.packed());
    }

    /**
     * Returns the size of the trick, as in: the number of cards contained
     * 
     * @return (int): the number of cards in the Trick
     */
    public int size() {
        return PackedTrick.size(this.packed());

    }

    /**
     * Returns the color that is currently Trump
     * 
     * @return (Color) : the current trump color
     */
    public Color trump() {
        return PackedTrick.trump(this.packed());
    }

    /**
     * Returns the index of the current Trick
     * 
     * @return (int): the index of the current Trick
     */
    public int index() {
        return PackedTrick.index(this.packed());
    }

    /**
     * Returns the PlayerId of the player whose turn it is, according to the index, where the first player has index 0
     * 
     * @param index (int): the index of the player in the current trick
     * 
     * @throws IllegalArgumentException if the index is not between 0(included) and 4(excluded)
     * @return (PlayerId): the player whose turn it is
     */
    public PlayerId player(int index) {
        Preconditions.checkIndex(index, PlayerId.COUNT);
        return PackedTrick.player(this.packed(), index);
    }

    /**
     * Returns the card of the trick at the index given
     * 
     * @throws IndexOutOfBoundsException if the index is not between 0(included) and the size of the trick(excluded)
     * 
     * @param index (int):
     * @return (Card): the Card at the given index of this Trick
     */
    public Card card(int index) {
        Preconditions.checkIndex(index, size());
        return Card.ofPacked(PackedTrick.card(this.packed(), index));

    }

    /**
     * Adds a Card to a Trick
     * 
     * @param c (Card): the Card to be added
     * @throws IllegalArgumentException if the Trick is full
     * @return (Trick): the new Trick with the card added
     */
    public Trick withAddedCard(Card c) {
        if(isFull())
            throw new IllegalStateException();
        
        return new Trick(PackedTrick.withAddedCard(this.packed(), c.packed()));
    }

    /**
     * Returns the current base color of the trick, that is the color of the card first played
     * 
     * @throws IllegalArgumentException if the given trick is empty
     * 
     * @return (Color): the current base color
     */
    public Color baseColor() {
        if(isEmpty())
            throw new IllegalStateException();
        
        return PackedTrick.baseColor(this.packed());
    }

    /**
     * Returns the CardSet of the playable cards, according to what has been played already in the Trick and the hand of the player
     * 
     * @param hand (CardSet): the hand of the player
     * @throws IllegalArgumentException if the given trick is full
     * @return (CardSet): The set of cards the player is allowed to play
     */
    public CardSet playableCards(CardSet hand) {
        if(isFull())
            throw new IllegalStateException();
        
        return CardSet.ofPacked(PackedTrick.playableCards(this.packed(), hand.packed()));
    }

    /**
     * Returns the total points of the trick, adding the 5 "points de der" if necessary
     * 
     * @return (int): the points of the Trick
     */
    public int points() {
        return PackedTrick.points(this.packed());
    }

    /**
     * Returns the current winning player, as in the player who played the highest card in the current trick
     * 
     * @throws IllegalArgumentException if the Trick is empty
     * 
     * @return (PlayerId): the winning player
     */
    public PlayerId winningPlayer() {
        if(isEmpty())
            throw new IllegalStateException();
        
        return PackedTrick.winningPlayer(this.packed());
    }

    /**
     * Determines whether two tricks are equal, might be two different instances
     * 
     * @return (boolean): true if the two Tricks are the same, false otherwise
     */
    @Override
    public boolean equals(Object that) {
            return that instanceof Trick && this.packed() == ((Trick)that).packed();
    }

    /**
     * Generates a hashvalue for the given trick
     * 
     * @return the packed version of the Trick
     */
    @Override
    public int hashCode() {
        return this.packed();
    }

    /**
     * Returns a textual representation of the trick
     * 
     * @return a String containing a textual representation of the Trick, representing all the Cards currently played
     */
    @Override
    public String toString() {
        return PackedTrick.toString(this.packed());
    }
}
