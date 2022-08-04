package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;


/**
 * The class TrickBean represents a JavaFX bean containing all the graphically observable properties of a trick in a game of Jass
 * 
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 *
 */
public final class TrickBean {

    private final SimpleObjectProperty<Color> trump;
    private final SimpleObjectProperty<PlayerId> winningPlayer;
    
    private final ObservableMap<PlayerId, Card> trick;

    
    /**
     * Standard constructor of TrickBean, sets the trump and winning player to null, leaves the trick empty
     */
    public TrickBean() {
        trump = new SimpleObjectProperty<>();
        trick = FXCollections.observableHashMap();

        for (PlayerId p : PlayerId.ALL)
            trick.put(p, null);

        winningPlayer = new SimpleObjectProperty<>();
    }

    
    /**
     * Returns trump color's property
     * @return The property of the trump color
     */
    public ReadOnlyObjectProperty<Color> trumpProperty() {
        return trump;
    }

    /**
     * Sets the trump color's property
     * @param color (Color): The new trump color
     */
    public void setTrump(Color color) {
        trump.set(color);
    }

    /**
     * Returns the trick property
     * @return An unmodifiable observable map containing the played trick
     */
    public ObservableMap<PlayerId, Card> trick() {
        return FXCollections.unmodifiableObservableMap(trick);
    }

    
    /**
     * Sets the trick property
     * @param newTrick (Trick): The new trick to set
     */
    public void setTrick(Trick newTrick) {
        for (int i = 0; i < PlayerId.COUNT; ++i)
            if (i < newTrick.size())
                trick.put(newTrick.player(i), newTrick.card(i));
            else
                trick.put(newTrick.player(i), null);
        
        if(!newTrick.isEmpty())
            winningPlayer.set(newTrick.winningPlayer());
        else
            winningPlayer.set(null);
    }
    
    /**
     * Returns the property of the winning player
     * @return The winning player's property
     */
    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty() {
        return winningPlayer;
    }
}
