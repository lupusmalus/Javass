package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * The class JassGame represents an entire game of Jass. It contains all of the players as well as the current state of the turn, which evolves over time, consisting of the current
 * score and the current trick, the latter consisting of all cards that have been played in the current trick. A game of jass is able to evolve in form of entire tricks of four
 * cards being played succsessively by each player.
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 * @see Jass
 */
public final class JassGame {

    // Used for shuffling the cards and determining the color that is trump
    private final Random shuffleRng, trumpRng;

    // The players in a game of Jass, along with their corresponding names and current hands, respectively
    private final Map<PlayerId, Player> players;
    private final Map<PlayerId, String> playerNames;
    private final Map<PlayerId, CardSet> hands;

    // used to determine which player will start the following turn
    private PlayerId formerFirstPlayer;

    // The state of the current turn
    private TurnState turnState;

    /**
     * Standard constructor of a game of Jass, initializes all of the players
     * 
     * @param rngSeed (long): The seed used for the PRNG determining the shuffling of the cards as well as the trump color
     * @param players (Map<PlayerId,Player>): The players of the game, must have four entries
     * @param playerNames (Map<PlayerId, String>): The names of the players, must have four entries
     */
    public JassGame(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {
        Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);

        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());

        this.players = Collections.unmodifiableMap(new HashMap<>(players));
        this.playerNames = Collections.unmodifiableMap(new HashMap<>(playerNames));
        hands = new HashMap<>();
    }

    /**
     * Determines whether the game is over or not, that is, whether one of the two teams has reached the needed amount of points
     * 
     * @return True if one the teams has reached the winning points, false otherwise
     */
    public boolean isGameOver() {

        // Turnstate must have been initialized such that game can be over
        if (turnState != null) {
            Score score = turnState.score();

            boolean team1won = (score.totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS);
            boolean team2won = (score.totalPoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS);

            return team1won || team2won;
        } 
        else {
            return false;
        }
    }

    /**
     * Advances the game to the end of the next trick, collecting the trick that was last played (if there is one).
     */
    public void advanceToEndOfNextTrick() {
        // game was never initialized, needs to be initialized
        if (turnState == null) {
            initializeGame();
            formerFirstPlayer = turnState.trick().player(0);
            playCards();
        } 
        else if (!isGameOver()) {

            // always collects last played trick
            turnState = turnState.withTrickCollected();

            // turn is over, but game is not over yet; need to initialize new turn
            if (turnState.isTerminal()) {
                initializeTurn(formerFirstPlayer);
                playCards();
            }
            // turn is not over; advance game normally
            else {
                updateScoreForAll();
                updateTrickForAll();
                playCards();
            }
        }

        //checks after playing the trick whether the game has ended or not
        if (isGameOver()) {
            updateScoreForAll(turnState.score().nextTurn());
            setWinnerForAll();
        }
    }

    /**
     * Shuffles and distributes the given deck among all of the players
     * 
     * @param deck (List<>): List containing the cards to be shuffled and distributed
     */
    private void shuffleAndDistributeCards(List<Card> deck) {
        Collections.shuffle(deck, shuffleRng);

        for (PlayerId pId : PlayerId.ALL)
            hands.put(pId, CardSet.of(deck.subList(pId.ordinal() * Jass.HAND_SIZE, (pId.ordinal() + 1) * Jass.HAND_SIZE)));
    }

    /**
     * Updates the trick for every player
     */
    private void updateTrickForAll() {
        for (Player p : players.values())
            p.updateTrick(turnState.trick());
    }

    /**
     * sets the winning team for every player; sets only players if at most 1 team has the needed points
     */
    private void setWinnerForAll() {
        boolean team1won = (turnState.score().totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS);
        boolean team2won = (turnState.score().totalPoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS);
        
        if(team1won ^ team2won) {
            for (Player p : players.values()) 
                p.setWinningTeam(team1won ? TeamId.TEAM_1 : TeamId.TEAM_2);
        }
    }

    /**
     * Updates the score of every player
     */
    private void updateScoreForAll() {
        updateScoreForAll(turnState.score());
    }

    /**
     * Updates the score of every player with a given score
     */
    private void updateScoreForAll(Score score) {
        for (Player p : players.values()) 
            p.updateScore(score);
    }

    /**
     * Lets every player play a card of their choice, thus representing an entire played trick
     */
    private void playCards() {

        for (int i = 0; i < PlayerId.COUNT; i++) {
            // The player in question according to the rules of the trick
            PlayerId playerId = turnState.trick().player(i);
            Player player = players.get(playerId);

            // determines the card to be played from the players hand
            CardSet playerHand = hands.get(playerId);
            Card cardToPlay = player.cardToPlay(turnState, playerHand);

            // takes the chosen card out of the players hand and places it into the trick
            playerHand = playerHand.remove(cardToPlay);
            hands.put(playerId, playerHand);
            player.updateHand(playerHand);
            turnState = turnState.withNewCardPlayed(cardToPlay);

            // After playing the card, the trick must be updated for every player
            updateTrickForAll();
        }
    }

    /**
     * Initializes the JassGame by shuffling and distributing the deck of cards in a game of Jass, determining the color of trump, as well as updating hands, players, trumpcolor,
     * current trick and score for every single player. Sets the turnState of the game to its initial state with a random trump color.
     */
    private void initializeGame() {
        // the trump color is determined randomly
        Color trump = Color.ALL.get(trumpRng.nextInt(Color.COUNT));
        shuffleAndDistributeCards(getAllCards());

        PlayerId firstPlayer = null;
        // first player is determined by the hand containing the seven of diamonds
        for (PlayerId pId : hands.keySet())
            if (hands.get(pId).contains(Card.of(Color.DIAMOND, Rank.SEVEN)))
                firstPlayer = pId;

        // Initializes all of the players
        for (PlayerId playerId : PlayerId.ALL) {
            Player player = players.get(playerId);
            player.setPlayers(playerId, playerNames);
            player.updateHand(hands.get(playerId));
            player.setTrump(trump);
            player.updateScore(Score.INITIAL);
            player.updateTrick(Trick.firstEmpty(trump, firstPlayer));
        }

        turnState = TurnState.initial(trump, Score.INITIAL, firstPlayer);
    }

    /**
     * Initializes a new turn in a game of Jass succeeding to the already played turns.
     */
    private void initializeTurn(PlayerId lastStarting) {
        // the trump color is determined randomly
        Color trump = Color.ALL.get(trumpRng.nextInt(4));
        shuffleAndDistributeCards(getAllCards());
        
        //player starting the new turn is the next after the former first player
        PlayerId firstPlayer = PlayerId.ALL.get((lastStarting.ordinal() + 1) % 4);
        formerFirstPlayer = firstPlayer;

        // Reinitializes all of the players with the values for the next turn
        for (PlayerId playerId : PlayerId.ALL) {
            Player player = players.get(playerId);
            player.updateHand(hands.get(playerId));
            player.setTrump(trump);
            player.updateScore(turnState.score().nextTurn());
            player.updateTrick(Trick.firstEmpty(trump, firstPlayer));
        }

        turnState = TurnState.initial(trump, turnState.score().nextTurn(), firstPlayer);
    }

    /**
     * Returns all of the cards in a game of Jass
     * 
     * @return (List<>): a List containing all of the cards in a game of Jass
     */
    private List<Card> getAllCards() {
        List<Card> allCards = new ArrayList<Card>();
        
        for (Color c : Color.ALL)
            for (Rank r : Rank.ALL)
                allCards.add(Card.of(c, r));

        return allCards;
    }
}