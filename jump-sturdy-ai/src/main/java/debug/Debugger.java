package debug;

import search.SearchConfig;
import search.ab.Minimax_AB;
import game.MoveGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Debugging tool for analyzing move order of an AI.
 */
public class Debugger {

    /**
     * Simulates a sequence of moves and prints board states after each move.
     *
     * @param fen Current board position in FEN format.
     * @param move First move to make.
     * @param sequence Number of moves to simulate.
     */
    public static void moveOrder(String fen, String move, int sequence) {
        Minimax_AB ai = new Minimax_AB();
        SearchConfig config = Minimax_AB.bestConfig.copy();
        config.timeLimit = 500.0;
        MoveGenerator moveGenerator = new MoveGenerator();
        moveGenerator.initializeBoard(fen);

        // show board
        System.out.println();
        moveGenerator.printBoard(true);

        // best move
        String currentColor = "" + fen.charAt(fen.length() - 1);
        System.out.println();
        System.out.println("Best move: " + move + " (color: " + currentColor + " | move: "+ 1 + ")");

        String current_fen; // helper fen

        // move piece
        int[] moveInt = moveGenerator.convertStringToPosWrapper(move);
        moveGenerator.movePiece(moveInt[0], moveInt[1]);

        // show board
        System.out.println();
        moveGenerator.printBoard(true);

        for (int i = 2; i <= sequence; i++)  {
            currentColor = (currentColor.equals("r")) ? "b" : "r"; // switch color

            // get best move
            current_fen = moveGenerator.getFenFromBoard() + " " + currentColor;
            String bestMove = ai.orchestrator(current_fen, config);
            System.out.println("Best move: " + bestMove + " (color: " + currentColor + " | move: "+ i + ")");

            // convert move
            int[] bestMoveInts = moveGenerator.convertStringToPosWrapper(bestMove);
            int bestMoveInt = bestMoveInts[0] * 100 + bestMoveInts[1];

            moveGenerator.movePiece(bestMoveInt); // move piece

            System.out.println();
            moveGenerator.printBoard(true); // show board
        }
    }

    /**
     * Main method to start the simulation of the move order and output to a file.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        try {
            PrintStream fileOut = new PrintStream(new File("src/main/java/debug/Debugger-output.txt"));
            System.setOut(fileOut);
            Debugger.moveOrder("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/1r0r02rr2/b03r01rr1/2r01r0r0 r", "G8-F8", 10);
            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
