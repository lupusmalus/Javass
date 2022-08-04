package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * The class GraphicalPlayer represents the graphical interface of a general Jassgame, representing the played trick
 *  and the current score. It evolves over time according to the changes of the model
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 *
 */
public final class GraphicalPlayer {

    //panes representing the main scene
    private final Pane trickPane;
    private final Pane[] victoryPanes;
    private final Pane scorePane;
    private final Pane handPane;
    
    //name of the graphical player
    private final String ownName;

    // dimensions of the source images used
    private final static int[] DIM_CARDS_IMG_HIGHRES = {240, 360};
    private final static int[] DIM_CARDS_IMG_LOWRES = {160, 240};

    // dimensions of the images shown
    private final static int[] DIM_CARDS_TRICK = {120, 180};
    private final static int[] DIM_CARDS_HAND = {80, 120};
    private final static int DIM_TRMP = 101;

    /**
     * Standard constructor of the graphical player. Prepares all of the needed panes to represent the program. Stage must be created separately.
     * 
     * @param ownId (PlayerId): The Id of the player using the GUI
     * @param playerNames (Map): The names of every player
     * @param sb (ScoreBean): The scorebean to observe
     * @param tb (TrickBean): The trickbean to observe
     */
    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> playerNames, ScoreBean sb, TrickBean tb, HandBean hb, ArrayBlockingQueue<Card> q) {
        ownName = playerNames.get(ownId);
        trickPane = createTrickPane(tb, ownId, playerNames);
        victoryPanes = createVictoryPanes(sb, playerNames);
        scorePane = createScorePane(sb, playerNames);
        handPane = createHandPane(hb, q);
    }

    /**
     * Creates and returns the stage in a new Thread
     * 
     * @return the newly created stage
     */
    public Stage createStage() {

        // creates and places single components of the scene
        BorderPane main = new BorderPane();
        main.setTop(scorePane);
        main.setCenter(trickPane);
        main.setBottom(handPane);

        // initializes the window
        Stage stage = new Stage();
        stage.setTitle(String.format("Javass - %s", ownName));
        stage.setScene(new Scene(new StackPane(main, victoryPanes[TeamId.TEAM_1.ordinal()], victoryPanes[TeamId.TEAM_2.ordinal()])));
        stage.getIcons().add(new Image("/trump_0.png"));
        
        return stage;
    }

    /**
     * Initializes and returns the trickpane required for the GUI
     * 
     * @param tb (TrickBean): The trickbean to observe
     * @param ownId (PlayerId): The PlayerId of the player using the GUIs
     * @param playerNames (Map): The names of all the players
     * @return the initialized TrickPane
     */
    private Pane createTrickPane(TrickBean tb, PlayerId ownId, Map<PlayerId, String> playerNames) {
        GridPane trickPane = new GridPane();

        // initializes the node representing the trump
        ObservableMap<Color, Image> trumps = getAllTrmpImages();
        ImageView trmp = new ImageView();
        trmp.setFitHeight(DIM_TRMP);
        trmp.setFitWidth(DIM_TRMP);
        trmp.imageProperty().bind(Bindings.valueAt(trumps, tb.trumpProperty()));

        // creates observable map of the played cards and their corresponding images
        ObservableMap<Card, Image> cards = getAllImages(DIM_CARDS_IMG_HIGHRES[0]);

        // creates and initializes the box consisting of the card and name of every player
        VBox[] box = new VBox[4];
        for (int i = 0; i < PlayerId.COUNT; ++i) {

            // starts counting from the player using the interface
            int j = (ownId.ordinal() + i) % PlayerId.COUNT;
            PlayerId pId = PlayerId.ALL.get(j);

            // initializes the name
            Text t = new Text(playerNames.get(pId));
            t.setStyle("-fx-font: 14 Optima;");

            // initializes the image of the card
            ImageView img = new ImageView();
            img.setFitWidth(DIM_CARDS_TRICK[0]);
            img.setFitHeight(DIM_CARDS_TRICK[1]);
            img.imageProperty().bind(Bindings.valueAt(cards, Bindings.valueAt(tb.trick(), PlayerId.ALL.get(j))));

            // initializes the halo surrounding the card
            Rectangle r = new Rectangle(DIM_CARDS_TRICK[0], DIM_CARDS_TRICK[1]);
            r.setStyle("-fx-arc-width: 20;-fx-arc-height: 20;-fx-fill: transparent;-fx-stroke: lightpink;-fx-stroke-width: 5;-fx-opacity: 0.5;");
            r.setEffect(new GaussianBlur(4));
            r.visibleProperty().bind(tb.winningPlayerProperty().isEqualTo(pId));
            StackPane withHalo = new StackPane(img, r);

            // creates composition in form of a Vbox
            if (i == 0)
                box[i] = new VBox(withHalo, t);
            else
                box[i] = new VBox(t, withHalo);
            box[i].setStyle("-fx-alignment: center; -fx-padding: 5px;");
        }

        // adding / aligning all of the components
        trickPane.add(box[0], 1, 2);
        trickPane.add(box[1], 2, 0, 1, 3);
        trickPane.add(box[2], 1, 0);
        trickPane.add(box[3], 0, 0, 1, 3);
        trickPane.add(trmp, 1, 1);
        GridPane.setHalignment(trmp, HPos.CENTER);

        trickPane.setStyle("-fx-background-color: whitesmoke;-fx-padding: 5px; -fx-border-width: 3px 0px;-fx-border-style: solid; -fx-border-color: gray;-fx-alignment: center;");

        return trickPane;
    }

    /**
     * Initializes and returns the scorePane required for the GUI
     * 
     * @param sb (ScoreBean): The scorebean to observe
     * @param playerNames (Map): The names of the players
     * @return The initialized ScorePane
     */
    private Pane createScorePane(ScoreBean sb, Map<PlayerId, String> playerNames) {

        GridPane scorePane = new GridPane();
        scorePane.setStyle("-fx-font: 16 Optima; -fx-background-color: lightgray; -fx-padding: 5px;-fx-alignment: center;");

        for (TeamId t : TeamId.ALL) {
            Text gamePoints = new Text();
            Text turnPoints = new Text();
            Text trickPoints = new Text();
            Text total = new Text(" /Total: ");
            Text team = new Text(String.format("%s and %s: ", playerNames.get(PlayerId.ALL.get(t.ordinal())), playerNames.get(PlayerId.ALL.get(t.ordinal() + TeamId.COUNT))));

            // auxiliary property to determine the trickpoints obtained in the last collected trick
            StringProperty tp = new SimpleStringProperty();
            sb.turnPointsProperty(t).addListener((p, o, n) -> tp.set(n.intValue() == Score.INITIAL.turnPoints(t) ? null : String.format(" (+%d)", n.intValue() - o.intValue())));

            // binds all of the text properties to the evolving values of the score
            trickPoints.textProperty().bind(tp);
            gamePoints.textProperty().bind(Bindings.convert(sb.gamePointsProperty(t)));
            turnPoints.textProperty().bind(Bindings.convert(sb.turnPointsProperty(t)));

            // adds the score to the pane and aligns some of its columns
            scorePane.addRow(t.ordinal(), team, turnPoints, trickPoints, total, gamePoints);

            GridPane.setHalignment(team, HPos.RIGHT);
            GridPane.setHalignment(turnPoints, HPos.RIGHT);
            GridPane.setHalignment(gamePoints, HPos.RIGHT);
        }
        return scorePane;
    }

    /**
     * Initializes and returns the VictoryPanes of each team required for the GUI
     * 
     * @param sb (ScoreBean): The scorebean to observe
     * @param playerNames (Map): The names of the players
     * @return The initialized VictoryPanes
     */
    private Pane[] createVictoryPanes(ScoreBean sb, Map<PlayerId, String> playerNames) {

        BorderPane[] victoryPanes = new BorderPane[TeamId.COUNT];

        for (TeamId t : TeamId.ALL) {
            BorderPane b = new BorderPane();
            Text team = new Text();

            team.textProperty().bind(Bindings.format("%s and %s won with %d versus %d points.", 
                                                     playerNames.get(PlayerId.ALL.get(t.ordinal())),
                                                     playerNames.get(PlayerId.ALL.get(t.ordinal() + TeamId.COUNT)),
                                                     sb.gamePointsProperty(t),
                                                     sb.gamePointsProperty(t.other())));

            // victorypane may appear only after corresponding team has won
            b.visibleProperty().bind(sb.winningTeamProperty().isEqualTo(t));
            b.setStyle("-fx-font: 16 Optima; -fx-background-color: white;");
            b.setCenter(team);

            victoryPanes[t.ordinal()] = b;
        }

        return victoryPanes;
    }

    /**
     * Initializes and returns the handPane required for the GUI
     * 
     * @param hb (HandBean): The handbean to observe
     * @param q (ArrayBlockingQueue): The queue to insert the card to play
     * @return The initialized HandPane
     */
    private Pane createHandPane(HandBean hb, ArrayBlockingQueue<Card> q) {

        HBox box = new HBox();
        box.setStyle("-fx-background-color: lightgray; -fx-spacing: 5px; -fx-padding: 5px;");

        // Map of all cards to their respective images
        ObservableMap<Card, Image> img = getAllImages(DIM_CARDS_IMG_LOWRES[0]);

        for (int i = 0; i < Jass.HAND_SIZE; ++i) {
            
            //creates new image for the card in the hand as well as the bindings to the value
            ImageView view = new ImageView();
            ObjectBinding<Card> b = Bindings.valueAt(hb.hand(), i);
            BooleanBinding isPlayable = Bindings.createBooleanBinding(() -> hb.playableCards().contains(b.getValue()), hb.playableCards());
            
            view.setOnMouseClicked(e -> { 
                try {
                    q.put(b.getValue());
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            });

            view.imageProperty().bind(Bindings.valueAt(img, b));
            view.opacityProperty().bind(Bindings.when(isPlayable).then(1).otherwise(0.2));
            view.disableProperty().bind(isPlayable.not());
            view.setFitWidth(DIM_CARDS_HAND[0]);
            view.setFitHeight(DIM_CARDS_HAND[1]);
            box.getChildren().add(view);
        }

        return box;
    }

    /**
     * Auxiliary method to create a map of cards to their high resolution image
     * 
     * @param width (int): The desired width of the images, must be either 160 or 240
     * @return ObservableMap of all cards to their respective high resolution image
     */
    private ObservableMap<Card, Image> getAllImages(int width) {
        assert width == DIM_CARDS_IMG_HIGHRES[0] || width == DIM_CARDS_IMG_LOWRES[0];
        ObservableMap<Card, Image> img = FXCollections.observableHashMap();

        for (Color c : Color.ALL) {
            for (Rank r : Rank.ALL) {
                img.put(Card.of(c, r), new Image(String.format("/card_%d_%d_%d.png", c.ordinal(), r.ordinal(), width)));
            }
        }

        return img;
    }

    /**
     * Auxiliary method to create a map of the trump colors and their image
     * 
     * @return ObservableMap of the trump colors and their respective image
     */
    private ObservableMap<Color, Image> getAllTrmpImages() {
        ObservableMap<Color, Image> img = FXCollections.observableHashMap();

        for (Color c : Color.ALL)
            img.put(c, new Image(String.format("/trump_%d.png", c.ordinal())));

        return img;
    }
}
