package communication;

import game.MoveGenerator;
import search.SearchConfig;
import search.ab.Minimax_AB;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
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

    HashMap<String, Integer> visitedPositions = new HashMap<>();

    HashMap<String, String> startingBib = new HashMap<>();

    /**
     * Connects to the game server and manages game play.
     *
     * @param isPlayer Indicates if the player is a human player (true) or AI (false).
     */
    public void connect(boolean isPlayer) {
        Minimax_AB ai = new Minimax_AB();
        SearchConfig config = Minimax_AB.bestConfig.copy();
        String serverAddress = "localhost";
        int port = 5555;

        MoveGenerator gameInstance =  new MoveGenerator();

        double overall = 115000.0; // overall time (in ms)
        int averageMoves = 40;
        config.timeLimit = overall / averageMoves; // set time for move (in ms)

        try (Socket server = new Socket(serverAddress, port)) {

            PrintWriter outputStream = new PrintWriter(server.getOutputStream(), true);
            InputStream inputStream = server.getInputStream();

            int temp = (int) inputStream.read();
            if (temp == 48) {
                this.player = 1;
                String basePath = new File("").getAbsolutePath();
                String path = basePath+"\\src\\main\\java\\search\\mcts_lib\\opening_book_startingMove.txt";
                readFileAndFillBib(path);
                System.out.println("\n" + "You are Player 1");

            } else {
                this.player = 2;
                String basePath = new File("").getAbsolutePath();
                String path = basePath+"\\src\\main\\java\\search\\mcts_lib\\opening_book_secondMove.txt";
                readFileAndFillBib(path);
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

                    String fenNoPlayer = fen.substring(0, fen.length() - 2);

                    // check for draw
                    gameInstance.initializeBoard(fenNoPlayer);
                    if(visitedPositions.containsKey(fenNoPlayer) ){
                        int numberOfVisits = visitedPositions.get(fenNoPlayer);
                        if (numberOfVisits == 3) {
                            return;
                        } else {
                            visitedPositions.put(fenNoPlayer, numberOfVisits + 1);
                        }
                    } else {
                        visitedPositions.put(fenNoPlayer,1);
                    }

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
                                //check if a position is in the starting bib
                                String moveStartingBib = startingBib.get(fenNoPlayer);
                                if (moveStartingBib!=null){
                                    this.move=moveStartingBib;
                                }
                                else {
                                    // check for dynamic time management
                                    if (moveCounter <= 6 || (31 < moveCounter && moveCounter <= 39)) {
                                        config.timeLimit = (overall * 0.2) / 15;
                                    } else if (timeLeft <= 5000) {
                                        config.timeLimit = (timeLeft * 0.5);
                                    } else {
                                        config.timeLimit = (overall * 0.8) / 25;
                                    }
                                    this.move = ai.orchestrator(fen, config);
                                    moveCounter++;
                                }
                            }

                            outputStream.println(gson.toJson(this.move));
                            gameInstance.movePiece(gameInstance.convertStringToPos(this.move));
                            String fenAfterMove = gameInstance.getFenFromBoard();

                            // check for draw
                            if(visitedPositions.containsKey(fenAfterMove)){
                                int numberOfVisits = visitedPositions.get(fenAfterMove);
                                if (numberOfVisits == 3){
                                    return;
                                } else {
                                    visitedPositions.put(fenAfterMove, numberOfVisits + 1);
                                }
                            } else {
                                visitedPositions.put(fenAfterMove, 1);
                            }

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
     *
     * @param fileLocation Path to the File
     * @throws IOException
     */
    public void readFileAndFillBib(String fileLocation) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileLocation), StandardCharsets.UTF_8));
        String line;
        int i  = 0;
        while ((line=in.readLine())!=null) {
            String[] tokens = line.split(", ");
            startingBib.put(tokens[0], tokens[1]);
            i  = i+1;
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
