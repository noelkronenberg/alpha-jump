package communication;

import search.ab.Minimax_AB;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Handles connection and communication with the game server.
 */
public class Connection {

    int player;
    String lastBoard = "";
    String move = "";
    Scanner scanner = new Scanner(System.in);

    int moveCounter = 0;
    long currentTime = 0;
    long timeLeft = 120000;

    /**
     * Connects to the game server and manages game play.
     *
     * @param isPlayer Indicates if the player is a human player (true) or AI (false).
     */
    public void connect(boolean isPlayer) {
        Minimax_AB ai = new Minimax_AB();
        String serverAddress = "localhost";
        int port = 5555;

        double overall = 115000.0; // overall time (in ms)
        int averageMoves = 40;
        Minimax_AB.bestConfig.timeLimit = overall / averageMoves; // set time for move (in ms)

        try (Socket server = new Socket(serverAddress, port)) {

            PrintWriter outputStream = new PrintWriter(server.getOutputStream(), true);
            InputStream inputStream = server.getInputStream();

            int temp = (int) inputStream.read();
            if (temp == 48) {
                this.player = 1;
                System.out.println("\n" + "You are Player 1");
            } else {
                this.player = 2;
                System.out.println("\n" +  "You are Player 2");
            }

            // System.out.println("\n" + "Player " + this.player + " | " + "Connected to the server.");
            
            Gson gson = new Gson();

            while (true) {
                // send request to get game status
                outputStream.println(gson.toJson("get"));

                // wait for server response
                Thread.sleep(5);

                // get server response
                byte[] data = new byte[9999];
                int bytesRead = inputStream.read(data);
                JSONObject response;

                if (bytesRead != -1) {

                    // convert response
                    String jsonString;
                    if (data[0] == 49 || data[0] == 48) { // check player turn (0 or 1)
                         jsonString = new String(data, 1, bytesRead);
                    } else {
                         jsonString = new String(data, 0, bytesRead);
                    }
                    
                    try {
                        response = new JSONObject(jsonString);
                    } catch (JSONException e) {
                        System.out.println("\n" + "Player " + this.player + " | " + "Error parsing JSON: " + jsonString);
                        continue;
                    }

                    // System.out.println("\n" + "Player "+ this.player + " | " + "Server response:  " + response);

                    String fen = response.getString("board");

                    // process server response
                    if (response.getBoolean("bothConnected") && !fen.equals(this.lastBoard)) {

                        this.lastBoard = fen;
                        // System.out.println("\n" + "Player "+ this.player + " | " + "Current board: ");
                        // System.out.println(fen);

                        // player turns
                        if ((response.getBoolean("player1") && this.player == 1) || (response.getBoolean("player2") && this.player == 2)) {
                            currentTime = System.currentTimeMillis();

                            // check if AI or human player
                            if (isPlayer) {
                                System.out.println("Enter your move: ");
                                this.move = this.scanner.nextLine();
                            } else {

                                // check for dynamic time management
                                if (moveCounter <= 6 || (31 < moveCounter && moveCounter <= 39)) {
                                    Minimax_AB.bestConfig.timeLimit = (overall * 0.2) / 15;
                                } else if (timeLeft <= 5000) {
                                    Minimax_AB.bestConfig.timeLimit = (timeLeft * 0.5);
                                } else {
                                    Minimax_AB.bestConfig.timeLimit = (overall * 0.8) / 25;
                                }

                                this.move = ai.orchestrator(fen, Minimax_AB.bestConfig);
                                moveCounter++;
                            }

                            outputStream.println(gson.toJson(this.move));

                            long timeForMove = System.currentTimeMillis() - currentTime;
                            timeLeft -= timeForMove;

                            System.out.println("Time For move: " + (timeForMove));
                            System.out.println("Time left for moves: " + timeLeft);
                            System.out.println("Move: " + moveCounter);

                            /*
                            MoveGenerator moveGenerator = new MoveGenerator();
                            moveGenerator.initializeBoard(fen);
                            if (moveGenerator.isGameOver(fen)) {
                                Thread.sleep(3000);
                                System.exit(0);
                               break;
                            }
                            moveGenerator.printBoard(true);
                            */

                            System.out.println("Player " + this.player + " | Move: " + this.move);
                            System.out.println("\n");
                        }
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("\n" + "Player " + this.player + " | " +  "Error occurred while connecting to the server: " + e.getMessage());
        }
    }

    /**
     * Main method to start the connection and game play.
     *
     * @param args Command line arguments (not used).
     * @throws InterruptedException if the main thread is interrupted.
     */
    public static void main(String[] args) throws InterruptedException {
        Connection player1 = new Connection();
        player1.connect(false); // only for single player

        /*
        // START: two player game

        Connection player2 = new Connection();

        Thread thread1 = new Thread(() -> player1.connect(false));
        Thread thread2 = new Thread(() -> player2.connect(false));

        thread1.start();
        Thread.sleep(100);
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // END: two player game
         */

    }
}
