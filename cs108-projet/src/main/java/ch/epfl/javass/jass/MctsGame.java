package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;

/**
 * Used for debugging, DELETE BEFORE RENDU
 * 
 * @author wengl
 *
 */
public final class MctsGame {

    public static void main(String[] args) {

        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();

        for (PlayerId pId : PlayerId.ALL) {
            Player player = new RandomPlayer(2019);

            if (pId == PlayerId.PLAYER_2)
                player = new PrintingPlayer(new MctsPlayer(pId, 2019, 100000));
            players.put(pId, player);
            playerNames.put(pId, pId.name());
        }

        JassGame g = new JassGame(2019, players, playerNames);
        while (!g.isGameOver()) {
            g.advanceToEndOfNextTrick();
        }

    }

}
