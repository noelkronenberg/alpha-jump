package debug;

import search.BasisKI;
import game.MoveGenerator;

public class BasisKIDebugger {

    public static void moveOrder(String fen, String move, int sequence) {
        BasisKI ki = new BasisKI();
        MoveGenerator moveGenerator = new MoveGenerator();
        moveGenerator.initializeBoard(fen);

        // show board
        System.out.println();
        moveGenerator.printBoard(true);

        // best move
        String currentColor = "" + fen.charAt(fen.length() - 1);
        System.out.println();
        System.out.println("Best move: " + move + " (color: " + currentColor + " | move: "+ 1 + ")");

        // move piece
        int[] moveInt = moveGenerator.convertStringToPosWrapper(move);
        moveGenerator.movePiece(moveInt[0], moveInt[1]);

        // show board
        System.out.println();
        moveGenerator.printBoard(true);

        for (int i = 2; i <= sequence; i++)  {
            currentColor = (currentColor.equals("r")) ? "b" : "r"; // switch color

            // get best move
            String bestMove = ki.orchestrator(moveGenerator.getFenFromBoard() + " " + currentColor, 20000.0, 0.25);
            System.out.println("Best move: " + bestMove + " (color: " + currentColor + " | move: "+ i + ")");

            // convert move
            int[] bestMoveInts = moveGenerator.convertStringToPosWrapper(bestMove);
            int bestMoveInt = bestMoveInts[0] * 100 + bestMoveInts[1];

            moveGenerator.movePiece(bestMoveInt); // move piece

            System.out.println();
            moveGenerator.printBoard(true); // show board
        }
    }

    public static void main(String[] args) {
        BasisKIDebugger.moveOrder("b0b01bb2/6b01/3bb4/4b0b02/3r04/3r04/1r0r05/1r0rrrr2 b", "E4-D5", 10);
    }

}
