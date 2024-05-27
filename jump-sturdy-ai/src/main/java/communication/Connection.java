package communication;

import search.BasisKI;

import org.json.JSONObject;
import java.io.*;
import java.net.Socket;

public class Connection {

    public static JSONObject toJSON(String move) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("move", move);
        return jsonObject;
    }

    public void connect() {
        BasisKI ki = new BasisKI();
        BasisKI opponent = new BasisKI();

        String serverAddress = "localhost";
        int port = 5555;

        try (Socket server = new Socket(serverAddress, port)) {
            System.out.println("Connected to the server.");

            boolean run = true;
            while (run) {
                // get streams
                PrintWriter output = new PrintWriter(server.getOutputStream(), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(server.getInputStream()));

                // wait for connection
                output.println("get");
                String response = input.readLine();
                while (!input.ready()) {
                    // wait
                }

                // error
                if (response == null) {
                    System.out.println("Couldn't get game");
                    run = false;
                    continue;
                }

                // process response from server input
                JSONObject response_json = new JSONObject(response);
                if (response_json.getBoolean("bothConnected")) {
                    String fen = response_json.getString("board");
                    System.out.println("Current board: ");
                    System.out.println(fen);

                    // our turn
                    if (response_json.getBoolean("player1")) {
                        String move = ki.orchestrator(fen);
                        JSONObject moveConverted = Connection.toJSON(move);
                        output.println(moveConverted);

                    // opponent
                    } else {
                        String move = opponent.orchestrator(fen);
                        JSONObject moveConverted = Connection.toJSON(move);
                        output.println(moveConverted);
                    }

                }
            }

        } catch (IOException e) {
            System.err.println("Error occurred while connecting to the server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Connection player = new Connection();
        Connection opponent = new Connection();
        player.connect();
        opponent.connect();
    }

}