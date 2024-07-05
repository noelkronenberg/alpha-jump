package benchmark;

import game.Color;
import game.MoveGenerator;
import search.*;
import search.ab.Minimax_AB;
import search.mcts.MCTS;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simulation of full games between two AIs.
 */
public class Simulation {

    /**
     * Class to represent the result of a game, including the winning AI and the color.
     */
    public static class GameResult {
        public final int number;
        public final Color color;

        public GameResult(int number, Color color) {
            this.number = number;
            this.color = color;
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
     * @return An integer indicating the winner of the game. Returns 1 if the first AI wins, 2 if the second AI wins.
     */
    public static GameResult playGame(AI firstAI, SearchConfig firstConfig, AI secondAI, SearchConfig secondConfig, String fen, boolean showGame) {

        MoveGenerator gameState = new MoveGenerator();
        gameState.initializeBoard(fen);

        String bestMove;
        boolean gameOver = false;
        int moveCount = 0;

        Color startingColor = fen.charAt(fen.length() - 1) == 'r' ? Color.RED : Color.BLUE;

        while (!gameOver) {

            // get best move
            if (moveCount % 2 == 0) {
                bestMove = firstAI.orchestrator(fen, firstConfig);
            } else {
                bestMove = secondAI.orchestrator(fen, secondConfig);
            }

            // convert move
            int[] bestMoveInts = gameState.convertStringToPosWrapper(bestMove);
            int bestMoveInt = bestMoveInts[0] * 100 + bestMoveInts[1];

            // check for game over
            char currentColorChar = fen.charAt(fen.length() - 1);
            Color currentColor = (currentColorChar == 'r') ? Color.RED : Color.BLUE;
            gameOver = gameState.isGameOver(bestMove, currentColor);

            if (!gameOver) {
                // move piece
                gameState.movePiece(bestMoveInt);
                moveCount++;
            }

            if (showGame) {
                System.out.println();
                System.out.println("Color: " + currentColor);
                System.out.println("Move: " + bestMove);
                System.out.println("MoveCount: " + moveCount);
                System.out.println("GameOver: " + gameOver);
                gameState.printBoard(true);
                System.out.println();
            }

            // get next FEN
            char nextColor = (currentColorChar == 'r') ? 'b' : 'r'; // switch color
            fen = gameState.getFenFromBoard() + " " + nextColor;
        }

        if (moveCount % 2 == 0) {
            Color otherColor = (startingColor == Color.RED) ? Color.BLUE : Color.RED;
            return new GameResult(2, otherColor);
        } else {
            return new GameResult(1, startingColor);
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
     * @param showGame Whether to show the game play.
     */
    public void simulate(AI firstAI, SearchConfig firstConfig, AI secondAI, SearchConfig secondConfig, String fen, int iterations, boolean showGame) {
        if (!(iterations % 2 == 0)) {
            System.out.println("Please enter an even number of iterations to make the results fair.");
            return;
        }

        int firstAIWins = 0;
        int secondAIWins = 0;

        for (int i = 1; i <= iterations; i++) {
            GameResult result;
            boolean firstAIBegins = (i % 2 == 0);

            if (firstAIBegins) {
                result = playGame(firstAI, firstConfig, secondAI, secondConfig, fen, showGame);
            } else {
                result = playGame(secondAI, secondConfig, firstAI, firstConfig, fen, showGame);
            }

            if ((firstAIBegins && result.number == 1) || (!firstAIBegins && result.number == 2)) {
                firstAIWins++;
                System.out.println("Winner of iteration " + i + " is: AI 1 (" + result.color + ")");
            } else {
                secondAIWins++;
                System.out.println("Winner of iteration " + i + " is: AI 2 (" + result.color + ")");
            }
        }

        System.out.println();
        System.out.println("First AI wins: " + firstAIWins);
        System.out.println("Second AI wins: " + secondAIWins);
        System.out.println();

        System.out.println("Settings: ");
        System.out.println();

        System.out.println("Iterations: " + iterations);

        System.out.println("FEN: " + fen);
        System.out.println();

        System.out.println("First AI: ");
        System.out.println(firstAI.toString());
        System.out.println();

        System.out.println("Second AI: ");
        System.out.println(secondAI.toString());
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
     * Main method to run a simulation between two AIs using predefined configurations and an initial board state.
     * Prints results to a file.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            // filename (DO NOT CHANGE)
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            String timestamp = dateFormat.format(new Date());
            String filename = timestamp + "_simulation-output" + ".txt";
            PrintStream fileOut = new PrintStream(new File("src/main/java/benchmark/output/" + filename));
            System.setOut(fileOut);

            // configuration of first AI (CAN BE CHANGED)
            AI firstAI = new Minimax_AB();
            SearchConfig firstConfig = Minimax_AB.bestConfig.copy();
            firstConfig.timeLimit = 3000;

            // configuration of second AI (CAN BE CHANGED)
            AI secondAI = new MCTS();
            SearchConfig secondConfig = Minimax_AB.bestConfig.copy();
            secondConfig.timeLimit = 3000;

            // configuration of simulation (CAN BE CHANGED)
            String initialFEN = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b"; // sanity check: b0b0b0b0b0b0/1r0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 r (red should always win)
            int iterations = 10;
            boolean showGame = false;

            // start simulation (DO NOT CHANGE)
            Simulation simulation = new Simulation();
            simulation.simulate(firstAI, firstConfig, secondAI, secondConfig, initialFEN, iterations, showGame);

            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}