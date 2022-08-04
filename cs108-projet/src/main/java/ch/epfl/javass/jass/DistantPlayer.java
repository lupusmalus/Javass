package ch.epfl.javass.jass;

import java.io.IOException;

import ch.epfl.javass.net.RemotePlayerServer;

public class DistantPlayer {
    public static void main(String[] args) throws IOException {
        RemotePlayerServer server = new RemotePlayerServer(new PrintingPlayer(new RandomPlayer(2019)));
            server.run();
    }
}
