package ch.epfl.javass.jass;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import ch.epfl.javass.net.RemotePlayerClient;

/**
 * Used for debugging, DELETE BEFORE RENDU
 * 
 * @author wengl
 *
 */
public final class DistantGame {

    public static void main(String[] args) {

        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
        
        

        try (RemotePlayerClient client = new RemotePlayerClient("localhost")) {

            for (PlayerId pId : PlayerId.ALL) {
                Player player = new RandomPlayer(2019);

                if (pId == PlayerId.PLAYER_2)
                    player = client;
                players.put(pId, player);
                playerNames.put(pId, pId.name());
            }

            JassGame g = new JassGame(2019, players, playerNames);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
            }
            
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        catch(Exception a) {
            //ignore
        }
  
    }
}
