package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * The class TeamId represents the identification for each team in a game of Jass
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 *
 */
public enum TeamId {
    
    TEAM_1,
    TEAM_2;

    /**
     * A list containing all of the distinct teams
     */
    public final static List<TeamId> ALL = Collections.unmodifiableList(Arrays.asList(values())); 
    
    /**
     * The total number of distinct teams
     */
    public final static int COUNT = ALL.size();

    /**
     * Returns the TeamId of the other team, i.e. TEAM_2 for TEAM_1 and TEAM_1 for TEAM_2 respectively
     * @return the TeamId of the other team
     */
    public TeamId other() {
        return this == TEAM_1 ? TEAM_2 : TEAM_1;
    }
}