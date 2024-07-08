package communication;

import game.MoveGenerator;
import search.SearchConfig;
import search.ab.Minimax_AB;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import search.mcts.MCTS;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Handles connection and communication with the game server.
 */
public class Connection {

    // logic
    int player;
    String lastBoard = "";
    String move = "";
    Scanner scanner = new Scanner(System.in);
    int moveCounter = 0;
    long currentTime = 0;
    double timeLeft = 120000; // default time for entire game (in ms)
    boolean firstIter = true; // helper
    HashMap<String, Integer> visitedPositions = new HashMap<>();
    HashMap<String, String> startingBib = new HashMap<>();

    // hyperparameter
    public boolean switchMCTS = true;

    /**
     * Connects to the game server and manages game play.
     *
     * @param isPlayer Indicates if the player is a human player (true) or AI (false).
     */
    public void connect(boolean isPlayer) {
        Minimax_AB ai = new Minimax_AB();
        MCTS ai_MCTS = new MCTS();
        SearchConfig config = Minimax_AB.bestConfig.copy();
        String serverAddress = "localhost";
        int port = 5555;

        MoveGenerator gameInstance =  new MoveGenerator();

        double overall = 115000.0; // default time for main game (in ms)
        int averageMoves = 40;
        config.timeLimit = overall / averageMoves; // set time for move (in ms)

        try (Socket server = new Socket(serverAddress, port)) {

            PrintWriter outputStream = new PrintWriter(server.getOutputStream(), true);
            InputStream inputStream = server.getInputStream();

            // get OS (for file path format)
            String os = System.getProperty("os.name").toLowerCase();
            String basePath = new File("").getAbsolutePath();
            String path;

            int temp = (int) inputStream.read();
            if (temp == 48) {
                this.player = 1;
                System.out.println("\n" + "You are Player 1");

                // adjust path to opening book
                if (os.contains("win")) {
                    path = basePath + "\\src\\main\\java\\search\\mcts_lib\\opening_book_startingMove.txt";
                } else {
                    path = basePath + "/src/main/java/search/mcts_lib/opening_book_startingMove.txt";
                }
                readFileAndFillBib(path);
            } else {
                this.player = 2;
                System.out.println("\n" +  "You are Player 2");

                // adjust path to opening book
                if (os.contains("win")) {
                    path = basePath + "\\src\\main\\java\\search\\mcts_lib\\opening_book_secondMove.txt";
                } else {
                    path = basePath + "/src/main/java/search/mcts_lib/opening_book_secondMove.txt";
                }
                readFileAndFillBib(path);
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

                    // set initial time
                    if (this.moveCounter == 0 && this.firstIter) {
                        firstIter = false;
                        double serverTime = response.getDouble("time");
                        this.timeLeft = serverTime; // time for entire game
                        overall = 0.95 * this.timeLeft; // time for main game + buffer for longer end phase

                        System.out.println("Set time left to: " + this.timeLeft);
                        System.out.println("Set time for main game to: : " + overall);
                    }

                    // System.out.println("\n" + "Player "+ this.player + " | " + "Server response:  " + response);

                    String fen = response.getString("board");

                    String fenNoPlayer = fen.substring(0, fen.length() - 2);

                    if (response.getBoolean("end")) {
                        System.out.println("Game has ended!");
                        Thread.sleep(1000);
                        return;
                    }

                    // process server response
                    if (response.getBoolean("bothConnected") && !fen.equals(this.lastBoard)) {

                        this.lastBoard = fen;
                        // System.out.println("\n" + "Player "+ this.player + " | " + "Current board: ");
                        // System.out.println(fen);

                        // player turns
                        if ((response.getBoolean("player1") && this.player == 1) || (response.getBoolean("player2") && this.player == 2)) {
                            this.currentTime = System.currentTimeMillis();

                            // check for draw
                            gameInstance.initializeBoard(fenNoPlayer);
                            if(this.visitedPositions.containsKey(fenNoPlayer) ){
                                int numberOfVisits = this.visitedPositions.get(fenNoPlayer);
                                if (numberOfVisits == 3) {
                                    Thread.sleep(1000);
                                    return;
                                } else {
                                    this.visitedPositions.put(fenNoPlayer, numberOfVisits + 1);
                                }
                            } else {
                                this.visitedPositions.put(fenNoPlayer, 1);
                            }

                            // human player
                            if (isPlayer) {
                                System.out.println("Enter your move: ");
                                this.move = this.scanner.nextLine();
                            }

                            // AI
                            else {
                                //check if a position is in the starting bib
                                String moveStartingBib = this.startingBib.get(fenNoPlayer);
                                if (moveStartingBib != null){
                                    this.move = moveStartingBib;
                                } else {

                                    // START: dynamic time management

                                    // CASE: start- and endgame
                                    if (this.moveCounter <= 6 || (averageMoves-9 < this.moveCounter && this.moveCounter <= averageMoves-1)) {
                                        config.timeLimit = (overall * 0.2) / 15; // 20% of the time (for on average 15 moves in these states)
                                    // CASE: overtime (if game goes beyond averageMoves)
                                    } else if (this.timeLeft <= 5000) {
                                        config.timeLimit = (this.timeLeft * 0.5); // continuously less (but never running out directly)
                                    // CASE: mid-game
                                    } else {
                                        config.timeLimit = (overall * 0.8) / 25; // 80% of the time (for on average 25 moves in this state)
                                    }

                                    // END: dynamic time management

                                    // get move (switch to MCTS if time low)
                                    if (this.timeLeft <= 500 && this.switchMCTS) {
                                        this.move = ai_MCTS.orchestrator(fen, config);
                                    } else {
                                        this.move = ai.orchestrator(fen, config);
                                    }
                                    this.moveCounter++;
                                }
                            }

                            outputStream.println(gson.toJson(this.move));
                            gameInstance.movePiece(gameInstance.convertStringToPos(this.move));
                            String fenAfterMove = gameInstance.getFenFromBoard();

                            // check for draw
                            if(this.visitedPositions.containsKey(fenAfterMove)){
                                int numberOfVisits = this.visitedPositions.get(fenAfterMove);
                                if (numberOfVisits == 3){
                                    return;
                                } else {
                                    this.visitedPositions.put(fenAfterMove, numberOfVisits + 1);
                                }
                            } else {
                                this.visitedPositions.put(fenAfterMove, 1);
                            }

                            long timeForMove = System.currentTimeMillis() - this.currentTime;
                            this.timeLeft -= timeForMove;

                            System.out.println("Time For move: " + (timeForMove));
                            System.out.println("Time left for moves: " + this.timeLeft);
                            System.out.println("Move: " + this.moveCounter);

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
     * Fills the startingBib HashMap with precalculated starting moves.
     *
     * @param fileLocation Path to the File
     * @throws IOException
     */
    public void readFileAndFillBib(String fileLocation) throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(fileLocation), StandardCharsets.UTF_8));
        String line;
        int i  = 0;
        while ((line=in.readLine()) != null) {
            String[] tokens = line.split(", ");
            this.startingBib.put(tokens[0], tokens[1]);
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
