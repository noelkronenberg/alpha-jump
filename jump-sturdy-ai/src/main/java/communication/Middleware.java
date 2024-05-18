package communication;

import game.MoveGenerator;
import search.BasisKI;

import static game.MoveGenerator.convertMoveToFEN;

public class Middleware {

    public static String getServerValue() {
        // TODO: connect to server
        return "";
    }

    public static void send(String move) {
        // TODO: send to server
    }

    public static void main(String[] args) {
        String serverValue = getServerValue();

        BasisKI ki = new BasisKI();
        String bestMove = ki.orchestrator(serverValue);

        send(bestMove);

    }
}
