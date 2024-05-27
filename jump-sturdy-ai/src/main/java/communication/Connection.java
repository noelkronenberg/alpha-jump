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
        String serverAddress = "localhost";
        int port = 5555;

        try (Socket server = new Socket(serverAddress, port)) {
            System.out.println("Connected to the server.");

            ObjectOutputStream output = new ObjectOutputStream(server.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(server.getInputStream()); // NOTE: seems to be null

            while (true) {
                // send request to get game status
                output.writeObject("\"get\"");

                // wait for server response
                Object responseObject = input.readObject();

                if (responseObject != null) {

                    // process server response
                    JSONObject response_json = (JSONObject) responseObject;
                    if (response_json.getBoolean("bothConnected")) {
                        String fen = response_json.getString("board");
                        System.out.println("Current board: ");
                        System.out.println(fen);

                        // player turns
                        if (response_json.getBoolean("player1")) {
                            String move = ki.orchestrator(fen);
                            JSONObject moveConverted = toJSON(move);
                            output.writeObject(moveConverted);
                        } else {
                            String move = ki.orchestrator(fen);
                            JSONObject moveConverted = toJSON(move);
                            output.writeObject(moveConverted);
                        }
                    }
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error occurred while connecting to the server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Connection player1 = new Connection();
        Connection player2 = new Connection();

        Thread thread1 = new Thread(() -> player1.connect());
        Thread thread2 = new Thread(() -> player2.connect());

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
