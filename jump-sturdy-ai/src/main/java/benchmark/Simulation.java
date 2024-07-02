package benchmark;

import game.Color;
import game.MoveGenerator;
import search.*;
import search.ab.BasisKI;
import search.mcts.MCTSKI;

/**
 * Simulation of full games between two AIs.
 */
public class Simulation {

    /**
     * Plays a full game between two AIs using the provided configurations and initial board state.
     *
     * @param firstKI The first AI that will play.
     * @param firstConfig The configuration for the first AI's search algorithm.
     * @param secondKI The second AI that will play.
     * @param secondConfig The configuration for the second AI's search algorithm.
     * @param fen The initial board state in FEN format.
     * @return An integer indicating the winner of the game. Returns 1 if the first AI wins, 2 if the second AI wins.
     */
    public static int playGame(KI firstKI, SearchConfig firstConfig, KI secondKI, SearchConfig secondConfig, String fen) {

        MoveGenerator gameState = new MoveGenerator();
        gameState.initializeBoard(fen);

        String bestMove;
        boolean gameOver = false;
        int moveCount = 0;

        while (!gameOver) {

            // get best move
            if (moveCount % 2 == 0) {
                bestMove = firstKI.orchestrator(fen, firstConfig);
            } else {
                bestMove = secondKI.orchestrator(fen, secondConfig);
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
            return 1;
        } {
            return 2;
        }
    }

    /**
     * Simulates a series of games between two AIs using the provided configurations and initial board state.
     * The results of each game and the overall win counts for each AI are printed to the console.
     *
     * @param firstKI The first AI that will play.
     * @param firstConfig The configuration for the first AI's search algorithm.
     * @param secondKI The second AI that will play.
     * @param secondConfig The configuration for the second AI's search algorithm.
     * @param fen The initial board state in FEN format.
     * @param iterations The number of iterations (games) to simulate. Must be an even number.
     */
    public void simulate(KI firstKI, SearchConfig firstConfig, KI secondKI, SearchConfig secondConfig, String fen, int iterations) {
        if (!(iterations % 2 == 0)) {
            System.out.println("Please enter an even number of iterations to make the results fair.");
            return;
        }

        int firstKIWins = 0;
        int secondKIWins = 0;

        for (int i = 1; i <= iterations; i++) {
            int result;
            boolean firstKIBegins = (i % 2 == 0);

            if (firstKIBegins) {
                result = playGame(firstKI, firstConfig, secondKI, secondConfig, fen);
            } else {
                result = playGame(secondKI, secondConfig, firstKI, firstConfig, fen);
            }

            if ((firstKIBegins && result == 1) || (!firstKIBegins && result == 2)) {
                firstKIWins++;
                System.out.println("Winner of iteration " + i + " is: KI 1");
            } else {
                secondKIWins++;
                System.out.println("Winner of iteration " + i + " is: KI 2");
            }
        }

        System.out.println("First KI wins: " + firstKIWins);
        System.out.println("Second KI wins: " + secondKIWins);
    }

    /**
     * Main method to run a simulation between two AIs using predefined configurations and an initial board state.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        KI firstKI = new BasisKI();
        SearchConfig firstConfig = BasisKI.bestConfig;
        firstConfig.timeLimit = 200.0;

        KI secondKI = new MCTSKI();
        SearchConfig secondConfig = BasisKI.bestConfig;
        secondConfig.timeLimit = 200.0;

        String initialFEN = "2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b";
        int iterations = 4;
        Simulation simulation = new Simulation();
        simulation.simulate(secondKI, secondConfig, firstKI, firstConfig, initialFEN, iterations);
    }
}