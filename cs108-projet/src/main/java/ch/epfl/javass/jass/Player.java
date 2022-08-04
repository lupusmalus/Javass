package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

/**
 * The interface Player represents a single player in a game of Jass
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 *
 */
public interface Player {

    /**
     * Determines which card to play based on the current TurnState and hand of cards
     * @param state (TurnState): The current state of a turn
     * @param hand (CardSet): The set of cards to choose from
     * @return The card of the CardSet to be played
     */
    Card cardToPlay(TurnState state, CardSet hand);

    
    /**
     * Updates the PlayerIds and names of the players involved in the game, including the PlayerId of the player the method is applied on
     * @param ownId (PlayerId): the PlayerId of the player the method is applied on
     * @param playerNames (Map<PlayerId, String>): The names and playerIds of all players
     */
    default void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        // is empty by default
    }

    
    /**
     * Updates the hand of the player
     * @param newHand (CardSet): the new hand of the player
     */
    default void updateHand(CardSet newHand) {
        // is empty by default
    }

    
    /**
     * Updates the color that is currently trump
     * @param trump (Color): The color that is trump
     */
    default void setTrump(Color trump) {
        // is empty by default
    }

    
    /**
     * Updates the state of the trick for the player
     * @param newTrick (Trick): The new trick
     */
    default void updateTrick(Trick newTrick) {
        // is empty by default
    }

    
    /**
     * Updates the score for the player
     * @param score (Score): The new score
     */
    default void updateScore(Score score) {
        // is empty by default
    }

    
    /**
     * Tells the player which team has won
     * @param winningTeam (TeamId): The team that has won
     */
    default void setWinningTeam(TeamId winningTeam) {
        // is empty by default
    }

}
