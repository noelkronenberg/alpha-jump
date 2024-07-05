package search;

import game.Color;
import game.MoveGenerator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import search.ab.Evaluation;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test class for Evaluation.
 */
public class EvaluationTest {

    static Evaluation evaluator;
    static MoveGenerator moveGenerator;

    /**
     * Initializes the needed instances before all test methods.
     */
    @BeforeAll
    public static void init() {
        evaluator = new Evaluation();
        moveGenerator = new MoveGenerator();
    }

    /**
     * Tests the ordering of moves to ensure the expected move is first.
     *
     * @param fen The FEN string representing the board state.
     * @param expectedFirst The expected combined integer first move in the ordered list.
     */
    private void testOrderMovesFirst(String fen, int expectedFirst) {
        init();

        // get moves
        LinkedHashMap<Integer, List<Integer>> movesMap = moveGenerator.getMovesWrapper(fen);
        LinkedList<Integer> movesList = evaluator.convertMovesToList(movesMap);

        // order moves
        char color_fen = fen.charAt(fen.length() - 1);
        Color color = getColorFromFen(color_fen);
        evaluator.orderMoves(movesList, color,moveGenerator);

        assertEquals(movesList.getFirst(), expectedFirst);
    }

    /**
     * Compares two positions to ensure the second one is rated higher.
     *
     * @param fen1 The FEN string for the first position.
     * @param color1 The color for the first position.
     * @param fen2 The FEN string for the second position.
     * @param color2 The color for the second position.
     */
    private void testPositionComparison(String fen1, Color color1, String fen2, Color color2) {
        init();
        moveGenerator.initializeBoard(fen1);
        double score1 = evaluator.ratePosition(moveGenerator, color1,1);
        moveGenerator.initializeBoard(fen2);
        double score2 = evaluator.ratePosition(moveGenerator, color2,1);
        assertTrue(score1 < score2, "Expected position 2 to be rated higher than position 1");
    }

    /**
     * Tests the rating of a winning move to ensure it is above a threshold.
     *
     * @param fen The FEN string representing the board state.
     * @param win Indicates if the move is a winning move.
     * @param startPosition The combined integer start position of the move.
     * @param endPosition The combined integer end position of the move.
     */
    private void testRateMoves(String fen, boolean win, int startPosition, int endPosition) {
        init();
        moveGenerator.initializeBoard(fen);
        char colorFen = fen.charAt(fen.length() - 1);
        Color color = getColorFromFen(colorFen);
        double rated = evaluator.rateMove(moveGenerator, color, startPosition, endPosition,1);
        assertTrue(rated >= 50000);
    }

    /**
     * Tests the rating of a winning position to ensure it is above a threshold.
     *
     * @param fen The FEN string representing the board state.
     * @param color The color for the position.
     * @param win Indicates if the position is a winning position.
     */
    private void testPositionRating(String fen, Color color, boolean win) {
        init();
        moveGenerator.initializeBoard(fen);
        double score = evaluator.ratePosition(moveGenerator, color,1);
        assertTrue(score >= 50000);
    }

    /**
     * Determines the color from the FEN character.
     *
     * @param colorFen The FEN character representing the color.
     * @return The corresponding Color enum value.
     */
    private Color getColorFromFen(char colorFen) {
        return colorFen == 'b' ? Color.BLUE : Color.RED;
    }

    /**
     * Compares the ratings of two moves to ensure the first move is rated higher.
     *
     * @param fen The FEN string representing the board state.
     * @param startPosition1 The combined integer start position of the first move.
     * @param endPosition1 The combined integer end position of the first move.
     * @param startPosition2 The combined integer start position of the second move.
     * @param endPosition2 The combined integer end position of the second move.
     */
    private void testMoveRatingComparison(String fen, int startPosition1, int endPosition1, int startPosition2, int endPosition2) {
        init();
        moveGenerator.initializeBoard(fen);
        char colorFen = fen.charAt(fen.length() - 1);
        Color color = getColorFromFen(colorFen);
        String boardFen = fen.substring(0,fen.length()-2);

        double rating1 = evaluator.rateMove(moveGenerator, color, startPosition1, endPosition1,1);
        double rating2 = evaluator.rateMove(moveGenerator, color, startPosition2, endPosition2,1);

        assertTrue(rating1 > rating2, "Expected move rating (" + rating1 + ") to be higher than (" + rating2 + ")");
    }

    @Test
    @DisplayName("Move Order 1")
    public void testMoveOrder1() {
        testOrderMovesFirst("1bb4/1b0b05/b01b0bb4/1b01b01b02/3r01rr2/b0r0r02rr2/4r01rr1/4r0r0 b", 5060);
    }

    @Test
    @DisplayName("Move Order 2")
    public void testMoveOrder2() {
        testOrderMovesFirst("b0b0b0b0b0b0/1b0b0b0b0b02/6b01/8/8/1r06/2r0r0r0r0r01/r0r0r0r0r0r0 b", 2636);
    }

    // prefer attacking the oppenent instead of cover your targeted player
    @Test
    @DisplayName("Move Order Cover")
    public void testMoveOrderCover() {
        testOrderMovesFirst("6/1b06/3b04/2b05/3r04/1r01r04/8/6 b", 3243);
    }

    // shows that attacking the opponent at the side is preferred to middle game
    @Test
    @DisplayName("Move Order Middle Game")
    public void testMoveOrderMG() {
        testOrderMovesFirst("b0b0b0b0b0b0/b0b0b0b02/8/7b0/3b02r01/1r06/1r03r0r01/r03r01 b", 3746);
    }

    // end game move at first
    @Test
    @DisplayName("Move Order 3")
    public void testMoveOrder3() {
        testOrderMovesFirst("6/8/4r03/8/8/8/3b04/6 b", 6373);
    }

    // end game move
    @Test
    @DisplayName("Move Rating (winning)")
    public void testRateMovesWin() {
        testRateMoves("6/8/4r03/8/8/8/3b04/6 b", true, 63, 73);
    }

    // compare Rating of moving forward as a single player or as a tower
    @Test
    @DisplayName("Move Rating Comparison 1")
    public void testMoveRatingComparison1() {
        String fen = "6/8/4r03/8/8/3b04/8/6 b";
        int startPosition2 = 53;
        int endPosition2 = 52; // normal move- one forward
        int startPosition1 = 53;
        int endPosition1 = 63; // tower move

        testMoveRatingComparison(fen, startPosition1, endPosition1, startPosition2, endPosition2);
    }

    // Compare the rating of different moves
    @Test
    @DisplayName("Move Rating Comparison 2")
    public void testMoveRatingComparison2() {
        String fen = "6/8/4r03/8/8/8/3b04/6 b";
        int startPosition1 = 63;
        int endPosition1 = 73; // end game move
        int startPosition2 = 12;
        int endPosition2 = 22; // sideway move

        testMoveRatingComparison(fen, startPosition1, endPosition1, startPosition2, endPosition2);
    }

    // end position
    @Test
    @DisplayName("Position Rating (winning)")
    public void testPositionRatingWin() {
        testPositionRating("6/8/4r03/8/8/8/8/b05 b", Color.BLUE, true);
    }

    // Compare two positions where one should be rated higher - end position vs. possible end
    @Test
    @DisplayName("Position Comparison")
    public void testPositionComparison() {
        String fen1 = "6/8/4r03/8/8/8/2bb5/6 b";
        String fen2 = "6/8/4r03/8/8/8/8/b05 b";
        testPositionComparison(fen1, Color.BLUE, fen2, Color.BLUE);
    }
}

