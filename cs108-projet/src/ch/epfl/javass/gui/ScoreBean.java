package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * The class ScoreBean represents a JavaFX bean containing all the graphically observable properties of the score in a game of
 * Jass.
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 *
 */
public final class ScoreBean {

    private final SimpleIntegerProperty turnPoints1, 
                                        gamePoints1, 
                                        totalPoints1,
                                        turnPoints2,
                                        gamePoints2,
                                        totalPoints2;
    
    private final SimpleObjectProperty<TeamId> winningTeam;

    /**
     * The standard constructor of a ScoreBean, initializes all the attributes, sets all the points to 0 and the winning player to
     * null
     */
    public ScoreBean() {
        turnPoints1 = new SimpleIntegerProperty();
        turnPoints2 = new SimpleIntegerProperty();
        gamePoints1 = new SimpleIntegerProperty();
        gamePoints2 = new SimpleIntegerProperty();
        totalPoints1 = new SimpleIntegerProperty();
        totalPoints2 = new SimpleIntegerProperty();
        winningTeam = new SimpleObjectProperty<>();
    }

    /**
     * Returns the observable turnPoints of the specified team
     * 
     * @param team (TeamId) : the team of which the turnPoints are to be returned
     * @return the observable turnPoints
     */
    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        return team == TeamId.TEAM_1 ? turnPoints1 : turnPoints2;
    }

    /**
     * Sets the turnPoints property of the specified team to the given turnPoints
     * 
     * @param team (TeamId): the team in question
     * @param newTurnPoints (int): the new turnPoints of the team
     */
    public void setTurnPoints(TeamId team, int newTurnPoints) {
        if (team == TeamId.TEAM_1)
            turnPoints1.set(newTurnPoints);
        else
            turnPoints2.set(newTurnPoints);
    }

    /**
     * Returns the observable gamePoints of the specified team
     * 
     * @param team (TeamId) : the team of which the gamePoints are to be returned
     * @return the observable gamePoints
     */
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        return team == TeamId.TEAM_1 ? gamePoints1 : gamePoints2;
    }

    /**
     * Sets the gamePoints property of the specified team to the given gamePoints
     * 
     * @param team (TeamId): the team in question
     * @param newGamePoints (int): the new gamePoints of the team
     */
    public void setGamePoints(TeamId team, int newGamePoints) {
        if (team == TeamId.TEAM_1)
            gamePoints1.set(newGamePoints);
        else
            gamePoints2.set(newGamePoints);
    }

    /**
     * Returns the totalPoints property of the specified team
     * @param team (TeamId): the team in question
     * @return the observable totalPoints
     */
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        return team == TeamId.TEAM_1 ? totalPoints1 : totalPoints2;
    }

    /**
     * Sets the totalPoints property of the specified team to the given totalPoints
     * 
     * @param team (TeamId): the team in question
     * @param newTotalPoints (int): the new totalPoints of the team
     */
    public void setTotalPoints(TeamId team, int newTotalPoints) {
        if (team == TeamId.TEAM_1)
            totalPoints1.set(newTotalPoints);
        else
            totalPoints2.set(newTotalPoints);
    }

    /**
     * Returns the property of the winning Team
     * @return the observable winning team
     */
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeam;
    }

    /**
     * Sets the winningTeamproperty to the given team
     * @param the winningTeam
     */
    public void setWinningTeam(TeamId team) {
        winningTeam.set(team);
    }
}
