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

    boolean isPlayer;

    // logic
    int player;
    String lastBoard = "";
    String move = "";
    Scanner scanner = new Scanner(System.in);
    int moveCounter = 0;
    long currentTime = 0;
    double timeLeft = 120000; // time counter for entire game (in ms)
    double maxTime = 120000; // the maximum time for the game (in ms)
    boolean firstIter = true; // helper
    HashMap<String, Integer> visitedPositions = new HashMap<>();
    HashMap<String, String> openingLib = new HashMap<>();

    // hyperparameter
    public boolean switchMCTS;
    double MCTSTime; // time to start using MCTS (in ms)
    public boolean useOpeningLib;

    /**
     * Constructs a Connection object.
     *
     * @param isPlayer Indicates if the player is a human player (true) or AI (false).
     * @param switchMCTS Indicates if the AI should switch to MCTS after MCTSTime.
     * @param MCTSTime The time limit after which to switch to MCTS (in ms). Defaults to 100ms if not provided.
     * @param useOpeningLib Indicates if the AI should use an opening library.
     */
    public Connection(boolean isPlayer, boolean switchMCTS, double MCTSTime, boolean useOpeningLib) {
        this.isPlayer = isPlayer;
        this.switchMCTS = switchMCTS;
        this.MCTSTime = MCTSTime;
        this.useOpeningLib = useOpeningLib;
    }

    /**
     * Constructs a Connection object.
     *
     * @param isPlayer Indicates if the player is a human player (true) or AI (false).
     * @param useOpeningLib Indicates if the AI should use an opening library.
     */
    public Connection(boolean isPlayer, boolean useOpeningLib) {
        this.isPlayer = isPlayer;
        this.switchMCTS = false;
        this.MCTSTime = 0;
        this.useOpeningLib = useOpeningLib;
    }

    /**
     * Connects to the game server and manages game play.
     *
     */
    public void connect() {
        Minimax_AB ai = new Minimax_AB();
        MCTS ai_MCTS = new MCTS();
        SearchConfig config = Minimax_AB.bestConfig.copy();
        String serverAddress = "localhost";
        int port = 5555;

        MoveGenerator gameInstance =  new MoveGenerator();

        double overall = this.maxTime * 0.96; // default time for main game (in ms)
        int averageMoves = 40;
        config.timeLimit = overall / averageMoves; // default time for move (in ms)

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
                System.out.println("You are Player 1");
                System.out.println();

                // adjust path to opening book
                path = "opening_book_startingMove.txt";
                readFileAndFillBib(path);
            } else {
                this.player = 2;
                System.out.println("You are Player 2");
                System.out.println();

                // adjust path to opening book
                path = "opening_book_secondMove.txt";
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
                        this.timeLeft = serverTime; // countdown for entire game
                        this.maxTime = serverTime; // time for entire game
                        overall = 0.95 * this.timeLeft; // time for main game + buffer for longer end phase

                        System.out.println("Player " + this.player);
                        System.out.println("Set time left to: " + this.timeLeft);
                        System.out.println("Set time for main game to:: " + overall);
                        System.out.println();

                        // Thread.sleep(100); // turn on for better visualisation
                    }

                    // System.out.println("\n" + "Player "+ this.player + " | " + "Server response:  " + response);

                    String fen = response.getString("board");

                    String fenNoPlayer = fen.substring(0, fen.length() - 2);

                    if (response.getBoolean("end")) {
                        System.out.println("Game has ended!");
                        Thread.sleep(1000);
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
                            if (this.isPlayer) {
                                gameInstance.printBoard(true);
                                System.out.println("Enter your move: ");
                                this.move = this.scanner.nextLine();

                            // AI
                            } else {

                                // gameInstance.printBoard(true);

                                // START: opening library
                                String moveOpeningLib = this.openingLib.get(fenNoPlayer);
                                if (this.useOpeningLib && moveOpeningLib != null){
                                    System.out.println("Player " + this.player + " | Using opening library");
                                    System.out.println();
                                    // check if a position is in the opening library
                                    this.move = moveOpeningLib;
                                    this.moveCounter++;
                                // END: opening library

                                } else {

                                    // START: dynamic time management

                                    // CASE: start- and endgame
                                    if (this.moveCounter <= (averageMoves * 0.2)) {
                                        config.timeLimit = (overall * 0.1) / (averageMoves * 0.2); // 10% of the time (for on average (averageMoves * 0.2) moves in these states)
                                    // CASE: overtime (if game goes beyond averageMoves)
                                    } else if (this.timeLeft <= maxTime * 0.04) { // if we have 4% of time left
                                        config.timeLimit = (this.timeLeft * 0.5); // continuously less (but never running out directly)
                                    // CASE: mid-game
                                    } else if ((averageMoves * 0.45) <= this.moveCounter && this.moveCounter <= (averageMoves * 0.575)) {
                                        config.timeLimit = 8000.0;
                                    } else {
                                        config.timeLimit = (overall * 0.9) / (averageMoves * 0.65); // 90% of the time (for on average (averageMoves * 0.65) moves in this state)
                                    }

                                    // END: dynamic time management

                                    // get move (switch to MCTS if time too low)
                                    if (this.timeLeft <= this.MCTSTime && this.switchMCTS) {
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

                            double timeForMove = System.currentTimeMillis() - this.currentTime;
                            this.timeLeft -= timeForMove;

                            System.out.println("Time For move: " + (timeForMove));
                            System.out.println("Time left for moves: " + this.timeLeft);
                            System.out.println("Move: " + this.moveCounter);

                            System.out.println("Player " + this.player + " | Move: " + this.move);
                            System.out.println("");
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
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classloader.getResourceAsStream(fileLocation);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            System.out.println(Thread.currentThread().getName() + " | Reading file: " + fileLocation);
            System.out.println();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                String position = parts[0];
                String move = parts[1];
                openingLib.put(position, move);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method to start the connection and game play.
     *
     * @param args Command line arguments (not used).
     * @throws InterruptedException if the main thread is interrupted.
     */
    public static void main(String[] args) throws InterruptedException {
        boolean twoPlayer = true;

        if (!twoPlayer) {
            Connection player1 = new Connection(false, true, 100, true);
            player1.connect(); // only for single player
        } else {
            Connection player1 = new Connection(false, true, 100, true);
            Connection player2 = new Connection(false, true, 100, true);

            Thread thread1 = new Thread(() -> player1.connect());
            Thread thread2 = new Thread(() -> player2.connect());

            thread1.setName("Player 1");
            thread2.setName("Player 2");

            thread1.start();
            Thread.sleep(100);
            thread2.start();

            try {
                thread1.join();
                thread2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
