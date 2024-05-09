package communication;

import game.MoveGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.List;

public class Player {

    static MoveGenerator moveGenerator = new MoveGenerator();

    public static void main(String[] args) {
        String serverAddress = "127.0.0.1";
        int port = 9999; // server port

        // connect to server
        try (Socket socket = new Socket(serverAddress, port)) {
            System.out.println("Connected to the server.");

            // input and output streams
            PrintWriter outToServer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            int i = 0;
            while (i <= 10) {

                String server_fen = inFromServer.readLine();
                System.out.println("Server says: " + server_fen);

                if (server_fen.equals("GAME OVER")) {
                    break;
                }

                moveGenerator.initializeBoard(server_fen);
                LinkedHashMap<Integer, List<Integer>> moves = moveGenerator.getMovesWrapper(server_fen);
                String move = moveGenerator.getRandomMove(moves);

                if (server_fen != null) {
                    outToServer.println(move);
                    System.out.println("I sent: " + move);
                }
            }
        } catch (IOException e) {
            System.err.println("Error occurred while connecting to the server: " + e.getMessage());
        }
    }
}