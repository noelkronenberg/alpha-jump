package benchmark;

import game.Color;
import game.MoveGenerator;

public class MoveGeneratorBM {

    static MoveGenerator moveGenerator;

    static void init() {
        moveGenerator = new MoveGenerator();
        moveGenerator.initializeBoard();
    }

    static double generateAllPossibleMovesSpeed(String fen) {
        // split color and board
        char color_fen = fen.charAt(fen.length() - 1);
        Color color = moveGenerator.getColor(color_fen);
        moveGenerator.initializeBoard(fen.substring(0, fen.length() - 2));

        // get metric
        double startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            moveGenerator.generateAllPossibleMoves(color);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / 1000) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;
    }

    static double generateAllPossibleMovesMemory(String fen) {
        // split color and board
        char color_fen = fen.charAt(fen.length() - 1);
        Color color = moveGenerator.getColor(color_fen);
        moveGenerator.initializeBoard(fen.substring(0, fen.length() - 2));

        // get metric
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        for (int i = 0; i < 1000; i++) {
            moveGenerator.generateAllPossibleMoves(color);
        }
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long usedMemory = (memoryAfter - memoryBefore) / 1000;
        return usedMemory;
    }

    static void wrapperBM(String fen) {
        double duration = MoveGeneratorBM.generateAllPossibleMovesSpeed(fen);
        System.out.println("Time to generate moves: " + duration + " milliseconds");

        double usedMemory = MoveGeneratorBM.generateAllPossibleMovesMemory(fen);
        System.out.println("Memory usage: " + usedMemory + " bytes");
    }

    public static void main(String[] args) {
        MoveGeneratorBM.init();

        System.out.println("Start position: ");
        MoveGeneratorBM.wrapperBM("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b");

        System.out.println();
        System.out.println("End game : ");
        MoveGeneratorBM.wrapperBM("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b");

    }
}
