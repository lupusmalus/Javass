package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

public final class PrintingPlayer implements Player {

    private final Player underlyingPlayer;
    
    
    public PrintingPlayer(Player underlyingPlayer) {
        this.underlyingPlayer = underlyingPlayer;
    }
    
    
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        System.out.println("It's my turn... I play : ");
        Card c = underlyingPlayer.cardToPlay(state, hand);
        System.out.println(c);
        return c;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
       
        System.out.println("These are all the players: ");
        for(PlayerId pId : PlayerId.ALL) {
            
            if(pId.equals(ownId)) {
                System.out.println(playerNames.get(pId) + " (me) ");
            }else {
                System.out.println(playerNames.get(pId));
            }
        }
    }
    
    @Override
    public void updateHand(CardSet newHand) {
        System.out.println("This is my new hand: " + newHand);
    }
    
    @Override
    public void setTrump(Color trump) {
        System.out.println("Trump is: " + trump);
    }
  
    @Override
    public void updateTrick(Trick newTrick) {
      System.out.println(newTrick);
    }
    
    @Override
    public void updateScore(Score score) {
      System.out.println("This is the new score: " + score);
    }
    
    @Override
    public void setWinningTeam(TeamId winningTeam) {
      System.out.println("The game is over, following team has won: " + winningTeam);
    }
}
