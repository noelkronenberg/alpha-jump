package benchmark;

import game.Color;
import game.MoveGenerator;
import search.BasisKI;

import java.util.LinkedHashMap;
import java.util.List;

public class MoveGeneratorBM {

    static MoveGenerator moveGenerator;

    static void init() {
        moveGenerator = new MoveGenerator();
        moveGenerator.initializeBoard();
    }

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

    static void wrapperBM(String fen) {
        // split color and board
        char color_fen = fen.charAt(fen.length() - 1);
        Color color = moveGenerator.getColor(color_fen);
        String board_fen = fen.substring(0, fen.length() - 2);

        // get metrics
        double duration = MoveGeneratorBM.generateAllPossibleMovesSpeed(board_fen, color);
        System.out.println("Time to generate moves: " + duration + " milliseconds");
    }

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
