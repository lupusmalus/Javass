package ch.epfl.javass.gui;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.collections.MapChangeListener;

class TrickBeanTest {

    
    @Test
    void trickTrumpWorks() {
        System.out.println("===== TRUMP =====");
        
        TrickBean tb = new TrickBean();
        tb.trumpProperty().addListener((p, o, n) -> System.out.println("old: " + o + ", new " + n));
        
        
        tb.setTrump(Color.SPADE);
        tb.setTrump(Color.HEART);
        tb.setTrump(Color.DIAMOND);
        tb.setTrump(Color.CLUB);
        tb.setTrump(Color.SPADE);
        tb.setTrump(Color.SPADE);
    }
    
    @Test
    void trickTrickPropertyWorks() {
        System.out.println("===== Trick =====");
        
        TrickBean tb = new TrickBean();
        
        MapChangeListener<PlayerId, Card> listener = e -> System.out.println(e);
        tb.trick().addListener(listener);

        List<Card> allCards = new ArrayList<Card>();
        
        
        Trick t = Trick.firstEmpty(Color.SPADE, PlayerId.PLAYER_1);
        
        for (Color c : Color.ALL)
            for (Rank r : Rank.ALL)
                allCards.add(Card.of(c, r));
        
        
        for(Card c : allCards) {
            
            if(t.isFull()) {
                t = t.nextEmpty();
                tb.setTrick(t);
                System.out.println("-----");
            }
            
            t = t.withAddedCard(c);
            tb.setTrick(t);
            System.out.println("-----");
        }
    }
}
