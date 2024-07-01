package benchmark;

import game.Color;
import game.MoveGenerator;
import search.*;

public class Simulation {

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

        // NOTE: check if correct
        if (moveCount % 2 == 0) {
            return 2;
        } {
            return 1;
        }

    }

    public void simulate(KI firstKI, SearchConfig firstConfig, KI secondKI, SearchConfig secondConfig, String fen, int iterations) {
        int firstKIWins = 0;
        int secondKIWins = 0;

        for (int i = 1; i <= iterations; i++) {
            int result = playGame(firstKI, firstConfig, secondKI, secondConfig, fen);
            if (result == 1) {
                firstKIWins++;
            } else {
                secondKIWins++;
            }
            System.out.println("Winner of iteration " + i + " is: KI " + result);
        }

        System.out.println("First KI wins: " + firstKIWins);
        System.out.println("Second KI wins: " + secondKIWins);
    }

    public static void main(String[] args) {
        KI firstKI = new BasisKI();
        SearchConfig firstConfig = BasisKI.bestConfig;
        // firstConfig.timeCriterion = false;
        // firstConfig.maxAllowedDepth = 3;
        firstConfig.timeLimit = 3000;

        KI secondKI = new MCTSKI();
        SearchConfig secondConfig = BasisKI.bestConfig;
        // secondConfig.maxAllowedDepth = 3;

        String initialFEN = "2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b";
        int iterations = 10;

        Simulation simulation = new Simulation();
        simulation.simulate(firstKI, firstConfig, secondKI, secondConfig, initialFEN, iterations);
    }
}