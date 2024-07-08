package app;

import game.Color;
import game.MoveGenerator;
import search.SearchConfig;
import search.ab.Minimax_AB;

/**
 * Orchestrates and manages the gameplay between a human player and an AI player.
 */
public class Orchestrator {

    static Player player = new Player();
    static Minimax_AB ai = new Minimax_AB();
    static SearchConfig config = Minimax_AB.bestConfig;
    static MoveGenerator gameState = new MoveGenerator();

    /**
     * Plays a game between a human player and an AI.
     *
     * @param fen The initial board state in FEN notation.
     */
    public static void playGame(String fen) {

        // adjust time for AI
        config.timeLimit = 3000;

        // initialise logic
        Orchestrator.gameState.initializeBoard(fen);
        boolean gameOver = false;
        int moveCount = 0;
        Color colorAI;
        Color colorPerson;

        while (!gameOver) {

            // initialise logic
            String move;
            Color currentColor = fen.charAt(fen.length() - 1) == 'r' ? Color.RED : Color.BLUE;

            // show board
            Orchestrator.gameState.printBoard(true);

            // get move
            if (moveCount % 2 == 0) {

                // get move
                System.out.println("AI is choosing a move ...");
                move = Orchestrator.ai.orchestrator(fen, Orchestrator.config);

                // show move
                colorAI = currentColor;
                System.out.printf("AI made the move (as %s): %s\n", colorAI, move);
                System.out.println();

            } else {

                // get move
                colorPerson = currentColor;
                move = Orchestrator.player.getMove(fen, colorPerson);

            }

            // check for game over
            gameOver = Orchestrator.gameState.isGameOver(move, currentColor);

            // make move
            if (!gameOver) {

                // convert move
                int[] bestMoveArray = Orchestrator.gameState.convertStringToPosWrapper(move);
                int bestMoveInt = bestMoveArray[0] * 100 + bestMoveArray[1];

                // move piece
                Orchestrator.gameState.movePiece(bestMoveInt);
                moveCount++;

            }

            // get next FEN
            char nextColor = (fen.charAt(fen.length() - 1) == 'r') ? 'b' : 'r'; // switch color
            fen = Orchestrator.gameState.getFenFromBoard() + " " + nextColor; // create next fen

        }

        // show winner
        if (moveCount % 2 == 0) {
            System.out.println("Player has won!");
        } else {
            System.out.println("AI has won!");
        }

    }

    /**
     * Main method to start the game with a specific initial board state.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Orchestrator.playGame("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");
    }

}
