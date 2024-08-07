package benchmark.simulation;

import game.Color;
import game.MoveGenerator;
import search.*;
import search.ab.Minimax_AB;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * ConnectionSimulation of full games between two AIs.
 */
public class ConnectionSimulation {

    static HashMap<String,String> startingMoveLib;
    static HashMap<String,String> secondMoveLib;

    /**
     * Class to represent the result of a game, including the winning AI and the color.
     */
    public static class GameResult {
        public final int number;
        public final Color color;
        public final ArrayList<Integer> depths;

        public GameResult(int number, Color color, ArrayList<Integer> depths) {
            this.number = number;
            this.color = color;
            this.depths = depths;
        }
    }

    /**
     * Plays a full game between two AIs using the provided configurations and initial board state.
     *
     * @param firstAI The first AI that will play.
     * @param firstConfig The configuration for the first AI's search algorithm.
     * @param secondAI The second AI that will play.
     * @param secondConfig The configuration for the second AI's search algorithm.
     * @param fen The initial board state in FEN format.
     * @param showGame Whether to show the game play.
     * @param timeConfigFirst Time configuration of firstAI.
     * @param timeConfigSecond Time configuration of secondAI.
     * @return An integer indicating the winner of the game. Returns 1 if the first AI wins, 2 if the second AI wins.
     */
    public static GameResult playGame(AI firstAI, SearchConfig firstConfig, AI secondAI, SearchConfig secondConfig, String fen, boolean showGame, ConnectionSimulationConfig timeConfigFirst, ConnectionSimulationConfig timeConfigSecond) throws IOException {
        double maxTime = 120000.0;

        double overall = 0.96 * maxTime;

        MoveGenerator gameState = new MoveGenerator();
        gameState.initializeBoard(fen);

        String bestMove;
        boolean gameOver = false;
        int moveCount = 0;

        int moveCountRed = 0;
        int moveCountBlue = 0;
        long currentTime = 0;
        double totalTimeRed = maxTime;
        double totalTimeBlue = maxTime;

        ArrayList<Integer> firstAIDepths = new ArrayList<>();
        ArrayList<Integer> secondAIDepths = new ArrayList<>();

        Color startingColor = fen.charAt(fen.length() - 1) == 'r' ? Color.RED : Color.BLUE;

        while (!gameOver) {

            currentTime = System.currentTimeMillis();

            char currentColorChar = fen.charAt(fen.length() - 1);
            Color currentColor = (currentColorChar == 'r') ? Color.RED : Color.BLUE;

            String moveOpeningLib;
            if (currentColor == Color.RED) {
                moveOpeningLib = startingMoveLib.get(fen.substring(0, fen.length() - 2));
            } else {
                moveOpeningLib = secondMoveLib.get(fen.substring(0, fen.length() - 2));
            }

            // get best move
            if (moveCount % 2 == 0) {
                // START: opening library
                if (moveOpeningLib != null) {
                    // check if a position is in the opening library
                    bestMove = moveOpeningLib;
                    moveCountRed++;
                // END: opening library

                } else {
                    if (moveCountRed <= timeConfigFirst.numberOfMovesStart /*|| (averageMoves-9 < this.moveCounter && this.moveCounter <= averageMoves-1)*/) {
                        firstConfig.timeLimit = (overall * timeConfigFirst.weightParameterStart) / timeConfigFirst.numberOfMovesStart; // 20% of the time (for on average 15 moves in these states)
                        // CASE: overtime (if game goes beyond averageMoves)
                    } else if (totalTimeRed <= maxTime * timeConfigFirst.weightParameterEndTime) { // if we have 5% of time left
                        firstConfig.timeLimit = (totalTimeRed * timeConfigFirst.weightParameterFinal); // continuously less (but never running out directly)
                        // CASE: mid-game
                    } else if (timeConfigFirst.activateLongSearch && moveCountRed<= timeConfigFirst.endLongSearch && moveCountRed>= timeConfigFirst.startLongSearch){
                        firstConfig.timeLimit = timeConfigFirst.longSearchDuration; // 80% of the time (for on average 25 moves in this state)
                    } else {
                        firstConfig.timeLimit = (overall * timeConfigFirst.weightParameterNormal) / timeConfigFirst.numberOfMovesNormal; // 80% of the time (for on average 25 moves in this state)
                    }
                    bestMove = firstAI.orchestrator(fen, firstConfig);
                    moveCountRed++;
                }

                if (firstAI instanceof Minimax_AB) {
                    firstAIDepths.add(((Minimax_AB) firstAI).maxDepth);
                }
            } else {
                // START: opening library
                if (moveOpeningLib != null) {
                    // check if a position is in the opening library
                    bestMove = moveOpeningLib;
                    moveCountBlue++;
                 // END: opening library

                } else {
                    if (moveCountBlue <= timeConfigSecond.numberOfMovesStart /*|| (averageMoves-9 < this.moveCounter && this.moveCounter <= averageMoves-1)*/) {
                        secondConfig.timeLimit = (overall * timeConfigSecond.weightParameterStart) / timeConfigSecond.numberOfMovesStart; // 20% of the time (for on average 15 moves in these states)
                        // CASE: overtime (if game goes beyond averageMoves)
                    } else if (totalTimeBlue <= maxTime * timeConfigSecond.weightParameterEndTime) { // if we have 5% of time left
                        secondConfig.timeLimit = (totalTimeBlue * timeConfigSecond.weightParameterFinal); // continuously less (but never running out directly)
                        // CASE: mid-game
                    } else if (timeConfigSecond.activateLongSearch && moveCountBlue<= timeConfigSecond.endLongSearch && moveCountBlue>= timeConfigSecond.startLongSearch){
                        secondConfig.timeLimit = timeConfigSecond.longSearchDuration;
                    } else {
                        secondConfig.timeLimit = (overall * timeConfigSecond.weightParameterNormal) / timeConfigSecond.numberOfMovesNormal; // 80% of the time (for on average 25 moves in this state)
                    }

                    bestMove = secondAI.orchestrator(fen, secondConfig);
                    moveCountBlue++;
                }

                if (secondAI instanceof Minimax_AB) {
                    secondAIDepths.add(((Minimax_AB) secondAI).maxDepth);
                }
            }

            double timeForMove = System.currentTimeMillis() - currentTime;


            // check for game over
            gameOver = gameState.isGameOver(bestMove, currentColor);
            if (!gameOver) {
                // convert move
                int[] bestMoveInts = gameState.convertStringToPosWrapper(bestMove);
                int bestMoveInt = bestMoveInts[0] * 100 + bestMoveInts[1];

                // move piece
                gameState.movePiece(bestMoveInt);
                moveCount++;

                // time keeping
                System.out.println(currentColor + " | Time for move: " + timeForMove);
                System.out.println();
                if (currentColor == Color.RED) {
                    totalTimeRed -= timeForMove;
                } else {
                    totalTimeBlue -= timeForMove;
                }

            }

            if (showGame) {
                System.out.println("Color: " + currentColor);
                System.out.println("Move: " + bestMove);
                System.out.println("MoveCount: " + moveCount);
                System.out.println("GameOver: " + gameOver);
                System.out.println("Board: ");
                gameState.printBoard(true);
            }

            // get next FEN
            char nextColor = (currentColorChar == 'r') ? 'b' : 'r'; // switch color
            fen = gameState.getFenFromBoard() + " " + nextColor;
        }

        if (moveCount % 2 == 0) {
            Color otherColor = (startingColor == Color.RED) ? Color.BLUE : Color.RED;

            System.out.println("Total moves for red: " + (moveCountRed-1));
            System.out.println("Total moves for blue: " + moveCountBlue);
            System.out.println();

            System.out.println("Total time left for red: " + totalTimeRed);
            System.out.println("Total time left for blue: " + totalTimeBlue);
            System.out.println();

            return new GameResult(2, otherColor, secondAIDepths);
        } else {
            System.out.println("Total moves for red: " + moveCountRed);
            System.out.println("Total moves for blue: " + (moveCountBlue-1));
            System.out.println();

            System.out.println("Total time left for red: " + totalTimeRed);
            System.out.println("Total time left for blue: " + totalTimeBlue);
            System.out.println();

            return new GameResult(1, startingColor, firstAIDepths);
        }
    }

    /**
     * Simulates a series of games between two AIs using the provided configurations and initial board state.
     * The results of each game and the overall win counts for each AI are printed to the console.
     *
     * @param firstAI The first AI that will play.
     * @param firstConfig The configuration for the first AI's search algorithm.
     * @param secondAI The second AI that will play.
     * @param secondConfig The configuration for the second AI's search algorithm.
     * @param fen The initial board state in FEN format.
     * @param iterations The number of iterations (games) to simulate. Must be an even number.
     * @param timeConfigFirst Time configuration of firstAI.
     * @param timeConfigSecond Time configuration of secondAI.
     * @param showGame Whether to show the game play.
     */
    public void simulate(AI firstAI, SearchConfig firstConfig, AI secondAI, SearchConfig secondConfig, String fen, int iterations, boolean showGame, ConnectionSimulationConfig timeConfigFirst, ConnectionSimulationConfig timeConfigSecond) throws IOException {
        if (!(iterations % 2 == 0)) {
            System.out.println("Please enter an even number of iterations to make the results fair.");
            return;
        }

        int firstAIWins = 0;
        int secondAIWins = 0;
        ArrayList<Integer> firstAIDepthsAllGames = new ArrayList<>();
        ArrayList<Integer> secondAIDepthsAllGames = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        String timestamp = dateFormat.format(new Date());
        System.out.println("START: " +  timestamp);
        System.out.println();

        for (int i = 1; i <= iterations; i++) {
            GameResult result;
            boolean firstAIBegins = (i % 2 == 0);

            if (showGame) {
                if (i != 1) {
                    System.out.println();
                }
                System.out.println("Game " + i + ":");
                System.out.println();
            }

            if (firstAIBegins) {
                result = playGame(firstAI, firstConfig, secondAI, secondConfig, fen, showGame,timeConfigFirst,timeConfigSecond);
            } else {
                result = playGame(secondAI, secondConfig, firstAI, firstConfig, fen, showGame,timeConfigSecond,timeConfigFirst) ;
            }

            if ((firstAIBegins && result.number == 1) || (!firstAIBegins && result.number == 2)) {
                firstAIWins++;
                System.out.println("Winner of game " + i + " is: AI 1 (" + result.color + ")");
                if (firstAI instanceof Minimax_AB) {
                    firstAIDepthsAllGames.addAll(result.depths);
                }
            } else {
                secondAIWins++;
                System.out.println("Winner of game " + i + " is: AI 2 (" + result.color + ")");
                if (secondAI instanceof Minimax_AB) {
                    secondAIDepthsAllGames.addAll(result.depths);
                }
            }

            if (i != iterations) {
                System.out.println();
            }
        }

        dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        timestamp = dateFormat.format(new Date());
        System.out.println();
        System.out.println("END: " +  timestamp);
        System.out.println();

        System.out.println("Results: ");
        System.out.println();

        System.out.println("First AI wins: " + firstAIWins);
        System.out.println("Second AI wins: " + secondAIWins);
        System.out.println();

        if (firstAI instanceof Minimax_AB) {
            double averageFirstAIDepth = firstAIDepthsAllGames.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            System.out.printf("First AI depths: %s\n", firstAIDepthsAllGames);
            System.out.printf("First AI average depth: %.2f\n", averageFirstAIDepth);
        }
        if (firstAI instanceof Minimax_AB) {
            double averageSecondAIDepth = secondAIDepthsAllGames.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            System.out.printf("Second AI depths: %s\n", secondAIDepthsAllGames);
            System.out.printf("Second AI average depth: %.2f\n", averageSecondAIDepth);
        }
        if (firstAI instanceof Minimax_AB || secondAI instanceof Minimax_AB) {
            System.out.println();
        }

        System.out.println("Settings: ");
        System.out.println();

        System.out.println("Iterations: " + iterations);

        System.out.println("FEN: " + fen);
        System.out.println();

        System.out.println("First AI: ");
        System.out.println(firstAI.toString());
        System.out.println(timeConfigFirst.toString());
        System.out.println();

        System.out.println("Second AI: ");
        System.out.println(secondAI.toString());
        System.out.println(timeConfigSecond.toString());
        System.out.println();

        System.out.println("System Information:");
        System.out.println();

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
        System.out.println("OS: " + System.getProperty("os.name") + " | " + System.getProperty("os.arch"));;
        System.out.println("Available processors (cores): " + osBean.getAvailableProcessors());
        System.out.println("System load average (cores): " + String.format("%.2f", osBean.getSystemLoadAverage()));
        System.out.println("Total physical memory (GB): " + bytesToGigabytes(sunOsBean.getTotalPhysicalMemorySize()));
        System.out.println("Free physical memory (GB): " + bytesToGigabytes(sunOsBean.getFreePhysicalMemorySize()));
    }

    public static String bytesToGigabytes(long bytes) {
        return String.format("%.2f", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    /**
     * Fills the startingBib HashMap with precalculated starting moves.
     *
     * @param fileLocation Path to the File
     * @throws IOException
     */
    public static HashMap<String,String> readFileAndFillBib(String fileLocation, int player, HashMap<String, String> openingLib) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classloader.getResourceAsStream(fileLocation);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {

            if (player == 1) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(", ");
                    openingLib.put(tokens[0], tokens[1]);
                }
            } else {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(", ");
                    openingLib.put(tokens[0], tokens[1]);
                }
            }

            return openingLib;
        }
    }

    /**
     * Main method to run a simulation between two AIs using predefined configurations and an initial board state.
     * Prints results to a file.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            startingMoveLib = readFileAndFillBib("opening_book_startingMove.txt",1, new HashMap<>());
            secondMoveLib = readFileAndFillBib("opening_book_secondMove.txt",2, new HashMap<>());

            // filename (DO NOT CHANGE)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            String timestamp = dateFormat.format(new Date());
            String filename = timestamp + "_connection-simulation-output" + ".txt";
            PrintStream fileOut = new PrintStream(new File("src/main/java/benchmark/simulation/output/" + filename));
            System.setOut(fileOut);

            // configuration of first AI (CAN BE CHANGED)
            AI firstAI = new Minimax_AB();
            SearchConfig firstConfig = Minimax_AB.bestConfig.copy();
            firstConfig.timeLimit = 1000;
            ConnectionSimulationConfig timeConfigFirst =  new ConnectionSimulationConfig(0.9,0.1,0.04,26,8,0.5, true,18,23,8000.0);

            // configuration of second AI (CAN BE CHANGED)
            // current (same): 0.92,0.08,0.04,29,6,0.5
            AI secondAI = new Minimax_AB();
            SearchConfig secondConfig = Minimax_AB.bestConfig.copy();
            secondConfig.timeLimit = 1000;
            ConnectionSimulationConfig timeConfigSecond =  new ConnectionSimulationConfig(0.92,0.08,0.04,29,6,0.5, false,0,0,0);
            // NOTE: otherwise: 1.920 ,4.712

            // configuration of connectionSimulation (CAN BE CHANGED)
            String initialFEN = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 r"; // sanity check: b0b0b0b0b0b0/1r0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 r (red should always win)
            int iterations = 20;
            boolean showGame = false;

            // start connectionSimulation (DO NOT CHANGE)
            ConnectionSimulation connectionSimulation = new ConnectionSimulation();
            connectionSimulation.simulate(firstAI, firstConfig, secondAI, secondConfig, initialFEN, iterations, showGame, timeConfigFirst, timeConfigSecond);

            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}