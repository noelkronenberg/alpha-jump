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
        double startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            moveGenerator.generateAllPossibleMoves(color);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / 1000) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;
    }

    static double generateAllPossibleMovesMemory(String board_fen, Color color) {
        moveGenerator.initializeBoard(board_fen);
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
        // split color and board
        char color_fen = fen.charAt(fen.length() - 1);
        Color color = moveGenerator.getColor(color_fen);
        String board_fen = fen.substring(0, fen.length() - 2);

        // get metrics

        double duration = MoveGeneratorBM.generateAllPossibleMovesSpeed(board_fen, color);
        System.out.println("Time to generate moves: " + duration + " milliseconds");

        double usedMemory = MoveGeneratorBM.generateAllPossibleMovesMemory(board_fen, color);
        System.out.println("Memory usage: " + usedMemory + " bytes");
    }

    static double fullGameTime() {
        MoveGeneratorBM.init();

        String fen = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b";
        LinkedHashMap<Integer, List<Integer>> moves;
        String move_string;
        int[] move_int;
        String new_fen;
        boolean gameEnded = false;

        double startTime = System.nanoTime();
        while(!gameEnded) {
            // BLUE player
            moves = moveGenerator.getMovesWrapper(fen);
            move_string = moveGenerator.getRandomMove(moves);
            move_int = moveGenerator.convertStringToPosWrapper(move_string);
            moveGenerator.movePiece(move_int[0], move_int[1]);
            new_fen = moveGenerator.getFenFromBoard();
            fen = new_fen + " r";

            if (moveGenerator.isGameOver(move_string, Color.BLUE)) {
                gameEnded = true;
                continue;
            }

            // RED player
            moves = moveGenerator.getMovesWrapper(fen);
            move_string = moveGenerator.getRandomMove(moves);
            move_int = moveGenerator.convertStringToPosWrapper(move_string);
            moveGenerator.movePiece(move_int[0], move_int[1]);
            new_fen = moveGenerator.getFenFromBoard();
            fen = new_fen + " b";

            if (moveGenerator.isGameOver(move_string, Color.RED)) {
                gameEnded = true;
                continue;
            }
        }

        double endTime = System.nanoTime();

        double duration = ((endTime - startTime) / 1000) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;

    }

    static double generateBestMoveSpeed(String board_fen) {
        moveGenerator.initializeBoard(board_fen);
        BasisKI ki = new BasisKI();
        double startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            ki.orchestrator(board_fen);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / 1000) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;
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

        System.out.println();
        System.out.println("Full game: ");
        System.out.println("Time to play full game: " + MoveGeneratorBM.fullGameTime() + " milliseconds");

        System.out.println();
        System.out.println("Best move: ");
        System.out.println("Time to play full game: " + MoveGeneratorBM.generateBestMoveSpeed("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b") + " milliseconds");
    }
}
