package benchmark;

import search.SearchConfig;
import search.ab.Minimax_AB;
import search.mcts.MCTS;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Locale;

/**
 * Benchmarking class for the MCTS.
 */
public class MCTSBM {

    static MCTS ai;

    /**
     * Initializes the MCTS instance.
     */
    static void init() {
        ai = new MCTS();
    }

    /**
     * Get number of Simulations.
     *
     * @param fen The board state in FEN format.
     * @param timeLimit The time limit for the search algorithm.
     */
    private static void getSimulations(String fen, double timeLimit) {
        init();
        SearchConfig config = Minimax_AB.bestConfig;
        config.timeLimit = timeLimit;

        String bestMove = ai.orchestrator(fen, config);
        double simulations = ai.numberOfAllSimulations;

        displayResults(bestMove, timeLimit, simulations);
    }

    /**
     * Displays the results of the benchmarks in a formatted table.
     *
     * @param bestMove The best move identified by the search algorithm.
     * @param timeLimit The time limit for the search algorithm in milliseconds.
     * @param simulations The total number of simulations performed.
     */
    private static void displayResults(String bestMove, double timeLimit, double simulations) {
        AsciiTable at = new AsciiTable();
        at.addRule();
        at.addRow("Time Limit (ms)",  "Best Move",  "Total Simulations", "Simulations/ms");
        at.addRule();

        at.addRow(timeLimit, bestMove, simulations, String.format(Locale.US, "%.2f", simulations / timeLimit));
        at.addRule();

        at.getRenderer().setCWC(new CWC_LongestLine());
        at.setPaddingLeftRight(1);
        at.setTextAlignment(TextAlignment.LEFT);

        System.out.println(at.render());
    }

    public static void main(String[] args) {
        try {
            PrintStream fileOut = new PrintStream(new File("src/main/java/benchmark/output/MCTSBM-output.txt"));
            System.setOut(fileOut);

            System.out.println("Start Position: ");
            String fen = "b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b";
            System.out.println(fen);
            double timeLimit = 3000;
            System.out.println();

            getSimulations(fen, timeLimit);

            System.out.println();
            System.out.println("Mid Game: ");
            fen = "2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b";
            System.out.println(fen);
            System.out.println();

            getSimulations(fen, timeLimit);

            System.out.println();
            System.out.println("End Game: ");
            fen = "5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b";
            System.out.println(fen);
            System.out.println();

            getSimulations(fen, timeLimit);

            System.out.println();
            System.out.println("Stellung 1 (Gruppe B): ");
            fen = "1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/1r0r02rr2/b03r01rr1/2r01r0r0 r";
            System.out.println(fen);
            System.out.println();

            getSimulations(fen, timeLimit);

            fileOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
