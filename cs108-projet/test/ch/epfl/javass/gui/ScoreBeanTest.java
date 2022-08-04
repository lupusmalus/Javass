package ch.epfl.javass.gui;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.TeamId;

class ScoreBeanTest {

    @Test
    void ScoreBeanTurnPointsWorks() {
        System.out.println("===== TURNPOINTS =====");
        
        ScoreBean sb = new ScoreBean();
        sb.turnPointsProperty(TeamId.TEAM_1).addListener((p, o, n) -> System.out.println("old: " + o + ", new " + n));
        sb.turnPointsProperty(TeamId.TEAM_2).addListener((p, o, n) -> System.out.println("old: " + o + ", new " + n));

        
        for(int i = 0; i < 258; ++i) {
            sb.setTurnPoints(TeamId.TEAM_1, i);
            sb.setTurnPoints(TeamId.TEAM_2, i);
            System.out.println("----");
        }
    }
    
    @Test
    void ScoreBeangamePointsWorks() {
        System.out.println("===== GAMEPOINTS =====");
        
        ScoreBean sb = new ScoreBean();
        sb.gamePointsProperty(TeamId.TEAM_1).addListener((p, o, n) -> System.out.println("old: " + o + ", new " + n));
        sb.gamePointsProperty(TeamId.TEAM_2).addListener((p, o, n) -> System.out.println("old: " + o + ", new " + n));

        
        for(int i = 0; i < 1001; ++i) {
            sb.setGamePoints(TeamId.TEAM_1, i);
            sb.setGamePoints(TeamId.TEAM_2, i);
            System.out.println("----");
        }
    }
    
    @Test
    void ScoreBeanTotalPointsWorks() {
        System.out.println("===== TOTAL POINTS =====");
        
        ScoreBean sb = new ScoreBean();
        sb.totalPointsProperty(TeamId.TEAM_1).addListener((p, o, n) -> System.out.println("old: " + o + ", new " + n));
        sb.totalPointsProperty(TeamId.TEAM_2).addListener((p, o, n) -> System.out.println("old: " + o + ", new " + n));

        
        for(int i = 0; i < 1001; ++i) {
            sb.setTotalPoints(TeamId.TEAM_1, i);
            sb.setTotalPoints(TeamId.TEAM_2, i);
            System.out.println("----");
        }
    }
    
    @Test
    void ScoreWinningTeamWorks() {
        System.out.println("===== WINNING TEAM =====");
        
        ScoreBean sb = new ScoreBean();
        sb.winningTeamProperty().addListener((p, o, n) -> System.out.println("old: " + o + ", new " + n));
        
        
        sb.setWinningTeam(TeamId.TEAM_1);
        sb.setWinningTeam(TeamId.TEAM_2);
    }
}
