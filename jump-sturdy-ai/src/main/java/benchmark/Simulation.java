package benchmark;

import game.Color;
import game.MoveGenerator;
import search.*;
import search.ab.Minimax_AB;
import search.mcts.MCTS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
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
     * @return An integer indicating the winner of the game. Returns 1 if the first AI wins, 2 if the second AI wins.
     */
    public static GameResult playGame(AI firstAI, SearchConfig firstConfig, AI secondAI, SearchConfig secondConfig, String fen) {

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

            /*
            // show status
            System.out.println("Color: " + currentColor);
            System.out.println("Move: " + bestMove);
            System.out.println("MoveCount: " + moveCount);
            System.out.println("GameOver: " + gameOver);
            gameState.printBoard(true);
            */

            // get next FEN
            char nextColor = (currentColorChar == 'r') ? 'b' : 'r'; // switch color
            fen = gameState.getFenFromBoard() + " " + nextColor;
        }

        if (moveCount % 2 == 0) {
            return new GameResult(1, startingColor);
        } else {
            Color otherColor = (startingColor == Color.RED) ? Color.BLUE : Color.RED;
            return new GameResult(2, otherColor);
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
     */
    public void simulate(AI firstAI, SearchConfig firstConfig, AI secondAI, SearchConfig secondConfig, String fen, int iterations) {
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
                result = playGame(firstAI, firstConfig, secondAI, secondConfig, fen);
            } else {
                result = playGame(secondAI, secondConfig, firstAI, firstConfig, fen);
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
    }

    /**
     * Main method to run a simulation between two AIs using predefined configurations and an initial board state.
     * Prints results to a file.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            String timestamp = dateFormat.format(new Date());

            String filename = timestamp + "-simulation-output" + ".txt";

            PrintStream fileOut = new PrintStream(new File("src/main/java/benchmark/" + filename));
            System.setOut(fileOut);

            // configuration of first AI
            AI firstAI = new Minimax_AB();
            SearchConfig firstConfig = Minimax_AB.bestConfig;
            firstConfig.timeLimit = 200;

            // configuration of second AI
            AI secondAI = new MCTS();
            SearchConfig secondConfig = Minimax_AB.bestConfig;
            secondConfig.timeLimit = 200.0;

            // configuration of simulation
            String initialFEN = "2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b";
            int iterations = 2;

            // start simulation
            Simulation simulation = new Simulation();
            simulation.simulate(firstAI, firstConfig, secondAI, secondConfig, initialFEN, iterations);

            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}