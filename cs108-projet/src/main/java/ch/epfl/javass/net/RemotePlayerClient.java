package ch.epfl.javass.net;

import static ch.epfl.javass.net.StringSerializer.deserializeInt;
import static ch.epfl.javass.net.StringSerializer.serializeComposition;
import static ch.epfl.javass.net.StringSerializer.serializeInt;
import static ch.epfl.javass.net.StringSerializer.serializeLong;
import static ch.epfl.javass.net.StringSerializer.serializeString;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Map;

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
 * The class RemotePlayerClient represents a player whose choices are determined  by a remote player server. The client serves as a mere communication tool between the game running on
 * its program instance and the server, running on a different program instance.
 * 
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 *
 */
public final class RemotePlayerClient implements Player, AutoCloseable {

    private Socket s;
    private BufferedReader r;
    private BufferedWriter w;

    /**
     * Standard RemotePlayerClient constructor. Tries to establish a connection to the network
     * @param host (String): The host address of the server
     * @throws IOException
     */
    public RemotePlayerClient(String host) throws IOException {
        s = new Socket(host, RemotePlayerServer.PORT);
        r = new BufferedReader(new InputStreamReader(s.getInputStream(), UTF_8));
        w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), UTF_8));
    }

    /**
     * Sends the server a command to set its players with the corresponding arguments
     * 
     * @param ownId (PlayerId): the PlayerId of the player the method is applied on
     * @param playerNames (Map<PlayerId, String>): The names and playerIds of all players
     */
    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

        String[] serializedNames = new String[PlayerId.COUNT];
        for (PlayerId p : PlayerId.ALL)
            serializedNames[p.ordinal()] = serializeString(playerNames.get(p));

        write(serializeComposition(" ", JassCommand.PLRS.name(), serializeInt(ownId.ordinal()), serializeComposition(",", serializedNames)));
    }

    /**
     * Sends the server a command to update its hand with the corresponding argument
     * 
     * @param newHand (CardSet): the new hand of the player
     */
    @Override
    public void updateHand(CardSet newHand) {
        write(serializeComposition(" ", JassCommand.HAND.name(), serializeLong(newHand.packed())));
    }

    /**
     * Sends the server a command to update its trump with the corresponding argument
     * 
     * @param trump (Color): The color that is trump
     */
    @Override
    public void setTrump(Color trump) {
        write(serializeComposition(" ", JassCommand.TRMP.name(), serializeInt(trump.ordinal())));
    }

    /**
     * Sends the server a command to update its trick with the corresponding argument
     * 
     * @param newTrick (Trick): The new trick
     */
    @Override
    public void updateTrick(Trick newTrick) {
        write(serializeComposition(" ", JassCommand.TRCK.name(), serializeInt(newTrick.packed())));
    }

    /**
     * Sends the server a command to update its score with the corresponding argument
     * 
     * @param score (Score): The new score
     */
    @Override
    public void updateScore(Score score) {
        write(serializeComposition(" ", JassCommand.SCOR.name(), serializeLong(score.packed())));
    }

    /**
     * Sends the server a command to update its winning team with the corresponding argument
     * 
     * @param winningTeam (TeamId): The team that has won
     */
    @Override
    public void setWinningTeam(TeamId winningTeam) {
        write(serializeComposition(" ", JassCommand.WINR.name(), serializeInt(winningTeam.ordinal())));
    }

    
    /**
     * Sends the server a command to determine which card to play along with the corresponding arguments. Returns the card determined by the server.
     * 
     * @param state (TurnState): the current TurnState
     * @param hand (CardSet): the current hand of the player
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Card c = null;

        write(serializeComposition(" ", JassCommand.CARD.name(), 
              serializeComposition(",", serializeLong(state.packedScore()), serializeLong(state.packedUnplayedCards()), serializeInt(state.packedTrick())),
              serializeLong(hand.packed())));

        try {
            c = Card.ofPacked(deserializeInt(r.readLine()));
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
        
        return c;
    }

    /**
     * Closes the connection of the client to the server
     */
    @Override
    public void close() throws Exception {
            s.close();
            w.close();
            r.close();
    }

    /**
     * Sends the command lines to the server
     * @param string (String): The line to send to the server
     */
    private void write(String string) {
        try {
            w.write(string);
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
