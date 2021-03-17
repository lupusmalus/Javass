package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * The class GraphicalPlayerAdapter represents an adapter used to implement a graphical player into a game of Jass
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 *
 */
public class GraphicalPlayerAdapter implements Player {

    // Beans used for the GraphicalPlayer
    private final HandBean hb;
    private final ScoreBean sb;
    private final TrickBean tb;
    
    private SimpleBooleanProperty active;
    // Queue to get the selected card from the GraphicalPlayer
    private final ArrayBlockingQueue<Card> q;

    /**
     * Standard constructor of GraphicalPlayerAdapter
     */
    public GraphicalPlayerAdapter() {
        hb = new HandBean();
        sb = new ScoreBean();
        tb = new TrickBean();
        q = new ArrayBlockingQueue<>(1);
        
        active = new SimpleBooleanProperty(false);
    }

    
    public BooleanProperty activeProperty() {
        return active;
    }
    /**
     * Plays the card selected by the user, i.e. the playable card that was clicked on. Makes all of the other cards unplayable
     * and thus unclickable after the click
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
      
        //displays all the cards that are playable
        Platform.runLater(() -> hb.setPlayableCards(state.trick().playableCards(hand)));
        Card c = null;

        try {
            c = q.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        //makes selecting multiple cards impossible
        Platform.runLater(() -> hb.setPlayableCards(CardSet.EMPTY));

        return c;

    }

    /**
     * Updates the PlayerIds and names of the players involved in the game, including the PlayerId of the player the method is
     * applied on, starts the graphical interface
     * @param ownId (PlayerId): the PlayerId of the player the method is applied on
     * @param playerNames (Map<PlayerId, String>): The names and playerIds of all players
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        GraphicalPlayer graph = new GraphicalPlayer(ownId, playerNames, sb, tb, hb, q);
        Platform.runLater(() -> {
            graph.createStage().show();
            active.set(true);
        });
    }

    /**
     * Updates the hand of the player
     * @param newHand (CardSet): the new hand of the player
     */
    @Override
    public void updateHand(CardSet newHand) {
        Platform.runLater(() -> hb.setHand(newHand));
    }

    /**
     * Updates the color that is currently trump
     * @param trump (Color): The color that is trump
     */
    @Override
    public void setTrump(Color trump) {
        Platform.runLater(() -> tb.setTrump(trump));
    }

    /**
     * Updates the state of the trick for the player
     * @param newTrick (Trick): The new trick
     */
    @Override
    public void updateTrick(Trick newTrick) {
        Platform.runLater(() -> tb.setTrick(newTrick));
    }

    /**
     * Updates the score for the player
     * @param score (Score): The new score
     */
    @Override
    public void updateScore(Score score) {
        Platform.runLater(() -> {
            sb.setGamePoints(TeamId.TEAM_1, score.gamePoints(TeamId.TEAM_1));
            sb.setGamePoints(TeamId.TEAM_2, score.gamePoints(TeamId.TEAM_2));

            sb.setTurnPoints(TeamId.TEAM_1, score.turnPoints(TeamId.TEAM_1));
            sb.setTurnPoints(TeamId.TEAM_2, score.turnPoints(TeamId.TEAM_2));

            sb.setTotalPoints(TeamId.TEAM_1, score.totalPoints(TeamId.TEAM_1));
            sb.setTotalPoints(TeamId.TEAM_2, score.totalPoints(TeamId.TEAM_2));
        });
    }

    /**
     * Tells the player which team has won
     * @param winningTeam (TeamId): The team that has won
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        Platform.runLater(() -> sb.setWinningTeam(winningTeam));
    }
}
