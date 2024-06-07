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
    static double calculatePercentageChange(double oldValue, double newValue) {
        if (oldValue == 0) {
            return newValue == 0 ? 0 : 100;
        }
        return ((newValue - oldValue) / oldValue) * 100;
    }

    static void compareResults(String fen, int depth, double aspirationWindowSize) {
        String bestMove = BasisKIBM.generateBestMoveDepthLimit(fen, depth);
        double duration = BasisKIBM.generateBestMoveSpeedDepthLimit(fen, depth);
        int uniquePositions = BasisKIBM.generateBestMoveUniquePositionsDepthLimit(fen, depth);
        int positions = BasisKIBM.generateBestMovePositionsDepthLimit(fen, depth);

        String bestMoveW = BasisKIBM.generateBestMoveWindowDepthLimit(fen, depth, aspirationWindowSize);
        double durationW = BasisKIBM.generateBestMoveWindowSpeedDepthLimit(fen, depth, aspirationWindowSize);
        int uniquePositionsW = BasisKIBM.generateBestMoveWindowUniquePositionsDepthLimit(fen, depth, aspirationWindowSize);
        int positionsW = BasisKIBM.generateBestMoveWindowPositionsDepthLimit(fen, depth, aspirationWindowSize);

        double uniquePositionsChange = calculatePercentageChange(uniquePositions, uniquePositionsW);
        double positionsChange = calculatePercentageChange( positions,positionsW);
        double positionsPerMsWithoutWindow = positions / duration;
        double positionsPerMsWithWindow = positionsW / durationW;
        double durationChange = calculatePercentageChange(duration, durationW);
        double positionsPerMsChange = calculatePercentageChange(positionsPerMsWithoutWindow, positionsPerMsWithWindow);

        // display as table (reference: https://github.com/vdmeer/asciitable)
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Depth", "Best Move", "Time (ms)", "Unique Positions", "Total Positions", "Positions/ms");
        at.addRule();
        at.addRow(depth, bestMove, String.format(Locale.US, "%.2f", duration), uniquePositions, positions, String.format(Locale.US, "%.2f", positions / duration));
        at.addRule();
        at.addRow(depth, bestMoveW, String.format(Locale.US, "%.2f", durationW), uniquePositionsW, positionsW, String.format(Locale.US, "%.2f", positionsW / durationW));
        at.addRule();
        at.addRow("Change (%)", "/", String.format(Locale.US, "%.2f", durationChange),String.format(Locale.US, "%.2f", uniquePositionsChange), String.format(Locale.US, "%.2f", positionsChange), String.format(Locale.US, "%.2f", positionsPerMsChange));
        at.addRule();
        at.getRenderer().setCWC(new CWC_LongestLine());
        at.setPaddingLeftRight(1);
        at.setTextAlignment(TextAlignment.LEFT);
        System.out.println(at.render());
    }

    // START: time limit

    // best move
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

    // best move (without dynamic time management)
    static Result generateBestMoveNotDynamicResultTimeLimit(String board_fen, double ms) {
        init();
        String bestMove = ki.orchestrator(board_fen, ms, false);
        int depth = ki.maxDepth;
        int uniquePositions = ki.positionsHM.size();
        int positions = 0;
        for (Map.Entry<String, Integer> entry : ki.positionsHM.entrySet()){
            positions += entry.getValue();
        }
        return new Result(bestMove, depth, uniquePositions, positions);
    }

    // speed for best move
    static double generateBestMoveSpeedTimeLimit(String board_fen, double ms) {
        init();
        int iterations = 3;
        double startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ki.orchestrator(board_fen, ms);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / iterations) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;
    }

    // speed for best move (without dynamic time management)
    static double generateBestMoveNotDynamicSpeedTimeLimit(String board_fen, double ms) {
        init();
        int iterations = 3;
        double startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ki.orchestrator(board_fen, ms, false);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / iterations) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;
    }

    // combination and visualisation of all
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
        //System.out.println(calculatePercentageChange(duration, ));
        at.addRule();
        at.getRenderer().setCWC(new CWC_LongestLine());
        at.setPaddingLeftRight(1);
        at.setTextAlignment(TextAlignment.LEFT);

        System.out.println(at.render());
    }

    // combination and visualisation of all (without dynamic time management)
    static void notDynamicWrapperBMTimeLimit(String fen, double ms) {
        double duration = BasisKIBM.generateBestMoveNotDynamicSpeedTimeLimit(fen, ms);
        Result bestMoveResult = BasisKIBM.generateBestMoveNotDynamicResultTimeLimit(fen, ms);

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

    // best move
    static String generateBestMoveDepthLimit(String board_fen, int depth) {
        init();
        String bestMove = ki.orchestrator(board_fen, depth);
        return bestMove;
    }

    // best move (with aspiration window)
    static String generateBestMoveWindowDepthLimit(String board_fen, int depth, double aspirationWindowSize) {
        init();
        String bestMove = ki.orchestrator(board_fen, depth, aspirationWindowSize);
        return bestMove;
    }

    // speed for best move
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

    // speed for best move (with aspiration window)
    static double generateBestMoveWindowSpeedDepthLimit(String board_fen, int depth, double aspirationWindowSize) {
        init();
        int iterations = 10;
        double startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ki.orchestrator(board_fen, depth, aspirationWindowSize);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / iterations) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;
    }

    // unique positions for best move
    static int generateBestMoveUniquePositionsDepthLimit(String board_fen, int depth) {
        init();
        ki.orchestrator(board_fen, depth);
        int uniquePositions = ki.positionsHM.size();
        return uniquePositions;
    }

    // unique positions for best move (with aspiration window)
    static int generateBestMoveWindowUniquePositionsDepthLimit(String board_fen, int depth, double aspirationWindowSize) {
        init();
        ki.orchestrator(board_fen, depth, aspirationWindowSize);
        int uniquePositions = ki.positionsHM.size();
        return uniquePositions;
    }

    // positions for best move
    static int generateBestMovePositionsDepthLimit(String board_fen, int depth) {
        init();
        ki.orchestrator(board_fen, depth);
        int positions = 0;
        for (Map.Entry<String, Integer> entry : ki.positionsHM.entrySet()){
            positions += entry.getValue();
        }
        return positions;
    }

    // positions for best move (with aspiration window)
    static int generateBestMoveWindowPositionsDepthLimit(String board_fen, int depth, double aspirationWindowSize) {
        init();
        ki.orchestrator(board_fen, depth, aspirationWindowSize);
        int positions = 0;
        for (Map.Entry<String, Integer> entry : ki.positionsHM.entrySet()){
            positions += entry.getValue();
        }
        return positions;
    }

    // combination and visualisation of all
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

    // combination and visualisation of all (with window)
    static void windowsWrapperBMDepthLimit(String fen, int depth, double aspirationWindowSize) {
        String bestMove = BasisKIBM.generateBestMoveWindowDepthLimit(fen, depth, aspirationWindowSize);
        double duration = BasisKIBM.generateBestMoveWindowSpeedDepthLimit(fen, depth, aspirationWindowSize);
        int uniquePositions = BasisKIBM.generateBestMoveWindowUniquePositionsDepthLimit(fen, depth, aspirationWindowSize);
        int positions = BasisKIBM.generateBestMoveWindowPositionsDepthLimit(fen, depth, aspirationWindowSize);

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

        System.out.println("Depth limit");

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
        BasisKIBM.wrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 6);

        System.out.println();
        System.out.println("End game: ");
        BasisKIBM.wrapperBMDepthLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 2);
        BasisKIBM.wrapperBMDepthLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 3);
        BasisKIBM.wrapperBMDepthLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 4);

        System.out.println();
        System.out.println("Position Gruppe B: ");
        BasisKIBM.wrapperBMDepthLimit("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/1r0r02rr2/b03r01rr1/2r01r0r0 r", 6);

        // ---

        System.out.println();
        double aspirationWindowSize = 0.5;
        System.out.println("Aspiration Window (" + aspirationWindowSize + " | with depth limit)");

        System.out.println();
        System.out.println("Start position: ");
        BasisKIBM.windowsWrapperBMDepthLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 2, aspirationWindowSize);
        compareResults("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 2, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 3, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 4, aspirationWindowSize);
        compareResults("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 4, aspirationWindowSize);

        System.out.println();
        System.out.println("Mid game: ");
        BasisKIBM.windowsWrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 2, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 3, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 4, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 6, aspirationWindowSize);

        System.out.println();
        System.out.println("End game: ");
        BasisKIBM.windowsWrapperBMDepthLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 2, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 3, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 4, aspirationWindowSize);

        System.out.println();
        System.out.println("Position Gruppe B: ");
        BasisKIBM.windowsWrapperBMDepthLimit("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/1r0r02rr2/b03r01rr1/2r01r0r0 r", 6, aspirationWindowSize);

        // ---

        aspirationWindowSize = 0.25;
        System.out.println("Aspiration Window (" + aspirationWindowSize + " | with depth limit)");

        System.out.println();
        System.out.println("Start position: ");
        System.out.println("Without: ");
        BasisKIBM.wrapperBMDepthLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 2);
        System.out.println("With: ");
        BasisKIBM.windowsWrapperBMDepthLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 2, aspirationWindowSize);
        compareResults("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 2, aspirationWindowSize);
        System.out.println("Without: ");
        BasisKIBM.wrapperBMDepthLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 3);
        System.out.println("With: ");
        BasisKIBM.windowsWrapperBMDepthLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 3, aspirationWindowSize);
        compareResults("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 3, aspirationWindowSize);
        System.out.println("Without: ");
        BasisKIBM.wrapperBMDepthLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 4);
        System.out.println("With: ");
        BasisKIBM.windowsWrapperBMDepthLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 4, aspirationWindowSize);
        compareResults("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 4, aspirationWindowSize);

        System.out.println();
        System.out.println("Mid game: ");
        BasisKIBM.windowsWrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 2, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 3, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 4, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 6, aspirationWindowSize);

        System.out.println();
        System.out.println("End game: ");
        BasisKIBM.windowsWrapperBMDepthLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 2, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 3, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 4, aspirationWindowSize);


        System.out.println();
        System.out.println("Position Gruppe B: ");
        BasisKIBM.windowsWrapperBMDepthLimit("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/1r0r02rr2/b03r01rr1/2r01r0r0 r", 6, aspirationWindowSize);

        // ---

        System.out.println();
        aspirationWindowSize = 0.05;
        System.out.println("Aspiration Window (" + aspirationWindowSize + " | with depth limit)");

        System.out.println();
        System.out.println("Start position: ");
        BasisKIBM.windowsWrapperBMDepthLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 2, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 3, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 4, aspirationWindowSize);


        System.out.println();
        System.out.println("Mid game: ");
        BasisKIBM.windowsWrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 2, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 3, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 4, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 6, aspirationWindowSize);

        System.out.println();
        System.out.println("End game: ");
        BasisKIBM.windowsWrapperBMDepthLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 2, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 3, aspirationWindowSize);
        BasisKIBM.windowsWrapperBMDepthLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 4, aspirationWindowSize);

        System.out.println();
        System.out.println("Position Gruppe B: ");
        BasisKIBM.windowsWrapperBMDepthLimit("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/1r0r02rr2/b03r01rr1/2r01r0r0 r", 6, aspirationWindowSize);

        // ---

        System.out.println();
        double timeLimit = 20000.0;
        System.out.println("Time limit (" + timeLimit + " ms):");

        System.out.println();
        System.out.println("Start position: ");
        BasisKIBM.wrapperBMTimeLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", timeLimit);

        System.out.println();
        System.out.println("Mid game: ");
        BasisKIBM.wrapperBMTimeLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", timeLimit);

        System.out.println();
        System.out.println("End game: ");
        BasisKIBM.wrapperBMTimeLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", timeLimit);

        System.out.println();
        System.out.println("Position Gruppe B: ");
        BasisKIBM.wrapperBMTimeLimit("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/1r0r02rr2/b03r01rr1/2r01r0r0 r", timeLimit);

        // ---

        System.out.println();
        timeLimit = 20000.0;
        System.out.println("Time limit (without dynamic time management | " + timeLimit + " ms):");

        System.out.println();
        System.out.println("Start position: ");
        BasisKIBM.notDynamicWrapperBMTimeLimit("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", timeLimit);

        System.out.println();
        System.out.println("Mid game: ");
        BasisKIBM.notDynamicWrapperBMTimeLimit("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", timeLimit);

        System.out.println();
        System.out.println("End game: ");
        BasisKIBM.notDynamicWrapperBMTimeLimit("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", timeLimit);

        System.out.println();
        System.out.println("Position Gruppe B: ");
        BasisKIBM.notDynamicWrapperBMTimeLimit("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/1r0r02rr2/b03r01rr1/2r01r0r0 r", timeLimit);
    }
}