package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import ch.epfl.javass.Preconditions;

/**
 * The class MctsPlayer represents a Player in a game of Jass, who chooses his cards to play using the Monte-Carlo-Search Algorithm. The algorithm generates a tree, its root
 * being the current TurnState, with each branch of the tree being a possible evolution of the parent node's TurnState. Upon adding a node, the obtainable points for the newly
 * created game's potential evolution is computed and distributed among the other potential scenarios. After adding a certain amount of evolutions to the tree, the player then
 * chooses the most promising child of the root, that is, the card that had the best ratio of obtainable points and random turns
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 *
 */
public final class MctsPlayer implements Player {

    /**
     * The nested class Node represents a Node in a Tree used for the Monte-Carlo-Search Algorithm implemented by the class MctsPLayer. A node without a parent is considered a
     * root, and represents the current TurnState, leaving the MctsPlayer do decide which card to play. Each child of this root then represent a possible evolution of the game, the
     * edge connecting to this new Node being the played Card. Each node can have multiple children, each corresponding to different evolutions of the game. Each leaf of the tree,
     * that is, a node without any further children, determines the end of the turn's potential evolution.
     * 
     * @author Hannah Laureen Casey (300981)
     * @author Erik Alessandro Wengle (297099)
     */
    private final static class Node {
        
        //empirically fixed constant for the calculations of selecting the next child
        private static final int C = 40;

        private final TurnState state;
        
        private Node[] children;
        private long possibleChildren;
        private int obtainablePoints, randomTurns;
        

        /**
         * Default constructor of the class Node
         * 
         * @param state (TurnState): The state to be represented by the node, must not be null
         * @param possibleChildren (long): The potential edges leaving from this node, must be a valid PackedCardSet
         * @see PackedCardSet
         */
        private Node(TurnState state, long possibleChildren) {
            this.possibleChildren = possibleChildren;
            this.state = state;
            children = new Node[PackedCardSet.size(possibleChildren)];
            obtainablePoints = 0;
            randomTurns = 0;
        }

        /**
         * Determines which node should be inspected next
         * 
         * @param c (int): A constant used to determine the "exploration value": the higher the constants value, the more likely are less visited nodes to be inspected more often
         * @return The index of the child to be inspected next
         */
        private int selectChild(int c) {
            double[] vPerChild = new double[children.length];
            int indexOfBest = 0;

            for (int i = 0; i < children.length; i++) {
                Node child = children[i];
                double expl = c * Math.sqrt(2 * Math.log(randomTurns));
                
                vPerChild[i] = ((double) child.obtainablePoints/child.randomTurns) + (expl/child.randomTurns);
                
                if (vPerChild[i] > vPerChild[indexOfBest])
                    indexOfBest = i;
            }
            
            return indexOfBest;
        }

        /**
         * Determines which node should be inspected next: if no value given, is going to use the fixed constant value
         * 
         * @return The index of the child to be inspected next
         */
        private int selectChild() {
            return selectChild(C);
        }

        /**
         * Adds a new node to the node applied on, if there are no new possible children, the node will be attached to one of the existing children. The method keeps calling itself
         * recursively until it reaches a terminal leaf or a node whose children have not all yet been initialized
         * 
         * @param hand (long): A packed card set representing the hand of the player the tree should be based on
         * @param owner (PlayerId): The playerId of the player the tree should be based on
         * @return A list of all the nodes leading to the newly added node (itself being included)
         */
        private List<Node> addNode(long hand, PlayerId owner) {

            // adds the current node to the list
            List<Node> path = new ArrayList<>();
            path.add(this);

            // terminal leaf reached, no new nodes to attach
            if (children.length == 0) 
                return path;
            
            // there exist non-initialized possible nodes; initializes the first of the list
            if (!PackedCardSet.isEmpty(possibleChildren)) {

                TurnState stOfChild = state;
                int iOfChild = children.length - PackedCardSet.size(possibleChildren);

                // the card to be added to the turnstate of the new node
                int pkCardToAdd = PackedCardSet.get(possibleChildren, 0);

                // computes the turnstate and possible children of the new node
                if (PackedTrick.isFull(stOfChild.packedTrick())) 
                    stOfChild = stOfChild.withTrickCollected();
                
                stOfChild = stOfChild.withNewCardPlayed(Card.ofPacked(pkCardToAdd));
                long posChildrenOfChild = playableCards(stOfChild, hand, owner);

                // adds the new node to the children of the current node, removes the new node from the set of possible children
                children[iOfChild] = new Node(stOfChild, posChildrenOfChild);
                path.add(children[iOfChild]);
                possibleChildren = PackedCardSet.remove(possibleChildren, pkCardToAdd);
            }
            // no new children possible; the node is going to be attached to one of the existing children
            else {
                path.addAll(children[selectChild()].addNode(hand, owner));
            }

            return path;
        }

        /**
         * Computes the obtainable points for the team of the corresponding node; that is, the team of the player who played the card leading to this node
         * 
         * @param score (long): The packed score the obtainable points should be extracted from
         * @return The obtainable points for the corresponding teams
         * @see PackedScore
         */
        private int obtainablePoints(long score) {
            //by default, if initial trick is empty, the player playing the card leading to this node is the one starting the trick
            PlayerId lastPlayer = PackedTrick.player(state.packedTrick(), 0);

            // determines who played the last card based on the current trick in case it is non-empty
            if (!PackedTrick.isEmpty(state.packedTrick()))
                lastPlayer = PackedTrick.player(state.packedTrick(), PackedTrick.size(state.packedTrick()) - 1);

            // returns the points correspondingly
            return  PackedScore.gamePoints(score, (lastPlayer.team() == TeamId.TEAM_1 ? TeamId.TEAM_1 : TeamId.TEAM_2));
        }
    }

    
    private final PlayerId ownId;
    private final int maxIterations;
    private final SplittableRandom rng;

    /**
     * Standard constructor of the class MctsPlayer
     * 
     * @param ownId (PlayerId): PlayerId corresponding to the MctsPlayer
     * @param rngSeed (long): Seed used for simulating the progress of a turn
     * @param iterations (int): Maximal numbers of iterations for a tree, that is, maximal numbers of children, must be at least 9
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        Preconditions.checkArgument(iterations >= Jass.TRICKS_PER_TURN);

        maxIterations = iterations;
        rng = new SplittableRandom(rngSeed);
        this.ownId = ownId;
    }

    /**
     * Determines which card to play using the Monte-Carlo-Tree Search Algorithm
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        // the root of the tree, based on the current state of the turn
        Node root = new Node(state, playableCards(state, hand.packed(), ownId));
        
        //returns only selectable card as to save computational resources
        if (hand.size() == 1)
            return hand.get(0);

        // keeps attaching nodes to the root until the maximal number of iterations is reached
        while (root.randomTurns < maxIterations) {
            List<Node> path = root.addNode(hand.packed(), ownId);

            Node newNode = path.get(path.size() - 1);

            // computes the score of the new node by finishing the turn randomly and extracting the points, distributes the points onto all nodes of the path
            long possibleScore = possibleScore(newNode.state, hand.packed());
            for(Node n : path) {
                n.randomTurns++;
                n.obtainablePoints += n.obtainablePoints(possibleScore);
            }
        }

        // returns the card whose representing node had the best ratio of  overall obtainable points over random iterations
        return Card.ofPacked(PackedCardSet.get(playableCards(state, hand.packed(), ownId), root.selectChild(0)));
    }

    /**
     * Computes the possible score based on the given turn by finishing it in a random manner
     * 
     * @param initialState (TurnState): The state to start with
     * @param hand (long): The packed CardSet representing hand of the player
     * @return The score based on the randomly finished turn
     * @see PackedCardSet
     * @see PackedScore
     */
    private long possibleScore(TurnState initialState, long hand) {
        // creates copy of the initial state as to not modify it
        TurnState t = TurnState.ofPackedComponents(initialState.packedScore(), initialState.packedUnplayedCards(), initialState.packedTrick());

        // collects trick as to not cause exception in following methods
        if (PackedTrick.isFull(t.packedTrick()))
            t = t.withTrickCollected();

        // plays cards randomly until turn is finished
        while (!t.isTerminal()) {
            long playable = playableCards(t, hand, ownId);
            int randomCard = PackedCardSet.get(playable, rng.nextInt(PackedCardSet.size(playable)));
            t = t.withNewCardPlayedAndTrickCollected(Card.ofPacked(randomCard));
        }

        // returns the final score at the end of the turn
        return PackedScore.nextTurn(t.packedScore());
    }

    /**
     * Computes the playable cards of the next player based on the set of unplayed cards and hand of the player
     * 
     * @param state (TurnState): the state to extract the playable cards from
     * @param hand (long): The packed CardSet representing the hand of a player
     * @param ownId (PlayerId): The playerId of the player associated to the given hand
     * @return a packed CardSet containing all of the playable cards
     * @see PackedCardSet
     */
    private static long playableCards(TurnState state, long hand, PlayerId ownId) {
        PlayerId nextPlayer = null;
        
        //the cards that are unplayed and on the players hand, and all of the other unplayed cards
        long pkUnplayedHand = PackedCardSet.intersection(state.packedUnplayedCards(), hand);
        long pkUnplayedOthers = PackedCardSet.difference(state.packedUnplayedCards(), hand);

        // trick is not full yet; next player follows according to order, cards are playable according to the played trick
        if (!PackedTrick.isFull(state.packedTrick())) {
            nextPlayer = state.nextPlayer();
            return PackedTrick.playableCards(state.packedTrick(), (nextPlayer == ownId) ? pkUnplayedHand : pkUnplayedOthers);
        }
        // trick is full, next player is going to be the won collecting the trick, all cards are playable in the next round
        else {
            nextPlayer = PackedTrick.winningPlayer(state.packedTrick());
            return (nextPlayer == ownId) ? pkUnplayedHand : pkUnplayedOthers;
        }
    }
}
