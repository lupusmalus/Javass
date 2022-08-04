package ch.epfl.javass.gui;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.CardSet;
import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;

class HandBeanTest {

    @Test
    void handBeanHandPropertyWorks() {
        System.out.println("===== HAND =====");
        HandBean hb = new HandBean();
        ListChangeListener<Card> listener = e -> System.out.println(e);
        hb.hand().addListener(listener);

        CardSet h = CardSet.EMPTY
          .add(Card.of(Color.SPADE, Rank.SIX))
          .add(Card.of(Color.SPADE, Rank.NINE))
          .add(Card.of(Color.SPADE, Rank.JACK))
          .add(Card.of(Color.HEART, Rank.SEVEN))
          .add(Card.of(Color.HEART, Rank.ACE))
          .add(Card.of(Color.DIAMOND, Rank.KING))
          .add(Card.of(Color.DIAMOND, Rank.ACE))
          .add(Card.of(Color.CLUB, Rank.TEN))
          .add(Card.of(Color.CLUB, Rank.QUEEN));
        hb.setHand(h);
        while (! h.isEmpty()) {
          h = h.remove(h.get(0));
          hb.setHand(h);
        }
    }
    
    @Test
    void handBeanPlayableCardsPropertyWorks() {
        System.out.println("===== PLAYABLE CARDS =====");
        
        HandBean hb = new HandBean();
        SetChangeListener<Card> listener = e -> System.out.println(e);
        hb.playableCards().addListener(listener);

        CardSet h = CardSet.EMPTY
          .add(Card.of(Color.SPADE, Rank.SIX))
          .add(Card.of(Color.SPADE, Rank.NINE))
          .add(Card.of(Color.SPADE, Rank.JACK))
          .add(Card.of(Color.HEART, Rank.SEVEN))
          .add(Card.of(Color.HEART, Rank.ACE))
          .add(Card.of(Color.DIAMOND, Rank.KING))
          .add(Card.of(Color.DIAMOND, Rank.ACE))
          .add(Card.of(Color.CLUB, Rank.TEN))
          .add(Card.of(Color.CLUB, Rank.QUEEN));
        hb.setPlayableCards(h);
        while (! h.isEmpty()) {
          h = h.remove(h.get(0));
          hb.setPlayableCards(h);
        }
    }

}
