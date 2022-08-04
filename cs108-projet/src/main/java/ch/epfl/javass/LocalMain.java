package ch.epfl.javass;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The class LocalMain represents the Main class in a local game of Javass. It is used to launch a game of Javass on a local system,
 * where the four players participating can be either human, simulated or remote players using a server.
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 *
 */
public final class LocalMain extends Application {

    private final static String[] DEFAULT_NAMES = { "Aline", "Bastien", "Colette", "David" };

    private final static String HUMAN_LABEL = "h",
                                SIM_LABEL = "s",
                                REMOTE_LABEL = "r",
                                DEFAULT_IP = "127.0.0.1";

    private final static int DEFAULT_ITERATIONS = 10_000,
                             DEFAULT_PLAYERDELAY = 2, 
                             DEFAULT_GAMEDELAY = 1000,
                             INDEX_LABEL = 0, 
                             INDEX_NAME = 1,
                             INDEX_ADDITIONAL = 2, 
                             MAX_LENGTH_HUMAN = 2, 
                             MAX_LENGTH_REMOTE = 3, 
                             MAX_LENGTH_SIM = 3, 
                             MIN_LENGTH_PLAYERARGS = 1;
                            

    /**
     * The main method is used to launch the JavaFX-application on a new thread
     * @param args: The arguments used to initialize the game, must contain at least four strings specifying the players,
     * may also contain an optional seed for the Random seedGenerator
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the game with the given parameters, creating a graphical representation of the game.
     */
    @Override
    public void start(Stage arg0) throws Exception {

        List<String> args = getParameters().getRaw();

        
        //verifies that the size of arguments was valid, explains how to initialize game if args are invalid
        if (!(args.size() == PlayerId.COUNT || (args.size() == PlayerId.COUNT + 1))) {
            System.err.println("ERROR: Size of arguments invalid: must be 4 or 5");
            
            System.out.println("INITIALIZATION OF THE GAME: \n" +
                               "Usage: java ch.epfl.javass.LocalMain <j1>...<j4> [<seed>] \n" +
                               "Where :\n" + 
                               "<jn> specifies the player n, as follows: \n" +
                               " h:<name> a human player called <name> \n" +
                               " s:<name>:<it> a simulated player called <name>, whose algorithm will run for <it> iterations \n" +
                               " r:<name>:<ip> a remote player called <name>, whose server has the address <ip> \n" +
                               "The arguments <name>, <it>, and <ip> are all optional  \n" +
                               "The default ip adress is 127.0.0.1, the default number of iterations is 10'000 \n" +
                               "The default names for players 1-4 are Aline, Bastien, Colette and David (in that order)");
            System.exit(1);
        }

        
        // used for generating the random seeds for the MctsPlayers and JassGame
        Random seedGenerator = new Random();

        // If an additional argument was given, it will be used as seed for the seed-generator
        if (args.size() > PlayerId.COUNT) {
            try {
                seedGenerator = new Random(Long.parseLong(args.get(PlayerId.COUNT)));
            } catch (NumberFormatException nfe) {
                System.err.println("ERROR SEED: Seed must be a valid long-value");
                System.exit(1);
            }
        }

        //seed for the game must be generated before the seeds of the mcts players to ensure correct order
        long gameSeed = seedGenerator.nextLong();
        
        //players and their corresponding names used for the initialization of the game
        Map<PlayerId, String> playerNames = new HashMap<>();
        Map<PlayerId, Player> players = new HashMap<>();
        
        //verifies and initializes all four players
        for (int i = 0; i < PlayerId.COUNT; ++i) {
            
            String[] playerArgs = args.get(i).split(":");
            String label = playerArgs[INDEX_LABEL];
            String name = DEFAULT_NAMES[i];

            PlayerId pId = PlayerId.ALL.get(i);
            long mctsSeed = seedGenerator.nextLong();

            //extracts the name given for the player, if it exists
            if (playerArgs.length > MIN_LENGTH_PLAYERARGS && !playerArgs[INDEX_NAME].isEmpty())
                name = playerArgs[INDEX_NAME];

            //verifies the arguments depending on the type of the player
            switch (label) {
            
            case HUMAN_LABEL:
                if (playerArgs.length > MAX_LENGTH_HUMAN) {
                    System.err.println("ERROR HUMANPLAYER: Too many arguments for a human player, must be at most" + MAX_LENGTH_HUMAN);
                    System.exit(1);
                }

                players.put(pId, new GraphicalPlayerAdapter());
                playerNames.put(pId, name);
                break;

            case SIM_LABEL:
                
                int iterations = DEFAULT_ITERATIONS;

                if (playerArgs.length == MAX_LENGTH_SIM) {
                    try {
                        iterations = Integer.parseInt(playerArgs[INDEX_ADDITIONAL]);
                    } catch (NumberFormatException nfe) {
                        System.err.println("ERROR SIMPLAYER: Number of iterations must be a valid int-value");
                        System.exit(1);
                    }

                    if (iterations < Jass.HAND_SIZE) {
                        System.err.println("ERROR SIMPLAYER: Number of iterations must be greater or equal than " + Jass.HAND_SIZE);
                        System.exit(1);
                    }
                } else if (playerArgs.length > MAX_LENGTH_SIM) {
                    System.err.println("ERROR SIMPLAYER: Too many arguments for a simulated player, must be at most " + MAX_LENGTH_SIM);
                    System.exit(1);
                }

                players.put(pId, new PacedPlayer(new MctsPlayer(pId, mctsSeed, iterations), DEFAULT_PLAYERDELAY));
                playerNames.put(pId, name);
                break;
                
            case REMOTE_LABEL:
                
                String ip = DEFAULT_IP;

                if (playerArgs.length == MAX_LENGTH_REMOTE) {
                    ip = playerArgs[INDEX_ADDITIONAL];
                } else if (playerArgs.length > MAX_LENGTH_REMOTE) {
                    System.err.println("ERROR REMOTEPLAYER: Too many arguments for a remote player, must be at most " + MAX_LENGTH_REMOTE);
                    System.exit(1);
                }

                try {
                    players.put(pId, new RemotePlayerClient(ip));
                } catch (IOException ioe) {
                    System.err.println("ERROR REMOTEPLAYER: Connection to server failed");
                    System.exit(1);
                } 

                playerNames.put(pId, name);
                break;
            default:
                System.err.println("ERROR: Invalid player specification: " + label);
                System.exit(1);
            }
        }
        
        //creates new thread to run the model of the game separately from the graphical interface
        Thread gameThread = new Thread(() -> {
            JassGame g = new JassGame(gameSeed, players, playerNames);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                
                // wait 1s before collecting
                try {
                    Thread.sleep(DEFAULT_GAMEDELAY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        gameThread.setDaemon(true);
        gameThread.start();
    }
}
