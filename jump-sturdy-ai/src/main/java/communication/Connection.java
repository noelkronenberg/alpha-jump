package communication;

import search.BasisKI;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class Connection {

    int player;

    public static JSONObject toJSON(String move) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("move", move);
        return jsonObject;
    }

    public void connect(int player) {
        BasisKI ki = new BasisKI();
        String serverAddress = "localhost";
        int port = 5555;

        try (Socket server = new Socket(serverAddress, port)) {
            System.out.println("Connected to the server.");

            PrintWriter output = new PrintWriter(server.getOutputStream(), true);
            InputStream inputStream = server.getInputStream();

            while (true) {

                // send request to get game status
                output.println("\"get\"");

                // wait for server response
                Thread.currentThread().sleep(2000);

                // get server response
                byte[] data = new byte[9999];
                int bytesRead = inputStream.read(data);
                JSONObject response;

                if (bytesRead != -1) {
                    // convert response
                    String jsonString = new String(data, 1, bytesRead);
                    response = new JSONObject(jsonString);

                    System.out.println("\n" + "Server response:  " + response);

                    // process server response
                    if (response.getBoolean("bothConnected")) {

                        String fen = response.getString("board");
                        System.out.println("\n" + "Current board: ");
                        System.out.println(fen);

                        // player turns
                        if (response.getBoolean("player1") && this.player == 1) {
                            String move = ki.orchestrator(fen);
                            JSONObject moveConverted = toJSON(move);
                            output.println(moveConverted);

                            System.out.println("\n" + "Player 1 move: " + moveConverted);
                        } else {
                            String move = ki.orchestrator(fen);
                            JSONObject moveConverted = toJSON(move);
                            output.println(moveConverted);

                            System.out.println("\n" + "Player 2 move: " + moveConverted);
                        }
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Error occurred while connecting to the server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Connection player1 = new Connection();
        Connection player2 = new Connection();

        Thread thread1 = new Thread(() -> player1.connect(1));
        Thread thread2 = new Thread(() -> player2.connect(2));

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
