package benchmark;

import search.BasisKI;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.util.Locale;
import java.util.Map;

public class BasisKIBM {

    static BasisKI ki;

    static void init() {
        ki = new BasisKI();
    }

    static class Result {
        String bestMove;
        int depth;
        int uniquePositions;
        int positions;

        Result(String bestMove, int depth, int uniquePositions, int positions) {
            this.bestMove = bestMove;
            this.depth = depth;
            this.uniquePositions = uniquePositions;
            this.positions = positions;
        }
    }

    // START: time limit

    static Result generateBestMoveResultTimeLimit(String board_fen, double ms) {
        init();
        String bestMove = ki.orchestrator(board_fen, ms);
        int depth = ki.maxDepth;
        int uniquePositions = ki.positionsHM.size();
        int positions = 0;
        for (Map.Entry<String, Integer> entry : ki.positionsHM.entrySet()){
            positions += entry.getValue();
        }
        return new Result(bestMove, depth, uniquePositions, positions);
    }

    static double generateBestMoveSpeedTimeLimit(String board_fen, double ms) {
        init();
        int iterations = 10;
        double startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ki.orchestrator(board_fen, ms);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / iterations) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;
    }

    static void wrapperBMTimeLimit(String fen, double ms) {
        double duration = BasisKIBM.generateBestMoveSpeedTimeLimit(fen, ms);
        Result bestMoveResult = BasisKIBM.generateBestMoveResultTimeLimit(fen, ms);

        String bestMove = bestMoveResult.bestMove;
        int depth = bestMoveResult.depth;
        int uniquePositions = bestMoveResult.uniquePositions;
        int positions = bestMoveResult.positions;

        // display as table (reference: https://github.com/vdmeer/asciitable)
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Time Limit (ms)", "Depth", "Best Move", "Time (ms)", "Unique Positions", "Total Positions", "Positions/ms");
        at.addRule();
        at.addRow(ms, depth, bestMove, String.format(Locale.US, "%.2f", duration), uniquePositions, positions, String.format(Locale.US, "%.2f", positions / duration));
        at.addRule();
        at.getRenderer().setCWC(new CWC_LongestLine());
        at.setPaddingLeftRight(1);
        at.setTextAlignment(TextAlignment.LEFT);

        System.out.println(at.render());
    }

    // END: time limit

    // START: depth limit

    static String generateBestMoveDepthLimit(String board_fen, int depth) {
        init();
        String bestMove = ki.orchestrator(board_fen, depth);
        return bestMove;
    }

    static double generateBestMoveSpeedDepthLimit(String board_fen, int depth) {
        init();
        int iterations = 10;
        double startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ki.orchestrator(board_fen, depth);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / iterations) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;
    }

    static int generateBestMoveUniquePositionsDepthLimit(String board_fen, int depth) {
        init();
        ki.orchestrator(board_fen, depth);
        int uniquePositions = ki.positionsHM.size();
        return uniquePositions;
    }

    static int generateBestMovePositionsDepthLimit(String board_fen, int depth) {
        init();
        ki.orchestrator(board_fen, depth);
        int positions = 0;
        for (Map.Entry<String, Integer> entry : ki.positionsHM.entrySet()){
            positions += entry.getValue();
        }
        return positions;
    }

    static void wrapperBMDepthLimit(String fen, int depth) {
        String bestMove = BasisKIBM.generateBestMoveDepthLimit(fen, depth);
        double duration = BasisKIBM.generateBestMoveSpeedDepthLimit(fen, depth);
        int uniquePositions = BasisKIBM.generateBestMoveUniquePositionsDepthLimit(fen, depth);
        int positions = BasisKIBM.generateBestMovePositionsDepthLimit(fen, depth);

        // display as table (reference: https://github.com/vdmeer/asciitable)
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Depth", "Best Move", "Time (ms)", "Unique Positions", "Total Positions", "Positions/ms");
        at.addRule();
        at.addRow(depth, bestMove, String.format(Locale.US, "%.2f", duration), uniquePositions, positions, String.format(Locale.US, "%.2f", positions / duration));
        at.addRule();
        at.getRenderer().setCWC(new CWC_LongestLine());
        at.setPaddingLeftRight(1);
        at.setTextAlignment(TextAlignment.LEFT);

        System.out.println(at.render());
    }

    // END: depth limit

    public static void main(String[] args) {
        System.out.println("Depth limit:");

        System.out.println();
        System.out.println("Start position: ");
        BasisKIBM.wrapperBMDepthLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 2);
        BasisKIBM.wrapperBMDepthLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 3);
        BasisKIBM.wrapperBMDepthLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 4);

        System.out.println();
        System.out.println("Mid game: ");
        BasisKIBM.wrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 2);
        BasisKIBM.wrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 3);
        BasisKIBM.wrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 4);

        System.out.println();
        System.out.println("End game: ");
        BasisKIBM.wrapperBMDepthLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 2);
        BasisKIBM.wrapperBMDepthLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 3);
        BasisKIBM.wrapperBMDepthLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 4);

        System.out.println();
        double timeLimit = 20000;
        System.out.println("Time limit: (" + timeLimit + " ms)");

        System.out.println();
        System.out.println("Start position: ");
        BasisKIBM.wrapperBMTimeLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", timeLimit);

        System.out.println();
        System.out.println("Mid game: ");
        BasisKIBM.wrapperBMTimeLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", timeLimit);

        System.out.println();
        System.out.println("End game: ");
        BasisKIBM.wrapperBMTimeLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", timeLimit);

    }
}