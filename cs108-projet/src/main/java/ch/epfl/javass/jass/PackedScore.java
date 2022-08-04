package ch.epfl.javass.jass;


import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

/**
 * The class PackedScore represents the score of a game of Jass according to the rules. The score is represented in a bitstring of length 64: The 32 LSB represent the score of
 * TEAM_1 at the given moment, and the 32 MSB represent the score of TEAM_2. The four LSB of the according 32 bit bitstring represent the tricks made in this turn, the nine
 * following bits represent the points made in this turn; The eleven following bits represent the teams points made in the whole game so far
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 * @see Score
 * 
 */
public final class PackedScore {

    /**
     * The class PackedScore is not instantiable
     */
    private PackedScore() {
    }

    // Represents the initial Score of the game, starting at 0
    public static final long INITIAL = 0;

    // The starting indices of the sub-bitstrings representing the scores, as well as their length such as their maximal values
    private static final int START_TURNTRICKS = 0,
                             SIZE_TURNTRICKS = 4, 
                             MAX_TURNTRICKS = Jass.TRICKS_PER_TURN,
                            
                             START_TURNPOINTS = 4, 
                             SIZE_TURNPOINTS = 9,  
                             MAX_TURNPOINTS = 257,
                            
                             START_GAMEPOINTS = 13,
                             SIZE_GAMEPOINTS = 11,
                             MAX_GAMEPOINTS = 2000,
                             
                             START_UNUSED = 24,
                             SIZE_UNUSED = 8,
                             UNUSED_BITS = 0,
                             
                             SIZE_TEAM = 32;

    
    /**
     * Determines if the given Integer represents a valid Score, where the number of tricks is below 9 (included), the points in this turn is below 257 (included) and the game
     * points are below 2000 (included)
     * 
     * @param pkScore (long): The integer to be validated
     * @return (boolean): true when the given bitstring is valid, false otherwise
     */
    public static boolean isValid(long pkScore) {

        return singleTeamIsValid(pkScore, TeamId.TEAM_1) && singleTeamIsValid(pkScore, TeamId.TEAM_2);
    }

    /**
     * Creates a bitstring that represents the given values. For each team the turnTricks are packed into the 4 LSB, the turnPoints are packed into the following 9 bits and the
     * gamePoints are packed into the following 11 bits. The score for TEAM_1 is packed into the 32 LSB and the score for TEAM_2 is packed into the 32 MSB
     * 
     * @param turnTricks1 (int): number of tricks made by TEAM_1 in this turn
     * @param turnPoints1 (int): points made by TEAM_1 in this round
     * @param gamePoints1 (int): points made by TEAM_1 in this game
     * @param turnTricks2 (int): number of tricks made by TEAM_2 in this turn
     * @param turnPoints2 (int): points made by TEAM_2 in this round
     * @param gamePoints2 (int): points made by TEAM_2 in this game
     * @return (long): the newly packed Score that represents the scores of both teams
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1, int turnTricks2, int turnPoints2, int gamePoints2) {
        // uses the auxiliary method packSingleTeam for readability
        int score1 = packSingleTeam(turnTricks1, turnPoints1, gamePoints1);
        int score2 = packSingleTeam(turnTricks2, turnPoints2, gamePoints2);

        return Bits64.pack(score1, SIZE_TEAM, score2, SIZE_TEAM);
    }

    /**
     * Extracts the number of turnTricks for a given team from the bitstring representing the total score at the moment
     * 
     * @param pkScore (long): packedScore version of the total scores at the moment, must be valid
     * @param t (TeamId): The team of which the tricks in this turn is to be returned
     * @return (int): number of tricks in this turn made by the team in question
     */
    public static int turnTricks(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        return (int) Bits64.extract(pkScore, START_TURNTRICKS + t.ordinal() * SIZE_TEAM, SIZE_TURNTRICKS);
    }

    /**
     * Extracts the number of points made in this turn by a given team from the bitstring representing the version of packed the total scores at the moment
     * 
     * @param pkScore (long): packedScore version of the total scores at the moment must be valid
     * @param t (TeamId): The team of which the points made in this turn is to be returned
     * @return (int): points made in this turn by the team in question
     */
    public static int turnPoints(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        return (int) Bits64.extract(pkScore, START_TURNPOINTS + t.ordinal() * SIZE_TEAM, SIZE_TURNPOINTS);
    }

    /**
     * Extracts the number of points made in the total game by given team from the bitstring representing the packed version of the total scores at the moment
     * 
     * @param pkScore (long): packedScore version of the total scores at the moment, must be valid
     * @param t (TeamId): The team of which the points made in this game is to be returned
     * @return (int): number of points made by the team in question in this game so far
     */
    public static int gamePoints(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        return (int) Bits64.extract(pkScore, START_GAMEPOINTS + t.ordinal() * SIZE_TEAM, SIZE_GAMEPOINTS);
    }

    /**
     * Returns the total score of one team at the given moment, by adding the current gamePoints and the current turnPoints
     * 
     * @param pkScore (long): packedScore version of the total scores at the moment, must be valid
     * @param t (TeamId): the team of which the total
     * @return (int): total score of the team in question at the moment
     */
    public static int totalPoints(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        return gamePoints(pkScore, t) + turnPoints(pkScore, t);
    }

    /**
     * Returns the given PackedScore with an additional trick, adding the trickPoints to the current turnPoints and giving the winning Team additional points if they have scored a
     * match
     * 
     * @param pkScore (long): packedScore version of the total scores at the moment
     * @param winningTeam (TeamId): the team that won the trick
     * @param trickPoints (int): points made by the winning team in this trick
     * @return (long): returns the altered PackedScore
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam, int trickPoints) {
        assert (isValid(pkScore));

        //extracts the initial score of the losing team to to repack it again
        long initialScore = extractSingleTeam(pkScore, winningTeam.other());

        int turnTricks = turnTricks(pkScore, winningTeam);
        int turnPoints = turnPoints(pkScore, winningTeam);

        turnTricks++;
        turnPoints = turnPoints + trickPoints;

        if (turnTricks == Jass.TRICKS_PER_TURN) 
            turnPoints += Jass.MATCH_ADDITIONAL_POINTS;

        long winningTeamScore = packSingleTeam(turnTricks, turnPoints, gamePoints(pkScore, winningTeam));
        long newScoreTeam1Wins = Bits64.pack(winningTeamScore, SIZE_TEAM, initialScore, SIZE_TEAM);
        long newScoreTeam2Wins = Bits64.pack(initialScore, SIZE_TEAM, winningTeamScore, SIZE_TEAM);
        
        return winningTeam.equals(TeamId.TEAM_1) ? newScoreTeam1Wins : newScoreTeam2Wins;
    }

    /**
     * Adds the turnPoints to the gamePoints of each team after one turn is played, resetting the turnPoints to 0
     * 
     * @param pkScore (long): packedScore version of the scores of both teams after one turn is played
     * @return (long): returns the packedScore of both teams with new gamePoints
     */
    public static long nextTurn(long pkScore) {
        assert isValid(pkScore);

        int score1 = packSingleTeam(0, 0, gamePoints(pkScore, TeamId.TEAM_1) + turnPoints(pkScore, TeamId.TEAM_1));
        int score2 = packSingleTeam(0, 0, gamePoints(pkScore, TeamId.TEAM_2) + turnPoints(pkScore, TeamId.TEAM_2));

        return Bits64.pack(score1, SIZE_TEAM, score2, SIZE_TEAM);
    }

    /**
     * creates a String with all the scores of both teams at the moment and returns the textual representation of those
     * 
     * @param pkScore (long): packedScore version of the total scores at the moment
     * @return (String): the textual representation of all the scores
     */
    public static String toString(long pkScore) {
        assert isValid(pkScore);

        
        String s1 = new StringJoiner(",", "(", ")")
                    .add(Integer.toString(turnTricks(pkScore,TeamId.TEAM_1)))
                    .add(Integer.toString(turnPoints(pkScore,TeamId.TEAM_1)))
                    .add(Integer.toString(gamePoints(pkScore,TeamId.TEAM_1)))
                    .toString();
        
        String s2 = new StringJoiner(",", "(", ")")
                    .add(Integer.toString(turnTricks(pkScore,TeamId.TEAM_2)))
                    .add(Integer.toString(turnPoints(pkScore,TeamId.TEAM_2)))
                    .add(Integer.toString(gamePoints(pkScore,TeamId.TEAM_2)))
                    .toString();

        return new StringBuilder(s1)
               .append("/")
               .append(s2)
               .toString();
    }

    /**
     * Auxiliary method to pack the scores of a single team into a bitstring of length 32
     * 
     * @param turnTricks (int): The number of tricks made by the team in this turn
     * @param turnPoints (int): the points made by the team in this turn
     * @param gamePoints (int): the total points made by the team in the game so far
     * @return (int): the bitstring representing the scores of a single team
     */
    private static int packSingleTeam(int turnTricks, int turnPoints, int gamePoints) {
        return Bits32.pack(turnTricks, SIZE_TURNTRICKS, turnPoints, SIZE_TURNPOINTS, gamePoints, SIZE_GAMEPOINTS);
    }
    
    
    /**
     * Auxiliary method to extract the score of a single team
     * @param pkScore (long): The score to extract from
     * @param team (TeamId): The desired team
     * @return
     */
    private static long extractSingleTeam(long pkScore, TeamId team) {
        return Bits64.extract(pkScore, team.ordinal() * SIZE_TEAM, SIZE_TEAM);
    }
    
    /**
     * Auxiliary method to determine whether a single team is valid
     * @param pkScore (long) The packed score to inspect
     * @param t (TeamId) the team whose score is to inspect
     * @return
     */
    private static boolean singleTeamIsValid(long pkScore, TeamId t) {

        long turnTricks = Bits64.extract(pkScore, START_TURNTRICKS + t.ordinal() * SIZE_TEAM, SIZE_TURNTRICKS);
        long turnPoints = Bits64.extract(pkScore, START_TURNPOINTS + t.ordinal() * SIZE_TEAM, SIZE_TURNPOINTS);
        long gamePoints = Bits64.extract(pkScore, START_GAMEPOINTS + t.ordinal() * SIZE_TEAM, SIZE_GAMEPOINTS);
        long unused = Bits64.extract(pkScore, START_UNUSED + t.ordinal() * SIZE_TEAM, SIZE_UNUSED);

        return turnTricks <= MAX_TURNTRICKS
                          && turnPoints <= MAX_TURNPOINTS
                          && gamePoints <= MAX_GAMEPOINTS
                          && unused == UNUSED_BITS;

    }
}
