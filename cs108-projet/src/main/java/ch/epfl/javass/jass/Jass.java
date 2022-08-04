package ch.epfl.javass.jass;

/**
 * 
 * The interface Jass represents a game of Jass, containing constants based on the games rules
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 *
 */

public interface Jass {
    /**
     *  The number of cards for each player at the beginning of the game
     */
    public static final int HAND_SIZE = 9;
    
    /**
     * The number of tricks per turn
     */
    public static final int TRICKS_PER_TURN = 9;
    
    /**
     *  Points needed to win one game
     */
    public static final int WINNING_POINTS = 1000;
    
    /**
     * Number of additional points when ending a game with a match (all 9 tricks won by the same team)
     */
    public static final int MATCH_ADDITIONAL_POINTS = 100;
    
    /**
     *  Number of additional points when winning the last trick
     */
    public static final int LAST_TRICK_ADDITIONAL_POINTS = 5;
    
}