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


    static void compareResults(String fen, int depth, double aspirationWindowSize, double timeLimit, boolean transpositionTables,  boolean dynamicTime) {
        //basis KI
        String bestMove = BasisKIBM.generateBestMoveDepthLimit(fen, depth);
        double duration = BasisKIBM.generateBestMoveSpeedDepthLimit(fen, depth);
        int uniquePositions = BasisKIBM.generateBestMoveUniquePositionsDepthLimit(fen, depth);
        int positions = BasisKIBM.generateBestMovePositionsDepthLimit(fen, depth);

        // apiration Windows
        String bestMoveW = BasisKIBM.generateBestMoveWindowDepthLimit(fen, depth, aspirationWindowSize);
        double durationW = BasisKIBM.generateBestMoveWindowSpeedDepthLimit(fen, depth, aspirationWindowSize);
        int uniquePositionsW = BasisKIBM.generateBestMoveWindowUniquePositionsDepthLimit(fen, depth, aspirationWindowSize);
        int positionsW = BasisKIBM.generateBestMoveWindowPositionsDepthLimit(fen, depth, aspirationWindowSize);

        //transposition tables with time limit
        Result bestMoveResult = BasisKIBM.generateBestMoveTT(fen, timeLimit, transpositionTables);
        double durationTT = BasisKIBM.generateBestMoveSpeedTimeLimitTT(fen, timeLimit, transpositionTables);
        String bestMoveTT = bestMoveResult.bestMove;
        int depthTT = bestMoveResult.depth;
        int uniquePositionsTT = bestMoveResult.uniquePositions;
        int positionsTT = bestMoveResult.positions;

        //dynamic time managment
        Result bestMoveResultDyn = BasisKIBM.generateBestMoveDyn(fen, timeLimit, dynamicTime);
        double durationDyn = BasisKIBM.generateBestMoveSpeedTimeLimitDyn(fen, timeLimit, transpositionTables);
        String bestMoveDyn = bestMoveResultDyn.bestMove;
        int depthDyn = bestMoveResultDyn.depth;
        int uniquePositionsDyn = bestMoveResultDyn.uniquePositions;
        int positionsDyn = bestMoveResultDyn.positions;

        // transposition tables and aspiration windows
        Result bestMoveResultTTAsp = BasisKIBM.generateBestMoveTTAsp(fen, depth, aspirationWindowSize, transpositionTables);
        double durationTTAsp = BasisKIBM.generateBestMoveSpeedTimeLimitTTAsp(fen,depth,aspirationWindowSize, transpositionTables);
        String bestMoveTTAsp = bestMoveResultTTAsp.bestMove;
        int depthTTAsp = bestMoveResultTTAsp.depth;
        int uniquePositionsTTAsp = bestMoveResultTTAsp.uniquePositions;
        int positionsTTAsp = bestMoveResultTTAsp.positions;

        // transposition tables, and dynamic time management
        Result bestMovesTTDyn = BasisKIBM.generateBestMoveTTDyn(fen, depth,  transpositionTables, dynamicTime);
        double durationTTDyn = BasisKIBM.generateBestMoveSpeedTimeLimitTTDyn(fen,depth,transpositionTables,dynamicTime);
        String bestMoveTTDyn = bestMovesTTDyn.bestMove;
        int depthTTDyn = bestMovesTTDyn.depth;
        int uniquePositionsTTDyn = bestMovesTTDyn.uniquePositions;
        int positionsTTDyn = bestMovesTTDyn.positions;

        // transposition tables, aspiration windows, and dynamic time management
        Result bestMovesTTDynAsp = BasisKIBM.generateBestMoveTTDynAsp(fen, depth, aspirationWindowSize, transpositionTables, dynamicTime);
        double durationTTDynAsp = BasisKIBM.generateBestMoveSpeedTimeLimitTTDynAsp(fen,depth,aspirationWindowSize,transpositionTables,dynamicTime);
        String bestMoveTTDynAsp = bestMovesTTDynAsp.bestMove;
        int depthTTDynAsp = bestMovesTTDynAsp.depth;
        int uniquePositionsTTDynAsp = bestMovesTTDynAsp.uniquePositions;
        int positionsTTDynAsp = bestMovesTTDynAsp.positions;

        //comparison
        double uniquePositionsChange = calculatePercentageChange(uniquePositions, uniquePositionsW);
        double positionsChange = calculatePercentageChange( positions,positionsW);
        double positionsPerMsWithoutWindow = positions / duration;
        double positionsPerMsWithWindow = positionsW / durationW;
        double durationChange = calculatePercentageChange(duration, durationW);
        double positionsPerMsChange = calculatePercentageChange(positionsPerMsWithoutWindow, positionsPerMsWithWindow);

        // display as table (reference: https://github.com/vdmeer/asciitable)
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Setting","Depth", "Best Move", "Time (ms)", "Unique Positions", "Total Positions", "Positions/ms");
        at.addRule();
        // without asp window
        at.addRow("basic KI",depth, bestMove, String.format(Locale.US, "%.2f", duration), uniquePositions, positions, String.format(Locale.US, "%.2f", positions / duration));
        at.addRule();
        // with asp window
        at.addRow(" +Asp. Window",depth, bestMoveW, String.format(Locale.US, "%.2f", durationW), uniquePositionsW, positionsW, String.format(Locale.US, "%.2f", positionsW / durationW));
        at.addRule();
        at.addRow("Change (%)",depth, "/", String.format(Locale.US, "%.2f", durationChange),String.format(Locale.US, "%.2f", uniquePositionsChange), String.format(Locale.US, "%.2f", positionsChange), String.format(Locale.US, "%.2f", positionsPerMsChange));
        at.addRule();
        at.addRow("","","","","","","");
        at.addRule();
        at.addRow("basic KI",depth, bestMove, String.format(Locale.US, "%.2f", duration), uniquePositions, positions, String.format(Locale.US, "%.2f", positions / duration));
        at.addRule();
        at.addRow("+ TT (timelimit: 1000)",depthTT, bestMoveTT, String.format(Locale.US, "%.2f", durationTT), uniquePositionsTT, positionsTT, String.format(Locale.US, "%.2f", positionsTT / durationTT));
        at.addRule();
        at.addRow("Change (%)",calculatePercentageChange(depth,depthTT), "/", String.format(Locale.US, "%.2f", calculatePercentageChange(duration,durationTT)),String.format(Locale.US, "%.2f", calculatePercentageChange(uniquePositions,uniquePositionsTT)), String.format(Locale.US, "%.2f", calculatePercentageChange(positions,positionsTT)), String.format(Locale.US, "%.2f", calculatePercentageChange(positions/duration,positionsTT/durationTT)));
        at.addRule();
        at.addRow("","","","","","","");
        at.addRule();
        at.addRow("basic KI",depth, bestMove, String.format(Locale.US, "%.2f", duration), uniquePositions, positions, String.format(Locale.US, "%.2f", positions / duration));
        at.addRule();
        at.addRow("+ dynamic time management",depthDyn, bestMoveDyn, String.format(Locale.US, "%.2f", durationDyn), uniquePositionsDyn, positionsDyn, String.format(Locale.US, "%.2f", positionsDyn / durationDyn));
        at.addRule();
        at.addRow("Change (%)",calculatePercentageChange(depth,depthDyn), "/", String.format(Locale.US, "%.2f", calculatePercentageChange(duration,durationDyn)),String.format(Locale.US, "%.2f", calculatePercentageChange(uniquePositions,uniquePositionsDyn)), String.format(Locale.US, "%.2f", calculatePercentageChange(positions,positionsDyn)), String.format(Locale.US, "%.2f", calculatePercentageChange(positions/duration,positionsDyn/durationDyn)));
        at.addRule();
        at.addRow("","","","","","","");
        at.addRule();
        at.addRow("basic KI",depth, bestMove, String.format(Locale.US, "%.2f", duration), uniquePositions, positions, String.format(Locale.US, "%.2f", positions / duration));
        at.addRule();
        at.addRow("+ TT, Asp. Window",depthTTAsp, bestMoveTTAsp, String.format(Locale.US, "%.2f", durationTTAsp), uniquePositionsTTAsp, positionsTTAsp, String.format(Locale.US, "%.2f", positionsTTAsp / durationTTAsp));
        at.addRule();
        at.addRow("Change (%)",calculatePercentageChange(depth,depthTTAsp), "/", String.format(Locale.US, "%.2f", calculatePercentageChange(duration,durationTTAsp)),String.format(Locale.US, "%.2f", calculatePercentageChange(uniquePositions,uniquePositionsTTAsp)), String.format(Locale.US, "%.2f", calculatePercentageChange(positions,positionsTTAsp)), String.format(Locale.US, "%.2f", calculatePercentageChange(positions/duration,positionsTTAsp/durationTTAsp)));
        at.addRule();
        at.addRow("","","","","","","");
        at.addRule();
        at.addRow("basic KI",depth, bestMove, String.format(Locale.US, "%.2f", duration), uniquePositions, positions, String.format(Locale.US, "%.2f", positions / duration));
        at.addRule();
        at.addRow(" + TT, dynamic time management",depthTTDyn, bestMoveTTDyn, String.format(Locale.US, "%.2f", durationTTDyn), uniquePositionsTTDyn, positionsTTDyn, String.format(Locale.US, "%.2f", positionsTTDyn / durationTTDyn));
        at.addRule();
        at.addRow("Change (%)",calculatePercentageChange(depth,depthTTDyn), "/", String.format(Locale.US, "%.2f", calculatePercentageChange(duration,durationTTDyn)),String.format(Locale.US, "%.2f", calculatePercentageChange(uniquePositions,uniquePositionsTTDyn)), String.format(Locale.US, "%.2f", calculatePercentageChange(positions,positionsTTDyn)), String.format(Locale.US, "%.2f", calculatePercentageChange(positions/duration,positionsTTDyn/durationTTDyn)));
        at.addRule();
        at.addRow("","","","","","","");
        at.addRule();
        at.addRow("basic KI",depth, bestMove, String.format(Locale.US, "%.2f", duration), uniquePositions, positions, String.format(Locale.US, "%.2f", positions / duration));
        at.addRule();
        at.addRow("+ Asp. Window, TT, dynamic time management",depthTTDynAsp, bestMoveTTDynAsp, String.format(Locale.US, "%.2f", durationTTDynAsp), uniquePositionsTTDynAsp, positionsTTDynAsp, String.format(Locale.US, "%.2f", positionsTTDynAsp / durationTTDynAsp));
        at.addRule();
        at.addRow("Change (%)",calculatePercentageChange(depth,depthTTDynAsp), "/", String.format(Locale.US, "%.2f", calculatePercentageChange(duration,durationTTDynAsp)),String.format(Locale.US, "%.2f", calculatePercentageChange(uniquePositions,uniquePositionsTTDynAsp)), String.format(Locale.US, "%.2f", calculatePercentageChange(positions,positionsTTDynAsp)), String.format(Locale.US, "%.2f", calculatePercentageChange(positions/duration,positionsTTDynAsp/durationTTDynAsp)));
        at.addRule();
        at.addRow("","","","","","","");
        at.addRule();
        at.addRow("basic KI",depth, bestMove, String.format(Locale.US, "%.2f", duration), uniquePositions, positions, String.format(Locale.US, "%.2f", positions / duration));
        at.addRule();
        at.addRow(("+ time limit  " + timeLimit), depth, bestMove, String.format(Locale.US, "%.2f", duration), uniquePositions, positions, String.format(Locale.US, "%.2f", positions / duration));
        at.addRule();

        at.getRenderer().setCWC(new CWC_LongestLine());
        at.setPaddingLeftRight(1);
        at.setTextAlignment(TextAlignment.LEFT);
        System.out.println(at.render());
    }

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
    static Result generateBestMoveDyn(String board_fen, double ms, boolean dynamicTime) {
        init();
        String bestMove = ki.orchestrator(board_fen, ms, dynamicTime);
        int depth = ki.maxDepth;
        int uniquePositions = ki.positionsHM.size();
        int positions = 0;
        for (Map.Entry<String, Integer> entry : ki.positionsHM.entrySet()){
            positions += entry.getValue();
        }
        return new Result(bestMove, depth, uniquePositions, positions);
    }

    // best move
    static Result generateBestMoveTT(String board_fen, double ms, boolean transpositionTables) {
        init();
        String bestMove = ki.orchestrator(board_fen, ms, transpositionTables);
        int depth = ki.maxDepth;
        int uniquePositions = ki.positionsHM.size();
        int positions = 0;
        for (Map.Entry<String, Integer> entry : ki.positionsHM.entrySet()){
            positions += entry.getValue();
        }
        return new Result(bestMove, depth, uniquePositions, positions);
    }

    static Result generateBestMoveTTAsp(String fen, int actualMaxDepth, double aspirationWindowSize, boolean transpositionTables) {
        init();
        String bestMove = ki.orchestrator(fen, actualMaxDepth, aspirationWindowSize, transpositionTables);
        int depth = ki.maxDepth;
        int uniquePositions = ki.positionsHM.size();
        int positions = 0;
        for (Map.Entry<String, Integer> entry : ki.positionsHM.entrySet()){
            positions += entry.getValue();
        }
        return new Result(bestMove, depth, uniquePositions, positions);
    }

    static Result generateBestMoveTTDynAsp(String fen, int actualMaxDepth, double aspirationWindowSize, boolean transpositionTables, boolean dynamicTime) {
        init();
        String bestMove = ki.orchestrator(fen, actualMaxDepth, aspirationWindowSize, transpositionTables, dynamicTime);
        int depth = ki.maxDepth;
        int uniquePositions = ki.positionsHM.size();
        int positions = 0;
        for (Map.Entry<String, Integer> entry : ki.positionsHM.entrySet()){
            positions += entry.getValue();
        }
        return new Result(bestMove, depth, uniquePositions, positions);
    }
    static Result generateBestMoveTTDyn(String fen, int actualMaxDepth, boolean transpositionTables, boolean dynamicTime) {
        init();
        String bestMove = ki.orchestrator(fen, actualMaxDepth, transpositionTables,1000, dynamicTime);
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

    static double generateBestMoveSpeedTimeLimitTT(String board_fen, double ms, boolean transpositionTables) {
        init();
        int iterations = 3;
        double startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ki.orchestrator(board_fen, ms, transpositionTables);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / iterations) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;
    }

    static double generateBestMoveSpeedTimeLimitDyn(String board_fen, double ms, boolean dynamicTime) {
        init();
        int iterations = 3;
        double startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ki.orchestrator(board_fen, ms, dynamicTime);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / iterations) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;
    }

    static double generateBestMoveSpeedTimeLimitTTAsp(String fen, int actualMaxDepth, double aspirationWindowSize, boolean transpositionTables) {
        init();
        int iterations = 3;
        double startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ki.orchestrator(fen, actualMaxDepth, aspirationWindowSize, transpositionTables);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / iterations) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;
    }

    static double generateBestMoveSpeedTimeLimitTTDyn(String fen, int actualMaxDepth,  boolean transpositionTables, boolean dynamicTime) {
        init();
        int iterations = 3;
        double startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ki.orchestrator(fen, actualMaxDepth, transpositionTables,10000,dynamicTime);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / iterations) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;
    }

    static double generateBestMoveSpeedTimeLimitTTDynAsp(String fen, int actualMaxDepth, double aspirationWindowSize, boolean transpositionTables, boolean dynamicTime) {
        init();
        int iterations = 3;
        double startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ki.orchestrator(fen, actualMaxDepth, aspirationWindowSize, transpositionTables, dynamicTime);
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
        double[] aspirationWindowSizes = {0.5, 0.25, 0.05};
        int[] depths = {2, 3, 4, 6};

        System.out.println();
        for (double aspirationWindowSize : aspirationWindowSizes) {
            System.out.println("Aspiration Window (" + aspirationWindowSize + " | with depth limit)");

            System.out.println();
            System.out.println("Start position: ");
            for (int depth : depths) {
                compareResults("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", depth, aspirationWindowSize, 1000, true, true);
                System.out.println();
            }

            System.out.println();
            System.out.println("Mid Game: ");
            for (int depth : depths) {
                compareResults("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", depth, aspirationWindowSize, 1000, true, true);
            }

            System.out.println();
            System.out.println("End Game: ");
            for (int depth : depths) {
                compareResults("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", depth, aspirationWindowSize, 1000, true, true);
            }

            System.out.println();
            System.out.println("Position Gruppe B: ");
            for (int depth : depths) {
                compareResults("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/1r0r02rr2/b03r01rr1/2r01r0r0 r", depth, aspirationWindowSize, 1000, true, true);
            }
        }

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