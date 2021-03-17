package ch.epfl.javass.gui;

import java.util.Collections;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * The class HandBean represents a JavaFX bean containing all the graphically observable properties of a hand in a game of Jass
 * 
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 *
 */
public final class HandBean {

    private final ObservableList<Card> hand;
    private final ObservableSet<Card> playableCards;

    /**
     * Standard constructor of the HandBean, sets all the values of the hand to null and leaves the playable cards empty
     */
    public HandBean() {
        hand = FXCollections.observableArrayList(Collections.nCopies(9, null));
        playableCards = FXCollections.observableSet();
    }

    /**
     * Returns the observable list of the cards on the hand
     * @return the observable hand
     */
    public ObservableList<Card> hand() {
        return FXCollections.unmodifiableObservableList(hand);
    }

    /**
     * Sets the new value of the cards stored in the hand
     * @param newHand (CardSet): The new hand, takes only the first nine cards of the cardset into account
     */
    public void setHand(CardSet newHand) {
        CardSet oldHand = CardSet.EMPTY;

        for (Card c : hand)
            if (c != null)
                oldHand = oldHand.add(c);

        CardSet removedCards = oldHand.difference(newHand);
        CardSet addedCards = newHand.difference(oldHand);

        for (int i = 0; i < removedCards.size(); ++i)
            hand.set(hand.indexOf(removedCards.get(i)), null);

        for (int i = 0; i < addedCards.size(); ++i)
            hand.set(hand.indexOf(null), addedCards.get(i));
    }

    /**
     * Returns the observable set of playable cards
     * @return An unmodifiable observable set containing all of the playable cards
     */
    public ObservableSet<Card> playableCards() {
        return FXCollections.unmodifiableObservableSet(playableCards);
    }

    /**
     * Sets the new value of the observable playable cards
     * 
     * @param newPlayable (CardSet): The new playable cards, takes only the first nine cards of the cardset into account
     */
    public void setPlayableCards(CardSet newPlayable) {
        CardSet oldPlayable = CardSet.EMPTY;

        for (Card c : playableCards)
            oldPlayable = oldPlayable.add(c);

        CardSet removedCards = oldPlayable.difference(newPlayable);
        CardSet addedCards = newPlayable.difference(oldPlayable);

        for (int i = 0; i < removedCards.size(); ++i)
            playableCards.remove(removedCards.get(i));

        for (int i = 0; i < addedCards.size(); ++i)
            playableCards.add(addedCards.get(i));
    }
}
