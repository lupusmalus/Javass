package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

/**
 * The class PacedPlayer represents a Player in a game of Jass whose time interval to decide which card to play can be prolonged by a certain amount of time.
 * 
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 * @see Player
 */
public final class PacedPlayer implements Player {

    private final Player underlyingPlayer;
    private final long minTime;

    /**
     * Standard constructor of the PacedPlayer
     * 
     * @param underlyingPlayer (Player): The player the delay should be applied on
     * @param minTime (double): The minimal number of seconds the player should wait until the card should be played, must be nonnegative
     * 
     */
    public PacedPlayer(Player underlyingPlayer, double minTime) {
        Preconditions.checkArgument(minTime >= 0);

        this.underlyingPlayer = underlyingPlayer;
        // Conversion from seconds to milliseconds
        this.minTime = (long) (minTime * 1000);
    }

    /**
     * Lets the underlying player decide Which card to play, if the minimal time is not attained, the thread will sleep until the desired minTime is reached
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        long initialtime = System.currentTimeMillis();
        Card cardToPlay = underlyingPlayer.cardToPlay(state, hand);
        long difference = System.currentTimeMillis() - initialtime;

        // Difference is at least minTime, no delay needed, else puts the thread to sleep until minTime reached
        if (difference >= minTime) {
            return cardToPlay;
        }
        else {
            try {
                Thread.sleep(minTime - difference);
            } catch (InterruptedException e) {
                /* ignore */}
            return cardToPlay;
        }
    }

    /**
     * Updates the PlayerIds and names of the players involved in the game, including the Id of the player the method is applied on
     * 
     * @param ownId (PlayerId): the PlayerId of the player the method is applied on
     * @param playerNames (Map<PlayerId, String>): The names and playerIds of all players
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        underlyingPlayer.setPlayers(ownId, playerNames);
    }

    /**
     * Updates the hand of the player
     * 
     * @param newHand (CardSet): the new hand of the player
     */
    @Override
    public void updateHand(CardSet newHand) {
        underlyingPlayer.updateHand(newHand);
    }

    /**
     * Updates the color that is currently trump
     * 
     * @param trump (Color): The color that is trump
     */
    @Override
    public void setTrump(Color trump) {
        underlyingPlayer.setTrump(trump);
    }

    /**
     * Updates the state of the trick for the player
     * 
     * @param newTrick (Trick): The new trick
     */
    @Override
    public void updateTrick(Trick newTrick) {
        underlyingPlayer.updateTrick(newTrick);
    }

    /**
     * Updates the score for the player
     * 
     * @param score (Score): The new score
     */
    @Override
    public void updateScore(Score score) {
        underlyingPlayer.updateScore(score);
    }

    /**
     * Tells the player which team has won
     * 
     * @param winningTeam (TeamId): The team that has won
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        underlyingPlayer.setWinningTeam(winningTeam);
    }
}
