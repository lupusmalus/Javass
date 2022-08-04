package ch.epfl.javass.gui;

import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.ObservableJassGame;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.RandomPlayer;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;

public final class GuiTest extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    int i = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Map<PlayerId, String> ns = new HashMap<>();
        ns.put(PlayerId.PLAYER_1, "Jean-Christophe");
        ns.put(PlayerId.PLAYER_2, "Marc");
        ns.put(PlayerId.PLAYER_3, "Christelle");
        ns.put(PlayerId.PLAYER_4, "Marie");
        ScoreBean sB = new ScoreBean();
        TrickBean tB = new TrickBean();
        HandBean hb = new HandBean();

        Map<PlayerId, Player> players = new HashMap<>();

        PlayerId main = PlayerId.PLAYER_4;
        
        
        
        for (PlayerId pId : PlayerId.ALL) {
            Player player = new RandomPlayer(2019);

            if (pId == main)
                player = new Player() {

                    Player randy = new RandomPlayer(428);

                    @Override
                    public Card cardToPlay(TurnState state, CardSet hand) {
                        hb.setPlayableCards(state.trick().playableCards(hand));
                        return randy.cardToPlay(state, hand);
                    }

                    @Override
                    public void updateHand(CardSet newHand) {
                        hb.setHand(newHand);
                    }

                    @Override
                    public void setTrump(Color trump) {
                        tB.setTrump(trump);
                    }

                    @Override
                    public void updateTrick(Trick newTrick) {
                        tB.setTrick(newTrick);
                    }

                    @Override
                    public void updateScore(Score score) {
                        sB.setGamePoints(TeamId.TEAM_1, score.gamePoints(TeamId.TEAM_1));
                        sB.setGamePoints(TeamId.TEAM_2, score.gamePoints(TeamId.TEAM_2));

                        sB.setTurnPoints(TeamId.TEAM_1, score.turnPoints(TeamId.TEAM_1));
                        sB.setTurnPoints(TeamId.TEAM_2, score.turnPoints(TeamId.TEAM_2));

                        sB.setTotalPoints(TeamId.TEAM_1, score.totalPoints(TeamId.TEAM_1));
                        sB.setTotalPoints(TeamId.TEAM_2, score.totalPoints(TeamId.TEAM_2));
                    }

                    @Override
                    public void setWinningTeam(TeamId winningTeam) {
                        sB.setWinningTeam(winningTeam);
                    }
                };

            players.put(pId, player);
        }

        ObservableJassGame game = new ObservableJassGame(2019, players, ns);
        GraphicalPlayer g = new GraphicalPlayer(main, ns, sB, tB, hb, null );
        g.createStage().show();

        new AnimationTimer() {
            
            long now0 = 0;

            @Override
            public void handle(long now) {

                if (now - now0 < 1_000_000_000L || game.isGameOver())
                    return;
                now0 = now;
                
                game.advanceToEndOfNextTrick();
            }
        }.start();
    }
}
