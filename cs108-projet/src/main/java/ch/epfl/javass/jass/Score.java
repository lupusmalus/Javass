package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;

/**
 * The class Score represents the score of a game of Jass, a score consisting of a triple for each team: collected tricks, points made in a turn, and overall points made in the
 * game so far (turnpoints excluded) The textual representation of the Score is (x1,y1,z1)/(x2,y2,z2) with the first triple belonging to team 1, the scond to team 2, consisting of
 * the tricks, turnpoints and gamepoints (in that order)
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 * @see PackedScore
 * 
 */
public final class Score {

    /**
     *  the initial score of each game is (0,0,0) / (0,0,0)
     */
    public final static Score INITIAL = new Score(PackedScore.INITIAL);
    
    private final long pkScore;

    /**
     * The standard constructor of a Score
     * 
     * @param pkScore (long): the bitstring that represents the score
     */
    private Score(long pkScore) {
        this.pkScore = pkScore;
    }

    /**
     * Creates an instance of Score, of which packed is the packedScore version
     * 
     * @param packed (long): packedScore version of the Score, must be valid
     * @throws IllegalArgument Exception if the packed score is not valid
     * @return (Score): the Score corresponding to the packed version
     */
    public static Score ofPacked(long packed) {
        Preconditions.checkArgument(PackedScore.isValid(packed));
        return new Score(packed);
    }

    /**
     * Returns the PackedScore version of the Score
     * 
     * @return (long): the packedScore version of the Score
     */
    public long packed() {
        return pkScore;
    }

    /**
     * Returns the number of tricks won by the team in question in this turn
     * 
     * @param t (TeamId): the team in question
     * @return the Tricks made by this team in this turn
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(this.packed(), t);
    }

    /**
     * Returns the number of points won by the team in question in this turn
     * 
     * @param t (TeamId): the team in question
     * @return the Points made by this team in this turn
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(this.packed(), t);
    }

    /**
     * Returns the number of points won by the team in question so far
     * 
     * @param t (TeamId): the team in question
     * @return the points made by this team in this game so far
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(this.packed(), t);
    }

    /**
     * 
     * 
     * @param t (TeamId): the team in question
     * @return the total points made by this team in this game (gamePoints plus turnPoints)
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(this.packed(), t);
    }

    /**
     * Returns the new Score after one trick is played
     * 
     * @param winningTeam (TeamId): The team that won the trick
     * @param trickPoints (int): the points won in this trick, must be positive
     * @throws IllegalArgumentException if the trick points are negative
     * @return the new Score
     */
    public Score withAdditionalTrick(TeamId winningTeam, int trickPoints) {
        Preconditions.checkArgument(trickPoints >= 0);
        return new Score(PackedScore.withAdditionalTrick(this.packed(), winningTeam, trickPoints));
    }

    /**
     * Adds the turnpoints of the last round to the gamepoints of each team and then resets the turnpoints to 0
     * 
     * @return the updated Score to play the next round
     */
    public Score nextTurn() {
        return new Score(PackedScore.nextTurn(this.packed()));
    }

    /**
     * Determines whether two Scores are equal
     * 
     * @return true if two Scores are equal, false otherwise
     */
    @Override
    public boolean equals(Object that) {
            return that instanceof Score && this.packed() == ((Score)that).packed();
    }

    /**
     * Generates a hash value for the score
     * 
     * @return a specific hashCode based on the bitstring of the Score's value
     */
    @Override
    public int hashCode() {
        return Long.hashCode(this.packed());
    }

    /**
     * Returns the textual representation of the score
     * 
     * @return A String containing the textual representation of the given Score
     */
    @Override
    public String toString() {
        return PackedScore.toString(this.packed());
    }
}
