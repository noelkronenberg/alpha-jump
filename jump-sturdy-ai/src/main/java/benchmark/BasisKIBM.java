package benchmark;

import game.Color;
import game.MoveGenerator;
import search.BasisKI;

public class BasisKIBM {

    static MoveGenerator moveGenerator;

    static void init() {
        moveGenerator = new MoveGenerator();
        moveGenerator.initializeBoard();
    }

    static double generateBestMoveSpeed(String board_fen) {
        moveGenerator.initializeBoard(board_fen);
        BasisKI ki = new BasisKI();
        double startTime = System.nanoTime();
        for (int i = 0; i < 100000; i++) {
            ki.orchestrator(board_fen);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / 100000) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;
    }

    static void wrapperBM(String fen) {
        double duration =  BasisKIBM.generateBestMoveSpeed(fen);
        System.out.println("Time to play best move: " + duration + " milliseconds");
    }

    public static void main(String[] args) {
        BasisKIBM.init();

        System.out.println("Example position: ");
        BasisKIBM.wrapperBM("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b");
    }
}
