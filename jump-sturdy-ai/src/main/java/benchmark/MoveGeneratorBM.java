package benchmark;

import game.Color;
import game.MoveGenerator;

/**
 * Benchmarking class for the MoveGenerator.
 */
public class MoveGeneratorBM {

    static MoveGenerator moveGenerator;

    /**
     * Initializes the MoveGenerator instance and sets up the board.
     */
    static void init() {
        moveGenerator = new MoveGenerator();
        moveGenerator.initializeBoard();
    }

    /**
     * Measures the average time taken to generate all possible moves for the given color
     * on the specified board state over a set number of iterations.
     *
     * @param board_fen The board state in FEN format.
     * @param color The color of the player.
     * @return The average time in milliseconds to generate all possible moves.
     */
    static double generateAllPossibleMovesSpeed(String board_fen, Color color) {
        moveGenerator.initializeBoard(board_fen);
        int iterations = 100000;
        double startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            moveGenerator.generateAllPossibleMoves(color);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / iterations) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;
    }

    /**
     * Wrapper method to benchmark move generation speed. It splits the FEN string to extract
     * the board state and the color, then prints the time taken to generate all possible moves.
     *
     * @param fen The board state with player color in FEN format.
     */
    static void wrapperBM(String fen) {
        // split color and board
        char color_fen = fen.charAt(fen.length() - 1);
        Color color = moveGenerator.getColor(color_fen);
        String board_fen = fen.substring(0, fen.length() - 2);

        // get metrics
        double duration = MoveGeneratorBM.generateAllPossibleMovesSpeed(board_fen, color);
        System.out.println("Time to generate moves: " + duration + " milliseconds");
    }

    /**
     * Main method to run the benchmark tests with different board states.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        MoveGeneratorBM.init();

        System.out.println("Start position: ");
        MoveGeneratorBM.wrapperBM("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");

        System.out.println();
        System.out.println("Mid game: ");
        MoveGeneratorBM.wrapperBM("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b");

        System.out.println();
        System.out.println("End game: ");
        MoveGeneratorBM.wrapperBM("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b");
    }
}
