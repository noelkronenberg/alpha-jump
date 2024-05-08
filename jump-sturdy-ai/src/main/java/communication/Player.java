package communication;

import game.MoveGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
                String response;

                if (i == 0) { // first message
                    response = "";
                } else { // get response
                    response = inFromServer.readLine();
                    System.out.println("Server says: " + response);
                }

                if (response != null) {
                    String message = String.valueOf(i++);
                    outToServer.println(message);
                }
            }
        } catch (IOException e) {
            System.err.println("Error occurred while connecting to the server: " + e.getMessage());
        }
    }
}
