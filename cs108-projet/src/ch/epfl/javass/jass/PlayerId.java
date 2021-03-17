package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * The class PlayerId represents the identification for each of the four players in an game of Jass
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 *
 */
public enum PlayerId {
    
    PLAYER_1,
    PLAYER_2,
    PLAYER_3,
    PLAYER_4;

    /**
     * A List containing all of the players
     */
    public final static List<PlayerId> ALL = Collections.unmodifiableList(Arrays.asList(values()));          
    
    /**
     * The number of all the distinct players
     */
    public final static int COUNT = ALL.size();                                                                                          

    /**
     * returns TEAM_1 for PLAYER_1 and PLAYER_3, and TEAM_2 for PLAYER_2 and PLAYER_4.
     * 
     * @return the Team of the player
     */
    public TeamId team() {
        return this == PLAYER_1 || this == PLAYER_3 ? TeamId.TEAM_1 : TeamId.TEAM_2;
    }
}