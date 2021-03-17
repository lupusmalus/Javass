package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The class RemoteMain represents the Main class in a remote game of Javass. It is used to let a human player join a game
 * of Javass running on a different system
 * 
 * @author Hannah Laureen Casey (300981)
 * @author Erik Alessandro Wengle (297099)
 *
 */
public final class RemoteMain extends Application {
    
    /**
     * The main method is used to launch the JavaFX application and run it on a new thread 
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    
    /**
     * Runs the server and waits for a connection to be established. As soon as a connection was successfully made, a
     * graphical interface will display the game running on the remote system
     */
    @Override
    public void start(Stage arg0) throws Exception {

        Thread gameThread = new Thread(() -> {
            RemotePlayerServer r = new RemotePlayerServer(new GraphicalPlayerAdapter());
            r.run();
        });
        
        gameThread.setDaemon(true);
        gameThread.start();
        
        System.out.println("Game starts as soon as connection established");
    }

}
