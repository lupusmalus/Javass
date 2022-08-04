package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * The class TurnState represents the state of the current turn in a game of Jass
 * 
 * @author Hannah Laureen Casey
 * @author Erik Alessandro Wengle
 */
public final class TurnState {

    // The score and played trick of the given turn, as well as the cards that have not yet been played
    private final long pkScore, pkUnplayedCards;
    private final int pkCurrentTrick;

    /**
     * The standard private constructor of a TurnState
     * 
     * @param pkScore (long): The packed version of the current score of the game
     * @param unplayedCardset (long): the set of cards that haven't been played yet
     * @param currentTrick (int): The packed version of the current trick being played
     */
    private TurnState(long pkScore, long pkUnplayedCards, int pkCurrentTrick) {
        this.pkScore = pkScore;
        this.pkUnplayedCards = pkUnplayedCards;
        this.pkCurrentTrick = pkCurrentTrick;
    }

    /**
     * Returns the initial TurnState of a turn, where the current trump, first player and score are given and the trick is empty and no cards have been played 
     * 
     * @param trump (Color): the current color of trump
     * @param score (Score): the current score of the game
     * @param firstPlayer (PlayerId): the player to play first in the trick
     * @return (TurnState): the initial TurnState of the turn
     */
    public static TurnState initial(Color trump, Score score, PlayerId firstPlayer) {
        return new TurnState(score.packed(), PackedCardSet.ALL_CARDS, PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * Returns a new TurnState according based on the given packed components
     * 
     * @param pkScore (long): The packed version of the current link PackedScore of the game
     * @param pkUnplayedCards (long): The PackedCardSet of unplayed cards
     * @param pkTrick (int): The link PackedTrick representing the current trick
     * 
     * @throws IllegalArgumentException if any of the given parameters are invalid
     * @return (TurnState): the new TurnState with the given parameters
     */
    public static TurnState ofPackedComponents(long pkScore, long pkUnplayedCards, int pkTrick) {
        Preconditions.checkArgument(PackedScore.isValid(pkScore) && PackedCardSet.isValid(pkUnplayedCards) && PackedTrick.isValid(pkTrick));
        return new TurnState(pkScore, pkUnplayedCards, pkTrick);
    }

    /**
     * Gives access to the PackedScore of the TurnState
     * 
     * @return (long): the packed version of the score of the current TurnState
     */
    public long packedScore() {
        return pkScore;
    }

    /**
     * Gives access to the current PackedCardSet of the unplayed cards of the TurnState 
     * 
     * @return (long): the packed version of the CardSet of unplayed cards in the current turn
     */
    public long packedUnplayedCards() {
        return pkUnplayedCards;
    }

    /**
     * Gives access to the current PackedTrick of the TurnState
     * 
     * @return (int): the packed version of the Trick being played
     */
    public int packedTrick() {
        return pkCurrentTrick;
    }

    /**
     * Gives access to the Score of the TurnState
     * 
     * @return (Score): the current Score of the game
     */
    public Score score() {
        return Score.ofPacked(pkScore);
    }

    /**
     * Gives access to the CardSet of unplayed cards of the the TurnState
     * 
     * @return (CardSet): the CardSet of unplayed cards in this turn
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(pkUnplayedCards);
    }

    /**
     * Gives access to the Trick of the TurnState
     * 
     * @return (Trick): the current Trick being played
     */
    public Trick trick() {
        return Trick.ofPacked(pkCurrentTrick);
    }

    /**
     * Returns true if the last Trick has been played
     * 
     * @return (boolean): returns true if the turnstate is terminal, false otherwise
     */
    public boolean isTerminal() {
        return pkCurrentTrick == PackedTrick.INVALID;
    }

    /**
     * Returns the PlayerId of the player whose turn is next in the current trick of the TurnState
     * 
     * @throws IllegalStateException is the current trick of the TurnState is full
     * @return (PlayerId): the player to play next
     */
    public PlayerId nextPlayer() {
        if (PackedTrick.isFull(pkCurrentTrick))
            throw new IllegalStateException();

        return PackedTrick.player(pkCurrentTrick, PackedTrick.size(pkCurrentTrick));
    }

    /**
     * Returns a new TurnState where the given card is added to the PackedTrick of the TurnState and removed from the of unplayed cards
     * 
     * @param card (Card): the card to be added
     * @throws IllegalStateException if the trick is already full
     * @return (TurnState): the new TurnState where the card has been added
     */
    public TurnState withNewCardPlayed(Card card) {
        if (PackedTrick.isFull(pkCurrentTrick))
            throw new IllegalStateException();
        
        if(!PackedCardSet.contains(pkUnplayedCards, card.packed()))
            throw new IllegalArgumentException();
        
        return new TurnState(pkScore, 
                             PackedCardSet.remove(pkUnplayedCards, card.packed()),
                             PackedTrick.withAddedCard(pkCurrentTrick, card.packed()));
    }

    /**
     * Returns a new TurnState with the last trick collected, hence the new trick of the TurnState is empty
     * 
     * @throws IllegalStateException if the current trick is not full
     * @return (TurnState): the new TurnState, where the trick has been collected
     */
    public TurnState withTrickCollected() {
        if (!PackedTrick.isFull(pkCurrentTrick))
            throw new IllegalStateException();

        return new TurnState(PackedScore.withAdditionalTrick(pkScore, PackedTrick.winningPlayer(pkCurrentTrick).team(), 
                             PackedTrick.points(pkCurrentTrick)), pkUnplayedCards,
                             PackedTrick.nextEmpty(pkCurrentTrick));
    }

    /**
     * Returns a new TurnState when the last card of the trick is played and then the trick is collected
     * 
     * @param card (Card): the card to be added to the TurnState's trick
     * @throws IllegalStateException if the current trick is full
     * @return (TurnState): the new TurnState where the card has been added to the trick and the trick is collected
     */
    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
        if (PackedTrick.isFull(pkCurrentTrick))
            throw new IllegalStateException();

        TurnState withAddedCard = withNewCardPlayed(card);

        return withAddedCard.trick().isFull() ? withAddedCard.withTrickCollected() : withAddedCard;
    }

}
