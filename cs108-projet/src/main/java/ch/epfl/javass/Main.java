package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalLauncher;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The class Main represents the Main class in a game of Javass. It is used to either launch a local game or to join a game.
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 *
 */
public final class Main extends Application {

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
        GraphicalLauncher l = new GraphicalLauncher();
        l.createStage().show();
    }

}
