package benchmark;

import search.BasisKI;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import search.SearchConfig;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
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

    // START: benchmarks

    private static void compareTimeManagement(String fen, double timeLimit) {
        Result result_static = generateBestMoveResultTimeLimit(fen, timeLimit, false, false, false);
        double duration_static = generateBestMoveSpeedTimeLimit(fen, timeLimit, false, false, false);

        Result result_dynamic = generateBestMoveResultTimeLimit(fen, timeLimit, false, false, true);
        double duration_dynamic = generateBestMoveSpeedTimeLimit(fen, timeLimit, false, false, true);

        displayResults("static time management", "dynamic time management", timeLimit, result_static, result_dynamic, duration_static, duration_dynamic);
    }

    private static void compareAspirationWindow(String fen, double timeLimit) {
        Result result_no_window = generateBestMoveResultTimeLimit(fen, timeLimit, false, false, false);
        double duration_no_window = generateBestMoveSpeedTimeLimit(fen, timeLimit, false, false, false);

        Result result_window = generateBestMoveResultTimeLimit(fen, timeLimit, false, true, false);
        double duration_window = generateBestMoveSpeedTimeLimit(fen, timeLimit, false, true, false);

        displayResults("without aspiration window", "with aspiration window", timeLimit, result_no_window, result_window, duration_no_window, duration_window);
    }

    private static void compareTranspositionTable(String fen, double timeLimit) {
        Result result_no_TT = generateBestMoveResultTimeLimit(fen, timeLimit, false, false, false);
        double duration_no_TT = generateBestMoveSpeedTimeLimit(fen, timeLimit, false, false, false);

        Result result_TT = generateBestMoveResultTimeLimit(fen, timeLimit, true, false, false);
        double duration_TT = generateBestMoveSpeedTimeLimit(fen, timeLimit, true, false, false);

        displayResults("without transposition table", "with transposition table", timeLimit, result_no_TT, result_TT, duration_no_TT, duration_TT);
    }

    private static void compareEverything(String fen, double timeLimit) {
        Result result_clean = generateBestMoveResultTimeLimit(fen, timeLimit, false, false, false);
        double duration_clean = generateBestMoveSpeedTimeLimit(fen, timeLimit, false, false, false);

        Result result_everything = generateBestMoveResultTimeLimit(fen, timeLimit, true, true, true);
        double duration_everything = generateBestMoveSpeedTimeLimit(fen, timeLimit, true, false, true);

        displayResults("without everything", "with everything", timeLimit, result_clean, result_everything, duration_clean, duration_everything);
    }

    // END: benchmarks

    // START: helper functions

    static double calculatePercentageChange(double oldValue, double newValue) {
        if (oldValue == 0) {
            return newValue == 0 ? 0 : 100;
        }
        return ((newValue - oldValue) / oldValue) * 100;
    }

    private static Result generateBestMoveResultTimeLimit(String fen, double timeLimit, boolean transpositionTable, boolean aspirationWindow, boolean dynamicTime) {
        init();
        SearchConfig config = new SearchConfig(true, timeLimit, aspirationWindow, 0.25, transpositionTable, 0, dynamicTime);
        String bestMove = ki.orchestrator(fen, config);
        int depth = ki.maxDepth;
        int uniquePositions = ki.positionsHM.size();
        int positions = 0;
        for (Map.Entry<String, Integer> entry : ki.positionsHM.entrySet()) {
            positions += entry.getValue();
        }
        return new Result(bestMove, depth, uniquePositions, positions);
    }

    private static double generateBestMoveSpeedTimeLimit(String fen, double timeLimit, boolean transpositionTable, boolean aspirationWindow, boolean dynamicTime) {
        init();
        int iterations = 3;
        SearchConfig config = new SearchConfig(true, timeLimit, aspirationWindow, 0.25, transpositionTable, 0, dynamicTime);
        double startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ki.orchestrator(fen, config);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / iterations) / 1e6; // convert to milliseconds
        return duration;
    }

    // END: helper functions

    private static void displayResults(String settingName1, String settingName2, double timeLimit, Result result1, Result result2, double duration1, double duration2) {
        double duration_comparison = calculatePercentageChange(duration1, duration2);
        double depth_comparison = calculatePercentageChange(result1.depth, result2.depth);
        double uniquePositions_comparison = calculatePercentageChange(result1.uniquePositions, result2.uniquePositions);
        double positions_comparison = calculatePercentageChange(result1.positions, result2.positions);
        double positions_ms_comparison = calculatePercentageChange(result1.positions / duration1, result2.positions / duration2);

        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Setting", "Time Limit (ms)", "Depth", "Best Move", "Time (ms)", "Unique Positions", "Total Positions", "Positions/ms");
        at.addRule();

        at.addRow(settingName1, timeLimit, result1.depth, result1.bestMove, String.format(Locale.US, "%.2f", duration1), result1.uniquePositions, result1.positions, String.format(Locale.US, "%.2f", result1.positions / duration1));
        at.addRule();

        at.addRow(settingName2, timeLimit, result2.depth, result2.bestMove, String.format(Locale.US, "%.2f", duration2), result2.uniquePositions, result2.positions, String.format(Locale.US, "%.2f", result2.positions / duration2));
        at.addRule();

        at.addRow("Change (%)", "/", String.format(Locale.US, "%.2f", depth_comparison), "/", String.format(Locale.US, "%.2f", duration_comparison), String.format(Locale.US, "%.2f", uniquePositions_comparison), String.format(Locale.US, "%.2f", positions_comparison), String.format(Locale.US, "%.2f", positions_ms_comparison));
        at.addRule();

        at.getRenderer().setCWC(new CWC_LongestLine());
        at.setPaddingLeftRight(1);
        at.setTextAlignment(TextAlignment.LEFT);

        System.out.println(at.render());
    }

    public static void main(String[] args) {
        try {
            PrintStream fileOut = new PrintStream(new File("src/main/java/benchmark/output.txt"));
            System.setOut(fileOut);

            System.out.println("Start Position: ");
            String fen = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b";
            System.out.println(fen);
            double timeLimit = 1000;
            System.out.println();

            compareTimeManagement(fen, timeLimit);
            compareAspirationWindow(fen, timeLimit);
            compareTranspositionTable(fen, timeLimit);
            compareEverything(fen, timeLimit);

            System.out.println();
            System.out.println("Mid Game: ");
            fen = "2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b";
            System.out.println(fen);
            System.out.println();

            compareTimeManagement(fen, timeLimit);
            compareAspirationWindow(fen, timeLimit);
            compareTranspositionTable(fen, timeLimit);
            compareEverything(fen, timeLimit);

            System.out.println();
            System.out.println("End Game: ");
            fen = "5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b";
            System.out.println(fen);
            System.out.println();

            compareTimeManagement(fen, timeLimit);
            compareAspirationWindow(fen, timeLimit);
            compareTranspositionTable(fen, timeLimit);
            compareEverything(fen, timeLimit);

            System.out.println();
            System.out.println("Stellung 1 (Gruppe B): ");
            fen = "1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/1r0r02rr2/b03r01rr1/2r01r0r0 r";
            System.out.println(fen);
            System.out.println();

            compareTimeManagement(fen, timeLimit);
            compareAspirationWindow(fen, timeLimit);
            compareTranspositionTable(fen, timeLimit);
            compareEverything(fen, timeLimit);

            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}