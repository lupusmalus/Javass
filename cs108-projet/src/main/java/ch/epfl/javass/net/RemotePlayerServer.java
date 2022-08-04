package ch.epfl.javass.net;

import static ch.epfl.javass.net.StringSerializer.deserializeComposition;
import static ch.epfl.javass.net.StringSerializer.deserializeInt;
import static ch.epfl.javass.net.StringSerializer.deserializeLong;
import static ch.epfl.javass.net.StringSerializer.deserializeString;
import static ch.epfl.javass.net.StringSerializer.serializeInt;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.TreeMap;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * The class RemotePlayerServer represents a server used to communicate with the corresponding client over the network, to enable
 * a player to play from a different program instance. The objective of the server is to inform to represent a remote player on
 * the localPlayer program instance, whilst informing over the game's procedure taking place on a different program instance. Once
 * initialized, the server will keep running until the game is over, that is to say a winner has been determined.
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 * @see RemotePlayerClient
 */
public final class RemotePlayerServer {

    // the underlying player whose methods shall be called
    private final Player localPlayer;
    
    public final static int PORT = 5108;
   


    /**
     * Standard constructor of the class RemotePlayerServer, let's the server connect to the network and let's it run until the
     * game is over
     * @param localPlayer (Player): The player on the local system
     */
    public RemotePlayerServer(Player localPlayer) {
        this.localPlayer = localPlayer;
    }

    
    
    /**
     * Runs the game for the remote player, determining its methods based on the commands received by the client and sending the
     * results back to the latter
     * @throws IOException
     */
    public void run() {
        try (ServerSocket s0 = new ServerSocket(PORT);
                Socket s = s0.accept();
                BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream(), UTF_8));
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), UTF_8))) {
            
            String msg;
            
            while ((msg = r.readLine()) != null) {
                
                // the command and the required arguments for the method to invoke
                String[] args = deserializeComposition(" ", msg);
                JassCommand cmd = JassCommand.valueOf(args[0]);

                switch (cmd) {
                case TRMP:
                    localPlayer.setTrump(Color.ALL.get(deserializeInt(args[1])));
                    break;
                case HAND:
                    localPlayer.updateHand(CardSet.ofPacked(deserializeLong(args[1])));
                    break;
                case TRCK:
                    localPlayer.updateTrick(Trick.ofPacked(deserializeInt(args[1])));
                    break;
                case SCOR:
                    localPlayer.updateScore(Score.ofPacked(deserializeLong(args[1])));
                    break;
                case WINR:
                    localPlayer.setWinningTeam(TeamId.ALL.get(deserializeInt(args[1])));
                    break;
                case CARD:
                    // decomposition of the arguments for the turnstate
                    String[] tsArgs = deserializeComposition(",", args[1]);
                    Card c = localPlayer.cardToPlay(
                            TurnState.ofPackedComponents(deserializeLong(tsArgs[0]), deserializeLong(tsArgs[1]), deserializeInt(tsArgs[2])),
                            CardSet.ofPacked(deserializeLong(args[2])));

                    // responds with the card to play
                    w.write(serializeInt(c.packed()));
                    w.write('\n');
                    w.flush();
                    break;
                case PLRS:
                    // needs to decompose the single names, which then have to be deserialized individually
                    String[] names = deserializeComposition(",", args[2]);
                    Map<PlayerId, String> playerNames = new TreeMap<>();

                    for (PlayerId p : PlayerId.ALL)
                        playerNames.put(p, deserializeString(names[p.ordinal()]));

                    localPlayer.setPlayers(PlayerId.ALL.get(deserializeInt(args[1])), playerNames);
                    break;
                default:
                    throw new IOException("Unknown JassCommand");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }
}
