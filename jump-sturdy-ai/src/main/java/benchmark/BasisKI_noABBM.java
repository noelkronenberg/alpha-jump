package benchmark;

import search.BasisKI_noAB;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import java.util.Locale;
import java.util.Map;

public class BasisKI_noABBM {

    static BasisKI_noAB ki;

    static void init() {
        ki = new BasisKI_noAB();
    }

    static String generateBestMove(String board_fen, int depth) {
        init();
        String bestMove = ki.orchestratorNoAlphaBeta(board_fen, depth);
        return bestMove;
    }

    static double generateBestMoveSpeed(String board_fen, int depth) {
        init();
        int iterations = 10;
        double startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ki.orchestratorNoAlphaBeta(board_fen, depth);
        }
        double endTime = System.nanoTime();
        double duration = ((endTime - startTime) / iterations) / 1e6; // convert to milliseconds (reference: https://stackoverflow.com/a/924220)
        return duration;
    }

    static int generateBestMoveUniquePositions(String board_fen, int depth) {
        init();
        ki.orchestratorNoAlphaBeta(board_fen, depth);
        int uniquePositions = ki.positionsHM.size();
        return uniquePositions;
    }

    static int generateBestMovePositions(String board_fen, int depth) {
        init();
        ki.orchestratorNoAlphaBeta(board_fen, depth);
        int positions = 0;
        for (Map.Entry<String, Integer> entry : ki.positionsHM.entrySet()){
            positions += entry.getValue();
        }
        return positions;
    }

    static void wrapperBM(String fen, int depth) {
        String bestMove = BasisKI_noABBM.generateBestMove(fen, depth);
        double duration = BasisKI_noABBM.generateBestMoveSpeed(fen, depth);
        int uniquePositions = BasisKI_noABBM.generateBestMoveUniquePositions(fen, depth);
        int positions = BasisKI_noABBM.generateBestMovePositions(fen, depth);

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

    public static void main(String[] args) {
        System.out.println("Start position: ");
        BasisKI_noABBM.wrapperBM("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 2);
        BasisKI_noABBM.wrapperBM("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 3);
        BasisKI_noABBM.wrapperBM("b0b0b0b0b0b0/1b0b0b0b0b0b01/8/8/8/8/1r0r0r0r0r0r01/r0r0r0r0r0r0 b", 4);

        System.out.println();
        System.out.println("Mid game: ");
        BasisKI_noABBM.wrapperBM("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 2);
        BasisKI_noABBM.wrapperBM("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 3);
        BasisKI_noABBM.wrapperBM("2bbbb1b0/1b06/1b01b04/4b03/4r03/3r02b01/1r0r02rr2/2rr2r0 b", 4);

        System.out.println();
        System.out.println("End game: ");
        BasisKI_noABBM.wrapperBM("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 2);
        BasisKI_noABBM.wrapperBM("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 3);
        BasisKI_noABBM.wrapperBM("5b0/1bbb0b0brb0b01/8/3b0r03/8/4b03/1rr1b0r0rrrr1/1r04 b", 4);
    }
}