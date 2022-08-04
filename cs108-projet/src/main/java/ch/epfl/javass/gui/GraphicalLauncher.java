
package ch.epfl.javass.gui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * The class Graphical launcher constitutes the bonus of our Javass Game. It represents a graphical interface to start the game.
 * Launches the game as soon as the user has clicked on start and all the arguments are verified, or joins an existing game.
 * 
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 *
 */
public final class GraphicalLauncher {

    // default names for the players
    private final static String[] DEFAULT_NAMES = { "Aline", "Bastien", "Colette", "David" };

    // default labels for the drop down menus
    private final static String HUMAN_LABEL = "Human",
                                SIM_LABEL = "Simulated",
                                REMOTE_LABEL = "Remote",
                                DEFAULT_IP = "127.0.0.1";
    // default numbers for the drop down menus
    private final static int DEFAULT_ITERATIONS = 10_000,
                             DEFAULT_PLAYERDELAY = 2,
                             DEFAULT_GAMEDELAY = 1000;  
    // panes showed as main scene
    private final Pane remotePane, localPane;
    
    //boolean property used to close the launcher as soon as the game is running
    private final SimpleBooleanProperty isRunning;

    
    /**
     * Standard constructor of the GraphicalLauncher. Prepares all the panes used to start the game and sets isRunning to false.
     */
    public GraphicalLauncher() {
        remotePane = createRemotePane();
        localPane = createLocalPane();
        isRunning = new SimpleBooleanProperty(false);
    }

    /**
     * Creates and returns the stage in a new Thread
     * @return the newly created stage
     */

    public Stage createStage() {
        //creates the main stage and adds a gridpane
        Stage stage = new Stage();
        GridPane main = new GridPane();
        main.getStyleClass().add("grid");
        
        //random generator used for the background
        Random rg = new Random();

        //creates the Title
        Text title = new Text("JAVASS");
        title.getStyleClass().add("text");
        title.setId("title");
        
        // creates the explanation to choose a game
        Text explanation = new Text("Choose a game");
        explanation.getStyleClass().add("text");
        
        //creates the host and the join button
        Button host = new Button("host game");
        Button join = new Button("join game");
        host.setOnMouseEntered(e -> explanation.setText("Host a game on your local system"));
        join.setOnMouseEntered(e -> explanation.setText("Join a game running on a different system"));
        HBox buttons = new HBox();
        buttons.setId("buttons");
        buttons.getChildren().addAll(host, join);

        // creates the creators signature
        Text creators = new Text("Presented by: Erik Wengle, Hannah Casey");
        creators.getStyleClass().add("text");
        creators.setId("authors");
        
        //sets the background icon and puts the buttons on top
        StackPane trmp = new StackPane(new ImageView(new Image(String.format("/trump_%d.png", rg.nextInt(Card.Color.COUNT)))), buttons);

        // adds all nodes to the main pane
        main.setBackground(new Background(new BackgroundImage(new Image("/felt-bg.jpg"), null, null, null, null)));
        main.getStyleClass().add("screen");
        
        //places all the components of the main scene
        main.add(new HBox(title), 0, 0);
        main.add(trmp, 0, 1);
        main.add(new HBox(explanation), 0, 2);
        main.add(new HBox(creators), 0, 3);
        

        //initialization of the stage
        Scene s = new Scene(new StackPane(main, localPane));
        s.getStylesheets().add("stylesheet.css");
        stage.setScene(s);
        stage.setTitle("Javass Launcher");
        stage.getIcons().add(new Image("/trump_0.png"));

        // closes the launcher as soon as the game is running
        isRunning.addListener((e, o, n) -> {
            try {
                Thread.sleep(DEFAULT_GAMEDELAY);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            stage.close();
        });
        
        // sets a new scene when either the host or the join button is clicked
        host.setOnMouseClicked(e -> localPane.visibleProperty().set(true));
        join.setOnMouseClicked(e -> {
            Scene rs = new Scene(remotePane);
            rs.getStylesheets().add("stylesheet.css");
            stage.setScene(rs);

            // creates new thread to run the model of the game separately from the graphical interface
            Thread gameThread = new Thread(() -> {

                GraphicalPlayerAdapter p = new GraphicalPlayerAdapter();
                isRunning.bind(p.activeProperty());

                RemotePlayerServer r = new RemotePlayerServer(p);
                r.run();
            });

            gameThread.setDaemon(true);
            gameThread.start();
        });

        return stage;
    }

    /**
     * Creates a new Pane to join a game of Javass, that shows the players IP adress
     * @return the pane for a remote pane
     */
    public Pane createRemotePane() {

        //creates the remote Pane
        BorderPane remoteGame = new BorderPane();
        remoteGame.getStyleClass().add("screen");

        //creates the text shown on the pane
        Text remoteLoad = new Text();
        remoteLoad.getStyleClass().add("text");
        try {
            remoteLoad.setText("Waiting for connection... " + "\n" + "Your IP is: " + InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            remoteLoad.setText("Error: something has gone wrong! Please restart the game.");
        }

        remoteGame.setBackground(new Background(new BackgroundImage(new Image("/felt-bg.jpg"), null, null, null, null)));
        remoteGame.setTop(new StackPane(new ImageView(new Image("/trump_0.png"))));
        remoteGame.setCenter(remoteLoad);

        return remoteGame;
    }
    
    /**
     * Creates a new Pane to host a game
     * @return the pane for a local game
     */
    public Pane createLocalPane() {

        //localGame Pane is invisible when created
        GridPane localGame = new GridPane();
        localGame.getStyleClass().add("screen");
        localGame.setBackground(new Background(new BackgroundImage(new Image("/felt-bg.jpg"), null, null, null, null)));
        localGame.setVisible(false);
        
        TextField[] names = new TextField[PlayerId.COUNT];
        TextField[] its = new TextField[PlayerId.COUNT];
        TextField[] ips = new TextField[PlayerId.COUNT];
        List<ComboBox<String>> playerTypes = new ArrayList<>(PlayerId.COUNT);

        for (int i = 0; i < PlayerId.COUNT; ++i) {
            
            // DropDown Menu to choose the player type
            ComboBox<String> playerPicker = new ComboBox<>();
            playerPicker.getItems().addAll(HUMAN_LABEL, SIM_LABEL, REMOTE_LABEL);
            playerTypes.add(playerPicker);

            // first player is by default human, whilst others are by default simulated
            if (i == 0)
                playerPicker.getSelectionModel().selectFirst();
            else
                playerPicker.getSelectionModel().select(1);

            TextField nameField = new TextField(DEFAULT_NAMES[i]);

            // adds additional info needed for simulated / remote player
            TextField iterationsField = new TextField(Integer.toString(DEFAULT_ITERATIONS));
            VBox iterations = new VBox(new Label("Number of iterations:"), iterationsField);
            iterations.visibleProperty().bind(playerPicker.valueProperty().isEqualTo(SIM_LABEL));

            TextField ipField = new TextField(DEFAULT_IP);
            VBox ip = new VBox(new Label("IP address: "), ipField);
            ip.visibleProperty().bind(playerPicker.valueProperty().isEqualTo(REMOTE_LABEL));
           
            StackPane additional = new StackPane(ip, iterations);

            //fields are stored in separate arrays in order to be able to extract the information
            names[i] = nameField;
            its[i] = iterationsField;
            ips[i] = ipField;
            
            //sets an icon for each player
            ImageView playerIcon = new ImageView(new Image(String.format("trump_%d.png", i)));
            playerIcon.setFitHeight(20);
            playerIcon.setFitWidth(20);
            
            HBox playerTitle = new HBox(playerIcon, new Text("PLAYER " + (i + 1)));
            playerTitle.setId("playerHeader");

            VBox playerInfo = new VBox(playerTitle, new Label("Playertype:"), playerPicker, new Label("Name"), nameField, additional);
            playerInfo.getStyleClass().add("playerinfo");

            //creates two different styles, one for each team
            if (i % 2 == 0)
                playerInfo.setId("info-red");
            else
                playerInfo.setId("info-white");
            
            localGame.add(playerInfo, i, 0);
        }

        //creates the seed field 
        Label seedLabel = new Label("Seed: ");
        TextField seedField = new TextField();
        ImageView bean = new ImageView(new Image("boninhos.png"));
        bean.setFitHeight(30);
        bean.setFitWidth(30);
        seedLabel.setGraphic(bean);
        
        HBox seed = new HBox(seedLabel, seedField);
        seed.setId("seed");
        localGame.add(seed, 1, 2, 2, 1);
        

        //creates the start button
        Button startButton = new Button("start game");
        localGame.add(startButton, 3, 4);
        GridPane.setHalignment(startButton, HPos.RIGHT);

        //creates the return button, if clicked the localGame Pane becomes invisible 
        Button returnButton = new Button("return");
        returnButton.setOnMouseClicked((e) -> localGame.setVisible(false));
        localGame.add(returnButton, 0, 4);

        //creates the error box
        Text errorMessage = new Text();
        errorMessage.setId("errorMessage");
        HBox errorBox = new HBox(errorMessage);
        SimpleBooleanProperty noErrors = new SimpleBooleanProperty(true);
        errorBox.visibleProperty().bind(noErrors.not());
        errorBox.setId("errorBox");
        localGame.add(errorBox, 0, 1, 4, 1);

        // initialization of local game
        startButton.setOnMouseClicked(e -> {
            
            // used for initializing game
            noErrors.set(true);
            // used for generating the random sets for the MctsPlayers and JassGame
            Random seedGenerator = new Random();
            
            //verifies if the given seed is valid
            if (!seedField.getText().isEmpty()) {
                try {
                    seedGenerator = new Random(Long.parseLong(seedField.getText()));
                } catch (NumberFormatException nfe) {
                    errorMessage.setText("ERROR: Seed must have a valid long value or be left empty");
                    noErrors.set(false);
                }
            }
            // seed for the game must be generated before the seeds of the mcts players to ensure correct order
            long gameSeed = seedGenerator.nextLong();

            // players and their corresponding names used for the initialization of the game
            Map<PlayerId, String> playerNames = new HashMap<>();
            Map<PlayerId, Player> players = new HashMap<>();

            //verifies if all the names of the players have been specified
            for (int i = 0; i < PlayerId.COUNT; ++i) {
                String label = playerTypes.get(i).getValue();
                String name = names[i].getText();

                if (name.isEmpty()) {
                    errorMessage.setText(String.format("ERROR: Name for %s was not specified", PlayerId.ALL.get(i).name()));
                    noErrors.set(false);
                }

                PlayerId pId = PlayerId.ALL.get(i);
                long mctsSeed = seedGenerator.nextLong();

                // verifies the arguments depending on the type of the player
                switch (label) {
                
                case HUMAN_LABEL:
                    players.put(pId, new GraphicalPlayerAdapter());
                    playerNames.put(pId, name);
                    break;
                case SIM_LABEL:
                    int iterations = DEFAULT_ITERATIONS;

                    //verifies the given number of iterations
                    try {
                        iterations = Integer.parseInt(its[i].getText());
                    } catch (NumberFormatException nfe) {
                        errorMessage.setText(String.format("ERROR: Number of iterations for %s is not a valid int-value",
                                PlayerId.ALL.get(i).name()));
                        noErrors.set(false);
                    }
                    
                    players.put(pId, new PacedPlayer(new MctsPlayer(pId, mctsSeed, iterations), DEFAULT_PLAYERDELAY));
                    playerNames.put(pId, name);
                    break;

                case REMOTE_LABEL:
                    String ip = ips[i].getText();
                    //connects to the remote player
                    try {
                        players.put(pId, new RemotePlayerClient(ip));
                    } catch (IOException ioe) {
                        errorMessage.setText("ERROR: Connection to server of remote player failed. Is the IP Address correct?");
                        noErrors.set(false);
                    }
                    playerNames.put(pId, name);
                    break;

                default:
                    errorMessage.setText("ERROR: An unexpected error has occured. Please restart the game");
                    noErrors.set(false);
                }
            }
            //starts the game if no errors occurred
            if (noErrors.getValue()) {
                Thread gameThread = new Thread(() -> {
                    JassGame g = new JassGame(gameSeed, players, playerNames);
                    while (!g.isGameOver()) {
                        g.advanceToEndOfNextTrick();

                        // wait 1 s before collecting
                        try {
                            Thread.sleep(DEFAULT_GAMEDELAY);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                gameThread.setDaemon(true);
                gameThread.start();
                isRunning.set(true);
            }
        });

        return localGame;
    }
}
