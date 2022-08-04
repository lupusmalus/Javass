package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;

public final class RandomJassGame {

    public static void main(String[] args) {
      
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
        
        playerNames.put(PlayerId.PLAYER_1, "Hans");
        playerNames.put(PlayerId.PLAYER_2, "Jörg");
        playerNames.put(PlayerId.PLAYER_3, "Blerta");
        playerNames.put(PlayerId.PLAYER_4, "Gertrud");
        
        for (PlayerId pId: PlayerId.ALL) {
            Player player = new RandomPlayer(2019);
            
            if(pId == PlayerId.PLAYER_2)
                player = new PrintingPlayer(new RandomPlayer(2019));
            players.put(pId, player);
            playerNames.put(pId, pId.name());
        }
        
        JassGame g = new JassGame(2019, players, playerNames);
        while(!g.isGameOver()) {
            g.advanceToEndOfNextTrick();
        }
    }

}
